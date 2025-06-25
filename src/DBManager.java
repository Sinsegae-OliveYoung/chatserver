

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/* AppMain에서 DB를 핸들링하지 않고, 중립적인 객체에서 Connection을 얻어오고 닫는 것까지
 * 대행해주는 기능을 보유한 객체를 선언*/
// 싱글턴 적용
public class DBManager {
	private static DBManager instance;
	
	private Connection con;
	
	// 아무도 직접 인스턴스를 생성하지 못하도록 생성자의 접근제한을 막아버림
	private DBManager() {
		try {
			// 1) 드라이버 로드
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			// 2) 접속
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/olive", "olive", "1234");
			
			//
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static DBManager getInstance() {
		// 만일 인스턴스가 존재하지 않으면 이 메서드에서 인스턴스 생성
		if (instance == null)
			instance = new DBManager();
		return instance;
	}
	
	public Connection getConnection() {
		return con;
	}

	// DB관련된 자원을 해제하는 메서드
	public void release(Connection con) {
		if (con != null)
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	// DB관련된 자원을 해제하는 메서드
	public void release(PreparedStatement pstmt) {
		if (pstmt != null)
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	// DB관련된 자원을 해제하는 메서드
	public void release(PreparedStatement pstmt, ResultSet rs) {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (pstmt != null)
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	// DB관련된 자원을 해제하는 메서드
	public void release(Connection con, PreparedStatement pstmt, ResultSet rs) {
		if (con != null)
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (pstmt != null)
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}
