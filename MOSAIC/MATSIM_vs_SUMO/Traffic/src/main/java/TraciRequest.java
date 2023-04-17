import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class TraciRequest {
    
        protected final String LADId;
        protected final String coord;

        public TraciRequest(String vehicleId, String coord) {
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
    

