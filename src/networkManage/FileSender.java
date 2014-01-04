package networkManage;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class FileSender extends Thread{
	private static ServerSocket ss=null;
	private String filetoSend;
	private JProgressBar jp = null;
	
	public FileSender(String filePath){
		filetoSend = filePath;
		start();
	}
	public void run(){
		sendFile(filetoSend);
	}
	public int getSocketPort(){
		while(ss ==null){
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ss.getLocalPort();
	}
	public void close(){
		if(ss != null){
			try{
				ss.close();
			}catch (Exception e){
				e.printStackTrace();
			}finally{
				try {
					ss.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void sendFile(String filePath){
		jp = new JProgressBar();
    	jp.setValue(0);
    	jp.setSize(200, 10);
    	JFrame contFrame = new JFrame("发送文件中...");
    	JPanel p = new JPanel();
    	p.add(jp);
    	contFrame.getContentPane().add(p);
    	contFrame.pack();
    	contFrame.setVisible(true);
    	
    	DataOutputStream dos=null;
		DataInputStream dis=null;
		Socket socket=null;
		try {
			File file=new File(filePath);
			ss=new ServerSocket(0);
			socket=ss.accept();
			dos=new DataOutputStream(socket.getOutputStream());
			dis=new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
	    	jp.setVisible(true);
	    	contFrame.setVisible(true);
			int buffferSize=1024;
			byte[]bufArray=new byte[buffferSize];
			dos.writeUTF(file.getName()); 
			dos.flush(); 
			dos.writeLong((long) file.length()); 
			dos.flush(); 
			while (true) { 
			    int read = 0; 
			    if (dis!= null) { 
			      read = dis.read(bufArray); 
			    } 

			    if (read == -1) { 
			      break; 
			    } 
			    dos.write(bufArray, 0, read); 
			    jp.setValue(80);
			  } 
			  dos.flush();
			  jp.setValue(100);
			  sleep(2000);
			  contFrame.setVisible(false);
			  contFrame = null;
			  jp = null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
		      // 关闭所有连接 
		      try { 
		        if (dos != null) 
		          dos.close(); 
		      } catch (IOException e) { 
		      } 
		      try { 
		        if (dis != null) 
		          dis.close(); 
		      } catch (IOException e) { 
		      } 
		      try { 
		        if (socket != null) 
		          socket.close(); 
		      } catch (IOException e) { 
		      } 
		      try { 
		        if (ss != null) 
		          ss.close(); 
		      } catch (IOException e) { 
		      } 
		    } 


	}
}