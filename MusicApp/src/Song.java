import java.util.*;

public class Song implements Comparable<Song> {
    private final String title;
    private final String artist;
    private final int bpm;

//Constructor
    public Song(String title, String artist, int bpm) {
        this.title = title;
        this.artist = artist;
        this.bpm = bpm;
    }

    @Override
    public int compareTo(Song other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song s)) return false;
        return title.equalsIgnoreCase(s.title) && artist.equalsIgnoreCase(s.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title.toLowerCase(), artist.toLowerCase());
    }

    @Override
    public String toString() {
        return "%-25s | %-15s | %3d BPM".formatted(title, artist, bpm);
    }

    // Getters
    public String title()  { return title; }
    public String artist() { return artist; }
    public int getBpm()    { return bpm; }
}
