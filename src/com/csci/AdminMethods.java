package com.csci;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AdminMethods {
    private Connection con;
	
    public AdminMethods(Connection con) {
        this.con = con;
        while(true){
		int check = 0;
		system.out.println("-----Operations for administrator menu-----");
		system.out.println("1. Create all tables");
		system.out.println("2. Delete all tables");
		system.out.println("3. Load from datafile");
		system.out.println("4. Show number of records");
		system.out.println("5. Return to the main menu");
		system.out.print("Enter Your Choice:");

		Scanner sc = new Scanner(System.in);
		int choice = sc.nexInt();
		switch(choice){
                	case 1:
				createTable();
       				break;
                	case 2:
				deleteAllTable();
       				break;
                	case 3:
				loadData(path);
       				break; 
                	case 4:
				showNumberOfRecords()
                    		break;
                	case 5:
				check = 999;
       			    	break; 
		}
		if(check == 999)
				break;
	}
    }

    public void createTable(){
        // need to add some code to check success or fail.
        Statement stmt = conn.createstatement();
        String str = "CREATE TABLE user_category( " +
            "ucid Integer, " +
            "max_books Integer NOT NULL, " +
            "loan_period Integer NOT NULL, " +
            "PRIMARY KEY (ucid), " +
            "CHECK (max_books<100 AND max_books>0), " +
            "CHECK (loan_period<100 AND loan_period>0)," +
            "CHECK (ucid<10 AND ucid > 0))";
        stmt.executeUpdate(str);
        
        str = "CREATE TABLE libuser( " +
            "libuid VARCHAR(10), " +
            "name VARCHAR(25) NOT NULL, " +
            "age INTEGER NOT NULL, " +
            "address VARCHAR(100) NOT NULL, " +
            "ucid INTEGER, " +
            "PRIMARY KEY (libuid)," +
            "FOREIGN KEY (ucid) REFERENCES user_category(ucid)," +
            "CHECK (AGE<1000 AND AGE>0))";/*three digit age?*/
        stmt.executeUpdate(str);
        
        str = "CREATE TABLE book_category( " +
            "bcid INTEGER, " +
            "bcname VARCHAR(30) NOT NULL, " +
            "PRIMARY KEY (bcid)," +
            "CHECK(bcid>0 AND bcid<10))";
        stmt.executeUpdate(str);
        
        str = "CREATE TABLE book( " +
            "callnum INTEGER, " +
            "title VARCHAR(30) NOT NULL, " +
            "publish DATE NOT NULL, " +
            "rating FLOAT DEFAULT NULL, " +
            "tborrowed INTEGER NOT NULL DEFAULT 0, " +
            "bcid INTEGER NOT NULL, " +
            "PRIMARY KEY (callnum), " +
            "FOREIGN KEY (bcid) REFERENCES book_category(bcid), " +
            "CHECK(tborrowed>-1 and tborrowed<100))";   /*rating should be updated after returning book, rating can be null before first borrow!!!!!*/
                                                        /*tborrow needs update with each borrow!!!!!!*/
                                                        /*we can do these updates with an addition UPDATE statement*/
        stmt.executeUpdate(str);
        
        str = "CREATE TABLE copy( " +
            "callnum INTEGER, " +
            "copynum INTEGER, " +
            "PRIMARY KEY (callnum,copynum), " +
            "FOREIGN KEY (callnum) REFERENCES book(callnum))";  /*what is copynum*/
        stmt.executeUpdate(str);
        
        str = "CREATE TABLE borrow( " +
            "libuid VARCHAR(10), " +
            "callnum INTEGER, " +
            "copynum INTEGER, " +
            "checkout DATE NOT NULL , " +
            "return_data DATE DEFAULT NULL, " +
            "PRIMARY KEY (libuid,callnum,copynum,checkout), " +
            "FOREIGN KEY (libuid) REFERENCES libuser(libuid), " +
            "FOREIGN KEY (callnum,copynum) REFERENCES copy(callnum, copynum))"; /*return can be null!!!!!*/
        stmt.executeUpdate(str);
        
        str = "CREATE TABLE authorship( " +
            "aname VARCHAR(25), " +
            "callnum INTEGER, " +
            "PRIMARY KEY (aname,callnum), " +
            "FOREIGN KEY (callnum) REFERENCES book(callnum))" +;
        stmt.executeUpdate(str);
        
        system.out.println("Processing... Done. Database is initialized.");
    }

    public void deleteAllTable() throws SQLException {
        //tables should be dropped in an order that avoid violating foreign key constraints
        String[] tables={"borrow","libuser","user_category","copy","authorship","book","book_category"};
        Statement st= null;
        for(int i=0;i<tables.length;i++){
            Statement s=con.createStatement();
            s.executeUpdate("DROP TABLE "+tables[i]);
        }
    }

    public void loadData(String path) throws SQLException, IOException {
        File fUserCategory=new File(path+"\\user_category.txt");
        loadUserCatagory(fUserCategory);

    }

    private void loadUserCatagory(File f) throws IOException, SQLException {
        BufferedReader br=new BufferedReader(new FileReader(f));
        String inputLine;
        while((inputLine= br.readLine())!=null){
            String[] splitedInput=inputLine.split("\\s+");
            Statement s= con.createStatement();
            String inputSQL="INSERT INTO user_category" +
                            "VALUES("+splitedInput[0]+","+splitedInput[1]+","+splitedInput[2]+")";
            s.executeUpdate(inputSQL);
        }


    }

    public void showNumberOfRecords(){

    }
}
