

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
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
import org.eclipse.mosaic.lib.database.Database;
import org.eclipse.mosaic.lib.util.scheduling.Event;
import org.eclipse.mosaic.rti.TIME;

public class MyTMCApp extends AbstractApplication<TrafficManagementCenterOperatingSystem> implements TrafficManagementCenterApplication,MosaicApplication {
    private String lastSentMsgId; 
    private LaneResponse laneResponse;  
    private LaneAreaDetector l = null;
    private int VEHICLES=4;
    String last= "";
    String last_id="";
    String coord ="";
    private final static long TIME_INTERVAL = 2 * TIME.SECOND;
    Collection<LaneAreaDetector> detectors;
    @Override
    public void onStartup() {
        
        
        getOs().getCellModule().enable();
        // File[] dbFiles = getOs().getConfigurationPath().listFiles((f, n) -> n.endsWith(".db"));
        // if (dbFiles != null && dbFiles.length > 0) {
        //     Database database = Database.loadFromFile(dbFiles[0]);
        //     database.getNode();
        // }
        // //detectors = getOs().getLaneAreaDetectors();
        //final byte[] traciMsg = assembleTraciCommand(getOs().getLaneAreaDetector("e2_0").getId()); // assemble the TraCI msg for sumo
            // for( LaneAreaDetector lad : detectors){
            //     getLog().info("det {} ",lad.getId());
            // }
    }
    @Override
    public void onInductionLoopUpdated(Collection<InductionLoop> updatedInductionLoops) {
    }
    static class LaneResponse {
        protected final String LADId;
        protected final String coord;

        public LaneResponse(String vehicleId, String coord) {
            this.LADId = vehicleId;
            this.coord = coord;
        }
        public String getLADId(){
            return this.LADId;
        }
        public String getcoord(){
            return this.coord;
        }
    }
    private byte[] assembleTraciCommand(String LAD_id) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        final byte TRACI_LAD = (byte) 0xad;
        final byte LAD_POSITION = (byte) 0x51;
        

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

    @Override
    public void onLaneAreaDetectorUpdated(Collection<LaneAreaDetector> updatedLaneAreaDetectors) {
        
       
        // for (LaneAreaDetector item : getOs().getLaneAreaDetectors()) {
        //     getLog().infoSimTime(this, "Segment '{}': average speed: {} m/s, traffic density: {} veh/km", item.getId(), item.getMeanSpeed(), item.getTrafficDensity());
        // }	
        
        // Collection<LaneAreaDetector> detectors = getOs().getLaneAreaDetectors();
         // assemble the TraCI msg for sumo
        //     for( LaneAreaDetector lad : detectors){
        //         getLog().info("det {} ha {} veicoli",lad.getId());
        //     }

        
        
        //String id = detector0.getId();
       
            
        //int vehiclesInSegment =0;// detector0.getAmountOfVehiclesOnSegment();
        
        // LaneAreaDetector l = getOs().getLaneAreaDetector()
        // if((TIME.SECOND * 1000) == getOs().getSimulationTime() )
            //getLog().info("{} {}", (TIME.SECOND * 10), getOs().getSimulationTime());
        
            for(LaneAreaDetector lad : getOs().getLaneAreaDetectors()){
                if(lad.getAmountOfVehiclesOnSegment() >= VEHICLES && !last.equals(lad.getId())){ 
                        if(laneResponse == null)
                            getLog().info("NULL");
                        else
                            coord = laneResponse.getcoord();

                        final byte[] traciMsg = assembleTraciCommand(lad.getId());
                        lastSentMsgId = getOs().sendSumoTraciRequest(traciMsg);
                        if(!last_id.equals(coord)){
                            MessageRouting server_0 = getOs().getCellModule().createMessageRouting().topoCast("server_0");
                            getOs().getCellModule().sendV2xMessage(new TrafficMessage(server_0,true,coord));
                            getLog().info(" [{}] Congestione rilevata su lane {}: Invio messaggio al server",lad.getId(),coord);
                            last_id=coord;
                        }
                        last=lad.getId();

                }

        }
    
            // if (lad.getAmountOfVehiclesOnSegment() >= 3){
            //     final byte[] traciMsg = assembleTraciCommand(lad.getId());
            //     lastSentMsgId = getOs().sendSumoTraciRequest(traciMsg);
            //     MessageRouting server_0 = getOs().getCellModule().createMessageRouting().topoCast("server_0");
            //     getOs().getCellModule().sendV2xMessage(new TrafficMessage(server_0,true,coord));
            //     getLog().info("Congestione rilevata su lane {}: Invio messaggio al server",coord);
            // }
    // } else if(delay == 0)
    //     delay = 31;
        //getLog().info("e2_0 --> Speed: {} , Traffico:{} , veicoli {}, id {} ",vehiclesInSegment, id );

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

    private LaneResponse decodeGetSpeedResponse(final byte[] msg) {
        final ByteArrayInputStream bais = new ByteArrayInputStream(msg);
        final DataInputStream dis = new DataInputStream(bais);

        try {
            byte command = dis.readByte(); // should be 0xa4 for vehicle info retrieval
            byte variableType = dis.readByte(); // should be 0x40 for speed response
            String LANId = readString(dis); // vehicle for which the response is
            byte returnType = dis.readByte(); // type of response, should be 
            String coord = readString(dis); // the actual value, speed in m/s here
            // getLog().info("{}{}",command, (byte) 0xad);
            // getLog().info("{}{}",variableType, (byte) 0x51);
            // getLog().info("{}{}",returnType, (byte) 0x0b);

            //getLog().info("Coord {}",coord);

            
            //String coor = new String(coordLAD,StandardCharsets.US_ASCII);
            return new LaneResponse(LANId, coord);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onShutdown() {
        // try {
            
        // } catch (TraCIException e) {
        //     // TODO: handle exception
        // }
        // TraCIException t = new TraCIException(lastSentMsgId);
        
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
        if (sumoTraciResult.getRequestCommandId().equals(lastSentMsgId)) {
            //String s = decodeGetSpeedResponse(sumoTraciResult.getTraciCommandResult());
            laneResponse = decodeGetSpeedResponse(sumoTraciResult.getTraciCommandResult());
            
        }
        
    }
   
    
    
  }