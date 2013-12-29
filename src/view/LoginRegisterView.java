package view;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.LinkedHashMap;

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
	private Box b1,b2,b3;
	private Box container;
	private JLabel userNameValidlabel = new JLabel("",10);
	private JLabel passwordValidlabel = new JLabel("",10);
	
	private JButton loginButton = null;
	private JButton registerButton = null;
	private JButton resetPasswordButton = null;
//	private static LoginRegisterView loginRegisterInstance= null;
	private Socket loginSocket;
	public LoginRegisterView(){
		InitView();
		loginRegisterView.setLocationRelativeTo(null);
		loginRegisterView.setVisible(true);
		loginRegisterView.addWindowListener(new WindowAdapter(){
	     	 public void windowClosing(WindowEvent e){
	     	   System.exit(0);	
	     	 }
	     });
		usernameField.setText("demo");
		pwdField.setText("12345");
	}
	public LoginRegisterView(String username){
		InitView();
		loginRegisterView.setLocationRelativeTo(null);
		loginRegisterView.setVisible(true);
		loginRegisterView.addWindowListener(new WindowAdapter(){
	     	 public void windowClosing(WindowEvent e){
	     	   System.exit(0);	
	     	 }
	     });
		this.usernameField.setText(username);
	}
	
	/*
	public synchronized  static  LoginRegisterView getInstance(){
		if (loginRegisterInstance == null){
			loginRegisterInstance = new LoginRegisterView();
		}
		return loginRegisterInstance;
	}
	*/
	private void InitView(){
		loginRegisterView = new JFrame();
		
		usernameField = new JTextField(15);
		userNameValidlabel.setVisible(false);
		
		pwdField = new JPasswordField(15);
		pwdField.setEchoChar('*');
		passwordValidlabel.setVisible(false);
		
		resetPasswordButton = new JButton("修改密码");
		resetPasswordButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loginRegisterView.setVisible(false);
				(new RenewPasswordView()).setVisible(true);;
			}
			
		});
		registerButton = new JButton("注册");
		registerButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				HandleRegiste();
			}
			
		});
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
		
		b1 = Box.createHorizontalBox();
		b2 = Box.createHorizontalBox();
		b3 = Box.createHorizontalBox();
		container = Box.createVerticalBox();
		b1.add(Box.createHorizontalStrut(10));
		b2.add(Box.createHorizontalStrut(10));
		b3.add(Box.createHorizontalStrut(10));
		b1.add(Box.createHorizontalStrut(10));
		b1.add(userNamelabel);
		b1.add(Box.createHorizontalStrut(5));
		b1.add(usernameField);
		b1.add(userNameValidlabel);
		
		b2.add(Box.createHorizontalStrut(22));
		b2.add(passwordlabel);
		b2.add(Box.createHorizontalStrut(5));
		b2.add(pwdField);
		b2.add(Box.createHorizontalStrut(5));
		b2.add(resetPasswordButton);
		b2.add(passwordValidlabel);
		
		
		b3.add(Box.createHorizontalStrut(25));
		b3.add(loginButton);
		b3.add(Box.createHorizontalStrut(20));
		b3.add(registerButton);
	
		container.add(Box.createVerticalStrut(40));
		container.add(b1);
		container.add(Box.createVerticalStrut(40));
		container.add(b2);
		container.add(Box.createVerticalStrut(40));
		container.add(b3);
		
		loginRegisterView.setTitle("登录");
		loginRegisterView.setSize(425,250);
		loginRegisterView.setResizable(false);
		loginRegisterView.setLayout(new FlowLayout());
		loginRegisterView.getContentPane().add(container);
	}
	private boolean inputValid(){
		boolean flag = true;
		userNameValidlabel.setVisible(false);
		passwordValidlabel.setVisible(false);
		if(usernameField.getText().equals("")){
			flag = false;
			userNameValidlabel.setText("Type a name");
			userNameValidlabel.setVisible(true);
		}
		if(pwdField.getPassword().length ==0){
			flag = false;
			passwordValidlabel.setText("Type password");
			passwordValidlabel.setVisible(true);
		}
		return flag;
	}
	private void HandleRegiste(){
		loginRegisterView.setVisible(false);
		(new RegisteView()).setVisible(true);
	}
	private void HandleLogin(){
		//TODO:LoginView
		try {
			
			if((new LoginSocket()).login(usernameField.getText(), String.valueOf(pwdField.getPassword()) ) ){
				loginRegisterView.setVisible(false);
				(new ChatRoomView(loginSocket)).setVisible(true);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class LoginSocket extends Socket{
		
		private BufferedReader in;
		private OutputStream out;
		private int P2PDetectPort = -1;
		
		public LoginSocket() throws Exception{
				super(DataModel.getInstance().getServerIP(),DataModel.getInstance().getServerPort());
				loginSocket = this;
				loginSocket.setSoTimeout(10000);
				in = new BufferedReader(new InputStreamReader(loginSocket.getInputStream()));
				out = loginSocket.getOutputStream();
		}
		public boolean login(String userName,String password) throws Exception{
			try {
				if(MessageManipulator.getInstance().shakeHand(in,out,"MINET")){
					P2PDetectPort = DataModel.getInstance().getp2pAcceptPort();
					if( P2PDetectPort ==-1){
						return false;
					}
					LinkedHashMap<String,String> headRequest =new LinkedHashMap<String,String>();
					LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
					LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
					
					headRequest.put("CSVersion", "CS1.0");
					headRequest.put("MessageType","LOGIN");
					headRequest.put("UserName",userName);
					headline.put("Password", password);
					headline.put("Port",String.valueOf(P2PDetectPort));
					headline.put("Content-length", "0");
					headline.put("Time", (new java.util.Date()).toString());
					
					LinkedHashMap<String,LinkedHashMap<String,String> > msgMap = new LinkedHashMap<String,LinkedHashMap<String,String> >();
					msgMap.put("requestline", headRequest);
					msgMap.put("headline", headline);
					msgMap.put("body", bodyline);
					String msg = MessageManipulator.getInstance().formatAMessage(msgMap);
					
					out.write(msg.getBytes("UTF-8") );
					
					//TODO: SET TIMET to live
					if (HandleLogin()){
						DataModel.getInstance().initUserInfo(userName,P2PDetectPort);
						return true;
					};
					return false;
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		private boolean HandleLogin(){
			try {
				LinkedHashMap<String,LinkedHashMap<String,String> > msgMap =  MessageManipulator.getInstance().parseMessage(in);
				String CSVersiont = msgMap.get("requestline").get("0");
				String MsgType = msgMap.get("requestline").get("1");
				String loginState = msgMap.get("requestline").get("2");
				String MsgDetail = msgMap.get("bodyline").get("Entity");
				
				if(loginState.equals("0")){
					//TODO:process failed to login
					JOptionPane.showMessageDialog(null, MsgDetail);
				}
				else{
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return false;
		}
		
		/*
		public class P2PDetectSocket extends ServerSocket{
			public P2PDetectSocket()throws Exception{
				super(DataModel.getInstance().getp2pAcceptPort());
				P2PDetectPort = this.getLocalPort();
			}
		}
		*/
	}
}
