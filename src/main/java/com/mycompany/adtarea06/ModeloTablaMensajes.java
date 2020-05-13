/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.adtarea06;

import java.util.ArrayList;
import java.util.Date;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author Carlos
 */
public class ModeloTablaMensajes implements TableModel {

    ArrayList<Mensaje> mensajes;

    public ModeloTablaMensajes(ArrayList<Mensaje> mensajes) {
        this.mensajes = mensajes;
        if (mensajes == null) {
            mensajes = new ArrayList();
        }

    }

    @Override
    public int getRowCount() {
        return mensajes.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        String salida = "";
        switch (columnIndex) {
            case 0:
                salida = "Fecha";
                break;
            case 1:
                salida = "Nombre";
                break;
            case 2:
                salida = "Username";
                break;
            case 3:
                salida = "Contenido";
                break;
        }
        return salida;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Date.class;
            case 1:

                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Mensaje m = this.mensajes.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return m.getDate();
            case 1:
                return m.getUser().getNombre();
            case 2:
                return m.getUser().getUsername();
            case 3:
                return m.getText();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Mensaje m = mensajes.get(rowIndex);
        switch (columnIndex) {
            case 0:
                m.setDate((Date) aValue);
                break;
            case 1:
                m.getUser().setNombre((String) aValue);
                break;
            case 2:
                m.getUser().setUsername((String) aValue);
                break;
            case 3:
                m.setText((String) aValue);
                break;
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }
}
