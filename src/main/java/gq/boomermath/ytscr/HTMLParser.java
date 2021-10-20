package gq.boomermath.ytscr;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

class HTMLParser {
    private static final String API_URL = "https://www.youtube.com/youtubei/v1/browse?key=";
    private static final String DEFAULT_PREFIX = "var ytInitialData =";
    private static final String PLAYER_PREFIX = "var ytInitialPlayerResponse =";

    protected static JSONObject parse(String url) {
        try {
            Document document = Jsoup.connect(url).get();

            for (Element node : document.select("script")) {
                String data = node.data().trim();

                if (data.startsWith(DEFAULT_PREFIX)) {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data.split(DEFAULT_PREFIX)[1]), null);
                    return new JSONObject(data.split(DEFAULT_PREFIX)[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static JSONObject[] parseVideoPlayerInfo(String url) {
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
            e.printStackTrace();
        }
        return null;
    }


    protected static void parseContinuation(String token) {
        String clientContext = "{\"client\":{\"utcOffsetMinutes\":0,\"gl\":\"US\",\"hl\":\"en\",\"clientName\":\"WEB\",\"clientVersion\":\"2.20211014.05.00\"},\"user\":{},\"request\":{}}";
        HashMap<String, String> data = new HashMap<>();

        data.put("continuation", token);
        data.put("context", clientContext);

        try {
            Document document = Jsoup.connect(API_URL + "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .data(data)
                    .post();
            System.out.println(document);
        } catch (IOException io) {

           io.printStackTrace();
        }
    }


    protected static String getQueryParameter(String input, String queryParam) {
        try {
            URL url = new URL(input);

            if (!url.getHost().equals("youtube.com") && !url.getHost().equals("www.youtube.com")) {
                throw new IllegalArgumentException("Input is not a valid youtube url!");
            }

            String[] queryString = url.getQuery().split("&");
            HashMap<String, String> queryParams = new HashMap<>();

            for (String param : queryString) {
                String[] string = param.split("=");
                queryParams.put(string[0], string[1]);
            }

            return queryParams.get(queryParam);
        } catch (MalformedURLException e) {
            return input;
        }
    }
}
