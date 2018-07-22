import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.osbot.rs07.script.Script;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


public class GuiClass {
	public Boolean guiActive = true;
	private String hostString;
	private String tabString;
	
	public void launchGui(MainClass main){
		//Initialize all variables for GUI
		JTextField hostNames = new JTextField();
		String[] tabChoices = new String[]{"House","Lumbridge","Varrock","Falador","Camelot","Ardougne","Watchtower","Bones to Peaches","Bones to Bananas",};
		JComboBox<String> tabSelection = new JComboBox<String>(tabChoices);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		JFrame frame = new JFrame("Azubu Smart Tabber");
		JPanel contentPanel = new JPanel();
		JLabel hostLabel = new JLabel("Use other's houses?: ");
		JLabel tabLabel = new JLabel("Tab type: "); 
		JLabel userName = new JLabel("Host's name (comas(,) between names): ");
		JRadioButton hostStateYes = new JRadioButton("Yes");
		JRadioButton hostStateNo = new JRadioButton("No");
		ButtonGroup hostButtonGroup = new ButtonGroup();
		JButton startButton = new JButton("Start");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//Done initalizing variables
		
		//Initalizing JPanel configurations
		contentPanel.setOpaque(true);
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setLayout(null);
		hostButtonGroup.add(hostStateYes);
		hostButtonGroup.add(hostStateNo);
		//Done JPanel configurations
		
		//Placing objects in JPanel
		tabLabel.setSize(150,20);
		tabLabel.setLocation(10,10);	
		hostLabel.setSize(150,20);
		hostLabel.setLocation(10,40);
		tabSelection.setSize(150,20);
		tabSelection.setLocation(75, 10);
		hostStateYes.setSize(50,20);
		hostStateNo.setSize(50,20);	
		hostStateYes.setLocation(140,40);
		hostStateNo.setLocation(190,40);
		userName.setSize(240,50);
		userName.setLocation(10, 45);
		hostNames.setSize(220, 20);
		hostNames.setLocation(10, 85);
		startButton.setSize(70,20);
		startButton.setLocation(90, 115);
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		//Done placing objects in JPanel
		
		//Adding items to appear in JFrame
		contentPanel.add(tabSelection);
		contentPanel.add(tabLabel);
		contentPanel.add(hostLabel);
		contentPanel.add(hostStateYes);
		contentPanel.add(hostStateNo);
		contentPanel.add(userName);
		contentPanel.add(hostNames);
		contentPanel.add(startButton);
		frame.setContentPane(contentPanel);
		frame.setSize(250, 170);
		frame.setResizable(false);
		frame.setVisible(true);
		//Done placing items in frame
		
		hostStateYes.setSelected(true);//setting default option to use host houses
		
		//Setting button listeners
		hostStateNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hostNames.setEditable(false);
				hostNames.setText(null);
				hostNames.setBackground(Color.lightGray);
			}
		});
		
		hostStateYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hostNames.setEditable(true);
				hostNames.setText(null);
				hostNames.setBackground(null);
			}
		});
		
		startButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        frame.dispose();
		        frame.setVisible(false);
		        tabString = tabSelection.getItemAt(tabSelection.getSelectedIndex());
		        guiActive = false;
		        hostString = hostNames.getText();
		        //System.out.println(hostNames.getText());
		        //System.out.println(tabSelection.getItemAt(tabSelection.getSelectedIndex()));
			}
		});
	}
	
	public String getTabString() {
		return this.tabString;
	}
	public String getHostString() {
		return this.hostString;
	}
	public Boolean isActive() {
		return guiActive;
	}

}
