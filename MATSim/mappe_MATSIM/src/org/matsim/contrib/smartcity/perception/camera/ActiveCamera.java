package org.matsim.contrib.smartcity.perception.camera;

import java.util.ArrayList;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.smartcity.perception.wrapper.ActivePerceptionWrapper;
import org.matsim.contrib.smartcity.perception.wrapper.LinkChangedListener;
import org.matsim.contrib.smartcity.perception.wrapper.LinkTrafficStatus;

/**
 * A camera that see one link and notify to listeners when the traffic change.
 * 
 * @author Filippo Muzzini
 *
 */
public class ActiveCamera extends Camera implements LinkChangedListener {
	
	private ArrayList<CameraListener> cameraListeners = new ArrayList<CameraListener>();
	private ActivePerceptionWrapper wrapper;
	
	/**
	 * Construct a camera.
	 * 
	 * @param idCamera arbitrary id for camera
	 * @param linkId id of link that camera watch
	 * @param wrapper the perception wrapper to utilize
	 */
	public ActiveCamera(Id<Camera> idCamera, Id<Link> linkId, ActivePerceptionWrapper wrapper) {
		super(idCamera, linkId);
		this.wrapper = wrapper;
		this.wrapper.addLinkChangedListener(this, linkId);
		LinkTrafficStatus status = wrapper.getLinkTrafficStatus(linkId);
		this.setStatus(status);
	}

	public void addCameraListener(CameraListener listener) {
		this.cameraListeners.add(listener);
	}

	@Override
	public void publishLinkChanged(Id<Link> idLink, LinkTrafficStatus status) {
		CameraStatus cameraStatus = this.getCameraStatus();
		for (CameraListener cl : this.cameraListeners) {
			cl.pushCameraStatus(cameraStatus);
		}
	}
	
}
