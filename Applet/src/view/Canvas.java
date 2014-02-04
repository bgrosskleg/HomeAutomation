package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
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
	private Point2D.Double cursor = new Point2D.Double(0, 0);
	
	//Selected objects
	private ArrayList<CanvasObject> selected = new ArrayList<CanvasObject>();;
	
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
		return cursor;
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
		for(Point point : points)
		{
			point.paintComponent(g);
		}
			
		
		if(Applet.getController() == null)
		{
			System.out.println("Applet Controller is null");
		}
		
		if(Applet.getController().getCM() == null)
		{
			System.out.println("Applet Controller's current model is null");
		}
		
		
		if(Applet.getController().getCM() != null)
		{
			for(Region region : Applet.getController().getCM().getRegions())
			{
				region.paintComponent(g);
			}
			
			for(Wall wall : Applet.getController().getCM().getWalls())
			{
				wall.paintComponent(g);
			}
			
			for(Light light : Applet.getController().getCM().getLights())
			{
				light.paintComponent(g);
			}
			
			for(Sensor sensor : Applet.getController().getCM().getSensors())
			{
				sensor.paintComponent(g);
			}
			
			for(User user : Applet.getController().getCM().getUsers())
			{
				user.paintComponent(g);
			}
		}
				
		//Paint temporary region
		if(tempRegion != null)
		{
			tempRegion.paintComponent(g);
		}
		
		//Paint temporary wall
		if(tempWall != null)
		{
			tempWall.paintComponent(g);
		}
					
		//Paint cursor
		if(cursor != null)
		{
			//Paint white dot
			g2.setColor(Color.WHITE);
			g2.fill(new Ellipse2D.Double((cursor.x-5)-1, (cursor.y-5)-1, 10, 10));

			//Paint object label
			if(Applet.getController().getCM() != null)
			{
				for(Sensor sensor : Applet.getController().getCM().getSensors())
				{
					if(sensor.location.equals(cursor))
					{
						g2.setColor(Color.BLACK);
						g2.setFont(new Font("default", Font.BOLD, 16));
						g2.drawString(sensor.toString(),	(int) cursor.x + 10, (int) cursor.y - 1);
					}
				}	
				for(Region region : Applet.getController().getCM().getRegions())
				{
					if(region.getRegion().contains(cursor))
					{
						g2.setColor(Color.BLACK);
						g2.setFont(new Font("default", Font.BOLD, 16));
						g2.drawString(region.toString(),	(int) cursor.x + 10, (int) cursor.y + 17);
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

		cursor.setLocation(finalx, finaly);
		repaint();
	}
	
}
