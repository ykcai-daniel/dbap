package com.csci;

import java.sql.Connection;

public class LibrarianMethod {
    private Connection con;

    public void libLoop(){
        while(true){
            break;
        }
    }

    public LibrarianMethod(Connection con) {
        this.con = con;
    }

    private void borrowBook(String callNumber,int copyNumber, String userID){

    }
    private void returnBook(String callNumber,int copyNumber, String userID){

    }
    private void listALl(){

    }
}
