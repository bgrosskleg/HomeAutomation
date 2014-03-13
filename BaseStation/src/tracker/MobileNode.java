package tracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class MobileNode 
{
	/**
	 * Mac address for this node
	 */
	public String mac;
	
	/**
	 * All of the signal strengths with the corresponding broadcast numbers for this static node(contained in a mobile node).
	 */
	private LinkedList<StaticNode> staticNodes;
	
	/**
	 * A list of the locations that this node has been in with the most recent at the front.
	 */
	private LinkedList<Location> locations;
	
	public MobileNode(String mac)
	{
		this.mac = mac;
		staticNodes = new LinkedList<StaticNode>();
		locations = new LinkedList<Location>();
	}
	
	/**
	 * Get this mobile nodes reference to the static node with the input mac address.
	 * This is used when we want to modify the signal strength of the static node identified
	 * by mac.  Will create a new static node and add it to the mobile node if the mac address
	 * is not found.
	 * @param mac - the mac address to search for
	 * @return - A static node with the mac address input.
	 */
	public StaticNode GetStaticNode(String mac)
	{
		StaticNode toReturn = null;
		Iterator<StaticNode> iterator = staticNodes.listIterator();
		while(toReturn == null && iterator.hasNext())
		{
			StaticNode currentNode = iterator.next();
			if(currentNode.mac.equals(mac))
				toReturn = currentNode;
		}
		if(toReturn == null)
		{
			toReturn = new StaticNode(mac);
			staticNodes.push(toReturn);
		}
		
		return toReturn;
	}	
	
	/**
	 * Get all of the static nodes whos most recent signal strength was measured on the input
	 * broadcast number.  Used in the location calculator to get the signal strengths and locations of
	 * static nodes for computation of the mobile nodes location.
	 * @param broadcastNumber - The broadcast number to check for.
	 * @return - The static nodes that are returned must have their current signal strengths 
	 * 			broadcast number equal to the input broadcast number.
	 */
	public ArrayList<StaticNode> GetSignalStengthsByBroadcastNumber(int broadcastNumber)
	{
		ArrayList<StaticNode> toReturn = new ArrayList<StaticNode>();
		
		// Iterate over all of the static nodes and check their current signal strength
		Iterator<StaticNode> iterator = staticNodes.listIterator();
		while(iterator.hasNext())
		{
			StaticNode currentNode = iterator.next();
			
			// This static node was part of the most recent measurement so add it to the list.
			if(currentNode.GetCurrentSignalStrength() != null && currentNode.GetCurrentSignalStrength().broadcastNumber == broadcastNumber)
				toReturn.add(currentNode);
		}
		
		return toReturn;
	}
	
	/**
	 * Add a signal strength between this mobile node and the input static node.
	 * @param strength - The strength and broadcast number to add
	 * @param mac - The mac address of the static node that measured the signal strength
	 */
	public void AddSignalStrength(SignalStrength strength, String mac)
	{
		StaticNode staticNode = GetStaticNode(mac);
		
		// Only add the input strength if it has a higher broadcast number
		if(staticNode.GetCurrentSignalStrength() == null || strength.broadcastNumber > staticNode.GetCurrentSignalStrength().broadcastNumber)
			staticNode.AddSignalStrength(strength);
	}
	
	public void SetLocation(Location location)
	{
		locations.push(location);
	}
	
	public Location LastLocation()
	{
		return locations.peekFirst();
	}
}
