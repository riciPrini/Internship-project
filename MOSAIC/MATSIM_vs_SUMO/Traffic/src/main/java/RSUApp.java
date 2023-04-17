import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CamBuilder;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedAcknowledgement;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedV2xMessage;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.CommunicationApplication;
import org.eclipse.mosaic.fed.application.app.api.MosaicApplication;
import org.eclipse.mosaic.fed.application.app.api.os.OperatingSystem;
import org.eclipse.mosaic.interactions.application.ApplicationInteraction;
import org.eclipse.mosaic.interactions.communication.V2xMessageTransmission;
import org.eclipse.mosaic.lib.geo.GeoCircle;
import org.eclipse.mosaic.lib.geo.GeoPoint;
import org.eclipse.mosaic.lib.objects.traffic.SumoTraciResult;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.util.scheduling.Event;




public class RSUApp extends AbstractApplication<OperatingSystem> implements CommunicationApplication, MosaicApplication {
    private final static GeoPoint HAZARD_LOCATION = GeoPoint.latLon(44.656258, 10.933027);
    private String lastSentMsgId; 
    private int delay = 61;
    private LaneResponse laneResponse; 
    private List<String> VehIds =new ArrayList<String>();
    @Override
    public void onShutdown() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStartup() {
        // TODO Auto-generated method stub
        getLog().info("Mi sono acceso");
        getOs().getCellModule().enable();
        
    }

    @Override
    public void processEvent(Event arg0) throws Exception {
        // TODO Auto-generated method stub
        
        
    }

    @Override
    public void onAcknowledgementReceived(ReceivedAcknowledgement arg0) {
        // TODO Auto-generated method stub

        
    }

    @Override
    public void onCamBuilding(CamBuilder arg0) {
        // TODO Auto-generated method stub
        
    }

    public List<String> stringParser(String ids){
        String[] splitString = ids.split(" ");
        List<String> myList = Arrays.asList(splitString);
        return myList;
    }
    public String ListParser(List<String> list){
        list.removeIf(String::isEmpty);
        // int n = list.size()/2;
        // System.out.println(n);
        // int startIndex = list.size() - n;
        // System
        // List<String> elementsToRemove = list.subList(startIndex, list.size());
        // elementsToRemove.clear();
        return String.join(" ", list);
    }
    @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {
        
        if (receivedV2xMessage.getMessage() instanceof TrafficMessage) {
            //lastSentMsgId = getOs().sendSumoTraciRequest(traciMsg);
            
            
            //getLog().info("Received Message {}", receivedV2xMessage.getMessage());
            final GeoCircle geoCircle = new GeoCircle(HAZARD_LOCATION, 3000.0D);
            MessageRouting rsu_0 = getOs().getCellModule().createMessageRouting().geoBroadcastBasedOnUnicast(geoCircle);
           
            String coord = receivedV2xMessage.getMessage().toString();
            final byte[] traciMsg = assembleTraciCommand("55_0",(byte)0xa4,(byte)0x00);
            lastSentMsgId = getOs().sendSumoTraciRequest(traciMsg); // assemble the TraCI msg for sumo
            if(laneResponse != null){
                VehIds = laneResponse.getStringList(); 
                String ids = ListParser(VehIds);   
                //List<String> ids = stringParser(coord);
                    findLane(VehIds);
                    getLog().info("Veicoli che stanno rannando {}",ids);
            }
                
            // getOs().getCellModule().sendV2xMessage(new TrafficMessage(rsu_0,true,coord));
            
        }
        
    }

    public void findLane(List<String> ids){
        for(int i = 1 ; i < ids.size() -1 ; i++){
        //getLog().info("{} {} ",ids.get(),getOs().getSimulationTime());
        if(!ids.get(i).equals("1889_0") && !ids.get(i).equals("55_0") ){
            final byte[] traciMsg = assembleTraciCommand(ids.get(i),(byte)0xa4,(byte)0x54); // assemble the TraCI msg for sumo
            
            lastSentMsgId = getOs().sendSumoTraciRequest(traciMsg);
        }
    }
    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission arg0) {
        // TODO Auto-generated method stub
        
    }
    static class LaneResponse {
        private List<String> stringList;

        public LaneResponse(List<String> stringList) {
            this.stringList = stringList;
        }
    
        public List<String> getStringList() {
            return stringList;
        }
    }
    private byte[] readBytes(final DataInputStream in, final int bytes) throws IOException {
        final byte[] result = new byte[bytes];

        in.read(result, 0, bytes);

        return result;
    }

    private String readString(final DataInputStream in) throws IOException {
        final int length = in.readInt();
        final byte[] bytes = readBytes(in, length);

        return new String(bytes, StandardCharsets.UTF_8);
    }
    private List<String> readList(final DataInputStream dataInputStream) throws IOException {
        final int length = dataInputStream.readInt();
        List<String> stringList = new ArrayList<String>();

        try {
            while (dataInputStream.available() > 0) { // controlla se ci sono dati disponibili nel flusso di input
                String str = dataInputStream.readUTF(); // leggi una stringa dal flusso di input
               // getLog().info("{}", str);
                stringList.add(str); // aggiungi la stringa alla lista
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dataInputStream.close(); // chiudi il flusso di input
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringList;
    }
    private LaneResponse decodeGetSpeedResponse(final byte[] msg) {
        final ByteArrayInputStream bais = new ByteArrayInputStream(msg);
        final DataInputStream dis = new DataInputStream(bais);
        List<String> coord = new ArrayList<String>();
        try {
            byte command = dis.readByte(); // should be 0xa4 for vehicle info retrieval
            byte variableType = dis.readByte(); // should be 0x40 for speed response
            String LANId = readString(dis); // vehicle for which the response is
            byte returnType = dis.readByte(); // type of response, should be 
            coord =readList(dis); // the actual value, speed in m/s here
            // getLog().info("{}{}",command, (byte) 0xad);
            // getLog().info("{}{}",variableType, (byte) 0x51);
            // getLog().info("{}{}",returnType, (byte) 0x0E);
            
            getLog().info("Coord {}",coord);
            
            //String coor = new String(coordLAD,StandardCharsets.US_ASCII);
            return new LaneResponse(coord);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            System.out.println("Sono io");
            return new LaneResponse(null);
        }
    }
    private byte[] assembleTraciCommand(String LAD_id,byte TraciReq,byte TraciReturn) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        final byte TRACI_LAD = TraciReq; //0xad
        final byte LAD_POSITION = TraciReturn; //0x51
        

        try {
            dos.writeByte(TRACI_LAD); // Bits Traci Command (0xa4 for vehicle value retrieval)
            dos.writeByte(LAD_POSITION); // Bits Traci Command (0xa4 for vehicle value retrieval)
            dos.writeInt(LAD_id.length()); // Length of Vehicle Identifier
            dos.write(LAD_id.getBytes(StandardCharsets.UTF_8)); // Vehicle Identifier
        } catch (IOException e) {
            System.out.println("SONO io l'eccezione");
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }
    @Override
    public void onInteractionReceived(ApplicationInteraction arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onInteractionReceived'");
    }

    @Override
    public void onSumoTraciResponded(SumoTraciResult sumoTraciResult) {
        // TODO Auto-generated method stub
        if (sumoTraciResult.getRequestCommandId().equals(lastSentMsgId)) {
            //String s = decodeGetSpeedResponse(sumoTraciResult.getTraciCommandResult());
            laneResponse = decodeGetSpeedResponse(sumoTraciResult.getTraciCommandResult());
            
        }
    }
    
}
