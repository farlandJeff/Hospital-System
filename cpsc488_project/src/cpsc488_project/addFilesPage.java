package cpsc488_project;



import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.Border;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class addFilesPage {

	JFrame frame = new JFrame();
	private JTextArea metadataNotesField;
	private String filename;
	private JLabel backgroundpic;
	private JTextField metadataTitleField;
	private JTextField metadataPhysicianField;

	public class CmdFiles {
		public void uploadFile(String filename) throws Exception {
		
		//Navigate into Client folder and run Python3 script to add files
        ProcessBuilder builder = new ProcessBuilder(
        		"cmd.exe", "/c", "cd.. && cd Client/ && python3 irods_up.py " + patientDirectory.lastName.toUpperCase() + "_" + patientDirectory.firstName.toUpperCase() + " " + filename);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            System.out.println(line);
        	}
		}
	}
	
	public addFilesPage() {
		
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		metadataPhysicianField = new JTextField();
		metadataPhysicianField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char c = e.getKeyChar();
				
				if(Character.isLetter(c) || Character.isISOControl(c)) {
					metadataPhysicianField.setEditable(true);
				}
				else {
					metadataPhysicianField.setEditable(false);
				}
			
			}
		});
		metadataPhysicianField.setColumns(10);
		metadataPhysicianField.setBounds(200, 263, 169, 20);
		metadataPhysicianField.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		frame.getContentPane().add(metadataPhysicianField);
		
		metadataTitleField = new JTextField();
		metadataTitleField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char c = e.getKeyChar();
				
				if(Character.isLetter(c) || Character.isISOControl(c)) {
					metadataTitleField.setEditable(true);
				}
				else {
					metadataTitleField.setEditable(false);
				}
			
			}
		});
		metadataTitleField.setBounds(10, 263, 169, 20);
		frame.getContentPane().add(metadataTitleField);
		metadataTitleField.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		
		
		JLabel physicianlabel = new JLabel("Overseeing Physician:");
		physicianlabel.setBounds(202, 249, 167, 14);
		frame.getContentPane().add(physicianlabel);
		
		
		
		JLabel titleLabel = new JLabel("Title:");
		titleLabel.setBounds(10, 249, 99, 14);
		frame.getContentPane().add(titleLabel);
		
		JLabel fileTitleLabel = new JLabel("Attach Files to Patient");
		fileTitleLabel.setFont(new Font("Rockwell", Font.BOLD, 24));
		fileTitleLabel.setBounds(100, 11, 269, 50);
		frame.getContentPane().add(fileTitleLabel);
		
		JLabel filesPatientLabel = new JLabel("");
		filesPatientLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		filesPatientLabel.setBounds(100, 53, 234, 20);
		frame.getContentPane().add(filesPatientLabel);
		
		JLabel picLabel = new JLabel("");
		picLabel.setIcon(new ImageIcon(addFilesPage.class.getResource("/cpsc488_project/FolderPic.png")));
		
		picLabel.setBounds(10, 11, 80, 80);
		frame.getContentPane().add(picLabel);
		
		metadataNotesField = new JTextArea();
		metadataNotesField.setRows(10);
		metadataNotesField.setBounds(10, 307, 369, 147);
		metadataNotesField.setLineWrap(true);
		metadataNotesField.setWrapStyleWord(true);
	    metadataNotesField.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		frame.getContentPane().add(metadataNotesField);
		
		JLabel metadataLabel = new JLabel("Attach Notes:");
		metadataLabel.setBounds(10, 292, 99, 14);
		frame.getContentPane().add(metadataLabel);
		
		JLabel fileNameLabel = new JLabel("");
		fileNameLabel.setForeground(new Color(0, 0, 255));
		fileNameLabel.setBounds(145, 142, 161, 14);
		frame.getContentPane().add(fileNameLabel);
		frame.setBounds(100, 100, 405, 504);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		filesPatientLabel.setText(patientDirectory.nameSelected);
		
		
		
		
		JButton browseButton = new JButton("Browse");
		browseButton.setFocusable(false);
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//String filename;
				int response;
				JFileChooser filechooser = new JFileChooser("../client");
				
				//Browse File Explorer for a file
				filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				response = filechooser.showOpenDialog(null);
				
				if(response == JFileChooser.APPROVE_OPTION) {
					
					filename = filechooser.getSelectedFile().getName();
					//Set Label 
					fileNameLabel.setText(filename);
					
				}
				
			}
			
		});
					
		browseButton.setBounds(10, 142, 125, 30);
		frame.getContentPane().add(browseButton);
		
		JButton addFileButton = new JButton("Upload File");
		addFileButton.setFocusable(false);
		addFileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//String PATIENT_NAME = patientDirectory.nameSelected;
				//String address = "54.227.89.39";
				
				
				
				
				
				
				//No File
				if(filename == null) {
					JOptionPane.showMessageDialog(null, "Please Add a File before Uploading");
					
				}
				if(metadataNotesField.getText().equals("") || metadataPhysicianField.getText().equals("") ||  metadataNotesField.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Please Attach all Metadata before Uploading");
				}
				//Metadata Empty
				
				else
				{
				//Upload File
					//Write data to File Meta.txt
					//For Date Created/Modified
					
					int secs = (int) ((new Date().getTime())/1000);
					
					//To JSON format
					String metadata = "{\n" +  "\""+ "title" + "\"" + ":" + "\"" + metadataTitleField.getText() + "\"" + ",\n" 
							+ "\"" + "file_name" + "\"" + ":" + "\"" + fileNameLabel.getText()  + "\"" + ",\n" 
							+ "\"" + "date_created" + "\"" + ":" + "\"" + secs  + "\"" + ",\n" 
							+ "\"" + "date_modified" + "\"" + ":" + "\"" + secs  + "\"" + ",\n" 
							+ "\"" + "overseeing" + "\"" + ":" + "\"" + metadataPhysicianField.getText()  + "\"" + ",\n" 
							+ "\"" + "notes"  + "\"" + ":" + "\"" + metadataNotesField.getText() + "\"" + "\n" + "}";
					
					
					//Write
					File dir = new File("../client/");
					String file = "meta.txt";
					FileWriter fw;
					PrintWriter pw =null;
					try {
						fw = new FileWriter(new File(dir, file));
						pw = new PrintWriter(fw);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					pw.println(metadata);
					pw.flush();
					
					
					
				//Run Python3	
				CmdFiles cmd = new CmdFiles();
				try {
					cmd.uploadFile(filename);
					JOptionPane.showMessageDialog(null, "File Added");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				}
			
			}
		});
		addFileButton.setBounds(10, 183, 125, 30);
		frame.getContentPane().add(addFileButton);
		
		backgroundpic = new JLabel("");
		backgroundpic.setBounds(0, 0, 389, 533);
		backgroundpic.setIcon(new ImageIcon(addPatientPage.class.getResource("/cpsc488_project/bluebackground.jpg")));
		frame.getContentPane().add(backgroundpic);
		
		
		
	}
}
