import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class Beat extends TimerTask{

		@Override
		public void run() {
			// TODO 自动生成的方法存根
			String string = "CS1.0 " + "BEAT " + P2PChat.uname + "\r\n";
			string += "Date " + P2PChat.getTime() + "\r\n";
			string += "\r\n\r\n";
			try {
				P2PChat.newsocket.getOutputStream().write(string.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		
	}