/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.population.routes.NetworkRoute;

/**
 * This class implements a static logic without smart behavior.
 * The agent with this logic take the planned road.
 * 
 * @author Filippo Muzzini
 *
 */
public class StaticDriverLogic extends AbstractDriverLogic {

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.agent.AbstractDriverLogic#chooseNextLinkId()
	 */
	private List<Id<Link>> linksList;
	private int index;

	@Override
	public Id<Link> getNextLinkId() {
		if (this.actualLink == this.getDestinationLinkId()) {
			return null;
		}
		
		if (this.index == this.linksList.size()) {
			//last link
			return this.getDestinationLinkId();
		}
		
		return this.linksList.get(index);
	}
	
	@Override
	public void setLeg(Leg leg) {
		super.setLeg(leg);
		this.linksList = ((NetworkRoute) this.route).getLinkIds();
		this.index = 0;
	}
	
	protected List<Id<Link>> getLinksList() {
		return this.linksList;
	}
	
	protected void setLinksList(List<Id<Link>> linksList) {
		this.linksList = linksList;
	}
	
	protected int getActualIndex() {
		return this.index;
	}
	
	@Override
	public void setActualLink(Id<Link> actualLink) {
		super.setActualLink(actualLink);
		
		int newIndex = this.linksList.indexOf(actualLink);
		if (newIndex == -1) {
			//siamo fuori strada, cosa fare?
		}
		
		this.index = newIndex+1;
	}

	

}
