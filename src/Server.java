

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;

public class Server extends JFrame{
	
	ServerSocket server;
	Map<Integer, ChatRoom> map = new HashMap<>();  // branchId, chatroom
	Map<Integer, ChatServerThread> loginUser = new ConcurrentHashMap<>();	// 로그인 한 유저를 저장함
	Thread thread;
	DBManager dbManager = DBManager.getInstance();
	List<Branch> branchList;
	
	public Server() {
		branchList = selectAllBranch();
		
		for(Branch br : branchList) {
			map.put(br.getBr_id(), new ChatRoom());
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
	
	
	public List selectAllBranch() {

		// 지점의 모든 데이터를 반환
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Branch> list = new ArrayList<>();
		
		try {
			con = dbManager.getConnection();
			StringBuffer sql = new StringBuffer();
			
			sql.append("SELECT * FROM branch");
			
			pstmt=con.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Branch branch = new Branch();
				branch.setBr_id(rs.getInt("br_id"));
				branch.setBr_name(rs.getString("br_name"));
				branch.setBr_address(rs.getString("br_address"));
				branch.setBr_tel(rs.getString("br_address"));
				branch.setBr_tel(rs.getString("br_tel"));
				
				list.add(branch);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbManager.release(pstmt, rs);
		}
		
		return list;
	}
	
	public static void main(String[] args) {
		new Server();
		System.out.println("서버 가동 시작");
	}

}
