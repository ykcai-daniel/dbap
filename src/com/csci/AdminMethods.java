package com.csci;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AdminMethods {
    private final Connection con;

    public void adminLoop() throws SQLException, IOException {
        while(true){
            System.out.println("-----Operations for administrator menu-----");
            System.out.println("1. Create all tables");
            System.out.println("2. Delete all tables");
            System.out.println("3. Load from datafile");
            System.out.println("4. Show number of records");
            System.out.println("5. Return to the main menu");
            System.out.print("Enter Your Choice:");
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            if(choice==1){
                createTable();
            }
            else if(choice==2){
                deleteAllTable();
            }
            else if(choice==3){
                sc.nextLine();
                System.out.println("");
                System.out.print("Type in the Source Data Folder Path:");
                String path=sc.nextLine();
                loadData(path);
            }
            else if(choice==4){
                showNumberOfRecords();
            }
            else if(choice==5){
                break;
            }
            else{
                System.out.print("Illegal input");
            }
        }
    }
	
    public AdminMethods(Connection con) throws SQLException {
        this.con = con;
    }

    private void createTable() throws SQLException {
        /* need to add some code to check success or fail. */
        Statement stmt = con.createStatement();
        String str = "CREATE TABLE user_category( " +
            "ucid Integer, " +
            "max_books Integer NOT NULL, " +
            "loan_period Integer NOT NULL, " +
            "PRIMARY KEY (ucid), " +
            "CHECK (max_books<100 AND max_books>0), " +
            "CHECK (loan_period<100 AND loan_period>0)," +
            "CHECK (ucid<10 AND ucid > 0))";
        try{
            stmt.executeUpdate(str);
        }catch(Exception e){/*do nothing*/}
        
        str = "CREATE TABLE libuser( " +
            "libuid VARCHAR(10), " +
            "name VARCHAR(25) NOT NULL, " +
            "age INTEGER NOT NULL, " +
            "address VARCHAR(100) NOT NULL, " +
            "ucid INTEGER, " +
            "PRIMARY KEY (libuid)," +
            "FOREIGN KEY (ucid) REFERENCES user_category(ucid)," +
            "CHECK (AGE<1000 AND AGE>0))";/*three digit age?*/
        try{
            stmt.executeUpdate(str);
        }catch(Exception e){/*do nothing*/}
        
        str = "CREATE TABLE book_category( " +
            "bcid INTEGER, " +
            "bcname VARCHAR(30) NOT NULL, " +
            "PRIMARY KEY (bcid)," +
            "CHECK(bcid>0 AND bcid<10))";
        try{
            stmt.executeUpdate(str);
        }catch(Exception e){/*do nothing*/}
        
        str = "CREATE TABLE book( " +
            "callnum VARCHAR(8), " +
            "title VARCHAR(30) NOT NULL, " +
            "publish VARCHAR(10) NOT NULL, " +
            "rating FLOAT DEFAULT NULL, " +
            "tborrowed INTEGER NOT NULL DEFAULT 0, " +
            "bcid INTEGER NOT NULL, " +
            "PRIMARY KEY (callnum), " +
            "FOREIGN KEY (bcid) REFERENCES book_category(bcid), " +
            "CHECK(tborrowed>-1 and tborrowed<100 and LEN(callnum)=8))";
        /*rating should be updated after returning book, rating can be null before first borrow!!!!!*/
        /*tborrow needs update with each borrow!!!!!!*/
        /*we can do these updates with an addition UPDATE statement*/

        /* callnum is a string that length is 8 */
        /* publish is not date type actually. The given format in data is different from the real date format. */
        try{
            stmt.executeUpdate(str);
        }catch(Exception e){/*do nothing*/}
        
        str = "CREATE TABLE copy( " +
            "callnum VARCHAR(8), " +
            "copynum INTEGER, " +
            "PRIMARY KEY (callnum,copynum), " +
            "FOREIGN KEY (callnum) REFERENCES book(callnum))";  /*what is copynum*/
        try{
            stmt.executeUpdate(str);
        }catch(Exception e){/*do nothing*/}
        
        str = "CREATE TABLE borrow( " +
            "libuid VARCHAR(10), " +
            "callnum VARCHAR(8), " +
            "copynum INTEGER, " +
            "checkout VARCHAR(10) NOT NULL , " +
            "return_data VARCHAR(10) NOT NULL, " +
            "PRIMARY KEY (libuid,callnum,copynum,checkout), " +
            "FOREIGN KEY (libuid) REFERENCES libuser(libuid), " +
            "FOREIGN KEY (callnum,copynum) REFERENCES copy(callnum, copynum))"; /*return can be null!!!!!*/
        try{
            stmt.executeUpdate(str);
        }catch(Exception e){/*do nothing*/}

        /* Non-empty string with at most 25 characters for each author. EACH!!! */
        str = "CREATE TABLE authorship( " +
            "aname VARCHAR(100), " +
            "callnum VARCHAR(8), " +
            "PRIMARY KEY (aname,callnum), " +
            "FOREIGN KEY (callnum) REFERENCES book(callnum))";
        try{
            stmt.executeUpdate(str);
        }catch(Exception e){/*do nothing*/}
        stmt.close();
        System.out.println("Processing... Done. Database is initialized.");
    }

    private void deleteAllTable() throws SQLException {
        /* tables should be dropped in an order that avoid violating foreign key constraints */
        String[] tables={"borrow","libuser","user_category","copy","authorship","book","book_category"};
        Statement stmt=con.createStatement();
        for(int i=0;i<tables.length;i++){
            try{
                stmt.executeUpdate("DROP TABLE "+tables[i]);
            }catch(Exception e){/*do nothing*/}
        }
        stmt.close();
        System.out.println("Processing... Done. Database is removed.");
    }

    private void loadData(String path) throws SQLException, IOException {
        try{
            loadUserCatagory(path);
            loadLibuser(path);
            loadBookCategory(path);
            loadBook(path);
            loadCopy(path);
            loadBorrow(path);
            loadAuthorship(path);
            System.out.println("Processing...Done. Data is inputted to the database.");
        }catch(FileNotFoundException e){
            System.out.println("Cannot find the datafile.");
        }
    }

    private void loadUserCatagory(String path) throws IOException, SQLException {
        File f = new File(path + "/user_category.txt");
        BufferedReader br = new BufferedReader(new FileReader(f));
        String inputLine;
        Statement stmt = con.createStatement();
        while ((inputLine = br.readLine()) != null) {
            String[] splitedInput = inputLine.split("\t");
            String inputSQL = "INSERT INTO user_category " +
                    "VALUES(" + splitedInput[0] + "," + splitedInput[1] + "," + splitedInput[2] + ")";
            stmt.executeUpdate(inputSQL);
        }
        stmt.close();
    }

    private void loadLibuser(String path) throws IOException, SQLException {
        File f = new File(path+"/user.txt");
        BufferedReader br=new BufferedReader(new FileReader(f));
        String inputLine;
        Statement stmt= con.createStatement();
        while((inputLine= br.readLine())!=null){
            inputLine = inputLine.replace("\'","\\\'");
            /* To avoid interference of char ' */
            String[] splitedInput=inputLine.split("\t");
            String inputSQL="INSERT INTO libuser " +
                    "VALUES ('"+splitedInput[0]+"','" +
                    splitedInput[1] + "'," +
                    splitedInput[2] + ",'" +
                    splitedInput[3] + "'," +
                    splitedInput[4] + ")";
            stmt.executeUpdate(inputSQL);
        }
        stmt.close();
    }

    private void loadBookCategory(String path) throws IOException, SQLException {
        File f = new File(path+"/book_category.txt");
        BufferedReader br=new BufferedReader(new FileReader(f));
        String inputLine;
        Statement stmt= con.createStatement();
        while((inputLine= br.readLine())!=null){
            inputLine = inputLine.replace("\'","\\\'");
            String[] splitedInput=inputLine.split("\t");
            String inputSQL="INSERT INTO book_category " +
                    "VALUES ("+splitedInput[0]+",'"+splitedInput[1]+"')";
            stmt.executeUpdate(inputSQL);
        }
        stmt.close();
    }

    private void loadBook(String path) throws IOException, SQLException {
        File f = new File(path+"/book.txt");
        BufferedReader br=new BufferedReader(new FileReader(f));
        String inputLine;
        Statement stmt= con.createStatement();
        while((inputLine= br.readLine())!=null){
            inputLine = inputLine.replace("\'","\\\'");
            String[] splitedInput=inputLine.split("\t");
            String inputSQL="INSERT INTO book " +
                    "VALUES ('"+splitedInput[0]+"','" +
                    splitedInput[2] + "','" +
                    splitedInput[4] + "'," +
                    splitedInput[5] + "," +
                    splitedInput[6] + "," +
                    splitedInput[7] + ")";
            stmt.executeUpdate(inputSQL);
        }
        stmt.close();
    }

    private void loadCopy(String path) throws IOException, SQLException {
        File f = new File(path+"/book.txt");
        BufferedReader br=new BufferedReader(new FileReader(f));
        String inputLine;
        Statement stmt= con.createStatement();
        while((inputLine= br.readLine())!=null){
            inputLine = inputLine.replace("\'","\\\'");
            String[] splitedInput=inputLine.split("\t");
            /*Different from other table. Copynum means how many copy we have. For each copy, we need to insert a tuple. */
            int callnum = Integer.parseInt(splitedInput[1]);
            for(int i=1; i <= callnum; i++){
                String inputSQL="INSERT INTO copy " +
                        "VALUES ('"+splitedInput[0]+"',"+ i +")";
                stmt.executeUpdate(inputSQL);
            }
        }
        stmt.close();
    }

    private void loadBorrow(String path) throws IOException, SQLException {
        File f = new File(path+"/check_out.txt");
        BufferedReader br=new BufferedReader(new FileReader(f));
        String inputLine;
        Statement stmt= con.createStatement();
        while((inputLine= br.readLine())!=null){
            inputLine = inputLine.replace("\'","\\\'");
            String[] splitedInput=inputLine.split("\t");
            String inputSQL="INSERT INTO borrow " +
                    "VALUES ('"+splitedInput[2]+"','" +
                    splitedInput[0] + "'," +
                    splitedInput[1] + ",'" +
                    splitedInput[3] + "','" +
                    splitedInput[4] + "')";
            stmt.executeUpdate(inputSQL);
        }
        stmt.close();
    }

    private void loadAuthorship(String path) throws IOException, SQLException {
        File f = new File(path + "/book.txt");
        BufferedReader br=new BufferedReader(new FileReader(f));
        String inputLine;
        Statement stmt= con.createStatement();
        while((inputLine= br.readLine())!=null){
            inputLine = inputLine.replace("\'","\\\'");
            String[] splitedInput=inputLine.split("\t");
            String inputSQL="INSERT INTO authorship " +
                    "VALUES ('"+splitedInput[3]+"','"+splitedInput[0]+"')";
            stmt.executeUpdate(inputSQL);
        }
        stmt.close();
    }

    private void showNumberOfRecords() throws  SQLException {
        Statement stmt = con.createStatement();
        System.out.println("Number of records in each table:");
        ResultSet rset = stmt.executeQuery("SELECT COUNT(*) FROM user_category");
        rset.next();
        System.out.println("user_category (ucid, max, period): "+rset.getInt(1));
        rset = stmt.executeQuery("SELECT COUNT(*) FROM libuser");
        rset.next();
        System.out.println("libuser (libuid, name, age, address, ucid): "+rset.getInt(1));
        rset = stmt.executeQuery("SELECT COUNT(*) FROM book_category");
        rset.next();
        System.out.println("book_category (bcid, bcname): "+rset.getInt(1));
        rset = stmt.executeQuery("SELECT COUNT(*) FROM book");
        rset.next();
        System.out.println("book (callnum, title, publish, rating, tborrowed, bcid): "+rset.getInt(1));
        rset = stmt.executeQuery("SELECT COUNT(*) FROM copy");
        rset.next();
        System.out.println("copy (callnum, copynum): "+rset.getInt(1));
        rset = stmt.executeQuery("SELECT COUNT(*) FROM borrow");
        rset.next();
        System.out.println("borrow (libuid, callnum, copynum, checkout, return): "+rset.getInt(1));
        rset = stmt.executeQuery("SELECT COUNT(*) FROM authorship");
        rset.next();
        System.out.println("authorship (aname, callnum): "+rset.getInt(1));
        rset.close();
        stmt.close();
    }
}
