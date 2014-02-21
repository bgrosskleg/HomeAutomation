package view;

import interfaces.ModelSubscriber;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import model.Region;
import model.StaticNode;
import model.User;

public class RegionEditor extends JFrame
{
	private static final long serialVersionUID = 1L;

	RegionEditorPane REP;
	
	/**
	 * Window displays information on the region
	 * @param region The region to be displayed in the editor
	 */
	public RegionEditor(Region region)
	{ 
		super("Region Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		REP = new RegionEditorPane(this, region);
		ClientApplet.getController().addModelSubscriber(REP);
		add(REP);
		
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * Overriding the stock dispose function to remove memory leak where
	 * this frame is added as a modelSubscriber but never removed.
	 */
	@Override
	public void dispose()
	{
		super.dispose();
		ClientApplet.getController().removeModelSubscriber(REP);
	}
	
	/**
	 * Panel that displays the contents of the region
	 * @author Brian Grosskleg
	 */
	private class RegionEditorPane extends JPanel implements ModelSubscriber
	{
		private static final long serialVersionUID = 1L;
		
		private Region region;
		
		private JLabel staticNodeLabel;
		
		private JLabel usersLabel;
		
		private JLabel lightingValueLabel;

		/**
		 * Creates the panel that displays the information
		 * @param frame		the parent frame, passed so an ActionListener can close it
		 * @param region	the region to be editted
		 */
		private RegionEditorPane(final JFrame frame, final Region region)
		{
			super();
			this.region = region;
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			//Create JDialog to edit region...

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.LINE_START;
			this.add(new JLabel("Edit Region"), gbc);

			gbc.gridy = 1;
			add(new JLabel("Name: "), gbc);
			final JTextField name = new JTextField(region.getName(), 15);
			gbc.gridx = 1;
			this.add(name, gbc);

			
			//Add list of paired sensors
			gbc.gridy = 2;
			gbc.gridx = 0;
			add(new JLabel("Paired Static Nodes: "), gbc);
			gbc.gridx = 1;
			String staticNodes = "N/A";
			if(!region.getStaticNodes().isEmpty())
			{
				staticNodes = "<html>";
				for(StaticNode sensor : region.getStaticNodes())
				{
					staticNodes += (sensor.getMACAddress() + "<br>");
				}
				staticNodes += "</html>";
			}
			staticNodeLabel = new JLabel(staticNodes);
			add(staticNodeLabel, gbc);

			
			//Add list of current users occupying region
			gbc.gridy = 3;
			gbc.gridx = 0;
			add(new JLabel("Occupied by: "), gbc);
			gbc.gridx = 1;
			String users = "N/A";
			if(!region.getUsers().isEmpty())
			{
				users = "<html>";
				for(User user : region.getUsers())
				{
					users += (user.getName()  + " (" + user.getPreferredLightingValue() + "%)" + "<br>");
				}
				users += "</html>";
			}
			usersLabel = new JLabel(users);
			add(usersLabel, gbc);
			
			
			//Add lightingValue indication
			gbc.gridy = 4;
			gbc.gridx = 0;
			add(new JLabel("Lighting Value: "), gbc);
			lightingValueLabel = new JLabel(String.valueOf(region.getLightingValue()) + "%");
			gbc.gridx = 1;
			add(lightingValueLabel, gbc);

			
			//Add buttons
			JButton OKButton = new JButton("OK");
			OKButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e) 
				{
					//Submit values to model and close dialog box

					//Testing changing parameters
					String [] parameters = new String[]{"name"};
					Object [] values = new Object[]{name.getText()};

					ClientApplet.getController().modifyObject(region, parameters, values);	

					frame.dispose();
				}

			});
			gbc.gridy = 4;
			gbc.gridx = 3;
			this.add(OKButton, gbc);

			JButton CancelButton = new JButton("CANCEL");
			CancelButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					//Close dialog box without saving changes
					frame.dispose();
				}

			});
			gbc.gridx = 5;
			this.add(CancelButton, gbc);
		}
	

		/**
		 * This function is called everytime the model is changed
		 * Here it will update the labels with occupancy and lightingValue information
		 */
		@Override
		public void modelChanged() 
		{			
			//Update staticNode label
			String staticNodes = "N/A";
			if(!region.getStaticNodes().isEmpty())
			{
				staticNodes = "<html>";
				for(StaticNode sensor : region.getStaticNodes())
				{
					staticNodes += (sensor.getMACAddress() + "<br>");
				}
				staticNodes += "</html>";
			}
			staticNodeLabel.setText(staticNodes);
			
			//Update users label
			String users = "N/A";
			if(!region.getUsers().isEmpty())
			{
				users = "<html>";
				for(User user : region.getUsers())
				{
					users += (user.getName() + " (" + user.getPreferredLightingValue() + "%)" + "<br>");
				}
				users += "</html>";
			}
			usersLabel.setText(users);
			
			//Update lightingValue label
			lightingValueLabel.setText(String.valueOf(region.getLightingValue()) + "%");
		}
	}
	
	
}
