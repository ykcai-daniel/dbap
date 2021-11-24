package com.csci;

import java.sql.Connection;

public class LibrarianMethod {
    private Connection con;

    public LibrarianMethod(Connection con) {
        this.con = con;
    }

    public void borrowBook(String callNumber,int copyNumber, String userID){

    }
    public void returnBook(String callNumber,int copyNumber, String userID){

    }
    public void listALl(){

    }
}
