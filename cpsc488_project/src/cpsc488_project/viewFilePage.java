package cpsc488_project;


import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JTabbedPane;
import java.awt.Panel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cpsc488_project.patientDirectory.CmdPatientsDelete;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

public class viewFilePage {

	JFrame frame = new JFrame();
	private JTextField searchFilesField;
	public JSONArray b;
	DefaultListModel<Object> DLMFiles = new DefaultListModel<Object>();
	private final JList<Object> listFiles = new JList<Object>();
	private final JPopupMenu pop = new JPopupMenu();
	public int indicies;
	public static String title = "";
	public static String SelectedFile = "";
	public static String dateCreated ;
	public static String dateModified;
	public static String overseeing = "";
	public static String notes = "";
	public static String fileName = "";
	public int filterNum=0;
	
	
	public class CmdFiles {
		public void fileNames() throws Exception {
			
		//Navigate into Client folder and run Python3 script to fetch patients
        ProcessBuilder builder = new ProcessBuilder(
        		//Shows All Files
        		//Change Filter number to access different dates
        		"cmd.exe", "/c", "cd.. && cd Client/ && python3 irods_files_info.py " + "-a "+ filterNum +" " + patientDirectory.lastName.toUpperCase() + "_" + patientDirectory.firstName.toUpperCase()); 
      
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
	
	public class CmdPatientsDeleteFile {
		public void patientNameDeleteFile() throws Exception {
			
		//Navigate into Client folder and run Python3 script to Delete Selected File
        ProcessBuilder builder = new ProcessBuilder(
        		"cmd.exe", "/c", "cd.. && cd Client/ && python3 irods_delete.py " + "-p" + " " + patientDirectory.lastName.toUpperCase() + "_" + patientDirectory.firstName.toUpperCase() + " " +  "-f" + " " + SelectedFile);
      
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
	
	
	// Downloading a file for viewing
	public class DownloadPatientsFile {
		public void patientDownloadFile() throws Exception {
			ProcessBuilder builder = new ProcessBuilder(
	        		"cmd.exe", "/c", "cd.. && cd Client/ && python3 irods_down.py " + patientDirectory.lastName.toUpperCase() + "_" + patientDirectory.firstName.toUpperCase() + " " + SelectedFile);
			
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
	
	public class OpenImageFile {
		public void openImage() throws Exception {
			ProcessBuilder builder = new ProcessBuilder(
	        		"cmd.exe", "/c", "cd src/cpsc488_project/temp/ && " + patientDirectory.lastName.toUpperCase() + "_" + patientDirectory.firstName.toUpperCase() + "-" + SelectedFile);
			
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
	
	private void bindData() throws IOException, org.json.simple.parser.ParseException {
		getNames().stream().forEach((name) -> {
			DLMFiles.addElement(name);
		});
		
	}
	
	//Search Box to filter through strings
		private void searchFilterFiles(String searchTerm) throws IOException, org.json.simple.parser.ParseException
		{
			DefaultListModel<Object> filterItems = new DefaultListModel<Object>();
			ArrayList<String> names=getNames();
			
			names.stream().forEach((name) -> {
				String filteredName=name.toString().toLowerCase();
				if (filteredName.contains(searchTerm.toLowerCase())) {
					filterItems.addElement(name);
				}
			});
			DLMFiles=filterItems;
			listFiles.setModel(DLMFiles);
			
		}
	
	
	public viewFilePage() throws org.json.simple.parser.ParseException, IOException {
		
		//Run Script
		CmdFiles cmd = new CmdFiles();
		try {
			//Run Command Prompt
			cmd.fileNames();
			 System.out.println();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		e.printStackTrace();
		}
		///////////////////////////////////////////
		//Bind the new data to the jlist
		this.bindData();
		///////////////////////////////////////////////////////////////
		//Run popup function
		addPopup();
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		
		JLabel view_patient_title = new JLabel("Patient:");
		view_patient_title.setFont(new Font("Rockwell", Font.BOLD | Font.ITALIC, 30));
		view_patient_title.setBounds(117, 0, 312, 68);
		frame.getContentPane().add(view_patient_title);
		
		JLabel view_patient_label = new JLabel("");
		view_patient_label.setFont(new Font("Rockwell", Font.BOLD | Font.ITALIC, 30));
		view_patient_label.setBounds(117, 33, 312, 68);
		view_patient_label.setText(patientDirectory.nameSelected);
		frame.getContentPane().add(view_patient_label);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setBounds(0, 90, 457, 463);
		frame.getContentPane().add(tabbedPane);
		
		Panel panelInfo = new Panel();
		tabbedPane.addTab("Patient Info", null, panelInfo, null);
		panelInfo.setLayout(null);
		
		JLabel weightLablelV = new JLabel("Weight:");
		weightLablelV.setFont(new Font("Rockwell", Font.BOLD, 15));
		weightLablelV.setBounds(10, 135, 64, 28);
		panelInfo.add(weightLablelV);
		
		JLabel dateCreatedLabelV = new JLabel("Patient Created:");
		dateCreatedLabelV.setFont(new Font("Rockwell", Font.BOLD, 15));
		dateCreatedLabelV.setBounds(10, 22, 137, 14);
		panelInfo.add(dateCreatedLabelV);
		
		JLabel heightLabelV = new JLabel("Height:");
		heightLabelV.setFont(new Font("Rockwell", Font.BOLD, 15));
		heightLabelV.setBounds(10, 156, 64, 26);
		panelInfo.add(heightLabelV);
		
		JLabel dateModifiedLabelV = new JLabel("Patient Modfied:");
		dateModifiedLabelV.setFont(new Font("Rockwell", Font.BOLD, 15));
		dateModifiedLabelV.setBounds(10, 48, 137, 14);
		panelInfo.add(dateModifiedLabelV);
		
		JLabel dobLabelV = new JLabel("Date of Birth:");
		dobLabelV.setFont(new Font("Rockwell", Font.BOLD, 15));
		dobLabelV.setBounds(10, 73, 104, 14);
		panelInfo.add(dobLabelV);
		
		JLabel sexLabelV = new JLabel("Sex:");
		sexLabelV.setFont(new Font("Rockwell", Font.BOLD, 15));
		sexLabelV.setBounds(10, 182, 64, 14);
		panelInfo.add(sexLabelV);
		
		JLabel ethnicityLabelV = new JLabel("Ethnicity:");
		ethnicityLabelV.setFont(new Font("Rockwell", Font.BOLD, 15));
		ethnicityLabelV.setBounds(10, 115, 71, 26);
		panelInfo.add(ethnicityLabelV);
		
		JLabel sexLabelAns = new JLabel("");
		sexLabelAns.setFont(new Font("Rockwell", Font.PLAIN, 15));
		sexLabelAns.setBounds(84, 183, 95, 14);
		panelInfo.add(sexLabelAns);
		
		JLabel ethnicityLabelAns = new JLabel("");
		ethnicityLabelAns.setFont(new Font("Rockwell", Font.PLAIN, 15));
		ethnicityLabelAns.setBounds(84, 122, 95, 14);
		panelInfo.add(ethnicityLabelAns);
		
		JLabel heightLabelAns = new JLabel("");
		heightLabelAns.setFont(new Font("Rockwell", Font.PLAIN, 15));
		heightLabelAns.setBounds(85, 163, 94, 14);
		panelInfo.add(heightLabelAns);
		
		JLabel weightLabelAns = new JLabel("");
		weightLabelAns.setFont(new Font("Rockwell", Font.PLAIN, 15));
		weightLabelAns.setBounds(84, 143, 94, 14);
		panelInfo.add(weightLabelAns);
		
		Panel panelFiles = new Panel();
		tabbedPane.addTab("Patient Files", null, panelFiles, null);
		panelFiles.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.GRAY);
		panel.setBounds(0, 0, 452, 47);
		panelFiles.add(panel);
		panel.setLayout(null);
		
		JLabel fileLabel = new JLabel("Files");
		fileLabel.setForeground(Color.WHITE);
		fileLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
		fileLabel.setBounds(10, 11, 57, 25);
		panel.add(fileLabel);
		
		searchFilesField = new JTextField();
		searchFilesField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				//Run Search filter to search through files
				try {
					searchFilterFiles(searchFilesField.getText());
				} catch (IOException | org.json.simple.parser.ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		searchFilesField.setForeground(Color.DARK_GRAY);
		searchFilesField.setBounds(65, 14, 131, 20);
		panel.add(searchFilesField);
		searchFilesField.setColumns(10);
		
		JButton allButton = new JButton("All");
		allButton.setForeground(Color.WHITE);
		allButton.setFont(new Font("Tahoma", Font.BOLD, 10));
		allButton.setBackground(Color.LIGHT_GRAY);
		allButton.setBounds(202, 13, 48, 23);
		allButton.setFocusable(false);
		panel.add(allButton);
		
		JButton monthButton1 = new JButton("5 YEARS");
		
		monthButton1.setForeground(Color.WHITE);
		monthButton1.setFont(new Font("Tahoma", Font.BOLD, 10));
		monthButton1.setBackground(Color.LIGHT_GRAY);
		monthButton1.setBounds(255, 0, 84, 23);
		monthButton1.setFocusable(false);
		panel.add(monthButton1);
		
		JButton monthButton2 = new JButton("1 YEAR");
		monthButton2.setForeground(Color.WHITE);
		monthButton2.setFont(new Font("Tahoma", Font.BOLD, 10));
		monthButton2.setBackground(Color.LIGHT_GRAY);
		monthButton2.setBounds(255, 24, 84, 23);
		monthButton2.setFocusable(false);
		panel.add(monthButton2);
		
		JButton monthButton3 = new JButton("6 MONTHS");
		monthButton3.setForeground(Color.WHITE);
		monthButton3.setFont(new Font("Tahoma", Font.BOLD, 10));
		monthButton3.setBackground(Color.LIGHT_GRAY);
		monthButton3.setBounds(347, 0, 94, 23);
		monthButton3.setFocusable(false);
		panel.add(monthButton3);
		
		JButton monthButton4 = new JButton("1 MONTH");
		monthButton4.setForeground(Color.WHITE);
		monthButton4.setFont(new Font("Tahoma", Font.BOLD, 10));
		monthButton4.setBackground(Color.LIGHT_GRAY);
		monthButton4.setBounds(347, 24, 94, 23);
		monthButton4.setFocusable(false);
		panel.add(monthButton4);
		listFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				//Selected item in jlist returns index number
				indicies = listFiles.getSelectedIndex();
				
				//Search through array with index number selected
				JSONObject selected = (JSONObject) b.get(indicies);
				
				//All File Properties
				title = (String) selected.get("title");
				
				SelectedFile = (String) selected.get("file_name");
				dateCreated = (String) selected.get("date_created");
				dateModified = (String) selected.get("date_modified");
				
				//Format date properly since it's a long number
				Date formated = new Date(Long.parseLong(dateCreated) * 1000);
				Date formated2 = new Date(Long.parseLong(dateModified) * 1000);
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				
				 dateCreated = (df.format(formated));
				 dateModified = (df.format(formated2));
				
				overseeing = (String) selected.get("overseeing");
				notes = (String) selected.get("notes");
				
				//Right click open pop up
				if (SwingUtilities.isRightMouseButton(e) && listFiles.locationToIndex(e.getPoint())==listFiles.getSelectedIndex())
				{
					if(! listFiles.isSelectionEmpty()) {
						
						pop.show(listFiles,e.getX(),e.getY());
						
					}
					
				}
			
				
			}
		});
		listFiles.setFont(new Font("Rockwell", Font.PLAIN, 18));
		
		
		listFiles.setBounds(0, 46, 446, 389);
		listFiles.setModel(DLMFiles);
		listFiles.setSelectionBackground(Color.lightGray);
		panelFiles.add(listFiles);
		
		JLabel patient_pic = new JLabel("");
		patient_pic.setBounds(10, 7, 87, 80);
		frame.getContentPane().add(patient_pic);
		patient_pic.setIcon(new ImageIcon(addFilesPage.class.getResource("/cpsc488_project/PatientPic.png")));
		
		
		JLabel createdLabelAns = new JLabel("");
		createdLabelAns.setFont(new Font("Rockwell", Font.PLAIN, 15));
		createdLabelAns.setBounds(135, 22, 137, 14);
		panelInfo.add(createdLabelAns);
		
		JLabel modifiedLabelAns = new JLabel("");
		modifiedLabelAns.setFont(new Font("Rockwell", Font.PLAIN, 15));
		modifiedLabelAns.setBounds(135, 48, 137, 14);
		panelInfo.add(modifiedLabelAns);
		
		JLabel dobLabelAns = new JLabel("");
		dobLabelAns.setFont(new Font("Rockwell", Font.PLAIN, 15));
		dobLabelAns.setBounds(135, 73, 137, 14);
		panelInfo.add(dobLabelAns);
		
		
		//Fill in metadata for view info page
				sexLabelAns.setText(patientDirectory.sexSelected.toUpperCase());
				weightLabelAns.setText(patientDirectory.weightSelected);
				//heightLabelAns.setText(patientDirectory.heightSelected);
				heightLabelAns.setText(patientDirectory.newHieght);
				ethnicityLabelAns.setText(patientDirectory.ethnicitySelected.toUpperCase());
				dobLabelAns.setText(patientDirectory.dobSelected);
				modifiedLabelAns.setText(patientDirectory.dateModifiedSelected);
				createdLabelAns.setText(patientDirectory.dateCreatedSelected);
				
				//Background image
				JLabel picLabel = new JLabel("");
				//Source Image
				//https://www.istockphoto.com/illustrations/persons-with-disabilities
				picLabel.setIcon(new ImageIcon(viewFilePage.class.getResource("/cpsc488_project/viewbackground.jpg")));
				picLabel.setBounds(0, 227, 442, 197);
				panelInfo.add(picLabel);
				
				
				
				
				
				JLabel backgroundLabel = new JLabel("");
				backgroundLabel.setIcon(new ImageIcon(viewFilePage.class.getResource("/cpsc488_project/bluebackground.jpg")));
				backgroundLabel.setBounds(0, 0, 449, 131);
				frame.getContentPane().add(backgroundLabel);
		
				
				//When clicked filter by date's by simply changing filter number and rerunning the script
				allButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						filterNum = 0;
						
						CmdFiles cmd = new CmdFiles();
						try {
							//Run Command Prompt
							cmd.fileNames();
							DLMFiles.clear();
							bindData();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
						e1.printStackTrace();
						}
						///////////////////////////////////////////
						
					}
				});
				
				monthButton1.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						filterNum = 1;
						
						CmdFiles cmd = new CmdFiles();
						try {
							//Run Command Prompt
							cmd.fileNames();
							DLMFiles.clear();
							bindData();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
						e1.printStackTrace();
						}
						///////////////////////////////////////////
						
					}
				});
				
