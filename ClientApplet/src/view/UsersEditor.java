package view;

import interfaces.ModelSubscriber;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.ModelObject;
import model.User;

public class UsersEditor extends JFrame
{
	private static final long serialVersionUID = 1L;

	/**
	 * Window to edit users (plural)
	 */
	public UsersEditor()
	{ 
		super("Users");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		add(new UsersEditorPane(this));
		
		this.pack();
		this.setVisible(true);
		this.setResizable(false);
	}
	
	//USERS EDITOR PANE*********************************************************************************************
	
	/**
	 * Panel to edit users information (plural)
	 * @author Brian Grosskleg
	 *
	 */
	private class UsersEditorPane extends JPanel implements ModelSubscriber
	{
		private static final long serialVersionUID = 1L;
		
		private DefaultListModel<User> userListModel;
		private JList<User> userList;
		
		JButton newUser;
		JButton editUser;
		JButton removeUser;		

		/**
		 * Creates a panel to display all users
		 * @param frame	parent frame used as reference for dispose
		 */
		private UsersEditorPane(final JFrame frame)
		{
			super();
			
			ClientApplet.getController().addModelSubscriber(this);
			
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0.8;
			gbc.weighty = 0.2;
			
			add(new JLabel("Users"), gbc);
			
			//Create and populate the list model
			userListModel = new DefaultListModel<User>();
			for(ModelObject object : ClientApplet.getController().getModelObjects())
			{
				if(object instanceof User)
				{
					User user = (User) object;
					userListModel.addElement(user);
				}
			}
			
			//Create the list from the model and set rendering style
			userList = new JList<User>(userListModel); 
			UserCellRenderer renderer = new UserCellRenderer(null);
			userList.setCellRenderer(renderer);
			userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			userList.setLayoutOrientation(JList.VERTICAL);
			userList.setVisibleRowCount(10);
			userList.addListSelectionListener(new ListSelectionListener()
			{
				@Override
				public void valueChanged(ListSelectionEvent e) 
				{
					if(userList.getSelectedIndex() == -1)
					{
						editUser.setEnabled(false);
						removeUser.setEnabled(false);
					}
					else
					{
						editUser.setEnabled(true);
						removeUser.setEnabled(true);
					}
				}
			});
			
			
			JScrollPane listScroller = new JScrollPane(userList);
			listScroller.setPreferredSize(new Dimension(400,150));
			gbc.gridy = 1;
			gbc.gridheight = 3;
			gbc.fill = GridBagConstraints.BOTH;
			add(listScroller, gbc);
			
			
			//NEW USER BUTTON
			gbc.gridy = 1;
			gbc.gridx = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.3;
			gbc.fill = GridBagConstraints.NONE;
			newUser = new JButton("New User");
			newUser.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					new UserEditor(null, userListModel);	
				}
			});
			add(newUser, gbc);
			
			
			//EDIT USER BUTTON
			gbc.gridy = 2;
			editUser = new JButton("Edit User");
			editUser.setEnabled(false);
			editUser.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					new UserEditor(userList.getSelectedValue(), userListModel);					
				}
			});
			add(editUser, gbc);
			
			//REMOVE USER BUTTON
			gbc.gridy = 3;
			removeUser = new JButton("Remove User");
			removeUser.setEnabled(false);
			removeUser.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{					
					//Remove user from systemModel
					ClientApplet.getController().removeModelObject(userList.getSelectedValue());
					
					//Remove user from userListModel
					userListModel.removeElement(userList.getSelectedValue());
				}
			});
			add(removeUser, gbc);
			
			
			//OKAY BUTTON
			gbc.weighty = 0.2;
			gbc.gridy = 4;
			JButton OKAY = new JButton("OK");
			OKAY.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					//Close box
					frame.dispose();	
				}
			});
			add(OKAY, gbc);
		}

		/**
		 * Called everytime the model is changed
		 * In this case it repaints the list with a new label for each user
		 */
		@Override
		public void modelChanged() 
		{
			this.repaint();
		}	
	}	
	
	
	//USER EDITOR FRAME***********************************************************************************************
	
	/**
	 * User editor window (singular)
	 * @author Brian Grosskleg
	 *
	 */
	private class UserEditor extends JFrame
	{
		private static final long serialVersionUID = 1L;

		private UserEditor(User user, DefaultListModel<User> userListModel)
		{
			super("User Editor");
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			add(new UserEditorPane(this, user, userListModel));

			this.pack();
			this.setVisible(true);
			this.setResizable(false);
		}
	}

	/**
	 * User editor panel (singular)
	 * @author Brian Grosskleg
	 *
	 */
	private class UserEditorPane extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		private JTextField name;
		private JTextField MACAddress;
		private JSlider slider;
		private JLabel value;
		
		/**
		 * Creates the user editor panel (singular)
		 * @param frame	parent frame used for disposing
		 * @param user	user to be editted
		 * @param userListModel to get which user had been selected and add user to
		 */
		UserEditorPane(final JFrame frame, final User user, final DefaultListModel<User> userListModel)
		{
			super();
			
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new JLabel("User Editor"), gbc);
			
			gbc.gridy = 1;
			add(new JLabel("Name: "),gbc);
			
			gbc.gridy = 2;
			add(new JLabel("MAC Address: "),gbc);
			
			
			//Set name and user
			if(user != null)
			{
				name = new JTextField(user.getName(),15);
				MACAddress = new JTextField(user.getMACAddress(), 15);
			}
			else
			{
				name = new JTextField(15);
				MACAddress = new JTextField(15);
			}
			
			gbc.gridx = 1;
			gbc.gridy = 1;
			add(name, gbc);
			
			gbc.gridy = 2;
			add(MACAddress, gbc);
			
			
			//Add color chooser
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.weightx = 0.75;
			gbc.gridwidth = 2;
			gbc.gridheight = 2;
			gbc.fill = GridBagConstraints.BOTH;
			ArrayList<Color> colors = new ArrayList<Color>();
			colors.add(Color.BLUE); 
			colors.add(Color.CYAN); 
			colors.add(Color.RED); 
			colors.add(Color.YELLOW);
			colors.add(Color.ORANGE);
			colors.add(Color.GREEN);
			colors.add(Color.PINK);
			colors.add(Color.MAGENTA);
			final CustomColorChooserPanel colorChooser = new CustomColorChooserPanel(colors, user);
			add(colorChooser, gbc);
			
			
			//Add lighting value slider
			if(user != null)
			{
				slider = new JSlider(SwingConstants.VERTICAL, 0, 100, user.getPreferredLightingValue());
			}
			else
			{
				slider = new JSlider(SwingConstants.VERTICAL, 0, 100, 100);	
			}
			slider.setMajorTickSpacing(10);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			
			//Sync slider and JTextField
			slider.addChangeListener(new ChangeListener() 
			{
				public void stateChanged(ChangeEvent changeEvent) 
				{
					JSlider theSlider = (JSlider) changeEvent.getSource();
					value.setText(String.valueOf(theSlider.getValue()+"%"));
				}
			});
			
			value = new JLabel(String.valueOf(slider.getValue()+"%"));
			
			gbc.gridx = 2;
			gbc.gridy = 1;
			gbc.weightx = 0.25;
			gbc.gridwidth = 2;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.NONE;
			add(new JLabel("Lighting Value"), gbc);
			
			gbc.gridy = 2;
			gbc.gridheight = 2;
			add(slider, gbc);
			
			gbc.gridy = 4;
			gbc.gridheight = 1;
			add(value, gbc);
						
			
			JButton OK = new JButton("OK");
			OK.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					//Grab new data and either create or modify user
					if(user == null)
					{
						//Create user
						User newUser = new User(name.getText(), MACAddress.getText(), slider.getValue(), colorChooser.getCurrentColor());
						
						//Add user to listModel
						userListModel.addElement(newUser);
						
						
						//Add user to systemModel
						ClientApplet.getController().addModelObject(newUser);
					}
					else
					{
						//Modify existing user
						String [] parameters = new String [] {"name", "MACAddress", "preferredLightingValue", "color"};
						Object [] values = new Object [] {name.getText(), MACAddress.getText(), slider.getValue(), colorChooser.getCurrentColor()};
						ClientApplet.getController().modifyObject(user, parameters, values);				
					}
					frame.dispose();
				}
				
			});
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.125;
			gbc.gridy = 5;
			gbc.gridx = 2;
			add(OK, gbc);
			
			
			JButton cancel = new JButton("CANCEL");
			cancel.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					//Close frame without saving
					frame.dispose();
				}
			});
			gbc.gridy = 5;
			gbc.gridx = 3;
			add(cancel, gbc);
		}
	}
	
	/**
	 * Quick custom color palatte used for selecting user color
	 * @author Brian Grosskleg
	 *
	 */
	private class CustomColorChooserPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		//http://www.java2s.com/Tutorial/Java/0240__Swing/AddingaCustomColorChooserPaneltoaJColorChooserDialog.htm
		
		JLabel selectedColor;
				
		private CustomColorChooserPanel(ArrayList<Color> colors, User user) 
		{		
			super();
			
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			//Determine user color
			selectedColor = new JLabel();
			selectedColor.setPreferredSize(new Dimension(50, 25));
			selectedColor.setOpaque(true);
			
			for(Color color : colors)
			{
				JButton button = new JButton();
								
				button.setPreferredSize(new Dimension(25, 25));
				
				button.setBackground(color);
				
				button.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						//Update selected color
						selectedColor.setBackground(((JButton)e.getSource()).getBackground());
					}
				});
				
				gbc.gridx++;
				add(button, gbc);
			}
			
			if(user != null)
			{
				selectedColor.setBackground(user.getUnselectedColor());
			}
			else
			{
				selectedColor.setBackground(colors.get(0));
			}
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = colors.size();
			add(new JLabel("Selected Color:"), gbc);
			gbc.gridy = 2;
			add(selectedColor, gbc);
		}

		private Color getCurrentColor() 
		{
			return selectedColor.getBackground();
		}
	}
	
	
	//CELL RENDERER FOR CUSTOM LIST RENDERING **********************************************************************************
	
	/**
	 * Renderer responsible for displaying the users icon, name and MAC address in the usersEditor listBox
	 * @author Brian Grosskleg
	 *
	 */
	private class UserCellRenderer extends JLabel implements ListCellRenderer<User>, Icon
	{
		private static final long serialVersionUID = 1L;

		//http://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#renderer
		
		public User user;
		
		private UserCellRenderer(User user)
		{
			super();
			this.setOpaque(true);
			this.user = user;
		}
		
		@Override
		public Component getListCellRendererComponent(
				JList<? extends User> list, User value, int index,
				boolean isSelected, boolean cellHasFocus) 
		{
			this.setIcon(new UserCellRenderer(value));
			this.setText("Name: " + value.getName() + "           MAC Address: " + value.getMACAddress());			
			this.setIconTextGap(5);
			
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
		

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) 
		{
			Graphics2D g2 = (Graphics2D) g;
			if(user != null)
			{
				g2.setColor(user.getUnselectedColor());
			}
			else
			{
				g2.setColor(Color.BLUE);
			}
	        Ellipse2D.Double user = new Ellipse2D.Double(x, y, getIconWidth(), getIconHeight());
	        g2.fill(user);
		}

		@Override
		public int getIconWidth() 
		{
			return 15;
		}

		@Override
		public int getIconHeight() 
		{
			return 15;
		}
	}
}
