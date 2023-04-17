package org.matsim.contrib.smartcity.perception.wrapper;

import java.util.HashMap;
import java.util.Map;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;

import com.google.inject.Inject;

/**
 * Implementation of an PassivePerceptionWrapper.
 * This implementation handle the event of MATSim and update a data structure.
 * 
 * @author Filippo Muzzini
 * 
 * @see PassivePerceptionWrapper
 *
 */
public class PassivePerceptionWrapperImpl implements PassivePerceptionWrapper {
	
	private HashMap<Id<Link>, LinkTrafficStatus> trafficMap;
	private Map<Id<Vehicle>, Vehicle> vehicles;
	
	/**
	 * Constructor of PassivePerceptionWrapper.
	 * It creates the data structure that represents the network and the vehicles
	 * 
	 * @param network MATSim network
	 * @param scenario MATSim scenario
	 */
	@Inject
	public PassivePerceptionWrapperImpl(Network network, Scenario scenario) {
		this.trafficMap = new HashMap<Id<Link>, LinkTrafficStatus>();
		for (Id<Link> id : network.getLinks().keySet()) {
			this.trafficMap.put(id, new LinkTrafficStatus());
		}
		
		this.vehicles = scenario.getVehicles().getVehicles();
	}
	

	@Override
	public void handleEvent(VehicleEntersTrafficEvent event) {
		Id<Link> idLink = event.getLinkId();
		Id<Vehicle> idVehicle = event.getVehicleId();
		vehicleEntered(idLink, idVehicle);
	}

	@Override
	public void handleEvent(VehicleLeavesTrafficEvent event) {
		Id<Link> idLink = event.getLinkId();
		Id<Vehicle> idVehicle = event.getVehicleId();
		vehicleLeaved(idLink, idVehicle);
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		Id<Link> idLink = event.getLinkId();
		Id<Vehicle> idVehicle = event.getVehicleId();
		vehicleLeaved(idLink, idVehicle);
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		Id<Link> idLink = event.getLinkId();
		Id<Vehicle> idVehicle = event.getVehicleId();
		vehicleEntered(idLink, idVehicle);

	}
	
	public LinkTrafficStatus getLinkTrafficStatus(Id<Link> idLink) {
		return this.trafficMap.get(idLink);
	}
	
	public int getTotalVehicleOnLink(Id<Link> idLink) {
		LinkTrafficStatus linkStatus = this.trafficMap.get(idLink);
		return linkStatus.getTotal();
	}
	
	public int getTypeVehicleOnLink(Id<Link> idLink, Id<VehicleType> idType) {
		LinkTrafficStatus linkStatus = this.trafficMap.get(idLink);
		return linkStatus.getTotalByType(idType);
	}
	
	protected void vehicleEntered(Id<Link> idLink, Id<Vehicle> vehicle) {
		Id<VehicleType> idType = typeFromVehicle(vehicle);
		this.trafficMap.get(idLink).addVehicle(idType);
	}
	
	protected void vehicleLeaved(Id<Link> idLink, Id<Vehicle> vehicle) throws VehicleLeftBeforeEnter {
		Id<VehicleType> idType = typeFromVehicle(vehicle); 
		try {
			this.trafficMap.get(idLink).subVehicle(idType);
		} catch (SubOnNull e) {
			throw new VehicleLeftBeforeEnter(vehicle, idLink);
		}
	}
	
	private Id<VehicleType> typeFromVehicle(Id<Vehicle> idVehicle) {
		Id<VehicleType> type = this.vehicles.get(idVehicle).getType().getId();
		return type;
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.perception.wrapper.PassivePerceptionWrapper#getTrafficMap()
	 */
	@Override
	public HashMap<Id<Link>, LinkTrafficStatus> getTrafficMap() {
		return this.trafficMap;
	}

}
