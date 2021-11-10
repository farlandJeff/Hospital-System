package cpsc488_project;



import javax.swing.JFrame;
import org.json.simple.parser.ParseException;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.Color;
import javax.swing.ImageIcon;

public class menuPage {

	JFrame frame = new JFrame();
	
	
	public menuPage() {
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setBounds(100, 100, 683, 391);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		
		
		JButton PatientDirectoryButton = new JButton("Patient Directory");
		PatientDirectoryButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		PatientDirectoryButton.setBounds(337, 283, 163, 58);
		PatientDirectoryButton.setFocusable(false);
		PatientDirectoryButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) {
				//frame.dispose();
				patientDirectory window;
				try {
					window = new patientDirectory();
					window.frame.setVisible(true);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		frame.getContentPane().add(PatientDirectoryButton);
		
		JButton btnAddPatient = new JButton("Add Patient");
		btnAddPatient.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnAddPatient.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) {
				//frame.dispose();
				addPatientPage window = new addPatientPage();
				window.frame.setVisible(true);
			}
		});
		btnAddPatient.setBounds(179, 283, 148, 58);
		btnAddPatient.setFocusable(false);
		frame.getContentPane().add(btnAddPatient);
		
		JLabel menuLabel = new JLabel("Main Menu");
		menuLabel.setForeground(Color.DARK_GRAY);
		menuLabel.setFont(new Font("Rockwell", Font.BOLD, 50));
		menuLabel.setBounds(198, 11, 298, 101);
		frame.getContentPane().add(menuLabel);
		
		//Image Source https://www.wellmo.com/news-posts
		// /wellmo-enables-lightning-speed-response-and-tracking-of-the-covid19-
		//crisis/doctor-using-digital-tablet-with-medical-icon-and-heartbeat-rate-in-the-hospital-background/
		JLabel backgroundPic = new JLabel("");
		backgroundPic.setIcon(new ImageIcon(menuPage.class.getResource("/cpsc488_project/menubackground.jpg")));
		backgroundPic.setBounds(0, -64, 677, 480);
		frame.getContentPane().add(backgroundPic);
		
	}
}