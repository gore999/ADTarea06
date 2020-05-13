/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.adtarea06;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.model.DBCollectionFindOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

/**
 *
 * @author Carlos
 */
public class Repositorio {

    Conexion con;
    private static Repositorio rep;
    MongoClient mgClient;
    //Colecciones: 
    DBCollection usuarioColeccion;
    DBCollection mensajeColeccion;
    Gson g;
    private Repositorio() {
        g = new Gson();
        File f = new File("conf.json");//File que representa al json
        FileReader json = null;
        try {
            json = new FileReader(f);
        } catch (FileNotFoundException ex) {
        }
        con = g.fromJson(json, Conexion.class);//Crear el objeto a partir de datos json.
        System.out.println(con);
        String conJson = g.toJson(con);
        System.out.println(conJson);
        //Configurar base de datos
        mgClient = new MongoClient(new MongoClientURI("mongodb://" + con.getAddress() + ":" + con.getPort()));//Establecer la conexion.
        DB db = mgClient.getDB(con.getDbname());// Obtener database
        usuarioColeccion = db.getCollection("usuario");
        mensajeColeccion = db.getCollection("mensaje");
    }

    public static Repositorio getInstance() {
        if (rep == null) {
            rep = new Repositorio();
        }
        return rep;
    }

    public void insertarUsuario(Usuario user) {
        String usuarioJSON = g.toJson(user);
        // DBBasicObjec
        DBObject usuarioMongo = (DBObject) JSON.parse(usuarioJSON);
        usuarioColeccion.insert(usuarioMongo);
    }

    public boolean existeUsuario(String username) {
        boolean salida = false;
        System.out.println("Nombre de user a comprobar: " + username);
        System.out.println("Buscando");
        DBObject query = new BasicDBObject("username", username);
        DBCursor cursor = usuarioColeccion.find(query);
        while (cursor.hasNext()) {
            DBObject documento = cursor.next();
            System.out.println(documento.toString());
            salida = true;
        }

        return salida;
    }

    public Usuario getUsuarioLogin(String user, String pass) {
        Usuario us = null;
        DBObject query = new BasicDBObject().append("username", user).append("password", pass);
        DBObject documento = usuarioColeccion.findOne(query);
        if (documento != null) {
            us = g.fromJson(documento.toString(), Usuario.class);
        }
        System.out.println(us.toString());
        if (us.getFollows() == null) {
            us.setFollows(new ArrayList());
        }
        return us;
    }

    //MENSAJES
    public void insertarMensaje(Mensaje msg) {
        
        //String mensajeJSON = g.toJson(msg);
        
        
        DBObject msgInsert = new BasicDBObject()
        .append("text", msg.getText())
        .append("user", new BasicDBObject()
                .append("nome", msg.getUser().getNombre())
                .append("username", msg.getUser().getUsername()))
        .append("date", new Date())
        .append("hashtags", Arrays.asList(msg.getHashtags()));
        System.out.println(msgInsert);
        // DBBasicObjec
        //DBObject mensajeMongo = (DBObject) JSON.parse(mensajeJSON);
        mensajeColeccion.insert(msgInsert);
    }

    ArrayList<Usuario> getSeguidos(Usuario user) {
        ArrayList<Usuario> seguidos = new ArrayList();
        for (String usuarioSeguido : user.getFollows()) {
            seguidos.add(getUsuarioByUserName(usuarioSeguido));
        }
        return seguidos;
    }

    Usuario getUsuarioByUserName(String username) {
        Usuario us = null;
        DBObject query = new BasicDBObject().append("username", username);
        DBObject documento = usuarioColeccion.findOne(query);
        if (documento != null) {
            us = g.fromJson(documento.toString(), Usuario.class);
        }
        System.out.println(us.toString());
        if (us.getFollows() == null) {
            us.setFollows(new ArrayList());
        }
        return us;
    }

    ArrayList<Usuario> getUsuariosBuscadosPorUsername(String criterio) {
        Usuario us = null;
        ArrayList<Usuario> encontrados = new ArrayList();
        //Filtro con expresion regular para encontrar usuarios cuyo nombre contenga el criterio de busqueda.
        Bson filter = Filters.or(Filters.regex("username", criterio));
        DBObject query = new BasicDBObject(filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        System.out.println(query.toString());
        DBCursor cursor2 = usuarioColeccion.find(query);
        while (cursor2.hasNext()) {
            DBObject documento = cursor2.next();
            us = g.fromJson(documento.toString(), Usuario.class);
            encontrados.add(us);
        }
        cursor2.close();
        for (Usuario u : encontrados) {
            System.out.println(u.toString());
        }
        return encontrados;
    }

//    Recupera mensajes en funcion de una query que se pasa por paramtro. 
//    }
    ArrayList<Mensaje> getMensajes(MiniTwitter mt) {
        ArrayList<Mensaje> salida = new ArrayList();
        Mensaje msgAux = null;
        DBObject query = null;
        Bson filter;
        //Filtrado, segun lo que estemos mostrando en la tabla, se establece un filtro.
        switch (mt.estadoPanelMensajes) {
            case 0: // Todos los mensajes.
                query = new BasicDBObject();
                break;
            case 1:// Mensajes por usuario
                filter = Filters.in("user.username",mt.user.follows);
                query = new BasicDBObject(filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
                System.out.println(query.toString());
                System.out.println(mt.user.toString());
                break;
            case 2:// Mensajes por hashtag.
                filter = Filters.in("hashtags", mt.cadenaAux);//Usamos la cadena auxiliar
                query = new BasicDBObject(filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
                System.out.println(query);
                break;

        }
        System.out.println("buscando mensajes");
        //Paginado, comun para todos los casos;
        DBCollectionFindOptions options = new DBCollectionFindOptions();
        //Crear sort para ordenado.
        Bson ordenar = Sorts.descending("date");
        DBObject sort = new BasicDBObject(ordenar.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        //Añadir restricciones
        options.sort(sort);
        options.limit(MiniTwitter.tamanoPagina);//Como maximo, el numero de resultados que marca la pagina.
        options.skip(mt.numeroPagina * MiniTwitter.tamanoPagina); //Se salta los resultados de las paginas anteriores.
        // Iterar
        System.out.println("Options"+options.toString());
        DBCursor cursor = this.mensajeColeccion.find(query, options);
        while (cursor.hasNext()) {
            DBObject documento = cursor.next();
            Mensaje m=new Mensaje();
            m.setText((String) documento.get("text"));
            m.setDate((Date)documento.get("date"));
            m.setHashtags((ArrayList<String>)documento.get("hashtags"));
            User u=new User();
            u.setNombre((String)documento.get("nome"));
            u.setUsername((String)documento.get("username"));
            m.setUser(u);
            //msgAux = g.fromJson(documento.toString(), Mensaje.class);
            salida.add(m);
        }
        return salida;
    }


    void updateUser(Usuario user) {
        System.out.println(user.toString());
    }
}
