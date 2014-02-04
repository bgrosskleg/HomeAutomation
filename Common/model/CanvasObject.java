package model;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

public abstract class CanvasObject  implements Serializable
{
	private static final long serialVersionUID = 1;
	
	/**
	 * color will be different depending on what the object is
	 * sensor = pink
	 * wall = black
	 * selected = red
	 * light = yellow
	 * region = random
	 */
	protected Color unselectedColor;
	protected Color selectedColor;
	
	public CanvasObject(Color unselected, Color selected)
	{			
		unselectedColor = unselected;
		selectedColor = selected;
	}
	
	public Color getUnselectedColor()
	{
		return unselectedColor;
	}
	
	public Color getSelectedColor()
	{
		return selectedColor;
	}
	
	public abstract boolean equals(CanvasObject object);
	public abstract void paintComponent(Graphics g);
}
