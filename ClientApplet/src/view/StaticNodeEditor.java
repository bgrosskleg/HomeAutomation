package view;

import interfaces.ModelSubscriber;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import model.ModelObject;
import model.Region;
import model.StaticNode;

public class StaticNodeEditor extends JFrame
{
	private static final long serialVersionUID = 1L;

	StaticNodeEditorPane SNEP;
	
	/**
	 * Creates the window that edits a static node
	 * @param staticNode
	 */
	public StaticNodeEditor(StaticNode staticNode)
	{ 
		super("Static Node Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		SNEP = new StaticNodeEditorPane(this, staticNode);
		ClientApplet.getController().addModelSubscriber(SNEP);
		add(SNEP);
		
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * Dispose method must be overridden to remove memory leak where editor is
	 * added as a model subscriber but never removed
	 */
	@Override
	public void dispose()
	{
		super.dispose();
		ClientApplet.getController().removeModelSubscriber(SNEP);
	}
	
	/**
	 * Displays the pane that allows staticNode editting
	 * @author Brian Grosskleg
	 */
	private class StaticNodeEditorPane extends JPanel implements ModelSubscriber
	{
		private static final long serialVersionUID = 1L;
		
		private StaticNode staticNode;
		
		private DefaultComboBoxModel<Region> regionListModel;
		
		private JLabel lightingValueLabel;
		
		/**
		 * Creates the pane for staticNode editting
		 * @param frame			parent frame used so listenerers can close window
		 * @param staticNode	staticNode to be editted/displayed
		 */
		private StaticNodeEditorPane(final JFrame frame, final StaticNode staticNode)
		{
			super();
			this.staticNode = staticNode;
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			

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
			regionListModel = new DefaultComboBoxModel<Region>();
			regionListModel.addElement(null);
			for(ModelObject object2 : ClientApplet.getController().getModelObjects())
			{
				if(object2 instanceof Region)
				{
					Region region = (Region) object2;
					regionListModel.addElement(region);
				}
			}

			final JComboBox<Region> regionList = new JComboBox<Region>(regionListModel);
			regionList.setRenderer(new RegionCellRenderer());
			if(staticNode.getPairedRegion() != null)
			{
				regionList.setSelectedItem(staticNode.getPairedRegion());
			}

			this.add(regionList , gbc);


			gbc.gridy = 3;
			gbc.gridx = 0;
			this.add(new JLabel("Lighting Value: "), gbc);
			gbc.gridx = 1;
			lightingValueLabel = new JLabel(String.valueOf(staticNode.getLightingValue()) + "%");
			this.add(lightingValueLabel, gbc);


			JButton OKButton = new JButton("OK");
			OKButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					//Submit values to model and close dialog box
					Region newPairedRegion = null;
					if(regionList.getSelectedItem() == null)
					{
						if(staticNode.getPairedRegion() != null)
						{
							//If new paired region is null, but had an old region, remove the staticNode from the old region
							staticNode.getPairedRegion().removeStaticNode(staticNode);
						}
					}
					else
					{
						for(ModelObject object : ClientApplet.getController().getModelObjects())
						{
							if(object instanceof Region)
							{
								Region region = (Region) object;
								
								if(regionList.getSelectedItem().equals(region))
								{
									//If selected region matches a region in the model
									//Add the static node to that region and set that region as the static nodes newPairedRegion
									if(!region.getStaticNodes().contains(staticNode))
									{
										region.addStaticNode(staticNode);
									}
	
									newPairedRegion = region;
								}
							}
						}
					}

					//Modify objects
					String [] parameters = new String[]{"MACAddress", "pairedRegion"};
					Object [] values = new Object[]{MACAddress.getText(), newPairedRegion};
					
					ClientApplet.getController().modifyObject(staticNode, parameters, values);	

					//Close parent window
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
		 * Called everytime the model is changed
		 * In this case the lightingValue is updated
		 */
		@Override
		public void modelChanged() 
		{	
			lightingValueLabel.setText(String.valueOf(staticNode.getLightingValue()) + "%");
		}
	}
	
	
	//CELL RENDERER FOR CUSTOM LIST RENDERING **********************************************************************************
	
	/**
	 * This class is responsible for displaying just the regions name and not the toString() method
	 * in the regionList list.
	 * @author Brian Grosskleg
	 *
	 */
	private class RegionCellRenderer extends JLabel implements ListCellRenderer<Region>
	{
		private static final long serialVersionUID = 1L;

		//http://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#renderer

		private RegionCellRenderer()
		{
			super();
			this.setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(
				JList<? extends Region> list, Region value, int index,
				boolean isSelected, boolean cellHasFocus) 
		{
			if(value == null)
			{
				this.setText("None - Must be paired to a region later");
			}
			else
			{
				this.setText(value.getName());
			}			

			if (isSelected) 
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else 
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			return this;
		}
	}
}
