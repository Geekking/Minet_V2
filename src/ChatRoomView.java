

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

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
	JButton submitButton = null;
	JTextArea messageTosend = new JTextArea(4,20);
	//private static ChatRoomView chatroomviewInstance = null;
	private  ChatRoomSocket chatRoomManager = null;
	/*
	public   ChatRoomView getInstance(){
		if(chatroomviewInstance ==null){
			try {
				chatroomviewInstance = new ChatRoomView();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return chatroomviewInstance;
	}
	*/
	public ChatRoomView() throws UnknownHostException, IOException{
		initChatRoom();
		chatRoomManager = new ChatRoomSocket();
		setVisible(true);
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
				String tmp = (String) onlineUserModel.getElementAt(index);
				JOptionPane.showMessageDialog(null,tmp);
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
	
	public class ChatRoomSocket extends Socket{
		private ChatRoomSocket chatRoomSocket=null;
		private BufferedReader in;
		private PrintWriter out;
		
		public ChatRoomSocket() throws UnknownHostException, IOException{
			super(DataModel.getInstance().getServerIP(),DataModel.getInstance().getServerPort());
			chatRoomSocket = this;
			in = new BufferedReader(new InputStreamReader(chatRoomSocket.getInputStream()));
			out = new PrintWriter(chatRoomSocket.getOutputStream(),true);
			for(int i=0;i<3;i++){
				if(MessageManipulator.getInstance().shakeHand(in, out, "MINET")){
					new GetChatRoomMessageThread();
					new SendBeatMessageThread();
					break;
				}
			}
			
		}
		public void closeSocket(){
				try {
					in.close();
					out.close();
					chatRoomSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
		}
		
		public void getOnlineList(){
			try{
				HashMap<String,String> headRequest =new HashMap<String,String>();
				HashMap<String,String> headline = new HashMap<String,String>();
				HashMap<String,String> bodyline = new HashMap<String,String>();
				headRequest.put("CSVersion", "CS1.0");
				headRequest.put("MessageType","GETLIST");
				
				headline.put("Content-length", "0");
				headline.put("Time", (new java.util.Date()).toString());
				HashMap<String,HashMap<String,String> > msgMap = new HashMap<String,HashMap<String,String> >();
				msgMap.put("requestline", headRequest);
				msgMap.put("headline", headline);
				msgMap.put("body", bodyline);
				String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
				out.println(msg);
			}catch (Exception e){
				
			}
		}
		public boolean SendCSMessage(String msgTosend){
			try{
				HashMap<String,String> headRequest =new HashMap<String,String>();
				HashMap<String,String> headline = new HashMap<String,String>();
				HashMap<String,String> bodyline = new HashMap<String,String>();
				headRequest.put("CSVersion", "CS1.0");
				headRequest.put("MessageType","MESSAGE");
				headRequest.put("UserName",DataModel.getInstance().getUserName());
				headline.put("Content-length", String.valueOf(msgTosend.length()) );
				headline.put("Time", (new java.util.Date()).toString());
				bodyline.put("bodyline", msgTosend);
				HashMap<String,HashMap<String,String> > msgMap = new HashMap<String,HashMap<String,String> >();
				msgMap.put("requestline", headRequest);
				msgMap.put("headline", headline);
				msgMap.put("body", bodyline);
				String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
				out.println(msg);
			}catch (Exception e){
				
			}
			return false;
		}
		class SendBeatMessageThread extends Thread{
			public SendBeatMessageThread(){
				}
			public void run(){
				try{
					while(true){
						HashMap<String,String> headRequest =new HashMap<String,String>();
						HashMap<String,String> headline = new HashMap<String,String>();
						HashMap<String,String> bodyline = new HashMap<String,String>();
						headRequest.put("CSVersion", "CS1.0");
						headRequest.put("MessageType","BEAT");
						headRequest.put("UserName",DataModel.getInstance().getUserName());
						headline.put("Content-length", "0");
						headline.put("Time", (new java.util.Date()).toString());
						HashMap<String,HashMap<String,String> > msgMap = new HashMap<String,HashMap<String,String> >();
						msgMap.put("requestline", headRequest);
						msgMap.put("headline", headline);
						msgMap.put("body", bodyline);
						String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
						out.println(msg);
						sleep(10000);
					}
				}catch (Exception e){
					
				}
				
			
			}
		}
		class GetChatRoomMessageThread extends Thread{
			private BufferedReader buff;
			
			public GetChatRoomMessageThread(){
				try{
					buff = new BufferedReader(new InputStreamReader(chatRoomSocket.getInputStream()));
					start();
				}catch (Exception e){
					
				}
				
			}
			public void run(){
					try{
						while(true){
							HashMap<String,String> headrequest = MessageManipulator.getInstance().getHeadRequest(in);
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
				HashMap<String,String> headline = MessageManipulator.getInstance().getHeadLine(in);
				HashMap<String,String> bodyline = MessageManipulator.getInstance().getEntityMessage(in);
				
				String time = headline.get("Time");
				String msgDetail = bodyline.get("Entity");
				
				if(true){
					msgCell += username+"\n";
					msgCell += time +"\n";
					msgCell += msgDetail;
				}
				chatMessageModel.addElement(msgCell);
			}
			public void HandleOnlineUsersInit() throws IOException{
				
				HashMap<String,String> headline = MessageManipulator.getInstance().getHeadLine(in);
				HashMap<String,String> bodyline = MessageManipulator.getInstance().getEntityMessage(in);
				
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
			public void HandleOnlineUsersUpdate(String status,String username) throws IOException{
				HashMap<String,String> headline = MessageManipulator.getInstance().getHeadLine(in);
				HashMap<String,String> bodyline = MessageManipulator.getInstance().getEntityMessage(in);
				
				String userip = headline.get("IPAddr");
				String port = headline.get("Port");
				String time = headline.get("Time");

				String msgDetail = bodyline.get("Entity");
				//0 for logout
				if(status.equals("0")){
					if(onlineUserModel.contains(username) ){
						int index = onlineUserModel.indexOf(username);
						onlineUserModel.removeElement(username);
						chatMessageModel.addElement("Server:"+username+"Log out");
						DataModel.getInstance().removeOnlineUsers(index);
					}
				}else if(status.equals("1")){
					if(!onlineUserModel.contains(username) ){
						onlineUserModel.addElement(username);
						chatMessageModel.addElement("Server:"+username+"Log In");
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
		String[] s = {"美国", " 日本", "大陆", "英国", "法国", "意大利", "美国", " 日本", "大陆", "英国", "法国", "意大利", "澳洲", "韩国" };
	
		public OnlineUserModel() {
			for (int i = 0; i < s.length; i++)
				addElement((i + 1) + "." + s[i]);
		}
		public void resetOnlineUser(){
			this.clear();
		}
	}
}

