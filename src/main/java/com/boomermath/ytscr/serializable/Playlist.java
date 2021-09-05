package com.boomermath.ytscr.serializable;

public record Playlist(String id, String title, Thumbnail[] thumbnails, Channel author, Video[] videos) {
    public String toString() {
        return title;
    }

    public String url() {
        return "https://www.youtube.com/playlist?list=" + id;
    }
}
