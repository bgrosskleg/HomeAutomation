package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import subscribers.CurrentModelSubscriber;
import subscribers.TempObjectSubscriber;
import controller.CanvasMouseAdapter;
import controller.ClientController;
import model.Light;
import model.Point;
import model.Region;
import model.Sensor;
import model.User;
import model.Wall;

public class Canvas extends JPanel implements CurrentModelSubscriber, TempObjectSubscriber
{
	private static final long serialVersionUID = 1L;
	private static Point2D.Double cursor;
	
	private CanvasMouseAdapter CMA;

	public Canvas()
	{
		setBackground(Color.GRAY);
		
		setPreferredSize(new Dimension(500,800));
		setMinimumSize(this.getPreferredSize());
		
		CMA = new CanvasMouseAdapter();
		addMouseListener(CMA);
		addMouseMotionListener(CMA);
		
		cursor = new Point2D.Double(0, 0);
	}

	@Override
	public void currentModelChanged() 
	{
		repaint();
	}
	
	@Override
	public void tempObjectChanged() 
	{
		repaint();		
	}

	public static Point2D.Double getCursorPoint() {
		return cursor;
	}

	public static void setCursorPoint(Point2D.Double cursor) {
		Canvas.cursor = cursor;
	}	
	
	
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		//NOTE paint ordering is important for layering
		
		//test
		this.setBackground(new Color(132,136,255));
		super.paintComponent(g);
		
		
		//Paint from model, only painting one type of object then the next achieves layering
		
		for(Point point : ClientController.getCM().points)
		{
			point.paintComponent(g);
		}
		
		for(Region region : ClientController.getCM().regions)
		{
			region.paintComponent(g);
		}
		
		for(Wall wall : ClientController.getCM().walls)
		{
			wall.paintComponent(g);
		}
		
		for(Light light : ClientController.getCM().lights)
		{
			light.paintComponent(g);
		}
		
		for(Sensor sensor : ClientController.getCM().sensors)
		{
			sensor.paintComponent(g);
		}
		
		for(User user : ClientController.getCM().users)
		{
			user.paintComponent(g);
		}
				
		
		//Paint temporary wall
		if(CMA.getTempWall() != null)
		{
			CMA.getTempWall().paintComponent(g);
		}

		//Paint temporary region
		if(CMA.getTempRegion() != null)
		{
			CMA.getTempRegion().paintComponent(g);
		}
				
		//Paint cursor
		if(cursor != null)
		{
			//Paint white dot
			g2.setColor(Color.WHITE);
			g2.fill(new Ellipse2D.Double((cursor.x-5)-1, (cursor.y-5)-1, 10, 10));
			
			//Paint object label
			for(Sensor sensor : ClientController.getCM().sensors)
			{
				if(sensor.location.equals(Canvas.getCursorPoint()))
				{
					g2.setColor(Color.BLACK);
					g2.setFont(new Font("default", Font.BOLD, 16));
					g2.drawString(sensor.toString(),	(int) Canvas.getCursorPoint().x + 10, (int) Canvas.getCursorPoint().y - 1);
				}
			}	
			for(Region region : ClientController.getCM().regions)
			{
				if(region.region.contains(cursor))
				{
					g2.setColor(Color.BLACK);
					g2.setFont(new Font("default", Font.BOLD, 16));
					g2.drawString(region.toString(),	(int) Canvas.getCursorPoint().x + 10, (int) Canvas.getCursorPoint().y + 17);
				}
			}
		}
	}
}
