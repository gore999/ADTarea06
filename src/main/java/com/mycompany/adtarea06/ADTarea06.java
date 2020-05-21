/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.adtarea06;

import java.awt.Toolkit;

/**
 *
 * @author Carlos
 */
public class ADTarea06 {
  
    static int COORD_X=Toolkit.getDefaultToolkit().getScreenSize().width/2;
    static int COORD_Y=Toolkit.getDefaultToolkit().getScreenSize().height/2;
    public static void main(String[] args) {
        Repositorio rep=Repositorio.getInstance();
        LoginJFrame login=new LoginJFrame();
        login.setRepositorio(rep);
        login.setVisible(true);
    }
}
