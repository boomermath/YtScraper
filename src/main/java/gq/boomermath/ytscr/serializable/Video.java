package gq.boomermath.ytscr.serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public final class Video {
    private final String id;
    private final String name;
    private final String description;
    private final String duration;
    private final String uploaded;
    private final Thumbnail[] thumbnails;
    private final Channel author;
    private final long views;

    public Video(String id, String name, String description,
                 String duration, String uploaded,
                 Thumbnail[] thumbnails,
                 Channel author, long views) {
        this.id = id;
        this.name = name;
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
        return name;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
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

    public Thumbnail[] thumbnails() {
        return thumbnails;
    }

    public Channel author() {
        return author;
    }

    public long views() {
        return views;
    }
}
