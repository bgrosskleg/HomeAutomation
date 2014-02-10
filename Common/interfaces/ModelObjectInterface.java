package interfaces;

import java.awt.Graphics;
import java.io.Serializable;

public interface ModelObjectInterface extends Serializable, Cloneable
{
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract String toString();
	
	public abstract String [] getParameters();
	
	public abstract Object [] getValues();
	
	public abstract boolean edit(String [] parameters, Object [] values) throws Exception;
	
	public abstract void paintComponent(Graphics g);	
}
