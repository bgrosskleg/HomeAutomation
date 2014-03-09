package tracker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import controller.BaseStationController;

public class LocationEstimator 
{
	private int width;
	private int height;
	
	private double distance[][];
	
	public LocationEstimator(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		distance = new double[width][height];
		
		ComputeDistances();
	}
	
	private void ComputeDistances()
	{
		int xDistSquare[] = new int[width];
		int yDistSquare[] = new int[height];
		
		for(int i = 0; i < width; i++)
			xDistSquare[i] = width * width;
		
		for(int i = 0; i < height; i++)
			xDistSquare[i] = height * height;
		
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				distance[i][j] = Math.sqrt(xDistSquare[i] + yDistSquare[j]);
		
	}
	
	/**
	 * Calculate the current location of a mobile node.  This only works if there are 3 or more 
	 * signal strengths with the current broadcast number.  ELse, we return the last known location.
	 * @param controller - The controller is used to get information from the controller such as static node locations.
	 * @param node - The node to calculate the location of.
	 * @return - The location of the mobile node.  Either newly calculated or the last location.
	 */
	public Location CalculateLocation(BaseStationController controller, MobileNode mNode, int broadcastNumber)
	{
		// Get the static nodes that got a signal strength reading in the most recent broadcast.
		ArrayList<StaticNode> sNodes = mNode.GetSignalStengthsByBroadcastNumber(broadcastNumber);
		
		// We need at least 3 locations to give a location
		if(sNodes.size() < 3)
			return mNode.LastLocation();
		
		LocAndStrength nl[] = new LocAndStrength[sNodes.size()];
		Iterator<StaticNode> iter = sNodes.iterator();
		for(int i = 0; i < nl.length; i++)
		{
			StaticNode sNode = iter.next();
			nl[i] = new LocAndStrength(sNode,
									new Location(controller.getStaticNode(sNode.mac).getLocation()));
		}
		
		int count[][] = new int[width][height];
		int highestCount = 0;
		
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
			{
				for(int k = 0; k < nl.length; k++)
				{
					if(InRange(i, j, nl[k]))
					{
						count[i][j] += 1;
					}					
				}
				if(count[i][j] > highestCount)
					highestCount = count[i][j];
			}
		
		int x = 0;
		int y = 0;
		int num = 0;
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
			{
				if(count[i][j] == highestCount)
				{
					x += i;
					y += j;
					num++;
				}
			}
		x = x / num;
		y = y / num;
		
		System.out.println("Estimator result: (X=" + x + ", Y=" + y + ")");
		
		return new Location(x, y);
	}
	
	private Boolean InRange(int x, int y, LocAndStrength node)
	{
		DistanceLookup dbmDist = distanceLookup[node.strengthIndex];
		
		int tempX = (int) (x - node.loc.x);
		if(tempX < 0)
			tempX = -tempX;
		
		int tempY = (int) (x - node.loc.x);
		if(tempY < 0)
			tempY = -tempY;
		
		double pointDist = distance[tempX][tempY];
		if(dbmDist.min < pointDist && dbmDist.max > pointDist)
			return true;
		return false;
	}
	
	class LocAndStrength
	{
		public int strengthIndex;
		public Location loc;
		
		public LocAndStrength(StaticNode n, Location l)
		{
			strengthIndex = -n.GetCurrentSignalStrength().dbm;
			loc = l;
		}
	}
	
	class DistanceLookup
	{
		// In pixels
		public int min;
		public int max;
		
		public DistanceLookup(double minMeters, double maxMeters)
		{
			min = (int) (minMeters / 0.024384);
			max = (int) (maxMeters / 0.024384);
		}
	}
	
	private DistanceLookup distanceLookup[] = 
		{
			new DistanceLookup(0, 1.5),//26
			new DistanceLookup(0, 1.5),//27
			new DistanceLookup(0, 1.5),//28
			new DistanceLookup(0, 1.5),//29
			new DistanceLookup(0, 1.5),//30
			new DistanceLookup(0, 1.5),//31
			new DistanceLookup(0, 1.5),//32
			new DistanceLookup(0, 1.5),//33
			new DistanceLookup(0, 1.5),//34
			new DistanceLookup(0, 1.5),//35
			new DistanceLookup(0, 1.5),//36
			new DistanceLookup(0, 1.5),//37
			new DistanceLookup(0, 1.5),//38
			new DistanceLookup(0, 1.5),//39
			new DistanceLookup(0, 1.5),//40
			new DistanceLookup(1.5, 3.0),//41
			new DistanceLookup(1.5, 3.0),//42
			new DistanceLookup(1.5, 3.0),//43
			new DistanceLookup(1.5, 3.0),//44
			new DistanceLookup(1.5, 3.0),//45
			new DistanceLookup(1.5, 3.0),//46
			new DistanceLookup(1.5, 3.0),//47
			new DistanceLookup(1.5, 3.0),//48
			new DistanceLookup(1.5, 3.0),//49
			new DistanceLookup(1.5, 3.0),//50
			new DistanceLookup(1.5, 3.0),//51
			new DistanceLookup(1.5, 3.0),//52
			new DistanceLookup(1.5, 3.0),//53
			new DistanceLookup(1.5, 3.0),//54
			new DistanceLookup(1.5, 3.0),//55
			new DistanceLookup(3.0, 6.0),//56
			new DistanceLookup(3.0, 6.0),//57
			new DistanceLookup(3.0, 6.0),//58
			new DistanceLookup(3.0, 6.0),//59
			new DistanceLookup(3.0, 6.0),//60
			new DistanceLookup(6.0, 9.1),//61
			new DistanceLookup(6.0, 9.1),//62
			new DistanceLookup(6.0, 9.1),//63
			new DistanceLookup(6.0, 9.1),//64
			new DistanceLookup(6.0, 9.1),//65
			new DistanceLookup(6.0, 9.1),//66
			new DistanceLookup(9.1, 12.2),//67
			new DistanceLookup(9.1, 12.2),//68
			new DistanceLookup(9.1, 12.2),//69
			new DistanceLookup(9.1, 12.2),//70
			new DistanceLookup(9.1, 12.2),//71
			new DistanceLookup(12.2, 18.3),//72
			new DistanceLookup(12.2, 18.3),//73
			new DistanceLookup(12.2, 18.3),//74
			new DistanceLookup(18.3, 24.4),//75
			new DistanceLookup(18.3, 24.4),//76
			new DistanceLookup(18.3, 24.4),//77
			new DistanceLookup(18.3, 24.4),//78
			new DistanceLookup(24.3, 30.5),//79
			new DistanceLookup(24.3, 30.5),//80
			new DistanceLookup(24.3, 30.5),//81
			new DistanceLookup(24.3, 30.5),//82
			new DistanceLookup(24.3, 30.5)//83			
		};
		
}