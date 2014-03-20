package view;

import interfaces.ModelSubscriber;

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
import model.ModelObject;
import model.Light;
import model.Region;
import model.StaticNode;
import model.User;
import model.Wall;

public class Canvas extends JPanel implements ModelSubscriber
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Grid parameters
	 */
	public final int gridSize = 25;
	public final int width = 800;
	public final int height = 800;
	
	/**
	 * Stores the grid dots of the canvas
	 */
	private final ArrayList<Point> points = createGrid();
		
	/**
	 * Temporary objects and logic used for object previewing while drawing
	 */
	private Wall tempWall = null;
	private boolean currentlyBuildingWall = false;
	
	private Region tempRegion = null;
	private boolean currentlyBuildingRegion = false;
	
	/**
	 * Cursor location
	 */
	private Point2D.Double cursorPoint = new Point2D.Double(0, 0);
	
	/**
	 * List of selected objects
	 */
	private ModelObject selected;
	
	/**
	 * Creates canvas that displays the current model
	 */
	public Canvas()
	{
		setBackground(new Color(132,136,255));
		
		setPreferredSize(new Dimension(500,800));
		setMinimumSize(this.getPreferredSize());
		
		CanvasMouseAdapter CMA = new CanvasMouseAdapter(this);
		addMouseListener(CMA);
		addMouseMotionListener(CMA);
	}
	
	/**
	 * Paint command is responsible for displaying the contents of the model on screen
	 */
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
		
		selected = null;
						
		//Select objects based on cursor position and paint to screen
		
		//Paint unselected regions first
		for(ModelObject object : ClientApplet.getController().getModelObjects())
		{
				if(object instanceof Region)
				{
					//If cursor is within the region
					if(((Region)object).getPath().contains(cursorPoint))
					{
						selected = object;				
					}	
					else
					{
						//Paint in unselected color
						g2.setColor(object.getUnselectedColor());
						object.paintComponent(g2);
					}
				}
		}
		
		//Paint unselected walls on top
		for(ModelObject object : ClientApplet.getController().getModelObjects())
		{
				if(object instanceof Wall)
				{
					//Create bounding box for line selection
					if(((Wall)object).getLine().intersects(new Rectangle2D.Double(cursorPoint.getX() - gridSize/2, cursorPoint.getY() - gridSize/2, gridSize, gridSize)))
					{
						selected = object;
					}	
					else
					{
						//Paint in unselected color
						g2.setColor(object.getUnselectedColor());
						object.paintComponent(g2);
					}
				}
		}
		
		//Paint unselected lights next
		for(ModelObject object : ClientApplet.getController().getModelObjects())
		{
				if(object instanceof Light)
				{
					if(((Light)object).getLocation().equals(cursorPoint))
					{	
						selected = object;
					}	
					else
					{
						//Paint in unselected color
						g2.setColor(object.getUnselectedColor());
						object.paintComponent(g2);
					}
				}
		}
		
		//Paint unselected staticNodes next
		for(ModelObject object : ClientApplet.getController().getModelObjects())
		{
				if(object instanceof StaticNode)
				{
					if(((StaticNode)object).getLocation().equals(cursorPoint))
					{	
						selected = object;
					}	
					else
					{
						//Paint in unselected color
						g2.setColor(object.getUnselectedColor());
						object.paintComponent(g2);
					}
				}
		}
		
		//Now paint selected regions or static nodes items in selected color
		if(selected != null)
		{
			g2.setColor(selected.getSelectedColor());
			selected.paintComponent(g2);
			
			if(selected instanceof Region || selected instanceof StaticNode)
			{
				//Paint label
				g2.setColor(Color.BLACK);
				g2.setFont(new Font("default", Font.BOLD, 16));
				
				{g2.drawString(selected.toString(),	(int) cursorPoint.x + 10, (int) cursorPoint.y - 1);}
			}
		}
			
			
		//Paint users on top
		for(ModelObject object : ClientApplet.getController().getModelObjects())
		{
			if(object instanceof User)
			{
				User temp = (User) object;
				
				//Paint in unselected color
				g2.setColor(temp.getUnselectedColor());
				temp.paintComponent(g2);
	
				//Paint label
				g2.setColor(Color.BLACK);
				g2.setFont(new Font("default", Font.BOLD, 16));
				g2.drawString(temp.getName(),	(int)temp.getLocation().x + 10, (int)temp.getLocation().y + 5);
			}
		}
		
				
		//Paint temporary region on top
		if(tempRegion != null)
		{
			g2.setColor(tempRegion.getUnselectedColor());
			tempRegion.paintComponent(g2);
		}
		
		//Paint temporary wall on top
		if(tempWall != null)
		{
			g2.setColor(tempWall.getUnselectedColor());
			tempWall.paintComponent(g2);
		}
					
		//Paint cursor last (so it's always on top and visible)
		if(cursorPoint != null)
		{
			//Paint white dot
			g2.setColor(Color.WHITE);
			g2.fill(new Ellipse2D.Double((cursorPoint.x-5)-1, (cursorPoint.y-5)-1, 10, 10));
		}		
	}
	
	/**
	 * Clears any temporary objecs used for previewing
	 */
	public void EraseTempObjects()
	{
		//Delete all temp objects
		tempWall = null;
		currentlyBuildingWall = false;
		
		tempRegion = null;
		currentlyBuildingRegion = false;
		repaint();
	}
	
	/**
	 * Creates the arrayList of grid dots
	 * @return arrayList containing the grid dots
	 */
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
	
	/**
	 * This function takes a mouse event in and based on the grid parameters,
	 * snaps the cursor to the nearest grid dot.  This function uses variables so
	 * grid parameters can be changes simply
	 * @param e	MouseClick or MouseMove Event that causes the on canvas cursor to update position
	 */
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

	public ModelObject getSelected() {
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

	/**
	 * Private class to store grid point information
	 * @author Brian Grosskleg
	 */
	private class Point
	{
		//Static variables will be same for all wall objects
		//Size must be even
		private static final int size = 4;
			
		//Member variables will be unique for each object
		private Color color;
		private Point2D.Double location;	
		
		//CONSTRUCTOR*********************************************************************
		/**
		 * Create a point on the Grid with an x and y value
		 * @param x the x axis value
		 * @param y the y axis value
		 */
		public Point(Point2D.Double p)
		{
			color = Color.WHITE;
			location = p;
		}
		
		
		//INTERFACE METHODS***************************************************************
		/**
		 * Paint a white dot at location of point
		 * @param g Graphics object from canvas
		 */
		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(color);
	        Ellipse2D.Double point = new Ellipse2D.Double((location.x-size/2)-1, (location.y-size/2)-1, size , size);
	        g2.fill(point);
		}
	}

	/**
	 * Called every time model changes
	 * In this case, redraws the model components on screen
	 */
	@Override
	public void modelChanged() 
	{
		repaint();
	}
	
	
}
