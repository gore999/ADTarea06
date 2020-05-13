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
public class ADTarea06 {
    public static void main(String[] args) {
        Repositorio rep=Repositorio.getInstance();
        LoginJFrame login=new LoginJFrame();
        login.setRepositorio(rep);
        login.setVisible(true);
    }
}
