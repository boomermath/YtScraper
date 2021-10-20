package gq.boomermath.ytscr;

import gq.boomermath.ytscr.serializable.Playlist;

public class Main {
    public static void main(String[] args) {
        Playlist playlist = Youtube.getPlaylist("https://www.youtube.com/playlist?list=RDCLAK5uy_k27uu-EtQ_b5U2r26DNDZOmNqGdccUIGQ");

        System.out.println(playlist.videos()[0].title());
    }
}
