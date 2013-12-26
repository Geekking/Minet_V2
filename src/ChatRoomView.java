

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class ChatRoomView extends MouseAdapter {
	private JFrame chatRoom= new JFrame("Chat Room");
	private JPanel onlineUserArea = new JPanel();
	private JPanel chatArea = new JPanel();
	private JPanel chatRoomWall = null;
	private OnlineUserModel  onlineUserModel = null;
	private JList onlinelist = null;
	private JList chatMessagelist = null;
	private ChatMessageModel chatMessageModel = null;
	JButton submitButton = null;
	JTextArea messageTosend = new JTextArea(4,20);
	private static ChatRoomView chatroomviewInstance = null;
	public synchronized  static ChatRoomView getInstance(){
		if(chatroomviewInstance ==null){
			chatroomviewInstance = new ChatRoomView();
		}
		return chatroomviewInstance;
	}
	private ChatRoomView(){
		initChatRoom();
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
		chatMessagelist = new JList(chatMessageModel);
		
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
		onlinelist = new JList(onlineUserModel);
		onlinelist.addMouseListener(this);
		onlinelist.setBorder(BorderFactory.createTitledBorder("在线用户"));
		onlineUserArea.add(new JScrollPane(onlinelist));
	}
	// 处理鼠标键击事件.
	public void mouseClicked(MouseEvent e) {
		int index;
			/*
			 * 对list1而言，当鼠标在某个项目连续按两下时，我们利用JList所提供的locationToIndex()方法，找到所键击的项目，并
			 * 由tmp取得此项目的项目值
			 * ，然后将此项目值增加到mode2中[mode2.addElement(tmp)],用setModel重新设置list2的
			 * ListModel,使list2可显示出所增加的项目，将刚刚在list1双击的项目删除.
			 */
		if (e.getSource() == onlinelist) {
			if (e.getClickCount() == 2) {
				index = onlinelist.locationToIndex(e.getPoint());
				String tmp = (String) onlineUserModel.getElementAt(index);
				JOptionPane.showMessageDialog(null,tmp);
			}
		}
		if (e.getSource() == submitButton) {
			if (e.getClickCount() == 1) {
				//TODO: sendmessage
				chatMessageModel.addElement(messageTosend.getText());
				messageTosend.setText(null);
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
	public ChatMessageModel() {
		
	}
}
class OnlineUserModel extends DefaultListModel{
	String[] s = {"美国", " 日本", "大陆", "英国", "法国", "意大利", "美国", " 日本", "大陆", "英国", "法国", "意大利", "澳洲", "韩国" };

	public OnlineUserModel() {
		for (int i = 0; i < s.length; i++)
			addElement((i + 1) + "." + s[i]);
	}
}