				monthButton2.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						filterNum = 2;
						
						CmdFiles cmd = new CmdFiles();
						try {
							//Run Command Prompt
							cmd.fileNames();
							DLMFiles.clear();
							bindData();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
						e1.printStackTrace();
						}
						///////////////////////////////////////////
						
					}
					
				});
				
				monthButton3.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						filterNum = 3;
						
						CmdFiles cmd = new CmdFiles();
						try {
							//Run Command Prompt
							
							cmd.fileNames();
							DLMFiles.clear();
							bindData();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
						e1.printStackTrace();
						}
						///////////////////////////////////////////
						
					}
				});
				
				monthButton4.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						filterNum = 4;
						
						CmdFiles cmd = new CmdFiles();
						try {
							//Run Command Prompt
							cmd.fileNames();
							DLMFiles.clear();
							bindData();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
						e1.printStackTrace();
						}
						///////////////////////////////////////////
						
					}
				});
			
				
				
				
				
		frame.setBounds(100, 100, 465, 586);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private ArrayList<String> getNames() throws IOException, org.json.simple.parser.ParseException
	{
		ArrayList<String> names =new ArrayList<String>();
		//Search through file_data.json
		FileReader reader = new FileReader("../client/file_data.json");
		JSONParser parser = new JSONParser();
		
		b = (JSONArray) parser.parse(reader);
		//System.out.println(a);
		// https://stackoverflow.com/questions/10926353/how-to-read-json-file-into-java-with-simple-json-library
		String name;
		for (Object o : b) {
		    JSONObject person = (JSONObject) o;
		    
		    //Fill in Jlist with Title's of Files
		    name = (String) person.get("title");
		    fileName = name.substring(0, 1).toUpperCase() + name.substring(1);
	
		    names.add(fileName);
		    
		    
		    //names.add(name);
		}
		
		
		return names;
	}
	
	
	private void addPopup() {
		
		//Image Sources
		//https://iconscout.com/icon/open-folder-2120181
		//https://www.iconfinder.com/icons/115801/settings_icon
		JMenuItem Open =new JMenuItem("Open File", new ImageIcon(patientDirectory.class.getResource("/cpsc488_project/open.png")));
		JMenuItem Properties =new JMenuItem("Properties", new ImageIcon(patientDirectory.class.getResource("/cpsc488_project/properties.png")));
		JMenuItem Delete =new JMenuItem("Delete File", new ImageIcon(patientDirectory.class.getResource("/cpsc488_project/trash.png")));
		
		
		
		//Pop up options
		pop.add(Open);
		pop.add(Properties);
		pop.add(Delete);
		
		
		
		Open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//Open File
				
				DownloadPatientsFile cmd = new DownloadPatientsFile();
				
				try {
					//Run Command Prompt
					cmd.patientDownloadFile();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// Files now need to be unzipped
				
				File directory = new File("../cpsc488_project/src/cpsc488_project");
				System.out.println("found directory");
				
				byte[] buffer = new byte[1024];
				FileInputStream fileInput;
				String[] fileParts = SelectedFile.split("\\.");
				
				
				// https://www.journaldev.com/960/java-unzip-file-example
				try {
					fileInput = new FileInputStream("../client/" + fileParts[0] + ".zip");
					ZipInputStream zipInput = new ZipInputStream(fileInput);
					ZipEntry zipEntry = zipInput.getNextEntry();
					while (zipEntry != null) {
						String zipFileName = zipEntry.getName();
						File newFile = new File("../cpsc488_project/src/cpsc488_project" + File.separator + zipFileName);
						System.out.println("Unzipping to " + newFile.getAbsolutePath());
						
						new File(newFile.getParent()).mkdirs();
						FileOutputStream fileOutput = new FileOutputStream(newFile);
						int x;
						while((x = zipInput.read(buffer)) > 0) {
							fileOutput.write(buffer, 0, x);
						}
						fileOutput.close();
						zipInput.closeEntry();
						zipEntry = zipInput.getNextEntry();
					}
					zipInput.closeEntry();
					zipInput.close();
					fileInput.close();
				} catch(Exception e1) {
					e1.printStackTrace();
				}
				
				OpenImageFile image = new OpenImageFile();
				try {
					image.openImage();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		Properties.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//Create New Popup Frame
				JFrame p;
				p= new JFrame();
				
				//Popup with Properties
				JOptionPane.showMessageDialog(p,"Title: " + title +"\n" +"Date Created: " + dateCreated + "\n" + "Date Modified: "+dateModified + "\n" + "Overseeing: " +overseeing + "\n" + "Notes: " + notes);
				
			}
			
		});
		Delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CmdPatientsDeleteFile cmd = new CmdPatientsDeleteFile();
				CmdFiles cmd2 = new CmdFiles();
				try {
					//Run Command Prompt
					cmd.patientNameDeleteFile();
					DLMFiles.clear();
					cmd2.fileNames();
					bindData();
					 //System.out.println();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
		});
	
	
	
	
	
	}
}