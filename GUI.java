

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class GUI {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		try
		{
			CertVIew.init();

			JFrame frame = new JFrame("Assignment");
			
			frame.setVisible(true);
			frame.setSize(500,200);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JPanel panel = new JPanel();
			frame.add(panel);
			JButton button = new JButton("START");
			panel.add(button);
			button.addActionListener (new Action1());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}




}

class Action1 implements ActionListener {        
	public void actionPerformed (ActionEvent e) {     
		try {
			ProducerConsumerService.prod();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}   
