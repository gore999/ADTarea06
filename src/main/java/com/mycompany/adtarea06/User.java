/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.adtarea06;

/**
 *
 * @author Carlos
 */
public class User {
        private String nome;
        private String username;
        public User() {
            
        }

        public User(String nome, String username) {
            this.nome = nome;
            this.username = username;
        }

        public String getNombre() {
            return nome;
        }

        public void setNombre(String nombre) {
            this.nome = nombre;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public String toString() {
            return "User{" + "nombre=" + nome + ", username=" + username + '}';
        }
        
    }

