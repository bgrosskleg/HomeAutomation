package interfaces;

import java.awt.Graphics;

import model.HouseObject;

public interface HouseObjectInterface 
{
	public HouseObject clone();
	
	public boolean equals(HouseObject object);
	
	public boolean edit(String... args);
	
	public void paintComponent(Graphics g);
}
