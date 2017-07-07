package sample.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OffsetManager {
	private final SimpleDateSource simpleDateSource;

	public OffsetManager() {
		this.simpleDateSource = SimpleDateSource.instance();
	}

	public void saveOffsetInExternalStore(String topic, int partition,
			long offset) {
		try {
			Connection connection = simpleDateSource.getConnection();
			String sql2 = "update kafkaoffset set offset=? where pid=? and topic=? ";
			PreparedStatement pstmt;

			pstmt = (PreparedStatement) connection.prepareStatement(sql2);
			pstmt.setLong(1, offset);
			pstmt.setLong(2, partition);
			pstmt.setString(3, topic);
			pstmt.executeUpdate();
			pstmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public long readOffsetFromExternalStore(String topic, int partition) {
		long startoffset = 0;

		Connection connection;
		try {
			connection = simpleDateSource.getConnection();
			String sqlq = "select offset from kafkaoffset where pid=? and topic = ?";
			PreparedStatement pstmt = (PreparedStatement) connection
					.prepareStatement(sqlq);
			pstmt.setLong(1, partition);
			pstmt.setString(2, topic);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				startoffset = rs.getLong(1);
			}
			pstmt.close();
			connection.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return startoffset;

	}
}
