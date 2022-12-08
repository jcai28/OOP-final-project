package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * Artist is a object inherited from Entity
 */
public class Artist extends Entity {

    protected ArrayList<Song> songs;
    protected ArrayList<Album> albums;

    public Artist(String name) {
        super(name);
        songs=new ArrayList<>();

    }

    protected ArrayList<Song> getSongs() {
        return songs;
    }

    protected void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    protected ArrayList<Album> getAlbums() {
        return albums;
    }

    protected void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    /**
     * add song s to the songs list of this artist
     * @param s
     */
    public void addSong(Song s) {
        songs.add(s);
    }

    /**
     * generate a sql execution statement inserting this artist to the database
     * @return
     */
    public String toSQL() {
        return "insert into artists (id, name) values (" + this.entityID + ", \"" + this.name + "\" "+ ");";
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
