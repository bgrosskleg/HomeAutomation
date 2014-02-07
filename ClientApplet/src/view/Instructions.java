package view;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Instructions extends JPanel
{
	private static final long serialVersionUID = 1L;

	
	public Instructions()
	{
		super();
		
		setPreferredSize(new Dimension(800,150));
		
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		JLabel instructions = new JLabel("<html><nobr>"
				+ "Instructions <br>"
				+ "Walls:&#09Click to define ends of walls. <br>"
				+ "Regions:&#09Click to define vertices of a region.  Return to initial vertex to complete region. <br>"
				+ "Lights:&#09Click to place a light. <br>"
				+ "Sensors:&#09Click to place a sensor. <br>"
				+ "Erase:&#09Click to erase highlighted objects.</nobr></html>");
		
	
		add(instructions);
	}
}
