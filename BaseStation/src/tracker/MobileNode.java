package tracker;

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
			if(currentNode.mac == mac)
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
	 * Add a signal strength between this mobile node and the input static node.
	 * @param strength - The strength and broadcast number to add
	 * @param mac - The mac address of the static node that measured the signal strength
	 */
	public void AddSignalStrength(SignalStrength strength, String mac)
	{
		StaticNode staticNode = GetStaticNode(mac);
		
		// Only add the input strength if it has a higher broadcast number
		if(strength.broadcastNumber > staticNode.GetCurrentSignalStrength().broadcastNumber)
			staticNode.AddSignalStrength(strength);
	}
	
	public void SetLocation(Location location)
	{
		locations.push(location);
	}
}
