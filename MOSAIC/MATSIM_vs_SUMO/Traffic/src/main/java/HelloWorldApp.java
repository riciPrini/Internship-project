import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.os.RoadSideUnitOperatingSystem;

import org.eclipse.mosaic.lib.util.scheduling.Event;
  
public class HelloWorldApp extends AbstractApplication<RoadSideUnitOperatingSystem>  {
       
    @Override
    public void onStartup() {
        getLog().info("Hello World!");
    }
  
   
   
    @Override
    public void onShutdown() {
        getLog().info("Good bye!");
    }
   
    @Override
    public void processEvent(Event event) {
        // ...
    }
}