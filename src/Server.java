

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

public class Server extends JFrame{
	
	ServerSocket server;
	Map<Integer, ChatRoom> map = new HashMap<>();  // branchId, chatroom
	Thread thread;
	DBManager dbManager = DBManager.getInstance();
	int temp = 5;  //db에서 지점 id 가져와야됨 
	public Server() {
		
		// 지점 id 가져와서 map에 추가 
		for(int i = 1; i <= temp; i++) {
			map.put(i, new ChatRoom());
		}
		
		thread = new Thread() {
			public void run() {
				startServer();
			}; 
		};
		thread.start();
	}
	
	public void startServer() {
		try {
			server = new ServerSocket(9999);
			while(true) {
				Socket socket = server.accept();
				System.out.println("접속 감지");
				
				ChatServerThread serverThread = new ChatServerThread(this, socket);
				serverThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Server();
		System.out.println("서버 가동 시작");
	}

}
