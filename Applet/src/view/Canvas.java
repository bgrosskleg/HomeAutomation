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
import model.CanvasObject;
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
	private ArrayList<CanvasObject> selected = new ArrayList<CanvasObject>();
	
	public Canvas()
	{
		setBackground(new Color(132,136,255));
		
		setPreferredSize(new Dimension(500,800));
		setMinimumSize(this.getPreferredSize());
		
		CanvasMouseAdapter CMA = new CanvasMouseAdapter(this);
		addMouseListener(CMA);
		addMouseMotionListener(CMA);
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
	
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		//NOTE paint ordering is important for layering
		
		super.paintComponent(g);
		
		//Painting one type of object then the next achieves layering	
		g2.setColor(Color.WHITE);
		for(Point point : points)
		{
			point.paintComponent(g2);
		}
		
		selected.clear();
		
		if(Applet.getController().getCM() != null)
		{
			//Paint regions
			for(Region region : Applet.getController().getCM().getRegions())
			{
				if(region.getRegion().contains(cursorPoint))
				{
					//Paint in selected color
					g2.setColor(region.getSelectedColor());
					selected.add(region);
				}	
				else
				{
					//Paint in unselected color
					g2.setColor(region.getUnselectedColor());
				}
				region.paintComponent(g);
			}
			
			//Paint walls
			for(Wall wall : Applet.getController().getCM().getWalls())
			{
				if(wall.getLine().intersects(new Rectangle2D.Double(cursorPoint.getX() - gridSize/2, cursorPoint.getY() - gridSize/2, gridSize, gridSize)))
				{
					//Paint in selected color
					g2.setColor(wall.getSelectedColor());
					selected.add(wall);
				}	
				else
				{
					//Paint in unselected color
					g2.setColor(wall.getUnselectedColor());
				}
				wall.paintComponent(g);
			}
			
			//Paint lights
			for(Light light : Applet.getController().getCM().getLights())
			{
				if(light.location.equals(cursorPoint))
				{	
					//Paint in selected color
					g2.setColor(light.getSelectedColor());
					selected.add(light);
				}	
				else
				{
					//Paint in unselected color
					g2.setColor(light.getUnselectedColor());
				}
				light.paintComponent(g);
			}
			
			//Paint sensors
			for(Sensor sensor : Applet.getController().getCM().getSensors())
			{
				if(sensor.getLocation().equals(cursorPoint))
				{	
					//Paint in selected color
					g2.setColor(sensor.getSelectedColor());
					selected.add(sensor);
				}	
				else
				{
					//Paint in unselected color
					g2.setColor(sensor.getUnselectedColor());
				}
				sensor.paintComponent(g);
			}
			
			//Paint users
			for(User user : Applet.getController().getCM().getUsers())
			{
				user.paintComponent(g);
			}
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

			//Paint object label
			if(Applet.getController().getCM() != null)
			{
				for(Sensor sensor : Applet.getController().getCM().getSensors())
				{
					if(sensor.getLocation().equals(cursorPoint))
					{
						g2.setColor(Color.BLACK);
						g2.setFont(new Font("default", Font.BOLD, 16));
						g2.drawString(sensor.toString(),	(int) cursorPoint.x + 10, (int) cursorPoint.y - 1);
					}
				}	
				for(Region region : Applet.getController().getCM().getRegions())
				{
					if(region.getRegion().contains(cursorPoint))
					{
						g2.setColor(Color.BLACK);
						g2.setFont(new Font("default", Font.BOLD, 16));
						g2.drawString(region.toString(),	(int) cursorPoint.x + 10, (int) cursorPoint.y + 17);
					}
				}
			}
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

	public ArrayList<CanvasObject> getSelected() {
		return selected;
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
	
}
