package gq.boomermath.ytscr;

import gq.boomermath.ytscr.serializable.Playlist;
import gq.boomermath.ytscr.serializable.Video;
import gq.boomermath.ytscr.serializer.Serializer;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Youtube {
    private static Video[] searchQuery(String query, int limit) {
        JSONObject inputData = HTMLParser.parse("https://www.youtube.com/results?search_query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
        return Serializer.processQuery(inputData, limit);
    }

    public static Video[] search(String query) {
        return searchQuery(query, 0);
    }

    public static Video[] search(String query, int limit) {
        if (limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0!");
        return searchQuery(query, limit);
    }

    public static Video searchOne(String query) {
        return searchQuery(query, 1)[0];
    }

    public static Video getVideo(String url) {
        String urlID = HTMLParser.getQueryParameter(url, "v");

        if (urlID == null) {
            throw new IllegalArgumentException("URL \"" + url + "\" is an invalid youtube url!");
        }

        JSONObject[] inputData = HTMLParser.parse("https://www.youtube.com/watch?v=" + urlID, true);
        return Serializer.processVideo(inputData[0], inputData[1]);
    }

    public static Playlist getPlaylist(String url) {
        String playlistID = HTMLParser.getQueryParameter(url, "list");

        if (playlistID == null) {
            throw new IllegalArgumentException("URL \"" + url + "\" is an invalid youtube playlist url!");
        }

        JSONObject inputData = HTMLParser.parse("https://www.youtube.com/playlist?list=" + playlistID);
        return Serializer.processPlaylist(inputData);
    }
}
