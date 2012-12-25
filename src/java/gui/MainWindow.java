package gui;

import httpclient.AutoVote;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainWindow {
	
	private JTextField txtVoteUrl;
	private ButtonGroup btgRequestType;
	private JRadioButton rbtnGet, rbtnPost;
	private JTextField txtProxy, txtPort;
	private JTextField txtVoteNum, txtThreadNum;
	private Map<String, String> keyValuePair = new HashMap<String, String>();
	private JTextField txtKey, txtValue;
	private JTextField txtSleepTime;
	private JButton btnRun;
	private JTextArea taLog;
	
	private AutoVote autoVote;

	public void init(){
		JFrame myFrame = new JFrame();
		myFrame.setTitle("my vote program");
		myFrame.setSize(600, 400);
		
		JPanel myPanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        myPanel.setLayout(gridbag);
        
        c.fill = GridBagConstraints.BOTH;

		
		JLabel lbUrl = new JLabel("url:");
		c.weightx = 2.0;
		c.weighty = 1.0;
		gridbag.setConstraints(lbUrl, c);
		myPanel.add(lbUrl);
		
		txtVoteUrl = new JTextField();
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(txtVoteUrl, c);
		myPanel.add(txtVoteUrl);
		
		c.gridwidth = 1;
		
		JLabel lbType = new JLabel("RequestType:");
		gridbag.setConstraints(lbType, c);
		myPanel.add(lbType);
		
		rbtnGet = new JRadioButton("Get", true);
		gridbag.setConstraints(rbtnGet, c);
		myPanel.add(rbtnGet);
		
		rbtnPost = new JRadioButton("Post", false);
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(rbtnPost, c);
		myPanel.add(rbtnPost);
		
		btgRequestType = new ButtonGroup();
		btgRequestType.add(rbtnGet);
		btgRequestType.add(rbtnPost);
		
		
		c.gridwidth = 1;
		
		JLabel lbProxy = new JLabel("Proxy:");
		txtProxy = new JTextField();
		JLabel lbPort = new JLabel("Port:");
		txtPort = new JTextField();
		gridbag.setConstraints(lbProxy, c);
		myPanel.add(lbProxy);
		gridbag.setConstraints(txtProxy, c);
		myPanel.add(txtProxy);
		gridbag.setConstraints(lbPort, c);
		myPanel.add(lbPort);
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(txtPort, c);
		myPanel.add(txtPort);
		
		c.gridwidth = 1;
		
		JLabel lbVoteNum = new JLabel("Vote Number:");
		txtVoteNum = new JTextField();
		JLabel lbThreadNum = new JLabel("Thread Number:");
		txtThreadNum = new JTextField();
		gridbag.setConstraints(lbVoteNum, c);
		myPanel.add(lbVoteNum);
		
		gridbag.setConstraints(txtVoteNum, c);
		myPanel.add(txtVoteNum);
		
		gridbag.setConstraints(lbThreadNum, c);
		myPanel.add(lbThreadNum);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(txtThreadNum, c);
		myPanel.add(txtThreadNum);
		
		c.gridwidth = 1;
		
		JLabel lbKey = new JLabel("Key:");
		txtKey = new JTextField();
		JLabel lbValue = new JLabel("Value:");
		txtValue = new JTextField();
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				if(txtKey.getText()==null||txtKey.getText().trim().equals("") || txtValue.getText()==null||txtValue.getText().trim().equals("")){
					taLog.append("error:key value can't be null\r\n");
				}
				else{
					keyValuePair.put(txtKey.getText(), txtValue.getText());
					taLog.append("add keyValuePair: key=" + txtKey.getText() + " value=" + txtValue.getText() + "\r\n");
					txtKey.setText(null);
					txtValue.setText(null);
				}
			}
		});
		
		gridbag.setConstraints(lbKey, c);
		myPanel.add(lbKey);
		
		gridbag.setConstraints(txtKey, c);
		myPanel.add(txtKey);

		gridbag.setConstraints(lbValue, c);
		myPanel.add(lbValue);

		gridbag.setConstraints(txtValue, c);
		myPanel.add(txtValue);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(btnAdd, c);
		myPanel.add(btnAdd);
		
		c.gridwidth = 1;
		
		JLabel lbSleepTime = new JLabel("Sleep Time:");
		txtSleepTime = new JTextField();

		gridbag.setConstraints(lbSleepTime, c);
		myPanel.add(lbSleepTime);

		gridbag.setConstraints(txtSleepTime, c);
		myPanel.add(txtSleepTime);
		
		btnRun = new JButton("Run");

		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(btnRun, c);
		myPanel.add(btnRun);
		
		
		taLog = new JTextArea(5, 0);
		taLog.setLineWrap(true);
		
		//把定义的JTextArea放到JScrollPane里面去
		JScrollPane scroll = new JScrollPane(taLog);

		//分别设置水平和垂直滚动条自动出现
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		
		gridbag.setConstraints(scroll, c);
		myPanel.add(scroll);
		
		Container contentPane = myFrame.getContentPane();
		contentPane.add(myPanel);
		myFrame.setVisible(true);
		
		myFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(null, "exit?") == JOptionPane.OK_OPTION){
					System.exit(0);
				}
			}
		});
		addEvent();
	}
	
	public void addEvent(){
		btnRun.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				if(setValue()){
					start();
				}
			}
		});
	}
	
	public boolean setValue(){
		
		if(txtVoteUrl.getText() != null && !txtVoteUrl.getText().trim().equals("")){
			autoVote.setVoteUrl(txtVoteUrl.getText());
		}else {
			showError("vote url can't be null");
			return false;
		}
		if(rbtnGet.isSelected()){
			autoVote.setRequestType(true);
		}else if(rbtnPost.isSelected()){
			autoVote.setRequestType(false);
		}else {
			showError("request type can't be null");
			return false;
		}
		if(txtProxy.getText() != null && !txtProxy.getText().trim().equals("")){
			autoVote.setProxy(txtProxy.getText());
		}else {
//			showError();
//			return false;
		}
		if(txtPort.getText() != null && !txtPort.getText().trim().equals("")){
			autoVote.setPort(Integer.parseInt(txtPort.getText()));
		}else {
//			showError();
//			return false;
		}
		if(txtVoteNum.getText() != null && !txtVoteNum.getText().trim().equals("")){
			autoVote.setVoteNum(Integer.parseInt(txtVoteNum.getText()));
		}else {
//			showError();
//			return false;
		}
		if(txtThreadNum.getText() != null && !txtThreadNum.getText().trim().equals("")){
			autoVote.setThreadNum(Integer.parseInt(txtThreadNum.getText()));
		}else{
//			showError();
//			return false;
		}
		if(!keyValuePair.isEmpty()){
			autoVote.setNameValuePair(keyValuePair);
		}else {
//			showError();
//			return false;
		}
		if(txtSleepTime.getText() != null && !txtSleepTime.getText().trim().equals("")){
			autoVote.setSleepTime(Integer.parseInt(txtSleepTime.getText()));
		}else{
//			showError();
//			return false;
		}
		return true;
	}
	
	public void showError(String message){
		JOptionPane.showMessageDialog(null, message, "error", JOptionPane.ERROR_MESSAGE);
	}
	
	public void start(){
		autoVote.start();
	}
	
	
	
	public MainWindow() {
		super();
		autoVote = new AutoVote();
	}

	public static void main(String[] args) {
		MainWindow window = new MainWindow();
		window.init();
		StringBuffer runLog = window.autoVote.getLog();
		while(true){
			window.taLog.append(runLog.toString());
			runLog.delete(0, runLog.length());
			window.taLog.repaint();
			if(window.autoVote.isFinished()){
				window.taLog.append("finished");
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
