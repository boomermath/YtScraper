package gq.boomermath.ytscr.entities;

public final class YoutubePlaylist {
    private final String id;
    private final String title;
    private final YoutubeThumbnail[] thumbnails;
    private final YoutubeChannel author;
    private final YoutubeVideo[] videos;

    public YoutubePlaylist(String id, String title, YoutubeThumbnail[] thumbnails, YoutubeChannel author, YoutubeVideo[] videos) {
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

    public YoutubeThumbnail[] thumbnails() {
        return thumbnails;
    }

    public YoutubeChannel author() {
        return author;
    }

    public YoutubeVideo[] videos() {
        return videos;
    }
}
