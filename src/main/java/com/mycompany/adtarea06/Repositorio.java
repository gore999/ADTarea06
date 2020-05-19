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
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DBCollectionFindOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
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

        MongoClientURI uri = new MongoClientURI("mongodb://"+con.getUsername()+":"+con.getPassword()+"@"+con.getAddress()+":"+con.getPort()+"/"+con.getDbname()+"?retryWrites=false");
        MongoClient mgClient = new MongoClient(uri);
 
        //mgClient = new MongoClient(new MongoClientURI("mongodb://" + con.getAddress() + ":" + con.getPort()));//Establecer la conexion.
        DB db = mgClient.getDB(con.getDbname());// Obtener database
        
        System.out.println(mgClient);
        usuarioColeccion = db.getCollection("usuario");
        mensajeColeccion = db.getCollection("mensaxe");
        
    }

    public static Repositorio getInstance() {
        if (rep == null) {
            rep = new Repositorio();
        }
        return rep;
    }

    public void insertarUsuario(Usuario user) {
        //String mensajeJSON = g.toJson(msg);
        DBObject usuarioInsert = new BasicDBObject()
                .append("nome", user.getNome())
                .append("username", user.getUsername())
                .append("password", user.getPassword())
                .append("follows", user.getFollows());
        System.out.println(usuarioInsert);
        // DBBasicObjec
        //DBObject mensajeMongo = (DBObject) JSON.parse(mensajeJSON);
        usuarioColeccion.insert(usuarioInsert);
//        String usuarioJSON = g.toJson(user);
//        // DBBasicObjec
//        DBObject usuarioMongo = (DBObject) JSON.parse(usuarioJSON);
//        usuarioColeccion.insert(usuarioMongo);
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
            us = new Usuario();
            us.setNome((String) documento.get("nome"));
            us.setUsername((String) documento.get("username"));
            us.setPassword((String) documento.get("password"));
            us.setFollows((ArrayList<String>) documento.get("follows"));
//          us = g.fromJson(documento.toString(), Usuario.class);
            if (us.getFollows() == null) {
                us.setFollows(new ArrayList());
            }
            System.out.println(us.toString());

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
                .append("hashtags", msg.getHashtags());
        System.out.println(msgInsert);
        // DBBasicObjec
        //DBObject mensajeMongo = (DBObject) JSON.parse(mensajeJSON);
        mensajeColeccion.insert(msgInsert);
    }

    ArrayList<Usuario> getSeguidos(Usuario user) {
        ArrayList<Usuario> seguidos = new ArrayList();
        System.out.println("Follows es: " + user.getFollows());
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
            us = new Usuario();
            us.setNome((String) documento.get("nome"));
            us.setUsername((String) documento.get("username"));
            us.setPassword((String) documento.get("password"));
            us.setFollows((ArrayList<String>) documento.get("follows"));
        }
        System.out.println(us.toString());
        if (us.getFollows() == null) {
            us.setFollows(new ArrayList());
        }
        return us;
    }

    ArrayList<Usuario> getUsuariosBuscadosPorUsername(String criterio, MiniTwitter mt) {
        ArrayList<Usuario> encontrados = new ArrayList();
        //Filtro con expresion regular para encontrar usuarios cuyo nombre contenga el criterio de busqueda.
        //Bson filter = Filters.or(Filters.regex("username", criterio) );
        //Nuevo Filtro---> solo los que no seguimos. 
        Bson filter = Filters.and(Filters.regex("username", criterio), Filters.nin("username", mt.user.getFollows()), Filters.not(Filters.eq("username", mt.user.getUsername())));//Solo muestra los que no sigue.
        DBObject query = new BasicDBObject(filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        //Opciones de busqueda para paginar.
        DBCollectionFindOptions options = new DBCollectionFindOptions();
        //Crear sort para ordenado.
        Bson ordenar = Sorts.ascending("username");
        DBObject sort = new BasicDBObject(ordenar.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));

        //Añadir restricciones
        options.sort(sort);
        options.limit(MiniTwitter.tamanoPagina);//Como maximo, el numero de resultados que marca la pagina.
        options.skip(mt.numeroPaginaBuscados * MiniTwitter.tamanoPagina); //Se salta los resultados de las paginas anteriores.

        //Iterar resultados.
        DBCursor cursor = usuarioColeccion.find(query, options);
        while (cursor.hasNext()) {
            Usuario us = new Usuario();
            DBObject doc = cursor.next();
            us.setNome((String) doc.get("nome"));
            us.setUsername((String) doc.get("username"));
            us.setPassword((String) doc.get("password"));
            us.setFollows((ArrayList<String>) doc.get("follows"));
            System.out.println(us);
            //if(!mt.user.getUsername().equals(us.getUsername()))encontrados.add(us);//Solo se añade si no es el mismo usuario al logueado.
            encontrados.add(us);
        }
        cursor.close();

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
                filter = Filters.in("user.username", mt.user.follows);
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
        System.out.println("Options" + options.toString());
        DBCursor cursor = this.mensajeColeccion.find(query, options);
        while (cursor.hasNext()) {
            DBObject documento = cursor.next();
            Mensaje m = new Mensaje();
            m.setText((String) documento.get("text"));
            try{
                m.setDate((Date) documento.get("date"));
            }catch(Exception ex){
                 m.setDate(new Date());
            }
            
            m.setHashtags((ArrayList<String>) documento.get("hashtags"));
            DBObject d2 = (DBObject) documento.get("user");
            User u = new User();
            u.setNombre((String) d2.get("nome"));
            u.setUsername((String) d2.get("username"));
            m.setUser(u);
            //msgAux = g.fromJson(documento.toString(), Mensaje.class);
            salida.add(m);
        }
        return salida;
    }

    void updateUser(Usuario user, Usuario uAux) {
        Bson filterUp = Filters.eq("username", user.getUsername()); //El Bson del filtro selecciona los datos del propio usuario.
        DBObject queryUpdate = new BasicDBObject(filterUp.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry())); //Convertir Bson filtro a objeto BD
        Bson updateAux = Updates.addToSet("follows", uAux.getUsername()); //crear Bson actualizacion
        DBObject update = new BasicDBObject(updateAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        usuarioColeccion.update(queryUpdate, update);
    }
}
