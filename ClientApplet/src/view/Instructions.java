package view;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Instructions extends JPanel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Panel displays a brief guide to users on how to use the interface
	 */
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
				+ "Static Nodes:&#09Click to place a static node. <br>"
				+ "Erase:&#09Click to erase highlighted objects."
				+ "Users:&#09Opens a dialog box to edit users."
				+ "Edit:&#09You can right click on regions and static nodes to edit them.</nobr></html>");
		
	
		add(instructions);
	}
}
