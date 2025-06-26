
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;

public class ChatServerThread extends Thread{
	
	Server server;
	Socket socket;
	BufferedReader br;
	BufferedWriter bw;
	Gson gson;
	
	boolean flag = true;
	
	public ChatServerThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		gson = new Gson();
		
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		listen();
	}
	
	//클라이언트로부터 메시지 수신 대기 
	// 수신을 하면 이걸 다른 클라이언트에게 전송해야함. 
	public void listen() {
		
		while(flag) {
			try {
				String jsonStr = br.readLine();
				Payload p = gson.fromJson(jsonStr, Payload.class);
				ChatRoom room = server.map.get(p.getSender().getBranch_id());
				
				// requestType이 "connect"인지 "message"인지 "disconnect"인지 파악 
				if(p.getRequestType().equals("connect")) {
					room.vec.add(this);
				}
				else if (p.getRequestType().equals("disconnect")) {
					room.vec.remove(this);
				}
				else {
					
					for(int i = 0; i < room.vec.size(); i++) {
						ChatServerThread serverThread = room.vec.get(i);
						if(serverThread == this) { continue; } //자기 자신은 빼고 다른 클라이언트에게 전달
						serverThread.send(jsonStr);
					}
				}
			} catch (SocketException e) {
				System.out.println("클라이언트 연결 종료됨: " + e.getMessage());
				flag = false;
			}catch (IOException e) {
				e.printStackTrace();
				flag = false;
			}
		}
	}
	
	public void send(String jsonStr) {
		try {
			bw.write(jsonStr + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
}
