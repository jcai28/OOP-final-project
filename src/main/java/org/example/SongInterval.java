package org.example;

/**
 * a helper class for song represent the duration of song
 */
public class SongInterval {
    private int length;

    public SongInterval(int l) {
        this.length = l;
    }


    public String toString() {
        int minutes = length / 60;
        int seconds = length % 60;
        return String.format("%d:%d", minutes, seconds);

    }

}
