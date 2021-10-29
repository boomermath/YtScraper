package gq.boomermath.ytscr.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public final class YoutubeVideo {
    private final String id;
    private final String title;
    private final String description;
    private final String duration;
    private final String uploaded;
    private final YoutubeThumbnail[] thumbnails;
    private final YoutubeChannel author;
    private final long views;

    public YoutubeVideo(String id, String title, String description,
                        String duration, String uploaded,
                        YoutubeThumbnail[] thumbnails, YoutubeChannel author, long views) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.uploaded = uploaded;
        this.thumbnails = thumbnails;
        this.author = author;
        this.views = views;
    }

    public long durationSeconds() {
        String pattern = duration.split(":").length == 2 ? "mm:ss" : "HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return simpleDateFormat.parse(duration).getTime() / 1000;
        } catch (ParseException e) {
            return 0;
        }
    }

    public String url() {
        return "https://www.youtube.com/watch?v=" + id;
    }

    public String embedURL() {
        return "https://www.youtube.com/embed/" + id;
    }

    public String toString() {
        return title;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String duration() {
        return duration;
    }

    public String uploaded() {
        return uploaded;
    }

    public YoutubeThumbnail[] thumbnails() {
        return thumbnails;
    }

    public YoutubeChannel author() {
        return author;
    }

    public long views() {
        return views;
    }
}
