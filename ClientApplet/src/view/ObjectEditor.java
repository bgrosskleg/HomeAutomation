package view;

import interfaces.ModelSubscriber;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.ModelObject;
import model.Region;
import model.StaticNode;
import model.User;

public class ObjectEditor extends JFrame
{
	private static final long serialVersionUID = 1L;

	ObjectEditorPane OEP;
	
	public ObjectEditor(ModelObject object)
	{ 
		super("Object Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		OEP = new ObjectEditorPane(this, object);
		ClientApplet.getController().addModelSubscriber(OEP);
		add(OEP);
		
		this.pack();
		this.setVisible(true);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		ClientApplet.getController().removeModelSubscriber(OEP);
	}
	
	private class ObjectEditorPane extends JPanel implements ModelSubscriber
	{
		private static final long serialVersionUID = 1L;
		
		private ModelObject object;
		
		private JLabel usersLabel;
		private String users;
		private String staticNodes;

		private ObjectEditorPane(final JFrame frame, ModelObject object)
		{
			super();
			this.object = object;
			
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			if(object instanceof StaticNode)
			{
				final StaticNode staticNode = (StaticNode) object;
				
				//Create JDialog to edit sensor...
				
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.anchor = GridBagConstraints.LINE_START;
				this.add(new JLabel("Edit Static Node"), gbc);
			
				gbc.gridy = 1;
				add(new JLabel("MAC Address: "), gbc);
				final JTextField MACAddress = new JTextField(staticNode.getMACAddress(), 15);
				gbc.gridx = 1;
				this.add(MACAddress, gbc);
				
				gbc.gridy = 2;
				gbc.gridx = 0;
				add(new JLabel("Paired Region: "), gbc);
				
				gbc.gridx = 1;
				DefaultComboBoxModel<String> regionListModel = new DefaultComboBoxModel<String>();
				final String defaultOption = "None - Must be paired to a region later";
				regionListModel.addElement(defaultOption);
				for(ModelObject object2 : ClientApplet.getController().getModelObjects())
				{
					if(object2 instanceof Region)
					{
						Region region = (Region) object2;
						regionListModel.addElement(region.getName());
					}
				}
				
				final JComboBox<String> regionList = new JComboBox<String>(regionListModel);
				if(staticNode.getPairedRegion() != null)
				{
					regionList.setSelectedItem(staticNode.getPairedRegion().getName());
				}

				this.add(regionList , gbc);
				
				
				gbc.gridy = 3;
				gbc.gridx = 0;
				this.add(new JLabel("Lighting Value: "), gbc);
				gbc.gridx = 1;
				this.add(new JLabel(String.valueOf(staticNode.getLightingValue())), gbc);
				
				
				JButton OKButton = new JButton("OK");
				OKButton.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) 
					{
						//Submit values to model and close dialog box
						
						if(staticNode.getPairedRegion() != null)
						{
							staticNode.getPairedRegion().removeStaticNode(staticNode);
						}
						
						Region newPairedRegion = null;
						if(!regionList.getSelectedItem().equals(defaultOption))
						{
							for(ModelObject object : ClientApplet.getController().getModelObjects())
							{
								if(object instanceof Region && regionList.getSelectedItem().equals(((Region)object).getName()))
								{
									if(!((Region)object).getStaticNodes().contains(staticNode))
									{((Region) object).addStaticNode(staticNode);}
									
									newPairedRegion = (Region) object;
								}
							}
						}
						
						//Testing changing parameters
						String [] parameters = new String[]{"MACAddress", "pairedRegion"};
						Object [] values = new Object[]{MACAddress.getText(), newPairedRegion};
						
						ClientApplet.getController().modifyObject(staticNode, parameters, values);	
						
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
			else if(object instanceof Region)
			{
				final Region region = (Region) object;
				
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

		}
	

		@Override
		public void modelChanged() 
		{
			//System.err.println("GETS HERE!");
			if(object instanceof StaticNode)
			{
			
			}
			else if(object instanceof Region)
			{
				System.err.println("GETS HERE!");
				Region region = (Region) object;
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
}
