package org.example;

import junit.framework.TestCase;

import java.sql.*;
import java.util.ArrayList;

public class AlbumTest extends TestCase {

    Album album1,album2;
    Artist artist1;
    Connection connection = null;


    void setup(){
        album1=new Album("white album");
        album2=new Album("white album");
        artist1=new Artist("bettles");
        album1.setArtist(artist1);
        album2.setArtist(artist1);

    }


    public void testTestEquals() {
        setup();
        assertTrue(album1.equals(album2));
    }

    public void testToSQL() {
        try {

            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists artists");
            statement.executeUpdate("create table artists " +
                    "(id INTEGER NOT NULL PRIMARY KEY, name VARCHAR(50) NOT NULL )");
            statement.executeUpdate("drop table if exists albums");
            statement.executeUpdate("create table albums " +
                    "(id INTEGER NOT NULL PRIMARY KEY, name VARCHAR(50) NOT NULL, artist INTEGER)");
            statement.executeUpdate("drop table if exists songs");
            statement.executeUpdate("create table songs " +
                    "(id INTEGER NOT NULL PRIMARY KEY, name VARCHAR(50) NOT NULL, artist INTEGER, album INTEGER, genre VARCHAR(50))");

            Song s=new Song("love story");
            s.entityID=6;
            Artist a=new Artist("taylor");
            Album al=new Album("fearless");
            s.setPerformer(a);
            s.setAlbum(al);
            al.setArtist(a);
            System.out.println(al.toSQL());

            statement.executeUpdate(al.toSQL());
            ResultSet rs = statement.executeQuery("select * from albums where name=\"fearless\"");
            assertTrue(rs.next());

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

    public void testFromSQL() {
        testToSQL();
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            ResultSet rs = statement.executeQuery("select * from albums");
            ArrayList<Album> albumsfromSQL=new ArrayList<>();
            while (rs.next()) {
                // read the result set
                Album al=new Album("");
                al.fromSQL(rs);
                albumsfromSQL.add(al);
            }
            assertEquals(1,albumsfromSQL.size());

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