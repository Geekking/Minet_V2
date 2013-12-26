import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;

import networkManage.*;

public class LoginRegisterView extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame loginRegisterView = null;
	private JLabel userNamelabel = new JLabel("用户名");
	private JLabel passwordlabel = new JLabel("密码");
	private JTextField usernameField = null;
	private JPasswordField pwdField = null;
	
	private JLabel userNameValidlabel = new JLabel("");
	private JLabel passwordValidlabel = new JLabel("");
	
	private JButton loginButton = null;
	private JButton registerButton = null;
	private JButton resetPasswordButton = null;
	private static LoginRegisterView loginRegisterInstance= null;
	private LoginRegisterView(){
		InitView();
		loginRegisterView.setLocationRelativeTo(null);
		loginRegisterView.setVisible(true);
	}
	
	
	public synchronized  static  LoginRegisterView getInstance(){
		if (loginRegisterInstance == null){
			loginRegisterInstance = new LoginRegisterView();
		}
		return loginRegisterInstance;
	}
	private void InitView(){
		JPanel container = new JPanel();
		//container.setSize(100, 100);
		loginRegisterView = new JFrame();
		JPanel userNamePanel = new JPanel();
		userNamePanel.add(userNamelabel);
		usernameField = new JTextField(15);
		userNamePanel.add(usernameField);
		userNameValidlabel.setVisible(false);
		userNamePanel.add(userNameValidlabel);
		container.add(userNamePanel);
		//loginRegisterView.getContentPane().add(userNamePanel);
		
		JPanel passwordPanel = new JPanel();
		passwordPanel.add(passwordlabel);
		pwdField = new JPasswordField(15);
		pwdField.setEchoChar('*');
		passwordPanel.add(pwdField);
		passwordValidlabel.setVisible(false);
		passwordPanel.add(passwordValidlabel);
		
		resetPasswordButton = new JButton("密码重置");
		resetPasswordButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		passwordPanel.add(resetPasswordButton);
		container.add(passwordPanel);
		//loginRegisterView.getContentPane().add(passwordPanel);
		
		JPanel buttonGroup = new JPanel();
		registerButton = new JButton("注册");
		registerButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				HandleRegiste();
			}
			
		});
		
		buttonGroup.add(registerButton);
		
		loginButton = new JButton("登录");
		loginButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 
				if(inputValid()){
					HandleLogin();
				}
				
			}
			
		});
		buttonGroup.add(loginButton);
		container.add(buttonGroup);
		loginRegisterView.setTitle("登录");
		loginRegisterView.setSize(400,300);
		loginRegisterView.setResizable(false);
		loginRegisterView.getContentPane().add(container);
		//loginRegisterView.getContentPane().add(buttonGroup);
	}
	private boolean inputValid(){
		boolean flag = true;
		userNameValidlabel.setVisible(false);
		passwordValidlabel.setVisible(false);
		if(usernameField.getText() ==""){
			flag = false;
			userNameValidlabel.setText("Type a name");
			userNameValidlabel.setVisible(true);
		}
		if(pwdField.getPassword().length ==0 ){
			flag = false;
			passwordValidlabel.setText("Type password");
			passwordValidlabel.setVisible(true);
		}
		return flag;
	}
	private void HandleRegiste(){
		loginRegisterView.setVisible(false);
		RegisteView.getInstance().setVisible(true);
	}
	private void HandleLogin(){
		//TODO:LoginView
		try {
			if((new LoginSocket()).login(usernameField.getText(), String.valueOf(pwdField.getPassword()) ) ){
				loginRegisterView.setVisible(false);
				ChatRoomView.getInstance().setVisible(true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void mouseClicked(MouseEvent e){
		if(e.getSource() == loginButton){
			System.out.println("Log in to the CS");
		}
		else if (e.getSource() == resetPasswordButton){
			System.out.println("Reset your password");
		}
		else if(e.getSource() == registerButton){
			System.out.println("Register");
		}
	}
	public class LoginSocket extends Socket{
		private Socket loginSocket;
		private P2PDetectSocket p2pDetectSocket;
		private BufferedReader in;
		private PrintWriter out;
		private int P2PDetectPort = -1;
		public LoginSocket()throws Exception{
			super(DataModel.getInstance().getServerIP(),DataModel.getInstance().getServerPort());
			loginSocket = this;
			loginSocket.setSoTimeout(6000);
			in = new BufferedReader(new InputStreamReader(loginSocket.getInputStream()));
			out = new PrintWriter(loginSocket.getOutputStream(),true);
		}
		public boolean login(String userName,String password) throws Exception{
			try {
				if(MessageManipulator.getInstance().shakeHand(in,out,"MINET")){
					if( P2PDetectPort ==-1){
						return false;
					}
					HashMap<String,String> headRequest =new HashMap<String,String>();
					HashMap<String,String> headline = new HashMap<String,String>();
					HashMap<String,String> bodyline = new HashMap<String,String>();
					headRequest.put("CSVersion", "CS1.0");
					headRequest.put("MessageType","LOGIN");
					headRequest.put("UserName",userName);
					p2pDetectSocket =new P2PDetectSocket();
					headline.put("Password", password);
					headline.put("CSPort",String.valueOf(P2PDetectPort));
					headline.put("Content-length", "0");
					headline.put("Time", (new java.util.Date()).toString());
					
					HashMap<String,HashMap<String,String> > msgMap = new HashMap<String,HashMap<String,String> >();
					msgMap.put("requestline", headRequest);
					msgMap.put("headline", headline);
					msgMap.put("body", bodyline);
					String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
					out.println(msg);
					//TODO: SET TIMET to live
					if (HandleLogin()){
						DataModel.getInstance().setUserName(userName);
						return true;
					};
					return false;
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				in.close();
				out.close();
				loginSocket.close();
			}
			return false;
		}
		private boolean HandleLogin(){
			try {
				String result = in.readLine();
				HashMap<String,HashMap<String,String> > msgMap =  MessageManipulator.getInstance().parseMessage(result);
				String CSVersiont = msgMap.get("requestline").get("0");
				String MsgType = msgMap.get("requestline").get("1");
				String loginState = msgMap.get("requestline").get("2");
				String MsgDetail = msgMap.get("bodyline").get("Entity");
				
				if(loginState == "0"){
					//TODO:process failed to login
				}
				else{
					in.close();
					out.close();
					loginSocket.close();
					
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return false;
		}
		private void HandleLoginResult(String status){
			
		}
		
		
		public class P2PDetectSocket extends ServerSocket{
			public P2PDetectSocket()throws Exception{
				super(DataModel.getInstance().getp2pAcceptPort());
				P2PDetectPort = this.getLocalPort();
			}
		}
	}
}
