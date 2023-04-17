import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CamBuilder;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedAcknowledgement;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedV2xMessage;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.CommunicationApplication;
import org.eclipse.mosaic.fed.application.app.api.os.OperatingSystem;
import org.eclipse.mosaic.interactions.communication.V2xMessageTransmission;
import org.eclipse.mosaic.lib.geo.GeoCircle;
import org.eclipse.mosaic.lib.geo.GeoPoint;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.util.scheduling.Event;

public class RSUApp extends AbstractApplication<OperatingSystem> implements CommunicationApplication {
    private final static GeoPoint HAZARD_LOCATION = GeoPoint.latLon(44.656258, 10.933027);
    String last= "";
    @Override
    public void onShutdown() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStartup() {
        // TODO Auto-generated method stub
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

    @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {
        
        if (receivedV2xMessage.getMessage() instanceof TrafficMessage && !last.equals(receivedV2xMessage.getMessage().toString())) {
            //getLog().info("Received Message {}", receivedV2xMessage.getMessage());
            final GeoCircle geoCircle = new GeoCircle(HAZARD_LOCATION, 3000.0D);
            MessageRouting rsu_0 = getOs().getCellModule().createMessageRouting().geoBroadcastBasedOnUnicast(geoCircle);
            String coord = receivedV2xMessage.getMessage().toString();
            getLog().info("Congestione rilevata su lane {}: Invio messaggio ai Veicoli",coord,last);
            getOs().getCellModule().sendV2xMessage(new TrafficMessage(rsu_0,true,coord));
            last = coord;
        }
        
    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission arg0) {
        // TODO Auto-generated method stub
        
    }
    
}
