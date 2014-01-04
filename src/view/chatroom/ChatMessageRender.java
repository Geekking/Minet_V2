package view.chatroom;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ChatMessageRender extends JLabel implements ListCellRenderer{
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
			        isSelected, cellHasFocus);
			String string = splitString(50, value.toString());
			String[] msg = string.split("\n");
			String text = "<html>";
			for(int i=0;i<msg.length;i++){
				text += "<br>"+msg[i]+"</br>";
			}
			text += "</html>";
			renderer.setText(text);
			return renderer;
		}
		public String splitString(int LINE_SIZE,String originString){
			String string = "";
			while(originString.length()>LINE_SIZE){
				string += originString.substring(0, LINE_SIZE);
				string += "\n";
				originString = originString.substring(LINE_SIZE); 
			}
			string += originString;
			return string;
		}
		
	}