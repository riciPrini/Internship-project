
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CamBuilder;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedAcknowledgement;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedV2xMessage;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.CommunicationApplication;
import org.eclipse.mosaic.fed.application.app.api.VehicleApplication;
import org.eclipse.mosaic.fed.application.app.api.os.OperatingSystem;
import org.eclipse.mosaic.fed.application.app.api.os.TrafficManagementCenterOperatingSystem;
import org.eclipse.mosaic.fed.application.app.api.os.VehicleOperatingSystem;
import org.eclipse.mosaic.interactions.communication.V2xMessageTransmission;
import org.eclipse.mosaic.interactions.vehicle.VehicleStop;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.objects.vehicle.VehicleData;
import org.eclipse.mosaic.lib.util.scheduling.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerApp extends AbstractApplication<OperatingSystem> implements CommunicationApplication {
    private final List<EmissionMessage> receivedMessages = new ArrayList<>();

    @Override
    public void onStartup() {
        getOs().getCellModule().enable();
        getLog().info("Starting the server! {}", getOs().getId());
    }

    @Override
    public void onShutdown() {
        
    }

    @Override
    public void processEvent(Event event) throws Exception {
    }

    @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {
        
        getLog().info("Received Message {}", receivedV2xMessage.getMessage());
        if (receivedV2xMessage.getMessage() instanceof TrafficMessage) {

            String ids = receivedV2xMessage.getMessage().toString();
           
            getLog().info("Congestione rilevata su lane {}: Invio messaggio a RSU", ids);
            MessageRouting rsu_0 = getOs().getCellModule().createMessageRouting().topoCast("rsu_0");
            getOs().getCellModule().sendV2xMessage(new TrafficMessage(rsu_0,true,ids));
            
        }
    }
    

    @Override
    public void onAcknowledgementReceived(ReceivedAcknowledgement receivedAcknowledgement) {

    }

    @Override
    public void onCamBuilding(CamBuilder camBuilder) {

    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission v2xMessageTransmission) {
        
        
    }
}
