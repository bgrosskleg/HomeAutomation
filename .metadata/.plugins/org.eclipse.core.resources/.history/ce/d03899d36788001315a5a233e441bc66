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
	protected Color currentColor;
	
	public CanvasObject(Color unselected, Color selected)
	{			
		unselectedColor = unselected;
		selectedColor = selected;
		
		currentColor = unselectedColor;
	}
	
	public void Select()
	{
		currentColor = selectedColor;
	}
	
	public void unSelect()
	{
		currentColor = unselectedColor;
	}
	
	public abstract void paintComponent(Graphics g);
}
