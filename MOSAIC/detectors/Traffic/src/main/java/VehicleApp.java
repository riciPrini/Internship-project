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


import javax.annotation.Nonnull;
import javax.annotation.Nullable;



public class VehicleApp extends AbstractApplication<VehicleOperatingSystem> implements CommunicationApplication, VehicleApplication {

    private boolean routeChanged = false;
 
    private final static float SPEED = 25 / 3.6f;
    private int routeTry=1;
    private String coord = "";
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
            // ReRouteSpecificConnectionsCostFunction myCostFunction = new ReRouteSpecificConnectionsCostFunction();
            // myCostFunction.setConnectionSpeedMS(coord,SPEED);
            
            INavigationModule navigationModule = getOs().getNavigationModule();
            
            RoutingPosition targetPosition = new RoutingPosition(navigationModule.getTargetPosition());
            RoutingParameters routingParameters = new RoutingParameters().costFunction(RoutingCostFunction.Fastest);
            //getLog().info("Get Time {} ",routingParameters);
            RoutingResponse response = navigationModule.calculateRoutes(targetPosition, routingParameters);
            //getLog().info("Get Time {} Get Length {}",response.getBestRoute().getTime(), response.getBestRoute().getLength());
            if(response.getBestRoute() != null)
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
        if (msg.getRouting().getSource().getSourceName().equals("rsu_0") ) {
            getOs().requestVehicleParametersUpdate().changeColor(Color.RED).apply();
            coord = receivedV2xMessage.getMessage().toString();
            routeChanged=true;
            //getLog().info("Mi è arrivato il messaggio {} at simulation time {}, routeChanged = {}", getOs().getId(),getOs().getSimulationTime(),routeChanged);
                
            
            
        }
       

        
    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission arg0) {
        // TODO Auto-generated method stub
        
    }

    
    @Override
    public void onVehicleUpdated(@Nullable VehicleData arg0, @Nonnull VehicleData updatedVehicleData) {
        // VehicleData vehicleData = getOperatingSystem().getNavigationModule().getVehicleData();
        // String route = vehicleData.getRouteId();
        
        // if (route != null) 
            //getLog().info("Prima {}",getOs().getNavigationModule().getCurrentRoute().toString());
            //getLog().info("{}",route);
        // vehicleData.getTime();

        //getLog().info("Route chenged --> {}",routeChanged);
        
        if(routeChanged==true  && --routeTry == 0){
            //getLog().info("Nome veicolo {} , rotta {}",updatedVehicleData.getName(),routeChanged );
            //getLog().infoSimTime(this, "Rerouting...");
            
            getLog().info("Coordinate lane congestionata {} , Rerouting...",coord);
            rerouting(coord);
            //getLog().info("Dopo {}",getOs().getNavigationModule().getCurrentRoute().toString());
        }

        

    }

   
   
   
    


    
   
  
    

}
