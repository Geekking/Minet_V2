import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class Task extends TimerTask{

		@Override
		public void run() {
			// TODO �Զ����ɵķ������
			long currentTime = new Date().getTime();
			for (String string : ChatServer.clientLists.keySet()) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					if (currentTime - df.parse(ChatServer.clientLists.get(string).getBeattime()).getTime() > 30000) {
						
						ChatServer.broadcastUpdateOnlineClientInfo(string, "offline");					
						System.out.println(string + "����ֹͣ��");
					}
				} catch (ParseException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				} catch (IOException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}
			}
		}
		
	}