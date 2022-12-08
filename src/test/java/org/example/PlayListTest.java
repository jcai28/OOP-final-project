package org.example;

import junit.framework.TestCase;

public class PlayListTest extends TestCase {
    PlayList pl;
    Song s1;
    Song s2;
    Album a1;
    Artist p1;

    void setup(){
        pl=new PlayList();
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
        pl.addSong(s1);
        assertEquals(1,pl.getSonglist().size());
        pl.addSong(s2);
        assertEquals(2,pl.getSonglist().size());


    }

    public void testDeletSong() {
        setup();
        testAddSong();
        pl.deletSong(s1);
        assertEquals(1,pl.getSonglist().size());
        pl.deletSong(s2);
        assertEquals(0,pl.getSonglist().size());
    }


    public void testWriteXML() {
        setup();
        testAddSong();
        pl.writeXML("writeXML.xml");
        pl.getFromXML("writeXML.xml");
        assertEquals(pl.getSonglist().size(),4);

    }
}