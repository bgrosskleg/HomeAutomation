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
	
	@Override
	public void dispose()
	{
		super.dispose();
		ClientApplet.getController().removeModelSubscriber(REP);
	}
	
	private class RegionEditorPane extends JPanel implements ModelSubscriber
	{
		private static final long serialVersionUID = 1L;
		
		private Region region;
		
		private JLabel usersLabel;
		private String users;
		private String staticNodes;

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
			add(new JLabel("Paired Sensors: "), gbc);
			gbc.gridx = 1;
			staticNodes = "N/A";
			if(!region.getStaticNodes().isEmpty())
			{
				staticNodes = "<html>";
				for(StaticNode sensor : region.getStaticNodes())
				{
					staticNodes += (sensor.getMACAddress() + "<br>");
				}
				staticNodes += "</html>";
			}
			add(new JLabel(staticNodes), gbc);

			//Add list of current users occupying region
			gbc.gridy = 3;
			gbc.gridx = 0;
			add(new JLabel("Occupied by: "), gbc);
			gbc.gridx = 1;
			users = "N/A";
			if(!region.getUsers().isEmpty())
			{
				users = "<html>";
				for(User user : region.getUsers())
				{
					users += (user.getName() + "<br>");
				}
				users += "</html>";
			}
			usersLabel = new JLabel(users);
			add(usersLabel, gbc);


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
	

		@Override
		public void modelChanged() 
		{			
			//System.err.println("GETS HERE!");

			users = "N/A";
			if(!region.getUsers().isEmpty())
			{
				users = "<html>";
				for(User user : region.getUsers())
				{
					users += (user.getName() + "<br>");
				}
				users += "</html>";
			}
			usersLabel.setText(users);

			System.err.print(users);
		}
	}
	
	
}
