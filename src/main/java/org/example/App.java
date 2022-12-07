package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App
{

    public static void main( String[] args )
    {
        Connection connection = null;
        String database="music.db";

        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:"+ database);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            //newDB(database);




            Scanner userInput=new Scanner(System.in);
            System.out.println("Hello, welcome to your personal music Library!" +
                    "\nType 1 for show all your music" +
                    "\nType 2 for return a random playlist " +
                    "\nType 3 for add an artist into your library" +
                    "\nType e for exit"
            );
            String input=userInput.nextLine();
            while (!input.equals("e")){
                if (input.equals("1")){
                    ResultSet rs = statement.executeQuery("select artists.name as a, songs.name as s, songs.genre as g from songs " +
                            "inner join artists on songs.artist == artists.id " );
                    while (rs.next()) {
                        // read the result set1
                        System.out.println( rs.getString("a")+","+rs.getString("s")+","+
                                rs.getString("g"));

                    }
                }else if(input.equals("2")){
                    ResultSet rs = statement.executeQuery("select distinct genre from songs " );
                    ArrayList<String> genres=new ArrayList<>();
                    if (!rs.next()){
                        System.out.println("empty library, try type 3 add some artist's music ");
                        System.out.println(
                                "\nType 1 for show all your music" +
                                        "\nType 2 for return a random playlist " +
                                        "\nType 3 for add an artist into your library" +
                                        "\nType e for exit");
                        input=userInput.nextLine();
                        continue;
                    }
                    while (rs.next()) {
                        // read the result set1
                        String g=rs.getString("genre");
                        if(! g.equals("null")){
                            genres.add(g);
                        }
                    }
                    System.out.println(genres);
                    Collections.shuffle(genres);
                    System.out.println(genres);
                    String pick= genres.get(0);
                   //select
                    ResultSet playlist = statement.executeQuery("select artists.id as aid, songs.id as id, albums.id as alid, " +
                            "artists.name as a, songs.name as s, albums.name as al,  songs.genre as g" +
                            " from ((songs inner join artists on songs.artist == artists.id) " +
                            "inner join albums on songs.album == albums.id)"+
                            "where genre= "+"\""+pick+"\"" );
                    PlayList mylist=new PlayList();

                    while (playlist.next()) {
                        String aname=playlist.getString("a");
                        Artist a=new Artist(aname);
                        a.entityID=Integer.parseInt(playlist.getString("aid"));
                        String s=playlist.getString("s");
                        Song song=new Song(s);
                        song.entityID=Integer.parseInt(playlist.getString("id"));

                        Album al=new Album(playlist.getString("al"));
                        al.entityID=Integer.parseInt(playlist.getString("alid"));
                        song.setPerformer(a);
                        song.setAlbum(al);
                        mylist.addSong(song);
                        System.out.println(a.name+","+song.name+","+al.name+","+playlist.getString("g"));

                    }
                    mylist.writeXML("newPlayList");



                }
                else if(input.equals("3")){
                    System.out.println("Please enter a artist's name");
                    String aname=userInput.nextLine();

                    ResultSet rs = statement.executeQuery("select * from songs inner join artists on songs.artist == " +
                            "artists.id " +
                            "where artists.name="+"\""+aname.toLowerCase()+"\"");

                    if (!rs.next()) {
                        System.out.println("artist not in library, adding the artist");
                        TheAudioDBAlbumAPI(aname.toLowerCase().replaceAll(" ","_"), database);

                    }
                    ResultSet newrs = statement.executeQuery("select * from songs inner join artists on songs.artist == " +
                            "artists.id " +
                            "where artists.name="+"\""+aname.toLowerCase()+"\"");
                    System.out.println("here are all songs of this artist:");
                    while (newrs.next()) {
                        // read the result set

                        System.out.println( rs.getString("name"));
                    }

                }
                System.out.println(
                        "\nType 1 for show all your music" +
                        "\nType 2 for return a random playlist " +
                        "\nType 3 for add an artist into your library" +
                        "\nType e for exit");
                    input=userInput.nextLine();


            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

    }
    public static void TheAudioDBAlbumAPI(String name, String database) {
        String requestURL = "https://theaudiodb.com/api/v1/json/523532/searchalbum.php?s=";
        String artist = name;
        StringBuilder response = new StringBuilder();
        URL u;
        try {
            u = new URL(requestURL + artist);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            return;
        }
        try {
            URLConnection connection = u.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            //System.out.println(code + " " + message);
            if (code != HttpURLConnection.HTTP_OK) {
                System.out.println("erro"+code);
                return;
            }
            InputStream instream = connection.getInputStream();
            Scanner in = new Scanner(instream);

            while (in.hasNextLine()) {

                response.append(in.nextLine());

            }

        } catch (IOException e) {
            System.out.println("Error reading response");
            return;
        }
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray albums = (JSONArray)jsonObject.get("album"); // get the list of all albums.
            if (albums==null){
                System.out.println("no such artist find, " +
                        "\ntry use a complete name, like 'the beatles' instead of 'beatles'");
                return;
            }
            JSONObject album1=(JSONObject) albums.get(0);
            String artistID= (String)album1.get("idArtist");
            String artistName= (String)album1.get("strArtist");
            Artist a=new Artist(artistName.toLowerCase());
            a.entityID=Integer.parseInt(artistID);
            Connection connection = null;
            try {
                // create a database connection
                connection = DriverManager.getConnection("jdbc:sqlite:music.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                statement.executeUpdate(a.toSQL());
                for (int i=0; i<albums.size();i++){
                    JSONObject album=(JSONObject) albums.get(i);
                    String id = (String)album.get("idAlbum");
                    //System.out.println("id: " + id);
                    String alname = (String)album.get("strAlbum");
                    //System.out.println("name: " + alname);
                    Album al=new Album(alname);
                    al.entityID=Integer.parseInt(id);
                    al.setArtist(a);
                    statement.executeUpdate(al.toSQL());
                    TheAudioDBSongsAPI(id,database);


                }

            } catch (SQLException e) {
                // if the error message is "out of memory",
                // it probably means no database file is found
                System.err.println(e.getMessage());
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }

        } catch(ParseException e) {
            System.out.println("Error parsing JSON");
            return;
        }

    }
    public static void TheAudioDBSongsAPI(String albumID, String database) {
        String requestURL = "https://theaudiodb.com/api/v1/json/2/track.php?m=";;
        StringBuilder response = new StringBuilder();
        URL u;
        try {
            u = new URL(requestURL + albumID);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            return;
        }
        try {
            URLConnection connection = u.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            //System.out.println(code + " " + message);
            if (code != HttpURLConnection.HTTP_OK) {
                return;
            }
            InputStream instream = connection.getInputStream();
            Scanner in = new Scanner(instream);
            while (in.hasNextLine()) {
                response.append(in.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
            return;
        }
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray tracks = (JSONArray)jsonObject.get("track"); // get the list of all tracks returned.
            Connection connection = null;
            try {
                // create a database connection
                connection = DriverManager.getConnection("jdbc:sqlite:"+database);
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                JSONObject track=(JSONObject) tracks.get(0);
                String artistID= (String)track.get("idArtist");
                String artistName= (String)track.get("strArtist");
                Artist a=new Artist(artistName);
                a.entityID=Integer.parseInt(artistID);
                String alid = (String)track.get("idAlbum");
                //System.out.println("id: " + alid);
                String alname = (String)track.get("strAlbum");
                //System.out.println("name: " + alname);
                Album al=new Album(alname);
                al.entityID=Integer.parseInt(alid);

                for (int i=0; i<tracks.size();i++){
                    JSONObject song=(JSONObject) tracks.get(i);
                    String id = (String)song.get("idTrack");
                    //System.out.println("id: " + id);
                    String name = (String)song.get("strTrack");
                    //System.out.println("name: " + name);
                    String genre=(String)song.get("strGenre");

                    Song s=new Song(name);
                    s.setAlbum(al);
                    s.setPerformer(a);
                    if (genre!=null){
                    s.setGenre(genre.toLowerCase());}
                    s.entityID=Integer.parseInt(id);
                    statement.executeUpdate(s.toSQL());
                }

            } catch (SQLException e) {
                // if the error message is "out of memory",
                // it probably means no database file is found
                System.err.println(e.getMessage());
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }

        } catch(ParseException e) {
            System.out.println("Error parsing JSON");
            return;
        }

    }

    public static void newDB(String database){
        Connection connection = null;
        try {
            // create a database connection

            connection = DriverManager.getConnection("jdbc:sqlite:"+ database);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate("drop table if exists songs");
            statement.executeUpdate("create table songs " +
                    "(id INTEGER NOT NULL PRIMARY KEY, name VARCHAR(50) NOT NULL, artist INTEGER, album INTEGER, genre VARCHAR(50))");
            statement.executeUpdate("drop table if exists artists");
            statement.executeUpdate("create table artists " +
                    "(id INTEGER NOT NULL PRIMARY KEY, name VARCHAR(50) NOT NULL )");
            statement.executeUpdate("drop table if exists albums");
            statement.executeUpdate("create table albums " +
                    "(id INTEGER NOT NULL PRIMARY KEY, name VARCHAR(50) NOT NULL, artist INTEGER )");
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }
}
