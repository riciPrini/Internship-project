/**
 * 
 */
package org.matsim.contrib.smartcity.scenariocreation;

import java.io.File;

import org.alex73.osmemory.MemoryStorage;
import org.alex73.osmemory.XMLReader;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

/**
 * Extract signals from osm file
 * @author Filippo Muzzini
 *
 */
public class SignalsFromOSM {

	private static final String DEFAULT_DIR = ".";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("need the input osm and matsim xlm network");
			System.exit(1);
		}
		File input = new File(args[0]);
		String matsimXml = args[1];
		System.out.println("SONO qui 1");
		XMLReader reader = new XMLReader();
		
		MemoryStorage result = null;
		System.out.println("SONO qui 2");
		try {
			result = reader.read(input);
		} catch (Exception e) {
			System.out.println("SONO entrato qui");
			e.printStackTrace();
			System.exit(1);
		}
		
		Network network = NetworkUtils.createNetwork();
		System.out.println("SONO qui 3");
		MatsimNetworkReader netReader = new MatsimNetworkReader(network);
		netReader.readFile(matsimXml);
		
		String controllerClass = null;
		if (args.length > 2) {
			controllerClass = args[2];
		}
		System.out.println("SONO qui 4");
		Signals sign = new Signals(result, network, controllerClass);
		System.out.println("SONO qui 5");
		sign.writeFiles(DEFAULT_DIR);
		

	}

}
