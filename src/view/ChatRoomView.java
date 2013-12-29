package view;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.*;

import networkManage.*;

public class ChatRoomView extends MouseAdapter {
	private JFrame chatRoom= new JFrame("Chat Room");
	private JPanel onlineUserArea = new JPanel();
	private JPanel chatArea = new JPanel();
	private OnlineUserModel  onlineUserModel = null;
	private JButton updateOnlineUsersButton = new JButton("刷新");
	private JList<String> onlinelist = null;
	private JList<String> chatMessagelist = null;
	private ChatMessageModel chatMessageModel = null;
	private JButton submitButton = null;
	private JTextArea messageTosend = new JTextArea(4,20);
	
	private  ChatRoomManage chatRoomManager = null;
	private Socket socket;
	private BufferedReader in;
	private OutputStream outCS;
	
	public ChatRoomView(Socket sk){
		initChatRoom();
		socket = sk;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			outCS = sk.getOutputStream();
			try {
				chatRoomManager = new ChatRoomManage();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setVisible(true);
			System.out.println("You join the chatroom");
			chatRoom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		chatRoom.addWindowListener(new WindowAdapter(){
	     	 public void windowClosing(WindowEvent e){
	     	   System.out.println("You leave the chatroom");
	     	   try {
				chatRoomManager.HandleLogout();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	     	   System.exit(0);	
	     	 }
	     });
	}

	public void setVisible(boolean label){
		chatRoom.setVisible(label);
	}
	
	public void initChatRoom(){
		chatRoom.setLocationRelativeTo(null);
		chatRoom.setSize(500,400);
		chatRoom.setResizable(true);
		initMessageArea();
		initOnlineUserArea();
		chatRoom.getContentPane().add(chatArea,BorderLayout.CENTER);
		chatRoom.getContentPane().add(onlineUserArea,BorderLayout.EAST);
		chatRoom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	}
	public void initMessageArea(){
		chatArea.setLayout(new BorderLayout());
		chatMessageModel = new ChatMessageModel();
		chatMessagelist = new JList<String>(chatMessageModel);
		
		chatArea.add(new JScrollPane(chatMessagelist),BorderLayout.NORTH);
		
		messageTosend.setLineWrap(true);
		messageTosend.setAutoscrolls(true);
		messageTosend.setRows(4);

		chatArea.add(messageTosend,BorderLayout.CENTER);
		submitButton = new JButton();
		submitButton.setText("发送");
		submitButton.setSize(5, 10);
		submitButton.addMouseListener(this);
		chatArea.add(submitButton,BorderLayout.SOUTH);
	}
	public void initOnlineUserArea(){
		onlineUserModel = new OnlineUserModel();
		onlinelist = new JList<String>(onlineUserModel);
		onlinelist.addMouseListener(this);
		onlinelist.setBorder(BorderFactory.createTitledBorder("在线用户"));
		updateOnlineUsersButton.addMouseListener(this);
		onlineUserArea.add(updateOnlineUsersButton);
		onlineUserArea.add(new JScrollPane(onlinelist));
	}
	// 处理鼠标键击事件.
	public void mouseClicked(MouseEvent e) {
		int index;
			
		if (e.getSource() == onlinelist) {
			if (e.getClickCount() == 2) {
				index = onlinelist.locationToIndex(e.getPoint());
				try {
				//	new P2PAskSocket(DataModel.getInstance().getOnlineUser(index));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else if (e.getSource() == submitButton) {
			if (e.getClickCount() == 1) {
				if (chatRoomManager.SendCSMessage(messageTosend.getText()) ){
					messageTosend.setText(null);
				}else{
					//TODO:handle failure
				}
				
			}
		}else if(e.getSource() == updateOnlineUsersButton){
			chatRoomManager.getOnlineList();
		}
		
	}
	/*
	public class P2PAskSocket extends Socket{
		Socket p2psocket;
		BufferedReader in;
		OutputStream out;
		public P2PAskSocket(ArrayList<String> oneUser) throws Exception{
			String username = oneUser.get(0);
			String ipAddr = oneUser.get(1);
			int port = Integer.valueOf(oneUser.get(2));
			p2psocket = new Socket(ipAddr,port);
			p2psocket.setSoTimeout(600000);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			out = p2psocket.getOutputStream();
			sendAskMessage(username);
		}
		public void sendAskMessage(String username) throws Exception{
			try {
				MessageManipulator.getInstance().shakeHand(in, out, "MINET");
				LinkedHashMap<String,String> headRequest =new LinkedHashMap<String,String>();
				LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
				LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
				headRequest.put("P2PVersion", "P2P1.0");
				headRequest.put("MessageType","P2PREQUEST");
				headRequest.put("UserName",DataModel.getInstance().getUserName());
				headline.put("Content-length", "0");
				headline.put("Time", (new java.util.Date()).toString());
				LinkedHashMap<String,LinkedHashMap<String,String> > msgMap = new LinkedHashMap<String,LinkedHashMap<String,String> >();
				msgMap.put("requestline", headRequest);
				msgMap.put("headline", headline);
				msgMap.put("body", bodyline);
				String responseMsg = MessageManipulator.getInstance().formatAMessage(msgMap);
				out.write(responseMsg.getBytes("UTF-8"));
				if(HandleResult()){
					(new P2PChatView(socket)).setTitle(username);
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Failed to connect");
			}
		}
		public boolean HandleResult() {
			JOptionPane.showMessageDialog(null, "Connecting...");
			try {
				if( (in.readLine()).length() >0 ){
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Failed to connect");
				return false;
				
			}
				
			return false;
		}
	}
	*/
	public class ChatRoomManage{
		
		public ChatRoomManage() throws Exception{
			new GetChatRoomMessageThread();
			new SendBeatMessageThread();
			getOnlineList();
			
		}
		
		public void getOnlineList(){
			try{
				LinkedHashMap<String,String> headRequest =new LinkedHashMap<String,String>();
				LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
				LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
				headRequest.put("CSVersion", "CS1.0");
				headRequest.put("MessageType","GETLIST");
				headline.put("Content-length", "0");
				headline.put("Time", (new java.util.Date()).toString());
				LinkedHashMap<String,LinkedHashMap<String,String> > msgMap = new LinkedHashMap<String,LinkedHashMap<String,String> >();
				msgMap.put("requestline", headRequest);
				msgMap.put("headline", headline);
				msgMap.put("body", bodyline);
				String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
				outCS.write(msg.getBytes("UTF-8"));
				
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		public boolean SendCSMessage(String msgTosend){
			try{
				msgTosend.replace("\r", "");
				msgTosend.replace("\n", "");
				if(msgTosend.length() ==0){
					return false;
				}
				
				LinkedHashMap<String,String> headRequest =new LinkedHashMap<String,String>();
				LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
				LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
				headRequest.put("CSVersion", "CS1.0");
				headRequest.put("MessageType","MESSAGE");
				headRequest.put("UserName",DataModel.getInstance().getUserName());
				headline.put("Content-length", String.valueOf(msgTosend.length()) );
				headline.put("Time", (new java.util.Date()).toString());
				bodyline.put("bodyline", msgTosend);
				LinkedHashMap<String,LinkedHashMap<String,String> > msgMap = new LinkedHashMap<String,LinkedHashMap<String,String> >();
				msgMap.put("requestline", headRequest);
				msgMap.put("headline", headline);
				msgMap.put("body", bodyline);
				String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
				outCS.write(msg.getBytes("UTF-8"));
				
				return true;
			}catch (Exception e){
				
			}
			return false;
		}
		public void HandleLogout() throws Exception{
			try{
				LinkedHashMap<String,String> headRequest =new LinkedHashMap<String,String>();
				LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
				LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
				headRequest.put("CSVersion", "CS1.0");
				headRequest.put("MessageType","LEAVE");
				headRequest.put("UserName",DataModel.getInstance().getUserName());
				headline.put("Content-length", "0");
				headline.put("Time", (new java.util.Date()).toString());
				LinkedHashMap<String,LinkedHashMap<String,String> > msgMap = new LinkedHashMap<String,LinkedHashMap<String,String> >();
				msgMap.put("requestline", headRequest);
				msgMap.put("headline", headline);
				msgMap.put("body", bodyline);
				String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
				outCS.write(msg.getBytes("UTF-8"));
				
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		
		class SendBeatMessageThread extends Thread{
			public SendBeatMessageThread(){
				    start();
			}
			public void run(){
				try{
					while(true){;
						sleep(10000);
						LinkedHashMap<String,String> headRequest =new LinkedHashMap<String,String>();
						LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
						LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
						headRequest.put("CSVersion", "CS1.0");
						headRequest.put("MessageType","BEAT");
						headRequest.put("UserName",DataModel.getInstance().getUserName());
						headline.put("Content-length", "0");
						headline.put("Time", (new java.util.Date()).toString());
						LinkedHashMap<String,LinkedHashMap<String,String> > msgMap = new LinkedHashMap<String,LinkedHashMap<String,String> >();
						msgMap.put("requestline", headRequest);
						msgMap.put("headline", headline);
						msgMap.put("body", bodyline);
						String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
						
						outCS.write(msg.getBytes("UTF-8"));						
					}
				}catch (Exception e){
					
				}
				
			
			}
		}
		class GetChatRoomMessageThread extends Thread{
			private BufferedReader buff;
			
			public GetChatRoomMessageThread(){
				try{
					start();
				}catch (Exception e){
					
				}
				
			}
			public void run(){
					try{
						while(true){
							LinkedHashMap<String,String> headrequest = MessageManipulator.getInstance().getHeadRequest(in);
							String CSVersion = headrequest.get("0");
							String MsgType = headrequest.get("1");
							String usernameorstatus = headrequest.get("2");
							String usernameornull = headrequest.get("3");
							if(CSVersion.equals("CS1.0")){
								if(MsgType.equals("CSMESSAGE")){
									HandleCSMessage(usernameorstatus);
								}else if(MsgType.equals("LIST")){
									HandleOnlineUsersInit();
								}else if(MsgType.equals("UPDATE")){
									HandleOnlineUsersUpdate(usernameorstatus,usernameornull);
								}
							}
						}
						
					}catch (Exception e){
						
					}
			}
			public void HandleCSMessage(String username) throws IOException{
				String msgCell = "";
				LinkedHashMap<String,String> headline = MessageManipulator.getInstance().getHeadLine(in);
				LinkedHashMap<String,String> bodyline = MessageManipulator.getInstance().getEntityMessage(in);
				
				String time = headline.get("Date");
				String msgDetail = bodyline.get("Entity");
				System.out.println(username);
				if(true){
					msgCell += username+":";
					msgCell += time +"\n";
					msgCell += msgDetail;
				}
				chatMessageModel.addElement(msgCell);
			}
			public void HandleOnlineUsersInit() throws Exception{
				
				LinkedHashMap<String,String> headline = MessageManipulator.getInstance().getHeadLine(in);
				LinkedHashMap<String,String> bodyline = MessageManipulator.getInstance().getEntityMessage(in);
				
				String content_length = headline.get("Content-Length");
				String time = headline.get("Time");
				String msgDetail = bodyline.get("Entity");
				String[] userlist = msgDetail.split("\n");
				
				DataModel.getInstance().resetOnlineUsers();
				
				
				onlineUserModel.resetOnlineUser();
				
				for(int i=0;i<userlist.length;i++){
					String[] oneUser = userlist[i].split(" ");
					ArrayList<String> aUser= new ArrayList<String>();
					aUser.add(oneUser[0]);
					aUser.add(oneUser[1]);
					aUser.add(oneUser[2]);
					DataModel.getInstance().addOnlineUsers(aUser);
					onlineUserModel.addElement(oneUser[0]);
				}
			}
			public void HandleOnlineUsersUpdate(String status,String username) throws Exception{
				LinkedHashMap<String,String> headline = MessageManipulator.getInstance().getHeadLine(in);
				LinkedHashMap<String,String> bodyline = MessageManipulator.getInstance().getEntityMessage(in);
				
				String userip = headline.get("IPAddr");
				String port = headline.get("Port");
				String time = headline.get("Time");
				
				String msgDetail = bodyline.get("Entity");
				//0 for logout
				if(status.equals("0")){
					if(onlineUserModel.contains(username) ){
						int index = onlineUserModel.indexOf(username);
						onlineUserModel.removeElement(username);
						chatMessageModel.addElement("Server: "+username+" Log out");
						DataModel.getInstance().removeOnlineUsers(index);
					}
				}else if(status.equals("1")){
					if(!onlineUserModel.contains(username) ){
						onlineUserModel.addElement(username);
						chatMessageModel.addElement("Server: "+username+" Log In");
						ArrayList<String> aUser= new ArrayList<String>();
						aUser.add(username);
						aUser.add(userip);
						aUser.add(port);
						DataModel.getInstance().addOnlineUsers(aUser);
					}
				}
		}
		
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
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ChatMessageModel(){
		
		}
	}
	class OnlineUserModel extends DefaultListModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public OnlineUserModel() {
		}
		public void resetOnlineUser(){
			this.removeAllElements();
		}
	}
}

