package org.example;

import junit.framework.TestCase;

import java.sql.*;
import java.util.ArrayList;

public class ArtistTest extends TestCase {
    Song s1;
    Song s2;
    Album a1;
    Artist p1;
    Connection connection = null;

    void setup(){

        s1=new Song("love story");
        s2=new Song("love");

        a1=new Album("love");
        p1=new Artist("taylor");
        s1.setPerformer(p1);
        s2.setPerformer(p1);
        s1.setAlbum(a1);
        s2.setAlbum(a1);


    }

    public void testAddSong() {
        setup();
        p1.addSong(s1);
        assertEquals(1,p1.getSongs().size());
        p1.addSong(s2);
        assertEquals(2,p1.getSongs().size());

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
            System.out.println(a.toSQL());

            statement.executeUpdate(a.toSQL());
            ResultSet rs = statement.executeQuery("select * from artists where name=\"taylor\"");
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

            ResultSet rs = statement.executeQuery("select * from artists");
            ArrayList<Artist> artistsfromSQL=new ArrayList<>();
            while (rs.next()) {
                // read the result set
                Artist a=new Artist("");
                a.fromSQL(rs);
                artistsfromSQL.add(a);
            }
            assertEquals(1,artistsfromSQL.size());

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