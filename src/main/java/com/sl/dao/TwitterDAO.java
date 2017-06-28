package com.sl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.sl.db.DBConn;
import com.sl.db.DBException;
import com.sl.pojo.UserPojo;


public class TwitterDAO {

	public static UserPojo selectUser(String userId) throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		UserPojo pojo = null;
		try {
			conn = DBConn.getConnection();
			ps = conn.prepareStatement("select * from twitter_user where user_id = ?");
			ps.setString(1, userId);
			res = ps.executeQuery();
			if (res != null) {
				while (res.next()) {
			       pojo = new UserPojo();
	               pojo.setUser_id(res.getInt(1));
	               pojo.setTwitter_user_id(res.getLong(2));
	               pojo.setTwitter_screen_name(res.getString(3));
	               pojo.setAccess_token(res.getString(4));
	               pojo.setAccess_token_secret(res.getString(5));
				}
			}
			DBConn.close(conn, ps, res);
		} catch (ClassNotFoundException | SQLException e) {
			DBConn.close(conn, ps, res);
			throw new DBException(e.toString());
		}
		return pojo;
	}
	
	
	public static UserPojo selectTwitterUser(long user_id) throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res=null;
		UserPojo pojo = null;
		try {
			conn = DBConn.getConnection();
			ps = conn.prepareStatement("select * from twitter_user where twitter_user_id = ?");
			ps.setLong(1, user_id);
			res = ps.executeQuery();
			if (res != null) {
				while (res.next()) {
				   pojo = new UserPojo();
	               pojo.setUser_id(res.getInt(1));
	               pojo.setTwitter_user_id(res.getLong(2));
	               pojo.setTwitter_screen_name(res.getString(3));
	               pojo.setAccess_token(res.getString(4));
	               pojo.setAccess_token_secret(res.getString(5));
				}
			}
			DBConn.close(conn, ps, res);
		} catch (ClassNotFoundException | SQLException e) {
			DBConn.close(conn, ps, res);
			throw new DBException(e.toString());
		}
		return pojo;
	}

	public static void updateAccessToken(UserPojo pojo) throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBConn.getConnection();
			ps = conn.prepareStatement("update twitter_user set access_token=?, access_token_secret=?  where twitter_user_id = ?");
			ps.setString(1, pojo.getAccess_token());
			ps.setString(2, pojo.getAccess_token_secret());
			ps.setLong(3, pojo.getTwitter_user_id());
			ps.executeUpdate();
			DBConn.close(conn, ps);
		} catch (ClassNotFoundException | SQLException e) {
			DBConn.close(conn, ps);
			throw new DBException(e.toString());
		}
	}
	
	
	// add users vote
	public static void insertRow(UserPojo pojo) throws DBException{
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBConn.getConnection();
			ps = conn.prepareStatement("insert into twitter_user (twitter_user_id, screen_name, access_token, access_token_secret) values (?,?,?,?)");
			ps.setLong(1,pojo.getTwitter_user_id());
			ps.setString(2,pojo.getTwitter_screen_name());
			ps.setString(3,pojo.getAccess_token());
			ps.setString(4,pojo.getAccess_token_secret());
			ps.executeUpdate();
			DBConn.close(conn, ps);
		} catch (ClassNotFoundException | SQLException e) {
			DBConn.close(conn, ps);
			throw new DBException(e.toString());
		}
	}

}	