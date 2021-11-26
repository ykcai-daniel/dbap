package com.csci;

import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class LibrarianMethod {
    private Connection con;

    public void libLoop(){
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

            }
            else if(choice==2){

            }
            else if(choice==3){

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

    private void borrowBook(String callNumber,int copyNumber, String userID){

    }
    private void returnBook(String callNumber,int copyNumber, String userID){

    }
    private void listALl(){

    }
}
