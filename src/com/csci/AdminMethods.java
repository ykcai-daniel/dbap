package com.csci;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminMethods {
    private Connection con;

    public AdminMethods(Connection con) {
        this.con = con;
    }

    public void createTable(){

    }

    public void deleteAllTable()  {
        Statement st= null;
        try {
            st = con.createStatement();
            ResultSet tables=st.executeQuery("show tables");
            while (tables.next()) {
                System.out.println(tables.getString(0));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadData(String path){
        
    }

    public void showNumberOfRecords(){

    }
}
