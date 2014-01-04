package view.login;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import networkManage.DataModel;

public class RenewPasswordView extends JFrame{

	private static final long serialVersionUID = 1L;
	RenewPasswordView selfView  =null;
	private JLabel userNamelabel = new JLabel("用户名");
	private JLabel passwordlabel = new JLabel("旧密码");
	private JTextField usernameField = new JTextField(15);
	private JTextField pwdField = new JPasswordField(15);
	private JLabel newpasswordlabel = new JLabel("新密码");
	private JTextField newPasswordField = new JTextField(15);
	private JLabel newpasswordConflabel = new JLabel("新密码");
	private JTextField newPasswordConfField = new JTextField(15);
	
	private Box b1,b2,b3,b4,b5;
	private Box container;
	private JLabel userNameValidlabel = new JLabel("",10);
	private JLabel newpasswordValidlabel = new JLabel("",10);
	private JLabel confValidLabel = new JLabel();
	
	private JButton submitButton = new JButton("确认");
	private JButton cancelButton = new JButton("返回");
	Socket socket;
	public RenewPasswordView(){
		initView();
		selfView = this;
		selfView.setSize(350, 400);
		this.setVisible(true);
	}
	private void initView(){
		b1 = Box.createHorizontalBox();
		b2 = Box.createHorizontalBox();
		b3 = Box.createHorizontalBox();
		b4 = Box.createHorizontalBox();
		b5 = Box.createHorizontalBox();
		
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
		b2.add(newpasswordValidlabel);
		
		
		b3.add(Box.createHorizontalStrut(25));
		submitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				if(isInputValid() && HandleRenewPassword()){
					selfView.setVisible(false);
					try {
						new LoginRegisterView(usernameField.getText());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
		b3.add(submitButton);
		b3.add(Box.createHorizontalStrut(20));
		b3.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int n = JOptionPane.showConfirmDialog(null, "确认返回吗？");
				if(n == 0){
					selfView.setVisible(false);
					try {
						new LoginRegisterView("");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		b4.add(Box.createHorizontalStrut(22));
		b4.add(newpasswordlabel);
		b4.add(Box.createHorizontalStrut(5));
		b4.add(newPasswordField);
		b4.add(Box.createHorizontalStrut(5));
		
		b5.add(Box.createHorizontalStrut(22));
		b5.add(newpasswordConflabel);
		b5.add(Box.createHorizontalStrut(5));
		b5.add(newPasswordConfField);
		b4.add(Box.createHorizontalStrut(5));
		
		container.add(Box.createVerticalStrut(40));
		container.add(b1);
		container.add(Box.createVerticalStrut(40));
		container.add(b2);
		container.add(Box.createVerticalStrut(40));
		container.add(b4);container.add(Box.createVerticalStrut(40));
		container.add(b5);
		container.add(Box.createVerticalStrut(40));
		container.add(b3);
		
		this.setTitle("修改密码");
		this.setSize(425,320);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setLayout(new FlowLayout());
		this.getContentPane().add(container);
	}
	
	private boolean isInputValid(){
		boolean flag = true;
		this.userNameValidlabel.setVisible(false);
		this.newpasswordValidlabel.setVisible(false);
		this.confValidLabel.setVisible(false);
		if(this.newPasswordField.getText().length() <=4){
			this.newpasswordValidlabel.setText("Too short");
			this.newpasswordValidlabel.setVisible(true);
			flag =false;
		}
		
		if(this.usernameField.getText().length()==0){
			this.userNameValidlabel.setText("Type a name");
			this.userNameValidlabel.setVisible(true);
			flag = false;
			
		}
		
		if(!newPasswordField.getText().equals(String.valueOf( this.newPasswordConfField.getText())) ){
			this.confValidLabel.setText(" NOT match");
			flag = false;
		}
		
		return flag;
	}
	
	public boolean HandleRenewPassword(){
		try {
			socket = new Socket(DataModel.getInstance().getServerIP(),DataModel.getInstance().getServerPort());
			socket.setSoTimeout(10000);
			String modifyString  = "MODIFYPASSWORD"+" "+usernameField.getText()+" "+pwdField.getText()+" "+newPasswordField.getText()+"\r\n";
			System.out.println("fdaf");
			socket.getOutputStream().write(modifyString.getBytes("UTF-8"));
			return HandleResult();
		} catch ( Exception e) {
			// TODO Auto-generated catch blockrin
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean HandleResult(){
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			String string = bufferedReader.readLine();
			JOptionPane.showMessageDialog(null, string);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
