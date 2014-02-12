package view;

import interfaces.ModelSubscriber;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.ModelObject;
import model.Region;
import model.Sensor;

public class ObjectEditor extends JFrame
{
	private static final long serialVersionUID = 1L;

	public ObjectEditor(ModelObject object)
	{ 
		super("Object Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		add(new ObjectEditorPane(this, object));
		
		this.pack();
		this.setVisible(true);
	}
	
	private class ObjectEditorPane extends JPanel implements ModelSubscriber
	{
		private static final long serialVersionUID = 1L;

		private ObjectEditorPane(final JFrame frame, ModelObject object)
		{
			super();
			
			ClientApplet.getController().addModelSubscriber(this);
			
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			if(object instanceof Sensor)
			{
				final Sensor sensor = (Sensor) object;
				
				//Create JDialog to edit sensor...
				
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.anchor = GridBagConstraints.LINE_START;
				this.add(new JLabel("Edit Sensor"), gbc);
			
				gbc.gridy = 1;
				add(new JLabel("MAC Address: "), gbc);
				final JTextField MACAddress = new JTextField(sensor.getMACAddress(), 15);
				gbc.gridx = 1;
				this.add(MACAddress, gbc);
				
				gbc.gridy = 2;
				gbc.gridx = 0;
				add(new JLabel("Paired Region: "), gbc);
				gbc.gridx = 1;
				ArrayList<String> possibilities = new ArrayList<String>();
				String defaultOption = "None - Must be paired to a region later";
				possibilities.add(defaultOption);
				
				DefaultComboBoxModel<Region> regionListModel = new DefaultComboBoxModel<Region>();
				for(ModelObject object2 : ClientApplet.getController().getModelObjects())
				{
					if(object2 instanceof Region)
					{
						Region region = (Region) object2;
						regionListModel.addElement(region);
					}
				}
				
				JComboBox<Region> regionList = new JComboBox<Region>(regionListModel);
				if(sensor.getPairedRegion() != null)
				{
					regionList.setSelectedItem(sensor.getPairedRegion());
				}
				else
				{
					regionList.setSelectedIndex(0);
				}
				regionList.addActionListener(regionList);
				
				this.add(regionList , gbc);
				
				
				gbc.gridy = 3;
				gbc.gridx = 0;
				this.add(new JLabel("Lighting Value: "), gbc);
				gbc.gridx = 1;
				this.add(new JLabel(String.valueOf(sensor.getLightingValue())), gbc);
				
				
				JButton OKButton = new JButton("OK");
				OKButton.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) 
					{
						//Submit values to model and close dialog box
						
						//Testing changing parameters
						String [] parameters = new String[]{"MACAddress"};
						Object [] values = new Object[]{MACAddress.getText()};
						
						ClientApplet.getController().modifyObject(sensor, parameters, values);	
						
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
				
				gbc.gridy = 2;
				gbc.gridx = 0;
				add(new JLabel("Occupied by: "), gbc);
				gbc.gridx = 1;
				
				//Add list box of current users occupying region
				
				
				
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
			// TODO Auto-generated method stub
			
		}
	}
}
