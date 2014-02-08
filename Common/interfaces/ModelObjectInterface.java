package interfaces;

import java.awt.Graphics;

import model.HouseObject;

public interface ModelObjectInterface 
{
	public HouseObject clone();
	
	public boolean equals(HouseObject object);
	
	//Parameters follow "parameter1=value1", "parameter2=value2",...
	public boolean edit(String [] parameters, Object [] values) throws Exception;
	
	public void paintComponent(Graphics g);
}
