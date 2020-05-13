/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.adtarea06;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Carlos
 */
public class Mensaje implements Serializable{
    String text;
    User user;
    Date date;
    ArrayList<String> hashtags;

    public Mensaje() {
        hashtags=new ArrayList();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(ArrayList<String> hashtags) {
        this.hashtags = hashtags;
    }

   

    @Override
    public String toString() {
        return "Mensaje{" + "text=" + text + ", user=" + user + ", date="+date + ", hashtags=" + hashtags + '}';
    }

    
    
}
