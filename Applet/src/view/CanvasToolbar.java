package view;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import controller.ClientController;


public class CanvasToolbar extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JToggleButton buttons[];
	private JButton saveButton;
		
	public CanvasToolbar()
	{
		//Create buttons
		buttons = new JToggleButton[5];
		
		buttons[0] = new JToggleButton("Walls", new ImageIcon(ClientController.getApplication().getImage(ClientController.getCodebase(), "resources/wallsIcon.png")), true);
		ClientController.setCurrentTool("Walls");
		buttons[1] = new JToggleButton("Regions", new ImageIcon(ClientController.getApplication().getImage(ClientController.getCodebase(), "resources/regionsIcon.png")), false);
		buttons[2] = new JToggleButton("Lights", new ImageIcon(ClientController.getApplication().getImage(ClientController.getCodebase(), "resources/lightbulbIcon.png")), false);
		buttons[3] = new JToggleButton("Sensors", new ImageIcon(ClientController.getApplication().getImage(ClientController.getCodebase(), "resources/sensorIcon.png")), false);
		buttons[4] = new JToggleButton("Erase", new ImageIcon(ClientController.getApplication().getImage(ClientController.getCodebase(), "resources/eraserIcon.png")), false);
		saveButton = new JButton("Save", new ImageIcon(ClientController.getApplication().getImage(ClientController.getCodebase(), "resources/saveIcon.png")));		
		
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
					ClientController.setCurrentTool(source.getText());	
				}
			});
		}
		
		saveButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//Submit model to Pi
				try {
					System.out.println("Sending model to server...");
					ClientController.getOutToServer().println("SENDINGMODEL");
					if(ClientController.getInFromServer().readLine().equals("OKAY"))
					{
						//ClientController.getOOS().reset();
						ClientController.getOOS().writeObject(ClientController.getCM());
						//ClientController.getOOS().flush();
						if(ClientController.getInFromServer().readLine().equals("OKAY"))
						{
							System.out.println("Model save complete.");
						}
						else
						{
							System.out.println("Model did not save correctly.");
						}
					}
				} catch (Exception e1) {
					System.out.println("Failure sending model to server");
					e1.printStackTrace();
				}
			}
		});
		
		
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
		//gbc.gridy++;
		//this.add(saveButton, gbc);		
	}
}