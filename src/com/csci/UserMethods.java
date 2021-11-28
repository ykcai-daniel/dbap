package com.csci;

import java.sql.*;
import java.util.Scanner;

public class UserMethods {

    private Connection con;

    public void userLoop(){
        while(true){
            System.out.println("-----Operations for user menu-----");
            System.out.println("1. Search for books");
            System.out.println("2. Show loan record of a user");
            System.out.println("3. Return to the main menu");
            System.out.print("Enter Your Choice:");
            Scanner sc = new Scanner(System.in);
            int choice = Integer.parseInt(sc.nextLine());
            if(choice == 1){
                System.out.println("Choose the Search criterion:");
                System.out.println("1. call number");
                System.out.println("2. title");
                System.out.println("3. author");
                System.out.print("Choose the Search criterion: ");
                choice = Integer.parseInt(sc.nextLine());
                System.out.print("Type in the Search Keyword: ");
                String keyword = sc.nextLine();
                System.out.println("|Call Num|Title|Book Category|Author|Rating|Available No. of Copy|");
                if(choice == 1){
                    try{
                        searchForBookByCallNumber(keyword);
                    }catch(Exception e){/*do nothing*/}
                }
                else if(choice == 2){
                    try{
                        searchForBookByTitle(keyword);
                    }catch (Exception e){/*do nothing*/}
                }
                else if(choice == 3){
                    try{
                        searchForBookByAuthor(keyword);
                    }catch(Exception e){/*do nothing*/}
                }
                else{
                    System.out.println("Illegal input!");
                    continue;
                }
            }
            else if(choice == 2){
                System.out.print("Enter The User ID: ");
                String userID = sc.nextLine();
                try{
                    showBorrowRecord(userID);
                }catch(Exception e){/*do nothing*/}
            }
            else if(choice == 3){
                break;
            }
            else{
                System.out.println("Illegal input!");
                continue;
            }
        }
    }
    

    public UserMethods(Connection con) throws SQLException{
        this.con = con;
    }

    private void searchForBookByCallNumber(String callNumber) throws SQLException{
        String title = "";
        String bcname = "";
        String aname = "";
        Float rating = null;
        int numAvailableCopy = 0;
        String sql = "SELECT title, rating, bcid FROM book WHERE callnum = '" + callNumber + "'";
        Statement stmt = con.createStatement();
        ResultSet rset = stmt.executeQuery(sql);
        if(rset.next()){
            title = rset.getString("title");
            int bcid = rset.getInt("bcid");
            rating = rset.getFloat("rating");
            if(rset.wasNull()){
                rating = null;
            }
            sql = "SELECT bcname FROM book_category WHERE bcid = '" + String.valueOf(bcid) + "'";
            rset = stmt.executeQuery(sql);
            rset.next();
            bcname = rset.getString("bcname");
            sql = "SELECT aname FROM authorship WHERE callnum = '" + callNumber + "'";
            rset = stmt.executeQuery(sql);
            rset.next();
            aname = rset.getString("aname");
            sql = "SELECT COUNT(*) FROM borrow WHERE callnum = '" + callNumber + "' AND return_date IS NULL";
            rset = stmt.executeQuery(sql);
            rset.next();
            int numBorrowedCopy = rset.getInt(1);
            sql = "SELECT COUNT(*) FROM copy WHERE callnum = '" + callNumber + "'";
            rset = stmt.executeQuery(sql);
            rset.next();
            numAvailableCopy = rset.getInt(1) - numBorrowedCopy;
            System.out.print("|" + callNumber + "|" + title + "|" + bcname + "|" + aname + "|");
            if(rating == null)
                System.out.println("null|" + String.valueOf(numAvailableCopy) + "|");
            else
                System.out.println(String.valueOf(rating) + "|" + String.valueOf(numAvailableCopy) + "|");
        }
        else
            System.out.println("Call number " + callNumber + " not found!");
        rset.close();
        stmt.close();
    }

    private void searchForBookByTitle(String title) throws SQLException{
        String sql = "SELECT callnum FROM book WHERE title LIKE '%" + title + "%' ORDER BY callnum ASC";
        Statement stmt = con.createStatement();
        ResultSet rset = stmt.executeQuery(sql);
        if(!rset.isBeforeFirst()){
            System.out.println("Title " + title + " not found!");
            return;
        }
        while(rset.next()){
            searchForBookByCallNumber(rset.getString("callnum"));
        }
        rset.close();
        stmt.close();
    }

    private void searchForBookByAuthor(String author) throws SQLException{
        String sql = "SELECT callnum FROM authorship WHERE aname LIKE '%" + author + "%' ORDER BY callnum ASC";
        Statement stmt = con.createStatement();
        ResultSet rset = stmt.executeQuery(sql);
        if(!rset.isBeforeFirst()){
            System.out.println("Author " + author + " not found!");
            return;
        }
        while(rset.next()){
            searchForBookByCallNumber(rset.getString("callnum"));
        }
        rset.close();
        stmt.close();
    }

    private void showBorrowRecord(String userID) throws SQLException{
        System.out.println("Loan Record:");
        System.out.println("|CallNum|CopyNum|Title|Author|Check-out|Returned?|");
        String sql = "SELECT callnum, copynum, checkout, return_date FROM borrow WHERE libuid = '" + userID + "' ORDER BY checkout DESC";
        Statement stmt = con.createStatement();
        ResultSet rset = stmt.executeQuery(sql);
        if(!rset.isBeforeFirst()){
            System.out.println("No record found for userID " + userID);
            return;
        }
        while(rset.next()){
            String callNumber = rset.getString("callnum");
            int copyNumber = rset.getInt("copynum");
            Date checkoutDate = rset.getDate("checkout");
            Date returnDate = rset.getDate("return_date");
            String returned = "Yes";
            if(rset.wasNull()){
                returned = "No";
            }
            Statement stmt2 = con.createStatement();
            ResultSet rset2 = stmt2.executeQuery("SELECT aname FROM authorship WHERE callnum = '" + callNumber + "'");
            rset2.next();
            String author = rset2.getString("aname");
            rset2 = stmt2.executeQuery("SELECT title FROM book WHERE callnum = '" + callNumber + "'");
            rset2.next();
            String title = rset2.getString("title");
            System.out.println("|" + callNumber + "|" + String.valueOf(copyNumber) + "|" + title + "|" + author + "|" + checkoutDate.toString() + "|" + returned + "|");
        }
        rset.close();
        stmt.close();
    }

}
