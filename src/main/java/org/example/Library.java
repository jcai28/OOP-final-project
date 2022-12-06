package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Library {
    private ArrayList<Song> songs;

    public Library() {
        songs = new ArrayList<Song>();
    }
    public boolean findSong(Song s) {
        return songs.contains(s);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void addSong(Song s) {
        songs.add(s);
    }
    public void deletSong(Song s){
        if (songs.contains(s)){
            songs.remove(s);}
        else{
            System.out.printf("%s is not in the Library\n", s.toString());
        }


    }
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

    public void getFromJSON(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename));
            sc.useDelimiter("\\Z");
            String s = sc.next();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(s);
            JSONObject jsonObject = (JSONObject)obj;
            JSONArray songs=(JSONArray) jsonObject.get("songs");
            for (Object song :songs ){
                JSONObject cur=(JSONObject) song;
                String title=(String) cur.get("title");
                System.out.println(title);
                Song newsong=new Song(title);
                JSONObject artisst=(JSONObject) cur.get("artist");
                JSONObject album=(JSONObject) cur.get("album");
                String p=(String)artisst.get("name");
                newsong.setPerformer(new Artist(p));
                System.out.println(p);
                String a=(String)album.get("name");
                newsong.setAlbum(new Album(a));
                System.out.println(a);
                addSong(newsong);

            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found " + e);
        } catch (ParseException e1) {
            System.out.println("Parser error");
        }
    }
    private static boolean possibleDplicate(Song s, Song other) {
        if (s.getName().equals(other.getName()) && (s.getPerformer().equals(other.getPerformer()) ||
                    (s.getAlbum().equals(other.getAlbum())))) {
                return true;
        }
        else if (s.getPerformer().equals(other.getPerformer()) &&
                s.getAlbum().equals(other.getAlbum()) &&
                (s.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "")
                    .equals(other.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "")))) {
                return true;
        }
        else{
            return false;
        }

    }
    public void findDuplicates(){
        //i wrote both test in junit and main method, more interaction in main method


        Scanner sc=new Scanner(System.in);

        for (int i=0; i<songs.size(); i++){

            int j=i+1;
            while (j<songs.size()){

                Song a=songs.get(i);
                Song b=songs.get(j);
//                System.out.println(i);
//                System.out.println(j);
//                System.out.println(a);
//                System.out.println(b);
                if (a.equals(b)){
                    String userInput=null;
                    while (true) {
                        System.out.println(a + " and " + b + " are duplicates, do you want to delete one?(y/n) ");
                        userInput = sc.nextLine().toLowerCase();
                        if (userInput.equals("y")) {
                            deletSong(b);
                            System.out.println("duplicate deleted");
                            j-=1;
                            break;

                        } else if (userInput.equals("n")) {
                            break;
                        } else {
                            System.out.println("please enter y or n");
                        }
                    }


                }
                else if (possibleDplicate(a,b)){
                    String userInput=null;
                    while (true) {
                        System.out.println(a + " and " + b + " are possible duplicates, do you want to delete one?(y/n) ");
                        userInput = sc.nextLine().toLowerCase();
                        if (userInput.equals("y")) {
                            deletSong(b);
                            System.out.println("duplicate deleted");
                            j-=1;
                            break;

                        } else if (userInput.equals("n")) {
                            break;
                        } else {
                            System.out.println("please enter y or n");
                        }
                    }

                }
                j+=1;
            }

        }

    }
    public void writeXML(String filename){
        String result="<library>" + "<songs>";
        for (Song s : songs) {
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
