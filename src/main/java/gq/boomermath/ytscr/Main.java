package gq.boomermath.ytscr;

import gq.boomermath.ytscr.entities.YoutubePlaylist;

public class Main {
    public static void main(String[] args) {
        YoutubePlaylist playlist = Youtube.getPlaylist("https://www.youtube.com/playlist?list=PLAuXvMFaTiZxNllvtCAObLD2tq31W0tgk");

        System.out.println(playlist.videos()[0].title());
    }
}
