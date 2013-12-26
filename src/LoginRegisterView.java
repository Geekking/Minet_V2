import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

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
		container.add(userNamePanel);
		//loginRegisterView.getContentPane().add(userNamePanel);
		
		JPanel passwordPanel = new JPanel();
		passwordPanel.add(passwordlabel);
		pwdField = new JPasswordField(15);
		pwdField.setEchoChar('*');
		passwordPanel.add(pwdField);
		
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
				HandleLogin();
				
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
	private void HandleRegiste(){
		loginRegisterView.setVisible(false);
		RegisteView.getInstance().setVisible(true);
	}
	private void HandleLogin(){
		//TODO:LoginView
		if(true){
			loginRegisterView.setVisible(false);
			ChatRoomView.getInstance().setVisible(true);
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
}
