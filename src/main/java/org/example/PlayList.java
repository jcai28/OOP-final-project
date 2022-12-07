package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlayList {
    private ArrayList<Song> songlist;

    public ArrayList<Song> getSonglist() {
        return songlist;
    }

    public PlayList(){
        songlist=new ArrayList<Song>();

    }

    public void addSong(Song s){
        songlist.add(s);

    }
    public void deletSong(Song s){
        if (songlist.contains(s)){
            songlist.remove(s);}
        else{
            System.out.printf("%s is not in the playlist\n", s.toString());
        }


    }

    public  void  shuffle(){
        Collections.shuffle(songlist);
    }

    public void writeXML(String filename){
        String result="<library>" + "<songs>";
        for (Song s : songlist) {
            result += s.toXML();
        }
        result= result + "</songs>"+"</library>" ;
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(result);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }





}
