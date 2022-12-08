package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * playlist object contains a list of songs
 * able to add songs, delete songs
 * able to shuffle the playlist and output as a xml file
 */
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

    /**
     *
     * @param filename
     */
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

    /**
     * read a xml and add songs in this xml into this playlist
     * @param filename
     */

    public void getFromXML(String filename) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filename));

            Element root = doc.getDocumentElement();
            //System.out.println("root name: "+root.getNodeName());
            //items include songs, albums, artists
            NodeList items= root.getChildNodes();
            Node currentNode, subNode, leafNode;
            for (int i = 0; i < items.getLength(); i++) {
                currentNode = items.item(i);
                if (currentNode.getNodeName().equals("songs")) {

                    //System.out.println("item name: " + currentNode.getNodeName());
                    /* each of these is a song node. */
                    NodeList songs = currentNode.getChildNodes();
                    //System.out.println(songs.getLength());
                    for (int j = 0; j < songs.getLength(); j++) {
                        subNode = songs.item(j);
                        if (subNode.getNodeName().equals("song")) {

                            //System.out.println("song:" + subNode.getNodeName());
                            NodeList attributess = subNode.getChildNodes();
                            Song s=new Song("");
                            for (int k = 0; k < attributess.getLength(); k++) {
                                String attributename = attributess.item(k).getNodeName();

                                if (!attributename.equals("#text")) {
                                    //System.out.println("song's attribute: " + attributename);
                                    //System.out.println(attributess.item(k).getFirstChild().getNodeValue());
                                    if(attributename.equals("title")){
                                        String name=attributess.item(k).getFirstChild().getNodeValue();
                                        s.setName(name);
                                    }
                                    else if(attributename.equals("artist")){
                                        String name=attributess.item(k).getFirstChild().getNodeValue();
                                        Artist p=new Artist(name);
                                        s.setPerformer(p);

                                    }
                                    else if (attributename.equals("album")){
                                        String name=attributess.item(k).getFirstChild().getNodeValue();
                                        Album a=new Album(name);
                                        s.setAlbum(a);

                                    }

                                }

                            }
                            addSong(s);
                        }
                    }
                }
            }
        }catch (Exception e) {
            System.out.println("Parsing error:" + e);
        }
    }






}
