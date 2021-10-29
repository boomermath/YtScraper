package gq.boomermath.ytscr;

import gq.boomermath.ytscr.entities.YoutubePlaylist;
import gq.boomermath.ytscr.entities.YoutubeVideo;
import gq.boomermath.ytscr.internal.Serializer;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Youtube {
    private static YoutubeVideo[] searchQuery(String query, int limit) {
        JSONObject inputData = HTMLParser.parse("https://www.youtube.com/results?search_query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
        return Serializer.processQuery(inputData, limit);
    }

    public static YoutubeVideo[] search(String query) {
        return searchQuery(query, 0);
    }

    public static YoutubeVideo[] search(String query, int limit) {
        if (limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0!");
        return searchQuery(query, limit);
    }

    public static YoutubeVideo searchOne(String query) {
        return searchQuery(query, 1)[0];
    }

    public static YoutubeVideo getVideo(String url) {
        String urlID = HTMLParser.getQueryParameter(url, "v");

        if (urlID == null) {
            throw new IllegalArgumentException("URL \"" + url + "\" is an invalid youtube url!");
        }

        JSONObject[] inputData = HTMLParser.parseVideoPlayerInfo("https://www.youtube.com/watch?v=" + urlID);
        return Serializer.processVideo(inputData[0], inputData[1]);
    }

    public static YoutubePlaylist getPlaylist(String url) {
        String playlistID = HTMLParser.getQueryParameter(url, "list");

        if (playlistID == null ||
                !playlistID.startsWith("PL") &&
                        !playlistID.startsWith("FL") &&
                        !playlistID.startsWith("LL") &&
                        !playlistID.startsWith("UU") &&
                        !playlistID.startsWith("RDC") &&
                        !playlistID.startsWith("O")
        ) {
            throw new IllegalArgumentException("URL \"" + url + "\" is an invalid youtube playlist url!");
        }

        JSONObject inputData = HTMLParser.parse("https://www.youtube.com/playlist?list=" + playlistID);
        return Serializer.processPlaylist(inputData);
    }
}
