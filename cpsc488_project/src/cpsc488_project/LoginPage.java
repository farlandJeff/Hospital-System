package cpsc488_project;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import java.awt.Color;

public class LoginPage implements ActionListener{

	//Setup for Buttons and Labels
	JFrame frame = new JFrame();
	JButton loginButton = new JButton("Login");
	JButton clearButton = new JButton("Clear");
	JTextField userIDField = new JTextField();
	JPasswordField userPasswordField = new JPasswordField();
	JLabel userIDLabel = new JLabel("Username:");
	JLabel userPasswordLabel = new JLabel("Password:");
	JLabel noteLabel = new JLabel();
	JLabel titleLabel = new JLabel("User Login");
	
	
	
	HashMap<String,String> logininfo = new HashMap<String,String>();
	
	LoginPage(HashMap<String,String> loginInfoOriginal){
		
		logininfo = loginInfoOriginal;
		userIDLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		frame.setResizable(false);
		
		//x 50 y 100 75 long 25 height 
		userIDLabel.setBounds(137,150,75,25);
		userPasswordLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		userPasswordLabel.setBounds(137,193,75,25);
		titleLabel.setBounds(185,69,229,50);
		titleLabel.setFont(new Font("Rockwell", Font.BOLD, 40));
		
		//Add Labels to Frame
		frame.getContentPane().add(userIDLabel);
		frame.getContentPane().add(userPasswordLabel);
		frame.getContentPane().add(noteLabel);
		frame.getContentPane().add(titleLabel);
		
		//Position of Note in Frame
		noteLabel.setBounds(130,280,250,35);
		noteLabel.setFont(new Font(null,Font.BOLD,15));
		
		//Position of UserName in Frame
		userIDField.setBounds(222,150,200,25);
		userPasswordField.setBounds(222,193,200,25);
		
		//Position of Login Button
		loginButton.setBounds(185,240,100,25);
		//Turn off Blue Highlight around button
		loginButton.setFocusable(false);
		//Listener
		loginButton.addActionListener(this);
		
		//Position for Clear Button
		clearButton.setBounds(295,240,100,25);
		clearButton.setFocusable(false);
		clearButton.addActionListener(this);
		
		//Add Frame to screen
		frame.getContentPane().add(userIDLabel);
		frame.getContentPane().add(userPasswordLabel);
		frame.getContentPane().add(noteLabel);
		frame.getContentPane().add(userIDField);
		frame.getContentPane().add(userPasswordField);
		frame.getContentPane().add(loginButton);
		frame.getContentPane().add(clearButton);
		frame.getContentPane().setBackground(Color.WHITE);
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Size of Frame
		frame.setSize(570,395);
		frame.getContentPane().setLayout(null);
		
		JLabel ImageLabel = new JLabel("");
		ImageLabel.setIcon(new ImageIcon(LoginPage.class.getResource("/cpsc488_project/lock.png")));
		ImageLabel.setBounds(23, 128, 91, 90);
		frame.getContentPane().add(ImageLabel);
		
		JLabel backgroundpic = new JLabel("");
		backgroundpic.setIcon(new ImageIcon(LoginPage.class.getResource("/cpsc488_project/bluebackground.jpg")));
		backgroundpic.setBounds(0, 0, 554, 356);
		frame.getContentPane().add(backgroundpic);
		//Visible
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Clear the Text Fields
		if(e.getSource()==clearButton) {
			//Empty Text Boxes
			userIDField.setText("");
			userPasswordField.setText("");
			noteLabel.setText("");
		}
		
		if(e.getSource()==loginButton) {
			
			String userID = userIDField.getText();
			String password = String.valueOf(userPasswordField.getPassword());
			
			if(logininfo.containsKey(userID)) {
				//If User name/Password is correct -> Success
				if(logininfo.get(userID).equals(password)) {
					noteLabel.setText("*Successful Login");
					//Before Launching Page Delete Login Screen
					frame.dispose();
					//Launch New Page
					menuPage window = new menuPage();
					window.frame.setVisible(true);
					
				}
				else {
					//User name/Password is wrong
					noteLabel.setText("*Incorrect Password");
				}
			}
			else {
				noteLabel.setText("*Username does not exist");
			}
		}
	}
}
