package view;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Region;

public class JSliderPane extends JOptionPane
{
	//http://www.java2s.com/Tutorial/Java/0240__Swing/UsingJOptionPanewithaJSlider.htm

	private static final long serialVersionUID = 1L;
	
	public JSliderPane(Region region)
	{
		super();
		JFrame parent = new JFrame();
		JSlider slider = getSlider(this, region);
		this.setMessage(new Object[] { "Select a lighting value for region: " + region.getName(), slider });
		this.setMessageType(JOptionPane.QUESTION_MESSAGE);
		this.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = this.createDialog(parent, "My Slider");
		dialog.setVisible(true);		
	}
	
	static JSlider getSlider(final JOptionPane optionPane, Region region) 
	{
		JSlider slider = new JSlider(SwingConstants.VERTICAL, 0, 100, region.getLightingValue());
		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JSlider theSlider = (JSlider) changeEvent.getSource();
				if (!theSlider.getValueIsAdjusting()) {
					optionPane.setInputValue(new Integer(theSlider.getValue()));
				}
			}
		};
		slider.addChangeListener(changeListener);
		return slider;
	}
}
