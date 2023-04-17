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
import org.eclipse.mosaic.fed.application.app.etsi.VehicleCamSendingApp;
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
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class VehicleApp extends AbstractApplication<VehicleOperatingSystem> implements CommunicationApplication, VehicleApplication {

    private boolean routeChanged = false;
 
    private final static float SPEED = 25 / 3.6f;
    private int routeTry=1,count=2;
    private List<String> ed =new ArrayList<String>();
    private String lastSentMsgId;
    private String coord = "";
    private Boolean ask = false,check=false;
    HashMap<String,String> edges = new HashMap<String,String>();
    @Override
    public void onStartup() {
        getOs().getCellModule().enable();
        //String route = vehicleData.getRouteId();
        try {
            edges = readXML();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
        

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
            
            
            RoutingResponse response = navigationModule.calculateRoutes(targetPosition, routingParameters);
            //getLog().info("Get Time {} Get Length {}",response.getBestRoute().getTime(), response.getBestRoute().getLength());
            getLog().info("{}",response.getBestRoute());
            if(response.getBestRoute() != null)
            navigationModule.switchRoute(response.getBestRoute());
            
    }
    public HashMap<String,String> readXML() throws ParserConfigurationException, SAXException, IOException{
        File file = new File("/home/riccardo/Scrivania/UNIVERSITA/tirocinio/eclipse-mosaic-22.1/scenarios/agenti/sumo/plans.rou.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        document.getDocumentElement().normalize();
        //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        //getLog().info("{}",document.getDocumentElement().getNodeName());
        NodeList nList = document.getElementsByTagName("route");
        HashMap<String,String> edges = new HashMap<String,String>();
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Element nNode = (Element) nList.item(temp);
            edges.put(nNode.getAttribute("id"), nNode.getAttribute("edges"));
            //getLog().info("Route ID :{} \n edges {}, {}" + nNode.getAttribute("id"),nNode.getAttribute("edges"), getOs().getNavigationModule().getVehicleData().getRouteId());
        }
        return edges;
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
    public List<String> stringParser(String ids){
        String[] splitString = ids.split(" ");
        List<String> myList = Arrays.asList(splitString);
        return myList;
    }

    @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {
        final V2xMessage msg = receivedV2xMessage.getMessage();
        VehicleData vehicleData = getOperatingSystem().getNavigationModule().getVehicleData();
        if(!ask){
            for (Map.Entry<String, String> entry : edges.entrySet()) {
                if (entry.getKey().equals(vehicleData.getRouteId()))
                    ed = stringParser(entry.getValue());
            }
            ask =true;
        }
        
        
        if (msg.getRouting().getSource().getSourceName().equals("rsu_0") && vehicleData.getRouteId().equals("r_0") ) {
            getOs().requestVehicleParametersUpdate().changeColor(Color.RED).apply();
            coord = receivedV2xMessage.getMessage().toString();
           
            if(ed.contains(coord.replace("_0","")) && !check){
                getLog().info("Lo contengo {} {} {}",ed,coord,vehicleData.getRouteId());
                routeChanged=true;
                check=true;
            }
            // final byte[] traciMsg = assembleTraciCommand(vehicleData.getRouteId(),(byte)0xa6,(byte)0x54);
            // lastSentMsgId = getOs().sendSumoTraciRequest(traciMsg);
            // if(laneResponse != null){
            //     edges = laneResponse.getStringList();
            //     String edg =  ListParser(edges);
            //     getLog().info("{}",edg);
            // }
            
            //routeChanged=true;// DA scommentera quando devi fre partire utto
            //getLog().info("Mi è arrivato il messaggio {} at simulation time {}, routeChanged = {}", getOs().getId(),getOs().getSimulationTime(),routeChanged);
                
            
            
        }
       

        
    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission arg0) {
        // TODO Auto-generated method stub
        
    }

    
    @Override
    public void onVehicleUpdated(@Nullable VehicleData arg0, @Nonnull VehicleData updatedVehicleData) {
        VehicleData vehicleData = getOperatingSystem().getNavigationModule().getVehicleData();
        String route = vehicleData.getRouteId();
        
        //if (route != null) 
            //getLog().info("Prima {}",getOs().getNavigationModule().getCurrentRoute().toString());
            //getLog().info("{}",route);
            //vehicleData.getTime();
        // getLog().info("{}",count);
        // if(--count > 0){
        //     getLog().info("Sono qua");
       
        // }
        
        
        //getLog().info("Route chenged --> {} {}",route, getOs().getId());
        
        if(routeChanged==true  && route.equals("r_0") && --routeTry == 0){
            //getLog().info("Nome veicolo {} , rotta {}",updatedVehicleData.getName(),routeChanged );
            //getLog().infoSimTime(this, "Rerouting...");
            //getLog().infoSimTime(this, "Rerouting...");
            //getLog().info("Coordinate lane congestionata {} , Rerouting...",coord,routeTry);
            rerouting(coord);
            //getLog().info("Dopo {}",getOs().getNavigationModule().getCurrentRoute().toString());
        }

        

    }



    
   
   
   
    


    
   
  
    

}
