package model;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

public abstract class HouseObject implements Serializable
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
	
	public HouseObject(Color unselected, Color selected)
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
	
	public abstract boolean equals(HouseObject object);
	
	public abstract HouseObject clone();
	
	public abstract void paintComponent(Graphics g);
}
