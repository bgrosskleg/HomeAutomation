package tracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import controller.BaseStationController;

public class LocationCalculator 
{
	/**
	 * Calculate the current location of a mobile node.  This only works if there are 3 or more 
	 * signal strengths with the current broadcast number.  ELse, we return the last known location.
	 * @param controller - The controller is used to get information from the controller such as static node locations.
	 * @param node - The node to calculate the location of.
	 * @return - The location of the mobile node.  Either newly calculated or the last location.
	 */
	public static Location CalculateLocation(BaseStationController controller, MobileNode mNode, int broadcastNumber)
	{
		// Get the static nodes that got a signal strength reading in the most recent broadcast.
		ArrayList<StaticNode> sNodes = mNode.GetSignalStengthsByBroadcastNumber(broadcastNumber);
		
		// We need at least 3 locations to give a location
		if(sNodes.size() < 3)
			return mNode.LastLocation();
		
		// Calculate the expected location for each set of 3 static nodes in our list.  If we have
		// 4 static nodes then we will calculate 4 locations.  For 5 static nodes we do 9 calculations.
		// For 3 nodes, only one calculation.  Average these values later.
		LinkedList<Location> intermediateLocations = new LinkedList<Location>();
		for(int i = 0; i < sNodes.size() - 2; i++)
			for(int j = i + 1; j < sNodes.size() - 1; j++)
				for(int k = j + 1; k < sNodes.size(); k++)
				{
					Location tempLocation = CalculateLocation(sNodes.get(i), sNodes.get(i), sNodes.get(i), controller);
					intermediateLocations.add(tempLocation);
				}
		
		// Average the x and y values in the locations.
		float x = 0.0f;
		float y = 0.0f;
		Iterator<Location> iterator = intermediateLocations.iterator();
		while(iterator.hasNext())
		{
			Location loc = iterator.next();
			x += loc.x;
			y += loc.y;
		}
		x = x / intermediateLocations.size();
		y = y / intermediateLocations.size();
		
		return new Location(x, y);
	}
	
	private static Location CalculateLocation(StaticNode n1, StaticNode n2, StaticNode n3, BaseStationController controller)
	{
		/* My test values
		Location l1 = new Location(1,1);
		Location l2 = new Location(7,3);
		Location l3 = new Location(3,7);
		float r12 = 1.497123679f;
		float r13 = 3.605551275f;
		*/
		
		model.StaticNode modelStaticNode1 = controller.getStaticNode(n1.mac);
		model.StaticNode modelStaticNode2 = controller.getStaticNode(n2.mac);
		model.StaticNode modelStaticNode3 = controller.getStaticNode(n3.mac);
		// Locations of the static nodes
		Location l1 = new Location(modelStaticNode1.getLocation());
		Location l2 = new Location(modelStaticNode2.getLocation());
		Location l3 = new Location(modelStaticNode3.getLocation());
		
		// TODO: might need to decay these signal strengths by r^2 as they aren't linear in this form?
		// The ratio's of the signal strengths.  
		float r12 = n1.GetCurrentSignalStrength().percent /  n2.GetCurrentSignalStrength().percent; 
		float r13 = n1.GetCurrentSignalStrength().percent /  n3.GetCurrentSignalStrength().percent;
		
		// Really just some intermediate computation
		Circle c1 = GetCircle(l1, l2, r12);
		Circle c2 = GetCircle(l1, l3, r13);
		
		// The rest is magic... or algebra.  Depends how you want to look at it.
		float d = (float) Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2));
		
		Location r1 = new Location();
		
		r1.x = (float) ((c2.x + c1.x) / 2 + (c2.x - c1.x) * (Math.pow(c1.r, 2) - Math.pow(c2.r, 2)) / (2 * d * d) +
				((c2.y - c1.y) / (2 * d * d)) * 
				Math.sqrt((Math.pow(c1.r + c2.r, 2) - (d * d)) * ((d * d) - Math.pow(c1.r - c2.r, 2))));
		
		r1.y = (float) ((c2.y + c1.y) / 2 + (c2.y - c1.y) * (Math.pow(c1.r, 2) - Math.pow(c2.r, 2)) / (2 * d * d) -
				((c2.x - c1.x) / (2 * d * d)) * 
				Math.sqrt((Math.pow(c1.r + c2.r, 2) - (d * d)) * ((d * d) - Math.pow(c1.r - c2.r, 2))));
		
		Location r2 = new Location();
		
		r2.x = (float) ((c2.x + c1.x) / 2 + (c2.x - c1.x) * (Math.pow(c1.r, 2) - Math.pow(c2.r, 2)) / (2 * d * d) -
				((c2.y - c1.y) / (2 * d * d)) * 
				Math.sqrt((Math.pow(c1.r + c2.r, 2) - (d * d)) * ((d * d) - Math.pow(c1.r - c2.r, 2))));
		
		r2.y = (float) ((c2.y + c1.y) / 2 + (c2.y - c1.y) * (Math.pow(c1.r, 2) - Math.pow(c2.r, 2)) / (2 * d * d) +
				((c2.x - c1.x) / (2 * d * d)) * 
				Math.sqrt((Math.pow(c1.r + c2.r, 2) - (d * d)) * ((d * d) - Math.pow(c1.r - c2.r, 2))));
		
		System.out.println("C1: (" + c1.x + ", " + c1.y + ", " + c1.r + ")");
		System.out.println("C2: (" + c2.x + ", " + c2.y + ", " + c2.r + ")");
		
		System.out.println("R1: (" + r1.x + ", " + r1.y + ")");
		System.out.println("R2: (" + r2.x + ", " + r2.y + ")");
		
		// TODO: return either r1 or r2 here. Need to figure out how to decide.
		return null;
	}
	
	/**
	 * Calculate a circle.  Not sure what it represents to be honest.
	 * @param l1 - location of the first static node
	 * @param l2 - location of the second static node
	 * @param strengthRatio - l1 signal strength / l2 signal strength
	 * @return a circle
	 */
	private static Circle GetCircle(Location l1, Location l2, float strengthRatio)
	{
		float a = l1.x;
		float b = l1.y;
		float c = l2.x;
		float d = l2.y;
		float e = strengthRatio;
		
		float f = e * e;
		
		float m = f - 1;
		float n = 2 * a - 2 * c * f;
		float o = 2 * b - 2 * d * f;
		float q = a * a + b * b - f * c * c - f * d * d;
		
		Circle toReturn = new Circle();
		toReturn.x = - n / (2 * m);
		toReturn.y = - o / (2 * m);
		toReturn.r = (float) Math.sqrt(q / m + Math.pow(n / (2 * m), 2) + Math.pow(o / (2 * m), 2));
		return toReturn;
	}
}

class Circle
{
	public float x;
	public float y;
	public float r;
}