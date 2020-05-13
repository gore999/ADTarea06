/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.adtarea06;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Carlos
 */
public class Usuario implements Serializable {
    String nome;
    String username;
    String password;
    ArrayList<String> follows;
    public Usuario() {
    
    }

    Usuario(String nombre, String usuario, String password) {
        nome=nombre;
        username=usuario;
        this.password=password;
        follows=new ArrayList();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public ArrayList<String> getFollows() {
        return follows;
    }

    public void setFollows(ArrayList<String> follows) {
        this.follows = follows;
    }

    @Override
    public String toString() {
        return "Usuario{" + "nome=" + nome + ", username=" + username + ", password=" + password + ", follows=" + follows + '}';
    }
    
    
}
