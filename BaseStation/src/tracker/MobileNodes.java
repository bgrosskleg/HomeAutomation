package tracker;

import java.util.Iterator;
import java.util.LinkedList;

public class MobileNodes 
{
	/**
	 * All of the mobile nodes that have sent signal strengths
	 */
	private LinkedList<MobileNode> mobileNodes;
	
	public MobileNodes()
	{
		mobileNodes = new LinkedList<MobileNode>();
	}
	
	/**
	 * Get a mobile node by its mac address.  Adds the mobile node if it is not already stored.
	 * @param mac - the mac address to search for
	 * @return - A mobile node with the mac address input.
	 */
	public MobileNode GetMobileNode(String mac)
	{
		MobileNode toReturn = null;
		Iterator<MobileNode> iterator = mobileNodes.listIterator();
		while(toReturn == null && iterator.hasNext())
		{
			MobileNode currentNode = iterator.next();
			if(currentNode.mac.equals(mac))
				toReturn = currentNode;
		}
		if(toReturn == null)
		{
			toReturn = new MobileNode(mac);
			mobileNodes.push(toReturn);
		}
		
		return toReturn;
	}

}
