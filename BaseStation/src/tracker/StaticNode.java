package tracker;

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
		return strengths.peek();
	}
	
	public void AddSignalStrength(SignalStrength strength)
	{
		strengths.push(strength);
	}
}
