package view;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;


public class CanvasToolbar extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JToggleButton buttons[];
		
	public CanvasToolbar()
	{
		//Create buttons
		buttons = new JToggleButton[5];
		
		buttons[0] = new JToggleButton("Walls", ClientApplet.getController().loadImageIconFromHost("resources/wallsIcon.png"), true);
		ClientApplet.getController().setCurrentTool("Walls");
		buttons[1] = new JToggleButton("Regions", ClientApplet.getController().loadImageIconFromHost("resources/regionsIcon.png"), false);
		buttons[2] = new JToggleButton("Lights", ClientApplet.getController().loadImageIconFromHost("resources/lightbulbIcon.png"), false);
		buttons[3] = new JToggleButton("Sensors", ClientApplet.getController().loadImageIconFromHost("resources/sensorIcon.png"), false);
		buttons[4] = new JToggleButton("Erase", ClientApplet.getController().loadImageIconFromHost("resources/eraserIcon.png"), false);
		
		//Create button group (so that only one may be selected at once), and assign action
		ButtonGroup group = new ButtonGroup();
		for(AbstractButton button : buttons)
		{
			group.add(button);
			button.addItemListener(new ItemListener()
			{
				@Override
				public void itemStateChanged(ItemEvent e) 
				{
					AbstractButton source = (AbstractButton) e.getItemSelectable();
					ClientApplet.getController().setCurrentTool(source.getText());	
				}
			});
		}
		
		
		//Create look and feel, layout
		this.setPreferredSize(new Dimension(200,800));
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		
		
		this.add(buttons[0], gbc);
		gbc.gridy++;
		this.add(buttons[1], gbc);
		gbc.gridy++;
		this.add(buttons[2], gbc);
		gbc.gridy++;
		this.add(buttons[3], gbc);
		gbc.gridy++;
		this.add(buttons[4], gbc);		
	}
}