package tracker;

import java.util.ArrayList;
import java.util.Iterator;
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
		
		/*distanceLookup = new DistanceLookup[57];
		for(int i = 0; i < 57; i++)
		{
			int dbm = -(i + 26);
			//double meters = 0.3048 * (0.0442 * dbm * dbm - 3.691 * dbm + 77.517);
			double meters = 0.3048 * (-0.0014 * dbm * dbm * dbm - 0.1988 * dbm * dbm - 10.477 * dbm - 190.53);
			double min = meters - 2;
			if(min < 0)
				min = 0;
			double max = meters + 2;
			distanceLookup[i] = new DistanceLookup(min, max);
		}*/
	}
	
	private void ComputeDistances()
	{
		int xDistSquare[] = new int[width];
		int yDistSquare[] = new int[height];
		
		for(int i = 0; i < width; i++)
			xDistSquare[i] = i * i;
		
		for(int i = 0; i < height; i++)
			yDistSquare[i] = i * i;
		
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
		
		System.out.println("Node: " + nl[0].loc.x + "," + nl[0].loc.y + ":" + nl[0].strengthIndex);
		System.out.println("Node: " + nl[1].loc.x + "," + nl[1].loc.y + ":" + nl[1].strengthIndex);
		System.out.println("Node: " + nl[2].loc.x + "," + nl[2].loc.y + ":" + nl[2].strengthIndex);
		System.out.println("Highest Count: " + highestCount);
		System.out.println("Number Overlap: " + num);		
		System.out.println("Estimator result: (X=" + x + ", Y=" + y + ")");
		
		return new Location(x, y);
	}
	
	private Boolean InRange(int x, int y, LocAndStrength node)
	{
		DistanceLookup dbmDist = distanceLookup[node.strengthIndex - 26];
		
		int tempX = (int) (x - node.loc.x);
		if(tempX < 0)
			tempX = -tempX;
		
		int tempY = (int) (y - node.loc.y);
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
			strengthIndex = n.GetCurrentSignalStrength().dbm;
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
		
		public DistanceLookup(double minFeet, double maxFeet, boolean feet)
		{
			min = (int) ((0.3048 * minFeet )/ 0.024384);
			max = (int) ((0.3048 * maxFeet )/ 0.024384);
		}
	}
	
	private DistanceLookup distanceLookup[] = 
		{
			new DistanceLookup(0, 5, true),//26
			new DistanceLookup(0, 5, true),//27
			new DistanceLookup(0, 5, true),//28
			new DistanceLookup(0, 5, true),//29
			new DistanceLookup(0, 5, true),//30
			new DistanceLookup(0, 5, true),//31
			new DistanceLookup(0, 5, true),//32
			new DistanceLookup(0, 5, true),//33
			new DistanceLookup(0, 10, true),//34
			new DistanceLookup(0, 10, true),//35
			new DistanceLookup(0, 10, true),//36
			new DistanceLookup(5, 10, true),//37
			new DistanceLookup(5, 10, true),//38
			new DistanceLookup(5, 10, true),//39
			new DistanceLookup(5, 15, true),//40
			new DistanceLookup(5, 15, true),//41
			new DistanceLookup(5, 15, true),//42
			new DistanceLookup(5, 20, true),//43
			new DistanceLookup(10, 20, true),//44
			new DistanceLookup(10, 20, true),//45
			new DistanceLookup(10, 20, true),//46
			new DistanceLookup(10, 25, true),//47
			new DistanceLookup(10, 25, true),//48
			new DistanceLookup(10, 25, true),//49
			new DistanceLookup(15, 25, true),//50
			new DistanceLookup(15, 25, true),//51
			new DistanceLookup(15, 30, true),//52
			new DistanceLookup(15, 30, true),//53
			new DistanceLookup(15, 30, true),//54
			new DistanceLookup(15, 30, true),//55
			new DistanceLookup(15, 30, true),//56
			new DistanceLookup(20, 30, true),//57
			new DistanceLookup(20, 30, true),//58
			new DistanceLookup(20, 35, true),//59
			new DistanceLookup(20, 35, true),//60
			new DistanceLookup(20, 35, true),//61
			new DistanceLookup(20, 35, true),//62
			new DistanceLookup(20, 35, true),//63
			new DistanceLookup(25, 35, true),//64
			new DistanceLookup(25, 35, true),//65
			new DistanceLookup(30, 75, true),//66
			new DistanceLookup(30, 75, true),//67
			new DistanceLookup(55, 75, true),//68
			new DistanceLookup(55, 80, true),//69
			new DistanceLookup(55, 80, true),//70
			new DistanceLookup(55, 80, true),//71
			new DistanceLookup(55, 80, true),//72
			new DistanceLookup(60, 80, true),//73
			new DistanceLookup(60, 80, true),//74
			new DistanceLookup(60, 80, true),//75
			new DistanceLookup(60, 80, true),//76
			new DistanceLookup(60, 85, true),//77
			new DistanceLookup(60, 85, true),//78
			new DistanceLookup(60, 85, true),//79
			new DistanceLookup(65, 85, true),//80
			new DistanceLookup(65, 85, true),//81
			new DistanceLookup(65, 90, true),//82
			new DistanceLookup(65, 90, true)//83			
		};
	
	/*private DistanceLookup distanceLookup[] = 
	{
		new DistanceLookup(0, 5, true),//26
		new DistanceLookup(0, 5, true),//27
		new DistanceLookup(0, 5, true),//28
		new DistanceLookup(0, 5, true),//29
		new DistanceLookup(0, 5, true),//30
		new DistanceLookup(0, 5, true),//31
		new DistanceLookup(0, 5, true),//32
		new DistanceLookup(2.5, 7.5, true),//33
		new DistanceLookup(5, 10, true),//34
		new DistanceLookup(5, 10, true),//35
		new DistanceLookup(5, 10, true),//36
		new DistanceLookup(5, 10, true),//37
		new DistanceLookup(5, 10, true),//38
		new DistanceLookup(5, 10, true),//39
		new DistanceLookup(10, 30, true),//40
		new DistanceLookup(10, 30, true),//41
		new DistanceLookup(10, 30, true),//42
		new DistanceLookup(10, 30, true),//43
		new DistanceLookup(10, 30, true),//44
		new DistanceLookup(15, 35, true),//45
		new DistanceLookup(15, 40, true),//46
		new DistanceLookup(15, 50, true),//47
		new DistanceLookup(15, 55, true),//48
		new DistanceLookup(20, 55, true),//49
		new DistanceLookup(20, 55, true),//50
		new DistanceLookup(20, 60, true),//51
		new DistanceLookup(20, 60, true),//52
		new DistanceLookup(20, 60, true),//53
		new DistanceLookup(20, 60, true),//54
		new DistanceLookup(20, 60, true),//55
		new DistanceLookup(20, 65, true),//56
		new DistanceLookup(25, 65, true),//57
		new DistanceLookup(25, 65, true),//58
		new DistanceLookup(25, 65, true),//59
		new DistanceLookup(25, 70, true),//60
		new DistanceLookup(25, 70, true),//61
		new DistanceLookup(25, 70, true),//62
		new DistanceLookup(25, 75, true),//63
		new DistanceLookup(25, 75, true),//64
		new DistanceLookup(25, 75, true),//65
		new DistanceLookup(30, 75, true),//66
		new DistanceLookup(30, 75, true),//67
		new DistanceLookup(55, 75, true),//68
		new DistanceLookup(55, 80, true),//69
		new DistanceLookup(55, 80, true),//70
		new DistanceLookup(55, 80, true),//71
		new DistanceLookup(55, 80, true),//72
		new DistanceLookup(60, 80, true),//73
		new DistanceLookup(60, 80, true),//74
		new DistanceLookup(60, 80, true),//75
		new DistanceLookup(60, 80, true),//76
		new DistanceLookup(60, 85, true),//77
		new DistanceLookup(60, 85, true),//78
		new DistanceLookup(60, 85, true),//79
		new DistanceLookup(65, 85, true),//80
		new DistanceLookup(65, 85, true),//81
		new DistanceLookup(65, 90, true),//82
		new DistanceLookup(65, 90, true)//83			
	};*/
	
	/*private DistanceLookup distanceLookup[] = 
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
		};*/
		
}