package tracker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
		
		// We need at least 4 locations to give a location
		if(sNodes.size() < 4)
			return mNode.LastLocation();
		
		// Calculate the expected location for each set of 4 static nodes in our list.  If we have
		// 5 static nodes then we will calculate 4 locations.  For 6 static nodes we do 9 calculations.
		// For 4 nodes, only one calculation.  Average these values later.
		LinkedList<Location> intermediateLocations = new LinkedList<Location>();
		for(int i = 0; i < sNodes.size() - 3; i++)
			for(int j = i + 1; j < sNodes.size() - 2; j++)
				for(int k = j + 1; k < sNodes.size() - 1; k++)
					for(int l = k + 1; l < sNodes.size(); l++)
					{
						Location tempLocation = new Location();
						try {
							tempLocation = CalculateLocation(sNodes.get(i), sNodes.get(j), sNodes.get(k), sNodes.get(l), controller);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						intermediateLocations.add(tempLocation);
					}
		
		// Average the x and y values in the locations.
		double x = 0.0f;
		double y = 0.0f;
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
	
	private static Location CalculateLocation(StaticNode n1, StaticNode n2, StaticNode n3, StaticNode n4, BaseStationController controller) throws Exception
	{
		/*// My test values
		Location l1 = new Location(1,1);
		Location l2 = new Location(7,3);
		Location l3 = new Location(3,7);
		double r12 = 1.497123679f;
		double r13 = 3.605551275f;*/
		
		
		model.StaticNode modelStaticNode1 = controller.getStaticNode(n1.mac);
		model.StaticNode modelStaticNode2 = controller.getStaticNode(n2.mac);
		model.StaticNode modelStaticNode3 = controller.getStaticNode(n3.mac);
		model.StaticNode modelStaticNode4 = controller.getStaticNode(n4.mac);
		// Locations of the static nodes
		Location l1 = new Location(modelStaticNode1.getLocation());
		Location l2 = new Location(modelStaticNode2.getLocation());
		Location l3 = new Location(modelStaticNode3.getLocation());
		Location l4 = new Location(modelStaticNode4.getLocation());
		
		// The ratio's of the distances.  Have to convert dbm into power by 10^(dbm/10) then power into distance by power ^ .5  
		double r12 = (double) Math.sqrt(1 / Math.pow(10, ((double)n1.GetCurrentSignalStrength().dbm - 30) / 10)) /  
							Math.sqrt(1 / Math.pow(10, ((double)n2.GetCurrentSignalStrength().dbm - 30) / 10)); 
		double r13 = (double) Math.sqrt(1 / Math.pow(10, ((double)n1.GetCurrentSignalStrength().dbm - 30) / 10)) /  
				Math.sqrt(1 / Math.pow(10, ((double)n3.GetCurrentSignalStrength().dbm - 30) / 10)); 
		double r14 = (double) Math.sqrt(1 / Math.pow(10, ((double)n1.GetCurrentSignalStrength().dbm - 30) / 10)) /  
				Math.sqrt(1 / Math.pow(10, ((double)n4.GetCurrentSignalStrength().dbm - 30) / 10)); 
		/*
		double r12 = (double) Math.sqrt(1 / Math.pow(10, ((double)-25.5716 - 30) / 10)) /  
				Math.sqrt(1 / Math.pow(10, ((double)-24.20637 - 30) / 10)); 
		double r13 = (double) Math.sqrt(1 / Math.pow(10, ((double)-25.5716 - 30) / 10)) /  
			Math.sqrt(1 / Math.pow(10, ((double)-22.6656 - 30) / 10)); 
		double r14 = (double) Math.sqrt(1 / Math.pow(10, ((double)-25.5716 - 30) / 10)) /  
			Math.sqrt(1 / Math.pow(10, ((double)-13.5436 - 30) / 10));
		double r12 = (double) Math.sqrt(1 / Math.pow(10, ((double)-61) / 10)) /  
				Math.sqrt(1 / Math.pow(10, ((double)-59) / 10)); 
		double r13 = (double) Math.sqrt(1 / Math.pow(10, ((double)-61) / 10)) /  
			Math.sqrt(1 / Math.pow(10, ((double)-58) / 10)); 
		double r14 = (double) Math.sqrt(1 / Math.pow(10, ((double)-61) / 10)) /  
			Math.sqrt(1 / Math.pow(10, ((double)-48.61) / 10));*/
		// Really just some intermediate computation
		Circle c1 = GetCircle(l1, l2, r12);
		Circle c2 = GetCircle(l1, l3, r13);
		Circle c3 = GetCircle(l1, l4, r14);
		
		Location[] locations = concat(CircleIntersection(c1, c2), CircleIntersection(c1, c3));
		
		for(int i = 0; i < 3; i++)
		{
			BigDecimal x1 = BigDecimal.valueOf(locations[i].x).setScale(1, BigDecimal.ROUND_HALF_UP);
			BigDecimal y1 = BigDecimal.valueOf(locations[i].y).setScale(1, BigDecimal.ROUND_HALF_UP);
			for(int j = i + 1; j < locations.length; j++)
			{
				BigDecimal x2 = BigDecimal.valueOf(locations[j].x).setScale(1, BigDecimal.ROUND_HALF_UP);
				BigDecimal y2 = BigDecimal.valueOf(locations[j].y).setScale(1, BigDecimal.ROUND_HALF_UP);
				
				if(x1.compareTo(x2) == 0 && y1.compareTo(y2) == 0)
					return locations[i];
			}
		}
		
		// Shouldn't reach here
		throw new Exception("Couldn't calculate a location.");
	}
	
	/**
	 * Calculate a circle.  Not sure what it represents to be honest.
	 * @param l1 - location of the first static node
	 * @param l2 - location of the second static node
	 * @param strengthRatio - l1 signal strength / l2 signal strength
	 * @return a circle
	 */
	private static Circle GetCircle(Location l1, Location l2, double strengthRatio)
	{
		double a = l1.x;
		double b = l1.y;
		double c = l2.x;
		double d = l2.y;
		double e = strengthRatio;
		
		double f = e * e;
		
		double m = f - 1;
		double n = 2 * a - 2 * c * f;
		double o = 2 * b - 2 * d * f;
		double q = a * a + b * b - f * c * c - f * d * d;
		
		Circle toReturn = new Circle();
		toReturn.x = - n / (2 * m);
		toReturn.y = - o / (2 * m);
		toReturn.r = (double) Math.sqrt(q / m + Math.pow(n / (2 * m), 2) + Math.pow(o / (2 * m), 2));
		return toReturn;
	}
	
	/**
	 * Calculate the intersections of 2 circles.
	 * @param c1 - First circle
	 * @param c2 - Second circle
	 * @return - 0, 1, 2 intersections
	 */
	private static Location[] CircleIntersection(Circle c1, Circle c2)
	{
		// The rest is magic... or algebra.  Depends how you want to look at it. Intersection of the circles
		double d = (double) Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2));
		
		Location r1 = new Location();
		
		r1.x = (double) ((c2.x + c1.x) / 2 + (c2.x - c1.x) * (Math.pow(c1.r, 2) - Math.pow(c2.r, 2)) / (2 * d * d) +
				((c2.y - c1.y) / (2 * d * d)) * 
				Math.sqrt((Math.pow(c1.r + c2.r, 2) - (d * d)) * ((d * d) - Math.pow(c1.r - c2.r, 2))));
		
		r1.y = (double) ((c2.y + c1.y) / 2 + (c2.y - c1.y) * (Math.pow(c1.r, 2) - Math.pow(c2.r, 2)) / (2 * d * d) -
				((c2.x - c1.x) / (2 * d * d)) * 
				Math.sqrt((Math.pow(c1.r + c2.r, 2) - (d * d)) * ((d * d) - Math.pow(c1.r - c2.r, 2))));
		
		Location r2 = new Location();
		
		r2.x = (double) ((c2.x + c1.x) / 2 + (c2.x - c1.x) * (Math.pow(c1.r, 2) - Math.pow(c2.r, 2)) / (2 * d * d) -
				((c2.y - c1.y) / (2 * d * d)) * 
				Math.sqrt((Math.pow(c1.r + c2.r, 2) - (d * d)) * ((d * d) - Math.pow(c1.r - c2.r, 2))));
		
		r2.y = (double) ((c2.y + c1.y) / 2 + (c2.y - c1.y) * (Math.pow(c1.r, 2) - Math.pow(c2.r, 2)) / (2 * d * d) +
				((c2.x - c1.x) / (2 * d * d)) * 
				Math.sqrt((Math.pow(c1.r + c2.r, 2) - (d * d)) * ((d * d) - Math.pow(c1.r - c2.r, 2))));
		
		System.out.println("C1: (" + c1.x + ", " + c1.y + ", " + c1.r + ")");
		System.out.println("C2: (" + c2.x + ", " + c2.y + ", " + c2.r + ")");
		
		System.out.println("R1: (" + r1.x + ", " + r1.y + ")");
		System.out.println("R2: (" + r2.x + ", " + r2.y + ")");
		
		return new Location[]{ r1, r2 };
	}


	public static <T> T[] concat(T[] first, T[] second) 
	{
	  T[] result = Arrays.copyOf(first, first.length + second.length);
	  System.arraycopy(second, 0, result, first.length, second.length);
	  return result;
	}
}

class Circle
{
	public double x;
	public double y;
	public double r;
}