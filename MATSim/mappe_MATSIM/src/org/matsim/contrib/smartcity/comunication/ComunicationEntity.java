/**
 * 
 */
package org.matsim.contrib.smartcity.comunication;

/**
 * @author Filippo Muzzini
 *
 */
public interface ComunicationEntity {

	/**
	 * Called by entites that want senda a message
	 * @param message
	 */
	public void sendToMe(ComunicationMessage message);
	
}
