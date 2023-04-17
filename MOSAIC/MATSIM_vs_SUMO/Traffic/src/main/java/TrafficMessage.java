import java.util.List;

import javax.annotation.Nonnull;

import org.eclipse.mosaic.lib.objects.v2x.EncodedPayload;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.objects.v2x.V2xMessage;

public class TrafficMessage extends V2xMessage{
    private final Boolean isTraffic;
    private final String position;

    public TrafficMessage(MessageRouting msn, Boolean isTraffic,String position){
        super(msn);
        this.isTraffic =  isTraffic;
        this.position = position;
    }

    public Boolean getIsTraffic(){
        return this.isTraffic;
    }


    @Override
    @Nonnull
    public EncodedPayload getPayLoad() {
        // TODO Auto-generated method stub
        return new EncodedPayload(1);
    }
    @Override
    public String toString() {
        return this.position;
    }
}
