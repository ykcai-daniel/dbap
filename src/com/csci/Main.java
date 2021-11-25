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
		while(1){
			int check = 0;
			system.out.println()//menu
			
			Scanner sc = new Scanner(System.in);
			int choice = sc.nexInt();
			switch(choice){
    				case 1 :
					AdminMethods am=new AdminMethods(con);
       					break;
    				case 2 :
       					break; 
				case 3 :
       					break; 
				case 4 :
					check = 999;
       					break; 
			}
			if(check == 999)
				break;
		}
		con.close();
	}
}
