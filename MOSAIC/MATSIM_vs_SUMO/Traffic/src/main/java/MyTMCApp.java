

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.eclipse.mosaic.fed.application.ambassador.simulation.tmc.InductionLoop;
import org.eclipse.mosaic.fed.application.ambassador.simulation.tmc.LaneAreaDetector;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.MosaicApplication;
import org.eclipse.mosaic.fed.application.app.api.TrafficManagementCenterApplication;
import org.eclipse.mosaic.fed.application.app.api.os.TrafficManagementCenterOperatingSystem;
import org.eclipse.mosaic.interactions.application.ApplicationInteraction;
import org.eclipse.mosaic.lib.objects.traffic.LaneAreaDetectorInfo;
import org.eclipse.mosaic.lib.objects.traffic.SumoTraciResult;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.util.scheduling.Event;

public class MyTMCApp extends AbstractApplication<TrafficManagementCenterOperatingSystem> implements TrafficManagementCenterApplication,MosaicApplication {
    private String lastSentMsgId, lastArrived; 
    private int request = 61;
    private LaneResponse laneResponse,arrivedResponse;  
    @Override
    public void onStartup() {
        
        getOs().getCellModule().enable();
    }
    @Override
    public void onInductionLoopUpdated(Collection<InductionLoop> updatedInductionLoops) {
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
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
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
    public void onLaneAreaDetectorUpdated(Collection<LaneAreaDetector> updatedLaneAreaDetectors) {
        
       
        // for (LaneAreaDetector item : getOs().getLaneAreaDetectors()) {
        //     getLog().infoSimTime(this, "Segment '{}': average speed: {} m/s, traffic density: {} veh/km", item.getId(), item.getMeanSpeed(), item.getTrafficDensity());
        // }	
        String coord ="";
        String ids = "";
        List<String> VehIds =new ArrayList<String>();
        List<String> arrived=new ArrayList<String>();
        LaneAreaDetector detector0 = getOs().getLaneAreaDetector("e2_0");
        
        String id = detector0.getId();
        int vehiclesInSegment = detector0.getAmountOfVehiclesOnSegment();

        if(vehiclesInSegment >= 1){
        
        final byte[] traciArrivedMsg = assembleTraciCommand(getOs().getLaneAreaDetector("e2_0").getId(),(byte)0xab,(byte)0x7a); // assemble the TraCI msg for sumo
        
        //lastSentMsgId = getOs().sendSumoTraciRequest(traciMsg);
        final byte[] traciMsg = assembleTraciCommand(getOs().getLaneAreaDetector("e2_0").getId(),(byte)0xa4,(byte)0x00); // assemble the TraCI msg for sumo
        lastArrived = getOs().sendSumoTraciRequest(traciArrivedMsg);
        
        if(laneResponse == null && arrivedResponse == null)
           getLog().info("NULL");
        //else
            //coord = laneResponse.getcoord();
            //VehIds = laneResponse.getStringList();
            //arrived = arrivedResponse.getStringList();
            //ids = ListParser(VehIds);
            
            //getLog().info("Sono arrivati {}",VehIds.get(0));
      
       

        
        //getLog().info("Veicoli {} at sim {}",vehiclesInSegment, getOs().getSimulationTime());
        //getLog().info("Traffico {} at sim {}",detector.getTrafficDensity(), getOs().getSimulationTime());
        
            MessageRouting server_0 = getOs().getCellModule().createMessageRouting().topoCast("server_0");
            getOs().getCellModule().sendV2xMessage(new TrafficMessage(server_0,true,ids));
            getLog().info("Congestione rilevata su lane {}: Invio messaggio al server",coord);
       
            //getLog().info("e2_0 --> Speed: {} ,Time {}, Traffico:{} , veicoli {}, id {} ",detector0.getMeanSpeed(),getOs().getSimulationTime(),detector0.getTrafficDensity(),vehiclesInSegment, id );
        }
        // else if (request == 0){
        //     request = 61;

        // }
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
            coord = readList(dis); // the actual value, speed in m/s here
            // getLog().info("{}{}",command, (byte) 0xad);
            // getLog().info("{}{}",variableType, (byte) 0x51);
            //getLog().info("{}{}",returnType, (byte) 0x0E);
            
            getLog().info("Coord {}",coord);
            
            //String coor = new String(coordLAD,StandardCharsets.US_ASCII);
            return new LaneResponse(coord);
        } catch (IOException e) {
            System.out.println("Sono io");
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onShutdown() {
    }

    @Override
    public void processEvent(Event event) {
        
    }
    @Override
    public void onInteractionReceived(ApplicationInteraction arg0) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void onSumoTraciResponded(SumoTraciResult sumoTraciResult) {
        //getLog().info("sono io {}",lastSentMsgId);
        if (sumoTraciResult.getRequestCommandId().equals(lastSentMsgId)) {
            //String s = decodeGetSpeedResponse(sumoTraciResult.getTraciCommandResult());
            laneResponse = decodeGetSpeedResponse(sumoTraciResult.getTraciCommandResult());
            
        }
        if (sumoTraciResult.getRequestCommandId().equals(lastArrived)) {
            //String s = decodeGetSpeedResponse(sumoTraciResult.getTraciCommandResult());
            arrivedResponse = decodeGetSpeedResponse(sumoTraciResult.getTraciCommandResult());
            
        }
        
    }
    
    
  }