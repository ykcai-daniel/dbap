package com.csci;
import java.sql.*;

//ssh yhcai0@linux2.cse.cuhk.edu.hk

public class Main {

	public static void main(String[] args) {
		final String dbAddress="jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db8";
		final String dbUserName="Group8";
		final String dbPassword="CSCI3170";
		Connection con=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection(dbAddress,dbUserName,dbPassword);
			System.out.print("Connection established");
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		AdminMethods am=new AdminMethods(con);
		am.deleteAllTable();
	}
}
