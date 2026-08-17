# MusicLibrary — Java Collections Framework

A console application exploring the Java Collections Framework through a small
song library: ordering, equality, deduplication and map keys. Written for a
**Station 2 Hackathon** submission.

The interesting part is not the library itself — it is what the collections do
when a domain object defines `equals`, `hashCode` and `compareTo`, and how those
three interact. See [What this demonstrates](#what-this-demonstrates).

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?logo=openjdk&logoColor=white)

---

## Running it

```bash
cd MusicApp
javac -d out src/*.java
java -cp out Main
```

No dependencies, no build tool. Verified on OpenJDK 25; requires Java 17 or
later for the record-style accessors, `instanceof` pattern matching and
`String.formatted`.

---

## The domain object

`Song` is a small immutable class carrying `title`, `artist` and `bpm`, and it
overrides three things that the collections rely on:

```java
public class Song implements Comparable<Song> {
    // natural order: by title, case-insensitive
    public int compareTo(Song other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    // equality: title AND artist, case-insensitive
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song s)) return false;
        return title.equalsIgnoreCase(s.title) && artist.equalsIgnoreCase(s.artist);
    }

    public int hashCode() {
        return Objects.hash(title.toLowerCase(), artist.toLowerCase());
    }
}
```

The seed data deliberately contains a duplicate — `"Lost on You"` by `LB`
appears twice — so the deduplicating structures have something to actually do.

---

## What this demonstrates

### 1. Traversal: for-each vs `Iterator`

Both loops are shown over the same `ArrayList`. The for-each form is syntactic
sugar over exactly the same iterator; the explicit form matters when you need
`it.remove()`, which is the only safe way to delete during traversal.

### 2. Deduplication via `HashSet`

```java
Set<Song> unique = new HashSet<>(library);   // 6 songs in, 5 out
```

This only works because `equals` and `hashCode` are overridden together. With
the default identity-based implementations the two `"Lost on You"` objects would
be distinct references and nothing would be removed.

The set is also **unordered** — the printed order is a function of hash buckets,
not insertion or alphabetical order.

### 3. Three different orderings

| Ordering | Mechanism |
|---|---|
| By artist | `Comparator.comparing(Song::artist, String.CASE_INSENSITIVE_ORDER)` |
| By BPM | `Comparator.comparingInt(Song::getBpm)` |
| By title | `Collections.sort()` → the class's own `compareTo` |

Natural order lives *inside* the class; alternative orders are supplied from
outside. That is the whole distinction between `Comparable` and `Comparator`.

### 4. Objects as `HashMap` keys — where it gets subtle

The ratings map keys on `Song` itself:

```java
Map<Song, Integer> rating = new HashMap<>();
rating.put(library.get(0), 5);
// ... six put() calls in total
```

**Six `put` calls produce five entries.** After sorting by title the two
`"Lost on You"` rows sit at indices 3 and 4, and because they are `equals` with
matching `hashCode`, the second `put` does not add a row — it *overwrites* the
first one's value. Actual output:

```
-- Ratings --
Feel Good Inc             | Gorillaz        | 133 BPM ⭐⭐⭐⭐
Hard Times                | Paramore        | 117 BPM ⭐⭐⭐
Lost on You               | LB              |  96 BPM ⭐⭐
Candy Eyes                | WonderBag       | 133 BPM ⭐⭐⭐⭐⭐
TroubleMan                | Electric Guest  | 171 BPM ⭐
```

`Lost on You` shows 2 stars, the value from the *later* `put` — the 4 assigned
first was silently replaced. This is the single most useful thing in the
program: a mutable-looking six-line block that quietly produces five results,
entirely because of how `equals` was defined.

### 5. A deliberate inconsistency worth noticing

`equals` compares **title and artist**. `compareTo` compares **title only**.

That makes the natural ordering *inconsistent with equals*, in the precise sense
the `Comparable` documentation warns about: two songs sharing a title but by
different artists return `compareTo == 0` while being `!equals`. Nothing breaks
here, but it would: a `TreeSet` treats `compareTo == 0` as "already present" and
would silently drop the second song, where `HashSet` keeps both.

Same objects, same data, two collections disagreeing about what a duplicate is.

---

## Requirements covered

From the Station 2 brief:

- [x] A `Song` class with three member variables
- [x] Iterate with both for-each and `Iterator`
- [x] Remove duplicates
- [x] Store ratings
- [x] Print the `keySet` with stars

## Project structure

```
MusicApp/
├── src/
│   ├── Song.java     domain class — Comparable, equals, hashCode, toString
│   └── Main.java     console demo of every collection behaviour above
└── README.txt        original hackathon submission notes
```

`toString` uses `"%-25s | %-15s | %3d BPM".formatted(...)` for fixed-width
columns, which is what makes the console output line up.

## Possible extensions

- Add a `TreeSet<Song>` next to the `HashSet` to show the two disagreeing on the
  duplicate — the point in section 5, made executable
- Fix the inconsistency by comparing artist as a tiebreaker in `compareTo`
- Replace `Song` with a `record`, which generates `equals`/`hashCode` for free
  and would change the duplicate behaviour again

## Author

**Shehan Nimsara** — B.Sc. Software Design (International), TH Aschaffenburg
