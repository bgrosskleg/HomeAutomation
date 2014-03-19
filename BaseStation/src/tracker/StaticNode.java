package tracker;

import java.util.Iterator;
import java.util.LinkedList;



public class StaticNode 
{
	/**
	 * Mac address for this node
	 */
	public String mac;
	
	/**
	 * All of the signal strengths with the corresponding broadcast numbers for this static node(contained in a mobile node).
	 */
	private LinkedList<SignalStrength> strengths;
	
	public StaticNode(String mac)
	{
		this.mac = mac;
		strengths = new LinkedList<SignalStrength>();
	}
	
	public SignalStrength GetCurrentSignalStrength()
	{
		if(strengths.peek() == null)
			return null;
		
		Iterator<SignalStrength> iter = strengths.iterator();
		int weight = 5;
		int totalWeight = 0;
		int sum = 0;
		while(iter.hasNext() && weight > 0)
		{
			SignalStrength stren = iter.next();
			sum += weight * stren.dbm;
			totalWeight += weight;
			weight--;
		}
		int dbm = sum / totalWeight;
		return new SignalStrength(dbm, strengths.peek().broadcastNumber);
	}
	
	public void AddSignalStrength(SignalStrength strength)
	{
		strengths.push(strength);
	}
}
