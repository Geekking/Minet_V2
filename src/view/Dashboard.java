package view;
import javax.swing.*;

import view.ChatRoomView.OnlineUserModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
public class Dashboard extends JFrame{
	private JList<String> onlinelist = null;
	private OnlineUserModel  onlineUserModel = null;
	private JButton updateOnlineUsersButton = new JButton("刷新");
	public void Dahsboard(){
		initUserInterface();
	}
	public void initUserInterface(){
		onlineUserModel = new OnlineUserModel();
		onlinelist = new JList<String>(onlineUserModel);
		onlinelist.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){  
				int index;
				if (e.getSource() == onlinelist) {
					if (e.getClickCount() == 2) {
						index = onlinelist.locationToIndex(e.getPoint());
						String tmp = (String) onlineUserModel.getElementAt(index);
						JOptionPane.showMessageDialog(null,tmp);
					}
				}  
		    }  
		});
		onlinelist.setBorder(BorderFactory.createTitledBorder("在线用户"));
		updateOnlineUsersButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		this.add(updateOnlineUsersButton,BorderLayout.NORTH);
		this.add(new JScrollPane(onlinelist),BorderLayout.CENTER);
	}
	public void mouseClicked(MouseEvent e) {
		int index;
			
		if (e.getSource() == onlinelist) {
			if (e.getClickCount() == 2) {
				index = onlinelist.locationToIndex(e.getPoint());
				String tmp = (String) onlineUserModel.getElementAt(index);
				JOptionPane.showMessageDialog(null,tmp);
			}
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
