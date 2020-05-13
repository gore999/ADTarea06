/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.adtarea06;

import java.io.Serializable;

/**
 *
 * @author Carlos
 */
public class Conexion implements Serializable{
    private String address;
    private String port;
    private String dbname;
    private String username;
    private String password;
    Conexion(){}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Conexion{" + "address=" + address + ", port=" + port + ", dbname=" + dbname + ", username=" + username + ", password=" + password + '}';
    }
    
}
