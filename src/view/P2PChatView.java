package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.TimerTask;

import javax.security.auth.callback.LanguageCallback;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import networkManage.DataModel;
import networkManage.MessageManipulator;



public class P2PChatView extends JFrame{
	
	private JPanel chatArea = new JPanel();
	private JList<String> chatMessagelist = null;
	private ChatMessageModel chatMessageModel = null;
	private JButton submitButton = null;
	private JTextArea messageTosend = new JTextArea(4,20);
	private Socket socket;
	private long LastBeatTime = 0;
	private P2PChatView self=null;
	private JButton filetransButton = null;
	private String uname = "";
	public P2PChatView(Socket st) throws Exception{
		socket = st;
		initUserInterface();
		self = this;
		self.setVisible(true);
		uname = DataModel.getInstance().getUserName();
		self.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		this.setLocationRelativeTo(null);
		this.setSize(700,400);
		this.setResizable(true);
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
				new FileTranSform();
			}
		});
		this.add(filetransButton,BorderLayout.EAST);
	}
	class FileTranSform {
		public FileTranSform() {
			// TODO Auto-generated constructor stub
		}
	}
	public void sendmessage(String data) throws UnsupportedEncodingException, IOException {
		String string = "P2P1.0 " + "P2PMESSAGE " + uname + "\r\n";
		string += "Date " + getTime() + "\r\n";
		string += "Content-Type " +"text/html; charset=ISO-8859-1\r\n\r\n";
		string += data + "\r\n";
		socket.getOutputStream().write(string.getBytes("utf-8"));
	}
	
	public void sendleavemessage() throws UnsupportedEncodingException, IOException {
		String string = "P2P1.0 " + "LEAVE " + uname + "\r\n";
		string += "Date " + getTime() + "\r\n";
		string += "\r\n\r\n";
		socket.getOutputStream().write(string.getBytes("utf-8"));
	}
	public static String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String nowDate = format.format(date);
		return nowDate;
	}
	public class Beat extends TimerTask{

		@Override
		public void run() {
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

	/*
	public void SendP2PMessage(String message) throws Exception{
		try{
			LinkedHashMap<String,String> headRequest =new LinkedHashMap<String,String>();
			LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
			LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
			headRequest.put("P2PVersion", "P2P1.0");
			headRequest.put("MessageType","P2PMESSAGE");
			headRequest.put("UserName",DataModel.getInstance().getUserName());
			headline.put("Content-length", "0");
			headline.put("Time", (new java.util.Date()).toString());
			bodyline.put("P2PMessage",message);
			LinkedHashMap<String,LinkedHashMap<String,String> > msgMap = new LinkedHashMap<String,LinkedHashMap<String,String> >();
			msgMap.put("requestline", headRequest);
			msgMap.put("headline", headline);
			msgMap.put("body", bodyline);
			
			String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
			socket.getOutputStream().write(msg.getBytes("UTF-8"));
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	*/
	/*
	public void HandleLogout() throws Exception{
		try{
			LinkedHashMap<String,String> headRequest =new LinkedHashMap<String,String>();
			LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
			LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
			headRequest.put("P2PVersion", "P2P1.0");
			headRequest.put("MessageType","LEAVE");
			headRequest.put("UserName",DataModel.getInstance().getUserName());
			headline.put("Content-length", "0");
			headline.put("Time", (new java.util.Date()).toString());
			LinkedHashMap<String,LinkedHashMap<String,String> > msgMap = new LinkedHashMap<String,LinkedHashMap<String,String> >();
			msgMap.put("requestline", headRequest);
			msgMap.put("headline", headline);
			msgMap.put("body", bodyline);
			String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
			socket.getOutputStream().write(msg.getBytes("UTF-8"));
			socket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	*/
	class SendBeatMessageThread extends Thread{
		public SendBeatMessageThread(){
			    start();
		}
		public void run(){
			try{
				while(true){
					Timer timer = new Timer();
					timer.schedule(new Beat(), 0, 10000);
					/*
					sleep(10000);
					LinkedHashMap<String,String> headRequest =new LinkedHashMap<String,String>();
					LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
					LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
					headRequest.put("P2PVersion", "P2P1.0");
					headRequest.put("MessageType","BEAT");
					headRequest.put("UserName",DataModel.getInstance().getUserName());
					headline.put("Content-length", "0");
					headline.put("Time", (new java.util.Date()).toString());
					LinkedHashMap<String,LinkedHashMap<String,String> > msgMap = new LinkedHashMap<String,LinkedHashMap<String,String> >();
					msgMap.put("requestline", headRequest);
					msgMap.put("headline", headline);
					msgMap.put("body", bodyline);
					//String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
					//out.write(msg.getBytes("UTF-8"));
					*/
				}
			}catch (Exception e){
				
			}
			
		
		}
	}
	public void initMessageArea(){
		//chatArea.setLayout(new BorderLayout());
		chatMessageModel = new ChatMessageModel();
		chatMessagelist = new JList<String>(chatMessageModel);
		chatArea.add(new JScrollPane(chatMessagelist));
		messageTosend.setLineWrap(true);
		messageTosend.setAutoscrolls(true);
		messageTosend.setRows(4);
		
		chatArea.add(messageTosend);
		submitButton = new JButton();
		submitButton.setText("发送");
		submitButton.setSize(5, 10);
		submitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(messageTosend.getText().length() !=0){
					try {
						sendmessage(messageTosend.getText());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		chatArea.add(submitButton);
		this.setResizable(true);
	}
	class HandleBeatThread extends Thread{
		public HandleBeatThread(){
			start();
		}
		public void run(){
			while(true){
				long curMil = java.lang.System.currentTimeMillis();
				if(curMil - LastBeatTime >30000){
					JOptionPane.showMessageDialog(null, "Failed to connect to the other size");
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
							}else if (MsgType.equals("FILETRANSFORM")) {
								//TODO:
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
			String msgDetail = bodyline.get("Entity");
			
			if(true){
				msgCell += username+"\n";
				msgCell += time +"\n";
				msgCell += msgDetail;
			}
			chatMessageModel.addElement(msgCell);
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
