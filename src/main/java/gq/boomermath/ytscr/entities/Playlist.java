package gq.boomermath.ytscr.serializable;

public final class Playlist {
    private final String id;
    private final String title;
    private final Thumbnail[] thumbnails;
    private final Channel author;
    private final Video[] videos;

    public Playlist(String id, String title, Thumbnail[] thumbnails, Channel author, Video[] videos) {
        this.id = id;
        this.title = title;
        this.thumbnails = thumbnails;
        this.author = author;
        this.videos = videos;
    }

    public String toString() {
        return title;
    }

    public String url() {
        return "https://www.youtube.com/playlist?list=" + id;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public Thumbnail[] thumbnails() {
        return thumbnails;
    }

    public Channel author() {
        return author;
    }

    public Video[] videos() {
        return videos;
    }
}
