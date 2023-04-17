import org.apache.commons.lang3.ObjectUtils.Null;
import org.eclipse.mosaic.fed.application.ambassador.navigation.INavigationModule;
import org.eclipse.mosaic.fed.application.ambassador.navigation.RoadPositionFactory;
import org.eclipse.mosaic.fed.application.ambassador.simulation.VehicleUnit;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CamBuilder;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedAcknowledgement;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedV2xMessage;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.CommunicationApplication;
import org.eclipse.mosaic.fed.application.app.api.MosaicApplication;
import org.eclipse.mosaic.fed.application.app.api.VehicleApplication;
import org.eclipse.mosaic.fed.application.app.api.os.VehicleOperatingSystem;
import org.eclipse.mosaic.interactions.application.ApplicationInteraction;
import org.eclipse.mosaic.interactions.communication.V2xMessageTransmission;
import org.eclipse.mosaic.interactions.vehicle.VehicleRouteChange;
import org.eclipse.mosaic.lib.enums.VehicleStopMode;
import org.eclipse.mosaic.lib.geo.CartesianPoint;
import org.eclipse.mosaic.lib.geo.GeoPoint;
import org.eclipse.mosaic.lib.objects.road.IConnection;
import org.eclipse.mosaic.lib.objects.road.INode;
import org.eclipse.mosaic.lib.objects.road.IRoadPosition;
import org.eclipse.mosaic.lib.objects.traffic.SumoTraciResult;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.objects.v2x.V2xMessage;
import org.eclipse.mosaic.lib.objects.vehicle.Emissions;
import org.eclipse.mosaic.lib.objects.vehicle.VehicleData;
import org.eclipse.mosaic.lib.objects.vehicle.VehicleType;
import org.eclipse.mosaic.lib.routing.CandidateRoute;
import org.eclipse.mosaic.lib.routing.RoutingCostFunction;
import org.eclipse.mosaic.lib.routing.RoutingParameters;
import org.eclipse.mosaic.lib.routing.RoutingPosition;
import org.eclipse.mosaic.lib.routing.RoutingResponse;
import org.eclipse.mosaic.lib.routing.util.ReRouteSpecificConnectionsCostFunction;
import org.eclipse.mosaic.lib.util.scheduling.Event;
import org.eclipse.mosaic.rti.TIME;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;



public class VehicleApp extends AbstractApplication<VehicleOperatingSystem> implements CommunicationApplication, VehicleApplication,MosaicApplication {

    private boolean routeChanged = false;
    private long sendTimes = 300000; 
    private final static float SPEED = 25 / 3.6f;
    private int routeTry=1;
    private String coord = "";
    private String lastSentMsgId; 
    private TraciRequest laneResponse;
    @Override
    public void onStartup() {
        getOs().getCellModule().enable();

    }


    
    @Override
    public void onShutdown() {
        double endTime = (double)getOs().getSimulationTime()/1000000f;
        TIME.format(getOs().getSimulationTimeMs());
        //VehicleData vehicleData = getOperatingSystem().getNavigationModule().getVehicleData();
        
        if(getOs().getVehicleParameters().getVehicleColor() == Color.RED)
            getLog().info("End simulation {} of {}",endTime , getOs().getId());
        
    }

    public void rerouting(String coord){
            ReRouteSpecificConnectionsCostFunction myCostFunction = new ReRouteSpecificConnectionsCostFunction();
            myCostFunction.setConnectionSpeedMS(coord,SPEED);
            
            INavigationModule navigationModule = getOs().getNavigationModule();
            
            RoutingPosition targetPosition = new RoutingPosition(navigationModule.getTargetPosition());
            RoutingParameters routingParameters = new RoutingParameters().costFunction(myCostFunction);
            RoutingResponse response = navigationModule.calculateRoutes(targetPosition, routingParameters);
            getLog().info("Get Time {} Get Length {}",response.getBestRoute().getTime(), response.getBestRoute().getLength());
            navigationModule.switchRoute(response.getBestRoute());
            
    }
    @Override
    public void processEvent(Event event) throws Exception {
        
        
    }

