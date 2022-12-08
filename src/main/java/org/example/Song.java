package org.example;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Song is a object inherited from Entity
 */
public class Song extends Entity {
    protected Album album;
    protected Artist performer;
    protected SongInterval duration;
    protected String genre;

    public Song(String name) {
        super(name);
        album = new Album("");
        performer = new Artist("");
        duration = new SongInterval(0);
        genre = "";

    }

    public Song(String name, int length) {
        super(name);
        duration = new SongInterval(length);
        genre = "";
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setLength(int length) {
        duration = new SongInterval(length);
    }

    public String showLength() {
        return duration.toString();
    }


    protected Album getAlbum() {
        return album;
    }

    protected void setAlbum(Album album) {
        this.album = album;
    }

    public Artist getPerformer() {
        return performer;
    }

    public void setPerformer(Artist performer) {
        this.performer = performer;
    }


    /**
     * generate a sql execution statement inserting this song to the database
     * @return a sql statement
     */
    public String toSQL() {
        return "insert into songs (id, name, artist, album, genre) values (" + this.entityID + ", \"" + this.name + "\", "
                + performer.entityID  + ", "+ album.entityID+ ", \"" + this.genre + "\""+ ");";
    }
    /**
     * generate a xml format string
     * @return a sql statement
     */
    public String toXML() {
        return "\n<song> <title>"+name +"</title> <artist>"+ performer.getName()
                +"</artist> <album>"+album.getName()+"</album> </song>";

    }
    /**
     * set this object the name and id read from database
     * @param rs the data from database
     */
    public void fromSQL(ResultSet rs) {
        try {
            this.entityID = rs.getInt("id");
            this.name = rs.getString("name");
        } catch(SQLException e) {
            System.out.println("SQL Exception" + e.getMessage());
        }

    }



}

