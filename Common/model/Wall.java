package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Wall extends CanvasObject
{
	//Static variables will be same for all wall objects
	private static final long serialVersionUID = 1;
	private static int strokeWidth = 4;
	//private static Image texture = MainController.getApplication().getImage(MainController.getCodebase(), "resources/chalkTexture.png");
	
	//Member variables will be unique for each object
	public Point2D.Double startingPoint;
	public Point2D.Double endingPoint;
	public Line2D.Double line;
	
	
	/**
	 * Creates a wall with a start and an end point
	 * @param start
	 * @param end
	 */
	public Wall(Point2D.Double start, Point2D.Double end)
	{
		//Super(unselectedColor, selectedColor)
		super(Color.BLACK, Color.RED);	
		startingPoint = start;
		endingPoint = end;
		line = new Line2D.Double(startingPoint, endingPoint);	
	}
	

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
        g2.setColor(currentColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.draw(new Line2D.Double(startingPoint, endingPoint));
	}
}