    @Override
    public void onAcknowledgementReceived(ReceivedAcknowledgement arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCamBuilding(CamBuilder arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {
        final V2xMessage msg = receivedV2xMessage.getMessage();
        VehicleData vehicleData = getOperatingSystem().getNavigationModule().getVehicleData();
        getLog().info("Mi è arrivato il messaggio {} ", vehicleData);
        // if (msg.getRouting().getSource().getSourceName().equals("rsu_0") && vehicleData.getRouteId().equals("r_0")) {
        //     getOs().requestVehicleParametersUpdate().changeColor(Color.RED).apply();
        //     coord = receivedV2xMessage.getMessage().toString();
        //     routeChanged=true;
        //     //getLog().info("Mi è arrivato il messaggio {} at simulation time {}, routeChanged = {}", getOs().getId(),getOs().getSimulationTime(),routeChanged);
                
            
            
        // }
       

        
    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission arg0) {
        // TODO Auto-generated method stub
        
    }
    private static byte[] assembleTraciCommand(String LAD_id) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        final byte TRACI_LAD = (byte) 0xa4;
        final byte LAD_POSITION = (byte) 0x00;
        

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


    private String readByte(final DataInputStream in) throws IOException {
        final int length = in.readInt();
        final byte[] bytes = readBytes(in, length);

        List<byte[]> byteArrayList = Arrays.asList(bytes);
        //getLog().info("{} {}",byteArrayList.get(0),length);
        return new String(byteArrayList.get(0),StandardCharsets.UTF_8);
    }


    private TraciRequest decodeGetSpeedResponse(final byte[] msg) {
        final ByteArrayInputStream bais = new ByteArrayInputStream(msg);
        final DataInputStream dis = new DataInputStream(bais);

        try {
            byte command = dis.readByte(); // should be 0xa4 for vehicle info retrieval
            byte variableType = dis.readByte(); // should be 0x40 for speed response
            String LANId = readString(dis); // vehicle for which the response is
            byte returnType = dis.readByte(); // type of response, should be 
            int coord = dis.readInt(); // the actual value, speed in m/s here
            // getLog().info("{}  --> {}",command, (byte) 0xa4);
            // getLog().info("{}   --> {}",variableType, (byte) 0x00);
            // getLog().info("{} -->   {}",returnType, (byte) 0x0E);
            // getLog().info("{} va ",LANId);
           getLog().info("Coord {}",coord);

            // // getLog().info("Coord 2 {}",LANId.get(0));
            // String pippo = String.valueOf(LANId.get(0));
            
            // String coor = new String(LANId.get(0), StandardCharsets.UTF_8);
            return new TraciRequest(LANId, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void onVehicleUpdated(@Nullable VehicleData arg0, @Nonnull VehicleData updatedVehicleData) {
        VehicleData vehicleData = getOperatingSystem().getNavigationModule().getVehicleData();
        
            String route = vehicleData.getRouteId();
            String l = "";
        if(--sendTimes > 0 ){
            final byte[] traciMsg = assembleTraciCommand(getOs().getId()); // assemble the TraCI msg for sumo
        // getLog().info("{}",sendTimes);
        lastSentMsgId = getOs().sendSumoTraciRequest(traciMsg);
        if(laneResponse == null)
            getLog().info("NULL");
        else
            l = laneResponse.getLADId();
            // getLog().info("{}",l);
        }
        if (route != null) 
            //getLog().info("Prima {}",getOs().getNavigationModule().getCurrentRoute().toString());
            //getLog().info("{}",route);
        vehicleData.getTime();

        //getLog().info("Route chenged --> {}",routeChanged);
        
        if(routeChanged==true  && --routeTry == 0){
            //getLog().info("Nome veicolo {} , rotta {}",updatedVehicleData.getName(),routeChanged );
            //getLog().infoSimTime(this, "Rerouting...");
            
            getLog().info("Coordinate lane congestionata {} , Rerouting...",coord,routeTry);
            rerouting(coord);
            //getLog().info("Dopo {}",getOs().getNavigationModule().getCurrentRoute().toString());
        }

        

    }



    @Override
    public void onInteractionReceived(ApplicationInteraction arg0) {
        // TODO Auto-generated method stub
        
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
