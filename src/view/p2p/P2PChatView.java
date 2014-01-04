package view.p2p;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileNameExtensionFilter;

import networkManage.*;
import view.chatroom.*;
public class P2PChatView extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel chatArea = new JPanel();
	private JList<String> chatMessagelist = null;
	private ChatMessageModel chatMessageModel = null;
	private JButton submitButton = null;
	private JTextArea messageTosend = new JTextArea(4,20);
	private Socket socket;
	private long LastBeatTime = 0;
	private JFrame self=new JFrame();
	private JButton filetransButton = null;
	private String uname = "";
	private String savePath ="";
	private String sendPath = "";
	private FileSender sender = null;
	private boolean isAlive = true;
	JScrollPane jScrollPane = null;
	JScrollBar jsb = null;
	public P2PChatView(Socket st,String pname) throws Exception{
		socket = st;
	
		Toolkit tk=Toolkit.getDefaultToolkit();
		Image image=tk.createImage("Minet_LOGO.png"); /*image.gif是你的图标*/
		setIconImage(image);
	
		initUserInterface();
		self.setVisible(true);
		uname = DataModel.getInstance().getUserName();
		self.setTitle(pname);
		self.addWindowListener(new WindowAdapter(){
	     	 public void windowClosing(WindowEvent e){
		     	   System.out.println("You join P2P chatroom");
		     	   try {
		     		  sendleavemessage();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	     	 }
	     });
	}
	
	public void initUserInterface(){
		self.setLocationRelativeTo(null);
		self.setSize(500,400);
		self.setResizable(true);
		initMessageArea();
		initFileTransforArea();
		LastBeatTime = java.lang.System.currentTimeMillis();
		new GetP2PMessageThread();
		new SendBeatMessageThread();
		new HandleBeatThread();
	}
	public void initFileTransforArea(){
		filetransButton = new JButton("文件传输");
		filetransButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(FileTransform()){
					JOptionPane.showMessageDialog(null, "等待对方确认");
				}
			}
		});
		self.getContentPane().add(filetransButton,BorderLayout.EAST);
	}
	public boolean FileTransform(){
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("选择文件:");
		fc.setVisible(true);
		int result = fc.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION){
			sendPath = fc.getSelectedFile().getAbsolutePath();
			sender = new FileSender(sendPath);
			int port = sender.getSocketPort();
			if(port ==0) return false;
			String string = "P2P1.0 " + "P2PMESSAGE " + uname + "\r\n";
			string += "Date " + getTime() + "\r\n";
			string += "Port " + String.valueOf(port) + "\r\n";
			string += "Content-Type " +"FILEREQUEST\r\n\r\n";
			string += "\r\n";
			string += "\r\n";
			try {
				socket.getOutputStream().write(string.getBytes("utf-8"));
				return true;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	public boolean sendmessage(String data) {
		String msgTosend = data;
		while(msgTosend.endsWith("\n")){
			msgTosend = msgTosend.substring(0, msgTosend.lastIndexOf('\n'));
		}
		if(msgTosend.length() ==0){
			messageTosend.setText(null);
			return false;
		}
		String string = "P2P1.0 " + "P2PMESSAGE " + uname + "\r\n";
		string += "Date " + getTime() + "\r\n";
		string += "Content-Type " +"text/html; charset=ISO-8859-1\r\n\r\n";
		string += data + "\r\n";
		string += "\r\n";
		try {
			socket.getOutputStream().write(string.getBytes("utf-8"));
			return true;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void sendleavemessage(){
		try{
			String string = "P2P1.0 " + "LEAVE " + uname + "\r\n";
			string += "Date " + getTime() + "\r\n";
			string += "\r\n\r\n";
			socket.getOutputStream().write(string.getBytes("utf-8"));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public static String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String nowDate = format.format(date);
		return nowDate;
	}
	public class Beat{

		public Beat(){
			String string = "P2P1.0 " + "BEAT " + uname + "\r\n";
			string += "Date " + getTime() + "\r\n";
			string += "\r\n\r\n";
			try {
				socket.getOutputStream().write(string.getBytes("utf-8"));
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	class SendBeatMessageThread extends Thread{
		public SendBeatMessageThread(){
			    start();
		}
		public void run(){
			try{
				while(true){
					new Beat();
					sleep(10000);
				}
			}catch (Exception e){
				
			}
			
		
		}
	}
	public void initMessageArea(){
		chatArea.setLayout(new BorderLayout());
		chatMessageModel = new ChatMessageModel();
		chatMessagelist = new JList<String>(chatMessageModel);
		chatMessagelist.setFixedCellWidth(330);
		chatMessagelist.setAutoscrolls(true);
		
		chatMessagelist.setCellRenderer(new ChatMessageRender());
		chatMessagelist.setFixedCellHeight(-1);
		jScrollPane = new JScrollPane(chatMessagelist);
		chatArea.add(jScrollPane,BorderLayout.NORTH);
		
		messageTosend.setLineWrap(true);
		messageTosend.setAutoscrolls(true);
		messageTosend.setRows(4);
		messageTosend.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyChar() =='\n'){
					if(messageTosend.getText().length() !=0){
						try {
							if(sendmessage(messageTosend.getText())){
								String msgCell ="";
								if(true){
									msgCell += "我 ";
									msgCell += getTime() +"\n";
									msgCell += messageTosend.getText();
									messageTosend.setText("");
								}
								chatMessageModel.addElement(msgCell);
							}
								
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
				
			}
		});
		chatMessageModel.addElement("Welcome to MINET!");
		chatArea.add(messageTosend,BorderLayout.CENTER);
		submitButton = new JButton();
		submitButton.setText("发送");
		submitButton.setSize(5, 10);
		submitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(messageTosend.getText().length() !=0){
					try {
						if(sendmessage(messageTosend.getText())){
							String msgCell ="";
							if(true){
								msgCell += "我 ";
								msgCell += getTime() +"\n";
								msgCell += messageTosend.getText();
								messageTosend.setText("");
							}
							chatMessageModel.addElement(msgCell);
						}
							
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		chatArea.add(submitButton,BorderLayout.SOUTH);
		self.getContentPane().add(chatArea,BorderLayout.CENTER);
	}
	class HandleBeatThread extends Thread{
		public HandleBeatThread(){
			start();
		}
		public void run(){
			while(true){
				long curMil = java.lang.System.currentTimeMillis();
				if(curMil - LastBeatTime >30000){
					if(isAlive){
						JOptionPane.showMessageDialog(null, "Failed to connect to the other side");
					}
					if(curMil - LastBeatTime >180000){
						isAlive = false;
					}
				}
				try {
					sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	class GetP2PMessageThread extends Thread{
		BufferedReader buff;

		public GetP2PMessageThread(){
			try{
				buff = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				start();
			}catch (Exception e){
				
			}
			
		}
		public void run(){
				try{
					while(true){
						LinkedHashMap<String,String> headrequest = MessageManipulator.getInstance().getHeadRequest(buff);
						String CSVersion = headrequest.get("0");
						String MsgType = headrequest.get("1");
						String usernameorstatus = headrequest.get("2");
						String usernameornull = headrequest.get("3");
						if(CSVersion.equals("P2P1.0")){
							if(MsgType.equals("P2PMESSAGE")){
								HandleP2PMessage(usernameorstatus);
							}else if(MsgType.equals("BEAT")){
								HandleLastBeat();
							}else if(MsgType.equals("LEAVE")){
								JOptionPane.showMessageDialog(null, "He is going to leave!");
								sleep(5000);
								self.setVisible(false);
							}
						}
						
					}
					
				}catch (Exception e){
					
				}
		}
		public void HandleLastBeat(){
			long time = java.lang.System.currentTimeMillis();
			if(time > LastBeatTime){
				LastBeatTime = time;
			}
		}
		public void HandleP2PMessage(String username) throws IOException{
			String msgCell = "";
			LinkedHashMap<String,String> headline = MessageManipulator.getInstance().getHeadLine(buff);
			LinkedHashMap<String,String> bodyline = MessageManipulator.getInstance().getEntityMessage(buff);
			
			String time = headline.get("Date");
			String type = headline.get("Content-Type");
			String msgDetail = bodyline.get("Entity");
			if(type.equals("FILEREQUEST")){
				int n = JOptionPane.showConfirmDialog(null,username+"请求发送文件给你,接收吗？","Attention", JOptionPane.YES_NO_OPTION);
				if(n !=0){
					sendRefuseMessage(msgDetail);
					// refuse to receive file;
				}else {
					// admit to receive file;
					String port = headline.get("Port");
					JFileChooser fChooser = new JFileChooser();
					fChooser.setDialogTitle("保存到：");
					fChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int result = fChooser.showSaveDialog(null);
					if(result == JFileChooser.APPROVE_OPTION){
						savePath = fChooser.getSelectedFile().getAbsolutePath();
						new FileReceiver(savePath,socket.getInetAddress().toString().substring(1),Integer.valueOf(port));
					}else{
						sendRefuseMessage(msgDetail);
					}
				}
			}
			else if(type.equals("FILEREFUSE")){
				JOptionPane.showMessageDialog(null, "对方拒绝接受文件");
				if(sender != null){
					sender.close();
				}
			}
			else {
				msgCell += username+" ";
				msgCell += time +"\n";
				msgCell += msgDetail;
				chatMessageModel.addElement(msgCell);
				jsb = jScrollPane.getVerticalScrollBar();
				jsb.setValue(jsb.getMaximum());
			}
		}
		public boolean sendRefuseMessage(String filename){
			String savedPath;
			String string = "P2P1.0 " + "P2PMESSAGE " + uname + "\r\n";
			string += "Date " + getTime() + "\r\n";
			string += "Content-Type " +"FILEREFUSE\r\n\r\n";
			string += filename+"\r\n";
			string += "\r\n";
			try {
				socket.getOutputStream().write(string.getBytes("utf-8"));
				return true;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
	}
	class OnlineUserCellRenderer extends JLabel implements ListCellRenderer {
		public OnlineUserCellRenderer() {  
			setOpaque(true);
		}
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (value != null) {
				setText(value.toString());
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				// 设置选取与取消选取的前景与背景颜色.
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			return this;
		}
		
	}
	class ChatMessageModel extends DefaultListModel{
		
		private static final long serialVersionUID = 1L;

		public ChatMessageModel(){
			
		}
	}
}
