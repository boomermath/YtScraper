package gq.boomermath.ytscr.serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public record Video(String id, String title, String description,
                    String duration, String uploaded,
                    Thumbnail[] thumbnails,
                    Channel author, long views) {

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
}
