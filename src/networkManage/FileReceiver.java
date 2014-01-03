package networkManage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class FileReceiver extends Thread{
	private String savePath,ip;
	private int port;
	private JProgressBar jp = null;
	public FileReceiver(String _savePath,String _ip,int _port){
		this.savePath = _savePath;
		this.ip = _ip;
		this.port = _port;
		start();
	}
	public void run(){
		receiveFile(savePath, ip, port);
	}
    public void receiveFile(String savePath,String ip,int port){
    	jp = new JProgressBar();
    	jp.setValue(0);
    	jp.setVisible(true);
    	jp.setSize(200, 10);
    	
    	JFrame contFrame = new JFrame("接收文件中...");
    	JPanel p = new JPanel();
    	p.add(jp);
    	contFrame.getContentPane().add(p);
    	contFrame.pack();
    	contFrame.setVisible(true);
    	
    	Socket socket=null;
        try { 
        	System.out.println(ip);
        	System.out.println(port);
            socket = new Socket(ip,port); 
          } catch (UnknownHostException e1) { 
            e1.printStackTrace(); 
          } catch (IOException e1) { 
            e1.printStackTrace(); 
          } 
        DataInputStream dis=null;
        try { 
            dis = new DataInputStream(new BufferedInputStream(socket 
                .getInputStream())); 
          } catch (IOException e1) { 
            e1.printStackTrace(); 
          } 
        int bufferSize = 1024; 
        // 缓冲区 
        byte[] buf = new byte[bufferSize]; 
        int passedlen = 0; 
        long len = 0; 
        // 获取文件名称 
       try{
    	savePath += dis.readUTF(); 
        DataOutputStream fileOut = new DataOutputStream( 
            new BufferedOutputStream(new BufferedOutputStream( 
                new FileOutputStream(savePath)))); 
        len = dis.readLong(); 
        System.out.println("文件的长度为:" + len + " KB"); 
        System.out.println("开始接收文件!"); 
        while (true) { 
            int read = 0; 
            if (dis!= null) { 
              read = dis.read(buf); 
            } 
            passedlen += read; 
            if (read == -1) { 
              break; 
            } 
            jp.setValue((int) (passedlen * 100 / len));
            System.out.println("文件接收了" + (passedlen * 100 / len) + "%"); 
            fileOut.write(buf, 0, read); 
          } 
          sleep(1000);
          contFrame.setVisible(false);
          contFrame = null;
          System.out.println("接收完成，文件存为" + savePath); 
          fileOut.close(); 
        } catch (Exception e) { 
          e.printStackTrace(); 
          return; 
        } 
    }
    /*
    public static void main(String[] args) { 
        new FileReceiver().receiveFile("D:\\", "localhost", 8821); 
      }
     */  
}