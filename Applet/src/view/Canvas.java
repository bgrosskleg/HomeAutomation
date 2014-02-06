package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JPanel;

import controller.CanvasMouseAdapter;
import model.HouseObject;
import model.Light;
import model.Point;
import model.Region;
import model.Sensor;
import model.User;
import model.Wall;

public class Canvas extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	//Grid parameters;
	public final int gridSize = 25;
	public final int width = 800;
	public final int height = 800;
	
	//Grid dots
	private final ArrayList<Point> points = createGrid();
		
	//TempObjects
	private Wall tempWall = null;
	private boolean currentlyBuildingWall = false;
	
	private Region tempRegion = null;
	private boolean currentlyBuildingRegion = false;
	
	//Cursor location
	private Point2D.Double cursorPoint = new Point2D.Double(0, 0);
	
	//Selected objects
	private ArrayList<HouseObject> selected = new ArrayList<HouseObject>();
	
	public Canvas()
	{
		setBackground(new Color(132,136,255));
		
		setPreferredSize(new Dimension(500,800));
		setMinimumSize(this.getPreferredSize());
		
		CanvasMouseAdapter CMA = new CanvasMouseAdapter(this);
		addMouseListener(CMA);
		addMouseMotionListener(CMA);
	}
	
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		//NOTE paint ordering is important for layering
		//Painting one type of object then the next achieves layering	
		
		super.paintComponent(g);
		
		//Paint grid dots
		g2.setColor(Color.WHITE);
		for(Point point : points)
		{
			point.paintComponent(g2);
		}
		
		selected.clear();
						
		//Select objects based on cursor position and paint to screen
		for(HouseObject object : Applet.getController().getSystemModel().getHouseObjectList())
		{
				if(object instanceof Region)
				{
					//If cursor is within the region
					if(((Region)object).getPath().contains(cursorPoint))
					{
						selected.add(object);					
					}	
					else
					{
						//Paint in unselected color
						g2.setColor(object.getUnselectedColor());
						object.paintComponent(g2);
					}
				}
		}
		
		for(HouseObject object : Applet.getController().getSystemModel().getHouseObjectList())
		{
				if(object instanceof Wall)
				{
					//Create bounding box for line selection
					if(((Wall)object).getLine().intersects(new Rectangle2D.Double(cursorPoint.getX() - gridSize/2, cursorPoint.getY() - gridSize/2, gridSize, gridSize)))
					{
						selected.add(object);
					}	
					else
					{
						//Paint in unselected color
						g2.setColor(object.getUnselectedColor());
						object.paintComponent(g2);
					}
				}
		}
		
		for(HouseObject object : Applet.getController().getSystemModel().getHouseObjectList())
		{
				if(object instanceof Light)
				{
					if(((Light)object).getLocation().equals(cursorPoint))
					{	
						selected.add(object);
					}	
					else
					{
						//Paint in unselected color
						g2.setColor(object.getUnselectedColor());
						object.paintComponent(g2);
					}
				}
		}
		
		for(HouseObject object : Applet.getController().getSystemModel().getHouseObjectList())
		{
				if(object instanceof Sensor)
				{
					if(((Sensor)object).getLocation().equals(cursorPoint))
					{	
						selected.add(object);
					}	
					else
					{
						//Paint in unselected color
						g2.setColor(object.getUnselectedColor());
						object.paintComponent(g2);
					}
				}
		}
		
		//Now paint selected items in selected color
		for(HouseObject object : selected)
		{
			g2.setColor(object.getSelectedColor());
			object.paintComponent(g2);
			
			if(object instanceof Region || object instanceof Sensor)
			{
				//Paint label
				g2.setColor(Color.BLACK);
				g2.setFont(new Font("default", Font.BOLD, 16));
				g2.drawString(object.toString(),	(int) cursorPoint.x + 10, (int) cursorPoint.y - 1);
			}
		}
			
			
		//Paint users
		for(User user : Applet.getController().getSystemModel().getUserList())
		{
			g2.setColor(user.getUnselectedColor());
			user.paintComponent(g2);
			
			//Paint label
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("default", Font.BOLD, 16));
			g2.drawString(user.toString(),	(int) user.getLocation().x + 10, (int) user.getLocation().y - 1);
		}
		
				
		//Paint temporary region
		if(tempRegion != null)
		{
			g2.setColor(tempRegion.getUnselectedColor());
			tempRegion.paintComponent(g2);
		}
		
		//Paint temporary wall
		if(tempWall != null)
		{
			g2.setColor(tempWall.getUnselectedColor());
			tempWall.paintComponent(g2);
		}
					
		//Paint cursor
		if(cursorPoint != null)
		{
			//Paint white dot
			g2.setColor(Color.WHITE);
			g2.fill(new Ellipse2D.Double((cursorPoint.x-5)-1, (cursorPoint.y-5)-1, 10, 10));
		}		
	}
	
	public void EraseTempObjects()
	{
		//Delete all temp objects
		tempWall = null;
		currentlyBuildingWall = false;
		
		tempRegion = null;
		currentlyBuildingRegion = false;
		repaint();
	}
	
	private  ArrayList<Point> createGrid()
	{
		ArrayList<Point> result = new ArrayList<Point>();
		
		for (int i = gridSize/2; i < width; i = i + gridSize) 
		{
			for (int j = gridSize/2; j < height; j = j + gridSize)
			{
				result.add(new Point(new Point2D.Double(i,j)));
			}
		}
		
		return result;
	}
	
	public void snapMouseToGrid(MouseEvent e)
	{
		//Quantify mouse position to be on grid
		//Equation split up to show where divide by zero, if any
		int tempx = ((Integer)(e.getX()/gridSize));
		int tempy = ((Integer)(e.getY()/gridSize));

		//Snap point to grid
		int finalx = tempx*gridSize + gridSize/2;
		int finaly = tempy*gridSize + gridSize/2;

		//Limit cursor to canvas size
		if(finalx >= width)
		{
			finalx = ((Integer)(width/gridSize))*gridSize - gridSize/2;
		}
		else if(finalx <= 0)
		{
			finalx = gridSize/2;
		}

		if(finaly >= height)
		{
			finaly = ((Integer)(height/gridSize))*gridSize - gridSize/2;
		}
		else if(finaly <= 0)
		{
			finaly = gridSize/2;;
		}

		cursorPoint.setLocation(finalx, finaly);
		repaint();
	}
	
	
	//MUTATORS AND ACCESSORS***************************************************

	public boolean isCurrentlyBuildingRegion() {
		return currentlyBuildingRegion;
	}

	public void setCurrentlyBuildingRegion(boolean currentlyBuildingRegion) {
		this.currentlyBuildingRegion = currentlyBuildingRegion;
	}
	
	public boolean isCurrentlyBuildingWall() {
		return currentlyBuildingWall;
	}

	public void setCurrentlyBuildingWall(boolean currentlyBuildingWall) {
		this.currentlyBuildingWall = currentlyBuildingWall;
	}

	public ArrayList<HouseObject> getSelected() {
		return selected;
	}
	
	public Point2D.Double getCursorPoint() {
		return cursorPoint;
	}
	
	public Wall getTempWall() {
		return tempWall;
	}
	
	public void setTempWall(Wall wall) {
		tempWall = wall;
	}
	
	
	public Region getTempRegion() {
		return tempRegion;
	}
	
	public  void setTempRegion(Region region) {
		tempRegion = region;
	}

	
	
}
