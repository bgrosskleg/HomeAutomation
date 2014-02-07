package view;

import interfaces.UserModelSubscriber;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.HouseObject;
import model.Region;
import model.Sensor;

public class ObjectEditor extends JFrame
{
	private static final long serialVersionUID = 1L;

	public ObjectEditor(HouseObject object)
	{ 
		super("Object Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		add(new ObjectEditorPane(this, object));
		
		this.pack();
		this.setVisible(true);
	}
	
	private class ObjectEditorPane extends JPanel implements UserModelSubscriber
	{
		private static final long serialVersionUID = 1L;

		private ObjectEditorPane(final JFrame frame, HouseObject object)
		{
			super();
			
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
				add(new JLabel("Sensors: "), gbc);
				gbc.gridx = 1;
				ComparisonBoxes compBox = new ComparisonBoxes();
				this.add(compBox , gbc);
				
				gbc.gridy = 3;
				gbc.gridx = 0;
				this.add(new JLabel("Sensors: "), gbc);
				gbc.gridx = 1;
				JLabel status = new JLabel("Status...");
				this.add(status, gbc);
				
				
				JButton OKButton = new JButton("OK");
				OKButton.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) 
					{
						//Submit values to model and close dialog box
						sensor.setMACAddress(MACAddress.getText());
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
				Region region = (Region) object;
				
				//Create JDialog to edit region...
				this.add(new JLabel("Edit Region"));
				
				
				
			}

		}
	
		private class ComparisonBoxes extends JPanel
		{
			private static final long serialVersionUID = 1L;

			private ComparisonBoxes()
			{
				//To complete...
			}
		}

		@Override
		public void userModelChanged() 
		{
			// TODO Auto-generated method stub
			
		}
	}
}
