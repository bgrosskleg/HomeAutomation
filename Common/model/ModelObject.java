package model;

import interfaces.ModelObjectInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public abstract class ModelObject implements ModelObjectInterface
{
	private static final long serialVersionUID = 1;
	
	protected Color unselectedColor;
	protected Color selectedColor;
	
	public ModelObject(Color unselected, Color selected)
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
	
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract String toString();
	
	public abstract String [] getParameters();
	
	public abstract Object [] getValues();
	
	public abstract boolean edit(String [] parameters, Object [] values) throws Exception;
	
	public abstract void paintComponent(Graphics g);	
	
	
	public static Color randomColor()
	{
		Random rand = new Random();
		
		/*
		//Make a random pastel color
		final float hue = rand.nextFloat();
		// Saturation between 0.1 and 0.3
		final float saturation = (rand.nextInt(2000) + 1000) / 10000f;
		final float luminance = 0.9f;
		final Color pastelColor = Color.getHSBColor(hue, saturation, luminance);
		
		//Create transparent color, red, green, blue, alpha, 0-255
		int alpha = 100;
		Color color = new Color(pastelColor.getRed(), pastelColor.getGreen(), pastelColor.getBlue(), alpha);
		*/
		
		//Makes lighter colors (125 + random number 0-125)
		final int r = rand.nextInt(126)+125;
		final int g = rand.nextInt(126)+125;
		final int b = rand.nextInt(126)+125;
		
		//Alpha 0-255 (transparent -> solid)
		final int alpha = 200;
		
		final Color color = new Color(r,g,b,alpha);
		
		return color;
	}
}
