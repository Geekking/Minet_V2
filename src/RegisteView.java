import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class RegisteView extends JFrame{
	 /**
	 * 
	 */
	    private static final long serialVersionUID = 1L;
		private JLabel userNameLabel = new JLabel("用户名");
		private JLabel passwordLabel = new JLabel("登录密码");
		private JLabel confpswLabel = new JLabel("确认密码");
		
		private JLabel emailLabel = new JLabel("邮箱地址");
		private JLabel userNameValidLabel = null;
		private JLabel passwordValidLabel = null;
		private JLabel confpswValidLabel = null;
		private JLabel emailValidLabel = null;
		
		private JTextField userNameField = new JTextField(15);
		private JPasswordField passswordField = new JPasswordField(15);
		private JPasswordField confpswField =new JPasswordField(15);
		private JTextField emailField = new JTextField(15);
		
		private JButton submitButton = new JButton("提交");
		private JButton cancelButton = new JButton("取消");
		
		private static RegisteView registeViewInstance=null;
		
		public synchronized  static RegisteView getInstance(){
			if (registeViewInstance == null){
				registeViewInstance = new RegisteView();
			}
			return registeViewInstance;
		}
		public RegisteView(){
			initView();
			setVisible(true);
			
		}
		public void initView(){
			this.setLayout(new FlowLayout());
			this.setSize(350,500);
			this.setResizable(false);
			this.setLocationRelativeTo(null);
			JPanel userNamePanel = new JPanel();
			userNamePanel.add(userNameLabel);
			userNamePanel.add(userNameField);
			userNameValidLabel = new JLabel("");
			userNamePanel.add(userNameValidLabel);
			//TODO: ADD A LISTENER
			userNamePanel.setVisible(true);
			getContentPane().add(userNamePanel);
			
			JPanel passwordPanel = new JPanel();
			passwordPanel.add(passwordLabel);
			passwordPanel.add(passswordField);
			passwordValidLabel = new JLabel("");
			passwordPanel.add(passwordValidLabel);
			//TODO:
			passwordPanel.setVisible(true);
			getContentPane().add(passwordPanel);
			
			JPanel confpswPanel = new JPanel();
			confpswPanel.add(confpswLabel);
			confpswPanel.add(confpswField);
			confpswValidLabel = new JLabel("",JLabel.LEFT);
			confpswPanel.add(confpswValidLabel);
			//TODO:
			confpswPanel.setVisible(true);
			getContentPane().add(confpswPanel);
			
			JPanel emailPanel = new JPanel();
			emailPanel.add(emailLabel);
			emailPanel.add(emailField);
			emailValidLabel = new JLabel("");
			emailPanel.add(emailValidLabel);
			//TODO:
			emailPanel.setVisible(true);
			getContentPane().add(emailPanel);
			
			JPanel buttonGroup = new JPanel();
			submitButton.addActionListener(new ActionListener() {
				 
	            public void actionPerformed(ActionEvent e)
	            {
	                if(isInputValid()){
	                	//TODO:
	                	if(true){
	                		HandleRegisterSuccess();
	                	}
	                	
	                }
	                
	                System.out.println("You clicked the submit button");
	            }
	        });      
			
			cancelButton.addActionListener(new ActionListener() {
				 
	            public void actionPerformed(ActionEvent e)
	            {
	            	int n = JOptionPane.showConfirmDialog(null, "确认取消？取消后，您所填信息将不被保存", "Warning", JOptionPane.YES_NO_OPTION);
	            	if (n==0){
	            		HandleCancel();
	            	}
	                System.out.println("You clicked the cancelButton button");
	            }
	        });  
			buttonGroup.add(submitButton);
			buttonGroup.add(cancelButton);
			getContentPane().add(buttonGroup);
		}
		public void HandleRegisterSuccess(){
			this.getInstance().setVisible(false);
			LoginRegisterView.getInstance().setVisible(true);
		}
		public void HandleCancel(){
			this.setVisible(false);
			LoginRegisterView.getInstance().setVisible(true);
		}
		private boolean isInputValid(){
			boolean flag = true;
			
			this.userNameValidLabel.setVisible(false);
			this.passwordValidLabel.setVisible(false);
			this.confpswValidLabel.setVisible(false);
			this.emailValidLabel.setVisible(false);
			if(passswordField.getPassword().length <= 4){
				this.passwordValidLabel.setText("Too short");
				this.passwordValidLabel.setVisible(true);
				flag = false;
			}
			if(this.userNameField.getText().length()==0){
				this.userNameValidLabel.setText("Type a name");
				this.userNameValidLabel.setVisible(true);
				flag = true;
			}
			if(!String.valueOf(this.confpswField.getPassword()).equals(String.valueOf( this.passswordField.getPassword())) ){
				this.confpswValidLabel.setText(" NOT match");
				this.confpswValidLabel.setVisible(true);
				flag = false;
			}
			if(!this.emailField.getText().contains("@") || !this.emailField.getText().contains(".")){
				this.emailValidLabel.setText("Invalid Email");
				this.emailValidLabel.setVisible(true);
				flag = false;
			}
			
			return flag;
		}
}
