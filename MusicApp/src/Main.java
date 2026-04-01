
import java.util.*;

public class Main {

    private static List<Song> seedSongs() {
        return List.of(
                new Song("TroubleMan",  "Electric Guest", 171),
                new Song("Feel Good Inc",  "Gorillaz", 133),
                new Song("Lost on You",   "LB",  96),// duplicate
                new Song("Candy Eyes",  "WonderBag", 133),
                new Song("Hard Times",    "Paramore", 117),
                new Song("Lost on You",   "LB",  96)  // duplicate

        );
    }

    // Comparator by artist name
    private static final Comparator<Song> BY_ARTIST =
            Comparator.comparing(Song::artist, String.CASE_INSENSITIVE_ORDER);

    // Comparator by BPM (ascending)
    private static final Comparator<Song> BY_BPM =
            Comparator.comparingInt(Song::getBpm);

    public static void main(String[] args) {
        List<Song> library = new ArrayList<>(seedSongs());

        System.out.println("-- For-each loop --");
        for (Song s : library) System.out.println(s);

        System.out.println("\n-- Iterator --");
        for (Iterator<Song> it = library.iterator(); it.hasNext(); )
            System.out.println(it.next());

        // De-duplicate with HashSet
        Set<Song> unique = new HashSet<>(library);
        System.out.println("\nAfter HashSet (duplicates gone):");
        unique.forEach(System.out::println);

        // Sorting demos
        library.sort(BY_ARTIST);
        System.out.println("\nSorted by artist:");
        library.forEach(System.out::println);

        library.sort(BY_BPM);
        System.out.println("\nSorted by BPM:");
        library.forEach(System.out::println);

        Collections.sort(library); // natural (title) order
        System.out.println("\nSorted by title (natural):");
        library.forEach(System.out::println);

        // Ratings map
        Map<Song, Integer> rating = new HashMap<>();
        rating.put(library.get(0), 5);
        rating.put(library.get(1), 4);
        rating.put(library.get(2), 3);
        rating.put(library.get(3),4);
        rating.put(library.get(4),2);
        rating.put(library.get(5),1);

        System.out.println("\n-- Ratings --");
        for (Song s : rating.keySet()) {
            System.out.println(s + " " + "⭐".repeat(rating.get(s)));
        }
    }
}
