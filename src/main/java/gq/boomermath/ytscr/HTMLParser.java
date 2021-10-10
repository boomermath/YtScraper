package gq.boomermath.ytscr;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HTMLParser {
    private static final String DEFAULT_PREFIX = "var ytInitialData =";
    private static final String PLAYER_PREFIX = "var ytInitialPlayerResponse =";

    public static JSONObject parse(String url) {
        try {
            Document document = Jsoup.connect(url).get();

            for (Element node : document.select("script")) {
                String data = node.data().trim();

                if (data.startsWith(DEFAULT_PREFIX)) {
                    return new JSONObject(data.split(DEFAULT_PREFIX)[1]);
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    protected static JSONObject[] parse(String url, boolean video) {
        if (!video) return null;

        try {
            Document document = Jsoup.connect(url).get();

            JSONObject[] jsonArgs = new JSONObject[2];
            for (Element node : document.select("script")) {
                String data = node.data().trim();

                if (data.startsWith(DEFAULT_PREFIX)) {
                    jsonArgs[0] = new JSONObject(data.split(DEFAULT_PREFIX)[1]);
                } else if (data.startsWith(PLAYER_PREFIX)) {
                    jsonArgs[1] = new JSONObject(data.split(PLAYER_PREFIX)[1]);
                }
            }

            return jsonArgs;
        } catch (IOException e) {
            return null;
        }
    }

    private static String getQueryParameter(String input, String queryParam) throws MalformedURLException {
        URL url = new URL(input);

        if (!url.getHost().equals("youtube.com") && !url.getHost().equals("www.youtube.com")) throw new MalformedURLException("Input is not a valid youtube url!");

        String[] queryString = url.getQuery().split("&");
        HashMap<String, String> queryParams = new HashMap<>();

        for (String param : queryString) {
            String[] string = param.split("=");
            queryParams.put(string[0], string[1]);
        }

        return queryParams.get(queryParam);
    }

    protected static String getVideoID(String url)  {
        try {
            return getQueryParameter(url, "v");
        } catch (MalformedURLException e) {
            return null;
        }
    }

    protected static String getPlaylistID(String url)  {
        try {
            return getQueryParameter(url, "list");
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
