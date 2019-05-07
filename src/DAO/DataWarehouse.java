package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import model.Host;

public class DataWarehouse {
	public static int isExistedData(Connection cnnDW, String mssv, int id_source) throws SQLException {
		String sql = "select id from student where mssv = ? and id_source = ? and is_active = 1";
		PreparedStatement stmt = cnnDW.prepareStatement(sql);
		stmt.setString(1, mssv);
		stmt.setInt(2, id_source);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return rs.getInt(1);
		}
		rs.close();
		stmt.close();
		return -1;
	}

	public static void insertWarehouse(Connection cnnDW, Host host, String oneLine) throws SQLException, ParseException {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into student(");
		sb.append(host.dw_query_insert);
		sb.append(") values(");
		for (int i = 0; i < host.number_of_fields; i++) {
			sb.append("?,");
		}
		sb.append("?)");
		String sql = sb.toString();
		PreparedStatement stmt = cnnDW.prepareStatement(sql);
		StringTokenizer format = new StringTokenizer(host.format_fields, ",");
		StringTokenizer values = new StringTokenizer(oneLine, ",");
		String type = "";
		int i = 1;
		while (format.countTokens() > 0) {
			type = format.nextToken();
			switch (type) {
			case "int":
				stmt.setInt(i, Integer.parseInt(values.nextToken()));
				break;
			case "date":
				stmt.setDate(i, new Date(new SimpleDateFormat(host.format_date).parse(values.nextToken()).getTime()));
				break;
			case "string":
				stmt.setString(i, values.nextToken());
				break;
			default:
				break;
			}
			i++;
		}
		stmt.setInt(i, host.id);
		stmt.executeUpdate();
		stmt.close();
	}

	public static void updateNonActice(Connection cnnDW, int id) throws SQLException {
		String sql = "update student set is_active = 0 where id = ?";
		PreparedStatement stmt = cnnDW.prepareStatement(sql);
		stmt.setInt(1, id);
		stmt.executeUpdate();
		stmt.close();
	}
	
	/**
	 * count added line perday
	 */
	public static Map<Date, Integer> getAmountStudentPerDay(Connection cnnDW) throws SQLException {
		Map<Date, Integer> result = new HashMap<>();
		String sql = "select DATE(added_date) dateonly, count(id) from student group by dateonly";
		Statement stmt = cnnDW.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			result.put(rs.getDate(1), rs.getInt(2));
		}
		rs.close();
		stmt.close();
		return result;
	}
}
