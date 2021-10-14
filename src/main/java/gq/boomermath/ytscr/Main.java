package gq.boomermath.ytscr;

import gq.boomermath.ytscr.serializable.Playlist;
import gq.boomermath.ytscr.serializable.Video;

import java.net.URL;

public class Main {
    public static void main(String[] args) {
        Playlist v = Youtube.getPlaylist("PLOHoVaTp8R7dfrJW5pumS0iD_dhlXKv17");
        log(v.videos()[0]);
    }

    public static void log(Video v) {
        System.out.println(v.id());
        System.out.println(v.name());
        System.out.println(v.url());
        System.out.println(v.duration());
        System.out.println(v.durationSeconds());
        System.out.println(v.embedURL());
        System.out.println(v.uploaded());
        System.out.println(v.thumbnails()[0]);
        System.out.println(v.author().iconURL(2056));
        System.out.println(v.author().title());
        System.out.println(v.views());
        System.out.println(v.description());
    }
}
