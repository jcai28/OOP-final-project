package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Album is a object inherited from Entity
 */
public class Album extends Entity {
    /**
     * a list of songs in this album
     */
    protected ArrayList<Song> songs;
    /**
     * performer of the album
     */
    protected Artist artist;

    /**
     * creat a new album
     * @param name album's name
     */
    public Album(String name) {
        super(name);
    }

    /**
     * return the name of the album
     * @return
     */
    public String getName() {

        return name;
    }

    /**
     * compare this album object to other album object
     * @param otherAlbum the album object compare with
     * @return if the otherAlbum have same artist and name with this album object
     */
    public boolean equals(Album otherAlbum) {
        if ((this.artist.equals(otherAlbum.getArtist())) &&
                (this.name.equals(otherAlbum.getName()))) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * return all songs in the album
     * @return
     */
    protected ArrayList<Song> getSongs() {
        return songs;
    }

    protected void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    /**
     * return the artist of the album
     * @return
     */
    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    /**
     * generate a sql execution statement inserting this album to the database
     * @return a sql statement
     */
    public String toSQL() {
        return "insert into albums (id, name,artist) values (" + this.entityID + ", \"" + this.name + "\", "
                + artist.entityID + ");";
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
