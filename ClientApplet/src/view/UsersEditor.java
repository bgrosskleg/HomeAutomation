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
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.ModelObject;
import model.User;

public class UsersEditor extends JFrame
{
	private static final long serialVersionUID = 1L;

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
	
	private class UsersEditorPane extends JPanel implements ModelSubscriber
	{
		private static final long serialVersionUID = 1L;
		
		private DefaultListModel<User> listModel;
		private JList<User> userList;
		
		JButton newUser;
		JButton editUser;
		JButton removeUser;		

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
			listModel = new DefaultListModel<User>();
			for(ModelObject object : ClientApplet.getController().getModelObjects())
			{
				if(object instanceof User)
				{
					User user = (User) object;
					listModel.addElement(user);
				}
			}
			
			//Create the list from the model and set rendering style
			userList = new JList<User>(listModel); 
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
			
			//NEW USER
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
					new UserEditor(null);	
				}
			});
			add(newUser, gbc);
			
			//EDIT USER
			gbc.gridy = 2;
			editUser = new JButton("Edit User");
			editUser.setEnabled(false);
			editUser.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					new UserEditor(userList.getSelectedValue());					
				}
			});
			add(editUser, gbc);
			
			//REMOVE USER
			gbc.gridy = 3;
			removeUser = new JButton("Remove User");
			removeUser.setEnabled(false);
			removeUser.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					ClientApplet.getController().removeModelObject(userList.getSelectedValue());
				}
			});
			add(removeUser, gbc);
			
			//OKAY
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

		@Override
		public void modelChanged() 
		{
			//Re-sync links from list to new model
			ClientApplet.getController().getModelObjects();

			//Add missing user to listModel
			for(ModelObject object : ClientApplet.getController().getModelObjects())
			{
				if(object instanceof User)
				{
					User user = (User) object;
					if(!listModel.contains(user))
					{
						listModel.addElement(user);
					}
				}
			}

			//Remove a user from listModel that should no longer be in it
			for(int iter = 0; iter < listModel.getSize(); iter++)
			{
				if(!ClientApplet.getController().getModelObjects().contains(listModel.get(iter)))
				{
					listModel.remove(iter);
				}
			}
			
			//Update list links to any existing objects
			for(int iter = 0; iter < listModel.getSize(); iter++)
			{
				for(ModelObject object : ClientApplet.getController().getModelObjects())
				{
					if(object instanceof User)
					{
						User user = (User) object;
						if(listModel.get(iter).equals(user))
						{
							listModel.setElementAt(user, iter);
						}
					}
				}
			}
			
		}	
	}	
	
	
	//USER EDITOR FRAME***********************************************************************************************
	
	private class UserEditor extends JFrame
	{
		private static final long serialVersionUID = 1L;

		private UserEditor(User user)
		{
			super("User Editor");
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			add(new UserEditorPane(this, user));

			this.pack();
			this.setVisible(true);
			this.setResizable(false);
		}
	}

	private class UserEditorPane extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		UserEditorPane(final JFrame frame, final User user)
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
			
			
			final JTextField name;
			final JTextField MACAddress;
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
			
			
			JButton OK = new JButton("OK");
			OK.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					//Grab new data and either create or modify user
					if(user == null)
					{
						System.err.println("Create new object");
						ClientApplet.getController().addModelObject(new User(name.getText(), MACAddress.getText(), 100, colorChooser.getCurrentColor()));
					}
					else
					{
						System.err.println("Modifying object");
						//Modify existing user
						String [] parameters = new String [] {"name", "MACAddress", "color"};
						Object [] values = new Object [] {name.getText(), MACAddress.getText(), colorChooser.getCurrentColor()};
						ClientApplet.getController().modifyObject(user, parameters, values);				
					}
					frame.dispose();
				}
				
			});
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.125;
			gbc.fill = GridBagConstraints.NONE;
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
