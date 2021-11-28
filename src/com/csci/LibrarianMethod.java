package com.csci;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class LibrarianMethod {

    private String formatDate(String hkDate){
        String ans;
        String[] splitedDate=hkDate.split("/");
        ans=splitedDate[2]+"-"+splitedDate[1]+"-"+splitedDate[0];
        return ans;
    }

    private Connection con;

    public void libLoop() throws SQLException {
        Scanner sc=new Scanner(System.in);
        while(true){
            System.out.println("-----Operations for librarian menu-----");
            System.out.println("What kind of operation would you like to perform?");
            System.out.println("1. Book borrowing");
            System.out.println("2. Book returning");
            System.out.println("3. List all un-returned book copies which are checked out within a period ");
            System.out.println("4. Return to the main menu");
            System.out.print("Enter Your Choice:");
            int choice=0;
            try{
                choice=Integer.parseInt(sc.nextLine());
            }
            catch (InputMismatchException e){
                System.out.println("Error: Input should be an integer within 1 to 4!");
                continue;
            }
            if(choice==1){
                borrowBook();
            }
            else if(choice==2){
                returnBook();
            }
            else if(choice==3){
                listALlUnreturned();
            }
            else if(choice==4){
                break;
            }
            else{
                System.out.println("Illegal input!");
                continue;
            }
        }
    }

    public LibrarianMethod(Connection con) {
        this.con = con;
    }

    private void borrowBook() throws SQLException {
        String userID;
        String callNum;
        int copyNum;
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter The User ID: ");
        userID=sc.nextLine();
        System.out.print("Enter The Call Number: ");
        callNum=sc.nextLine();
        System.out.print("Enter The Copy Number: ");
        copyNum=Integer.parseInt(sc.nextLine());
        //first check if the book is checked out
        String checkIfBorrowed="SELECT callnum, copynum FROM borrow WHERE callnum=? AND copynum=? AND return_date IS NULL";
        PreparedStatement cIfBorrowedPS=con.prepareStatement(checkIfBorrowed);
        cIfBorrowedPS.setString(1,callNum);
        cIfBorrowedPS.setInt(2,copyNum);
        ResultSet cifb=cIfBorrowedPS.executeQuery();
        if(!cifb.isBeforeFirst()){
            //The book is available, check if the maximum quota is exceded
            boolean isWithin=true;
            //Firstly get the maximum borrowing quota of the reader
            String maxQuotaQ="SELECT max_books FROM user_category WHERE ucid=(SELECT ucid FROM libuser WHERE libuid=?)";
            PreparedStatement maxQuotaPS=con.prepareStatement(maxQuotaQ);
            maxQuotaPS.setString(1,userID);
            ResultSet rs=maxQuotaPS.executeQuery();
            rs.next();
            int maxQuota=rs.getInt(1);
            //Then, get the number of books he has borrowed
            String getNumBorrowed="SELECT COUNT(libuid) FROM borrow WHERE libuid=? AND return_date IS NULL";
            PreparedStatement getNumBorrowedPS=con.prepareStatement(getNumBorrowed);
            getNumBorrowedPS.setString(1,userID);
            ResultSet rsNumBorrowed=getNumBorrowedPS.executeQuery();
            rsNumBorrowed.next();
            int numBorrowed=rsNumBorrowed.getInt(1);
            if(numBorrowed<maxQuota){
                //insert borrow record
                String updateBorrowed="INSERT INTO borrow(libuid, callnum, copynum, checkout, return_date) VALUES(?,?,?,current_date(),null)";
                PreparedStatement updateBorrowedPS=con.prepareStatement(updateBorrowed);
                updateBorrowedPS.setString(1,userID);
                updateBorrowedPS.setString(2,callNum);
                updateBorrowedPS.setInt(3,copyNum);
                updateBorrowedPS.executeUpdate();
                System.out.println("Book borrowing performed successfully");
            }
            else{
                System.out.println("Error: Maximum quota exceeded");
            }
        }
        else{
            System.out.println("Error: This book has been checked out.");
        }
    }

    private void returnBook() throws SQLException {
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter The User ID:");
        String userID=sc.nextLine();
        System.out.print("Enter The Call Number:");
        String callNum=sc.nextLine();
        System.out.print("Enter The Copy Number: ");
        int copyNum=Integer.parseInt(sc.nextLine());
        String updateRecords="UPDATE borrow SET return_date=current_date() WHERE (libuid=? AND callnum=? AND copynum=? AND return_date IS NULL)";
        PreparedStatement updateRecordsPS=con.prepareStatement(updateRecords);
        updateRecordsPS.setString(1,userID);
        updateRecordsPS.setString(2,callNum);
        updateRecordsPS.setInt(3,copyNum);
        int edited=updateRecordsPS.executeUpdate();
        if(edited==0){
            //test foreign key violations
            System.out.println("Error: Borrow record not found");
        }
        else{
            do{
                int userScore;
                System.out.print("Enter Your Rating of the Book:");
                try{
                    userScore=Integer.parseInt(sc.nextLine());
                }
                catch(InputMismatchException e){
                    System.out.println("Error: Please input a number from 1 to 10.");
                    continue;
                }
                if(userScore<=10&&userScore>=1){
                    String updateBookRating="UPDATE book SET rating=(1.0*coalesce(rating,0)*tborrowed+?*1.0)/(1.0*tborrowed+1)*1.0 WHERE callnum=?";
                    PreparedStatement bookRatingPS=con.prepareStatement(updateBookRating);
                    bookRatingPS.setInt(1,userScore);
                    bookRatingPS.setString(2,callNum);
                    bookRatingPS.executeUpdate();
                    //update tborrowed in book
                    String updateBook="UPDATE book SET tborrowed=tborrowed+1 WHERE callnum=?";
                    PreparedStatement psBook=con.prepareStatement(updateBook);
                    psBook.setString(1,callNum);
                    psBook.executeUpdate();
                    System.out.println("Book returning performed successfully");
                    break;
                }
                else {
                    System.out.print("Please input a number from 1 to 10.");
                }
            }while(true);
        }
    }
    private void listALlUnreturned() throws SQLException {
        Scanner sc=new Scanner(System.in);
        System.out.print("Type in the starting date [dd/mm/yyyy]: ");
        String startingDate=formatDate(sc.nextLine());
        System.out.print("Type in the ending date [dd/mm/yyyy]: ");
        String endDate=formatDate(sc.nextLine());
        String unreturnedDuringQ="SELECT libuid,callnum,copynum,checkout FROM borrow WHERE (return_date IS NULL) AND checkout>=? AND checkout<=? ORDER BY checkout";
        PreparedStatement ps=con.prepareStatement(unreturnedDuringQ);
        ps.setDate(1,Date.valueOf(startingDate));
        ps.setDate(2,Date.valueOf(endDate));
        ResultSet rs=ps.executeQuery();
        System.out.println("List of UnReturned Book:");
        System.out.println("|LibUID|CallNum|CopyNum|Checkout|");
        while(rs.next()){
            System.out.print("|"+rs.getString(1));
            System.out.print("|"+rs.getString(2));
            System.out.print("|"+ rs.getInt(3));
            System.out.print("|"+rs.getString(4));
            System.out.print("|\n");
        }
        System.out.println("End of query");
    }
}
