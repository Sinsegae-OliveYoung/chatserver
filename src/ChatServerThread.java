
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;

public class ChatServerThread extends Thread {

	Server server;
	Socket socket;
	BufferedReader br;
	BufferedWriter bw;
	Gson gson;
	Payload p;

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
		try {
			listen();
		} finally {
			clean();
		}
	}

	// 클라이언트로부터 메시지 수신 대기
	// 수신을 하면 이걸 다른 클라이언트에게 전송해야함.
	public void listen() {

		while (flag) {
			try {
				String jsonStr = br.readLine();
				p = gson.fromJson(jsonStr, Payload.class);
				ChatRoom room = server.map.get(p.getSender().getBranch_id());

				// requestType이 "connect"인지 "message"인지 "disconnect"인지 파악
				if (p.getRequestType().equals("connect")) { // connect 일 때
					if (server.loginUser.containsKey(p.getSender().getUser_id())) { // 중복된 아이디라면 다시 반환
						System.out.println("[서버] 로그인 시도 user_id: " + p.getSender().getUser_id());
						System.out.println("[서버] 현재 로그인 유저 목록: " + server.loginUser.keySet());
						p.setRequestType("duplicated");
						p.setData("이미 접속중인 아이디입니다.");
						send(gson.toJson(p));
						System.out.println("[서버] 중복 로그인 시도 차단: " + p.getSender().getUser_id() + " - "
								+ p.getSender().getUser_name());

						socket.close(); // 소켓 종료
						return;
					}

					server.loginUser.put(p.getSender().getUser_id(), this);

					// 채팅룸 벡터에 추가
					room.vec.add(this);

					p.setData(p.getSender().getUser_name() + "님이 입장하셨습니다.");
					String json = gson.toJson(p);

					// 입장 메시지를 전체 사용자에게 전송
					for (ChatServerThread serverThread : room.vec) {
						serverThread.send(json);
					}
					continue;
				}

				else if (p.getRequestType().equals("disconnect")) {
					// 로그인 된 유저 목록, 채팅룸에서 전부 삭제
					server.loginUser.remove(p.getSender().getUser_id());
					room.vec.remove(this);

					p.setData(p.getSender().getUser_name() + "님이 퇴장하셨습니다.");
					String json = gson.toJson(p);

					// 입장 메시지를 전체 사용자에게 전송
					for (ChatServerThread serverThread : room.vec) {
						serverThread.send(json);
					}
					continue;
				}

				for (int i = 0; i < room.vec.size(); i++) {
					ChatServerThread serverThread = room.vec.get(i);
					if (serverThread == this) {
						continue;
					} // 자기 자신은 빼고 다른 클라이언트에게 전달
					serverThread.send(jsonStr);
					System.out.println("채팅방 인원: " + room.vec.size());
				}
			} catch (SocketException e) {
				System.out.println(" ChatServerThread 예외 종료: " + e.getMessage());
				flag = false;
			} catch (IOException e) {
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
			System.out.println(" 클라이언트로 메시지 전송 실패: " + e.getMessage());
		}
	}

	public void clean() {
		try {
			socket.close();
		} catch (IOException e) {
		}

		if (p != null) {
			ChatRoom room = server.map.get(p.getSender().getBranch_id());
			server.loginUser.remove(p.getSender().getUser_id());
			room.vec.remove(this);
			System.out.println(
					"[서버] 클라이언트 종료 처리 완료: " + p.getSender().getUser_id() + " - " + p.getSender().getUser_name());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		ChatServerThread that = (ChatServerThread) obj;
		return socket != null && socket.equals(that.socket);
	}

	@Override
	public int hashCode() {
		return socket != null ? socket.hashCode() : 0;
	}

}
