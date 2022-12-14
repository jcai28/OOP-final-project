package org.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.*;

import static org.example.App.TheAudioDBAlbumAPI;
import static org.example.App.TheAudioDBSongsAPI;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {


        assertTrue( true );
    }

    /**
     * test if the method can correctly insert all the songs and albums of the input artist into database
     */
    public void testTheAudioDBAlbumAPI() {
        Connection connection = null;
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
            TheAudioDBAlbumAPI("coldplay","test.db");
            ResultSet rs = statement.executeQuery("select * from albums where id=2109614");
            assertTrue(rs.next());
            ResultSet rs2 = statement.executeQuery("select * from songs where id=32724169");
            assertTrue(rs2.next());

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

    /**
     * test if the method can correctly insert all the songs in the input album into database
     */
    public void testTheAudioDBSongsAPI() {

        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate("drop table if exists songs");
            statement.executeUpdate("create table songs " +
                    "(id INTEGER NOT NULL PRIMARY KEY, name VARCHAR(50) NOT NULL, artist INTEGER, album INTEGER, genre VARCHAR(50))");
            TheAudioDBSongsAPI("2115888", "test.db");
            ResultSet rs = statement.executeQuery("select * from songs where id=32793508");
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
}
