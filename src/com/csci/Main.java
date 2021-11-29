package com.csci;
import java.io.IOException;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

//ssh yhcai0@linux2.cse.cuhk.edu.hk

public class Main {

	public static void main(String[] args) throws SQLException, IOException {
		final String dbAddress="jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db8";
		final String dbUserName="Group8";
		final String dbPassword="CSCI3170";
		Connection con=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection(dbAddress,dbUserName,dbPassword);
			//System.out.print("Connection established");
		} catch (SQLException e) {
			System.out.println("Cannot connect to the database. Please check your connection and restart the application");
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			System.out.println("Cannot find the database driver");
			e.printStackTrace();
		}
		AdminMethods am=new AdminMethods(con);
		LibrarianMethod lm=new LibrarianMethod(con);
		UserMethods um=new UserMethods(con);
		System.out.println("Welcome to Library Inquiry System!");
		while(true){
			System.out.println();
			System.out.println("-----Main menu-----");
			System.out.println("What kinds of operations would you like to perform?");
			System.out.println("1. Operations for Administrator");
			System.out.println("2. Operations for Library User");
			System.out.println("3. Operations for Librarian");
			System.out.println("4. Exit this program");
			System.out.print("Enter Your Choice:");	//menu
			Scanner sc = new Scanner(System.in);
			int choice;
			try {
				choice = sc.nextInt();
			}
			catch (NumberFormatException e){
				System.out.println("Illegal input");
				continue;
			}
			catch (InputMismatchException e){
				System.out.println("Illegal input");
				continue;
			}
			if(choice==1){
				am.adminLoop();
			}
			else if(choice==2){
				um.userLoop();
			}
			else if(choice==3){
				lm.libLoop();
			}
			else if(choice==4){
				break;
			}
			else{
				System.out.println("Illegal input");
			}
		}
	}
}
