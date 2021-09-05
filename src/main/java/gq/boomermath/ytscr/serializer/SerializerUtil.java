package gq.boomermath.ytscr.serializer;

import gq.boomermath.ytscr.serializable.Channel;
import gq.boomermath.ytscr.serializable.Thumbnail;
import org.json.JSONArray;
import org.json.JSONObject;

public class SerializerUtil {
    protected static JSONObject parseJSONObject(JSONObject obj, String path) {
        String[] keys = path.split("\\.");
        JSONObject object = obj;

        for (String key : keys) {
            try {
                object = object.getJSONObject(key);
            } catch (Exception e) {
               e.printStackTrace();
            }
        }

        return object;
    }

    protected static Thumbnail[] parseThumbnails(JSONObject json) {
        JSONArray thumbnailsJSON = json.getJSONArray("thumbnails");
        Thumbnail[] thumbnailArr = new Thumbnail[thumbnailsJSON.length()];

        for (int i = 0; i < thumbnailsJSON.length(); i++) {
            JSONObject thumbnailJSON = (JSONObject) thumbnailsJSON.get(i);
            Thumbnail thumbnail = new Thumbnail(thumbnailJSON.getString("url"), thumbnailJSON.getInt("width"), thumbnailJSON.getInt("height"));
            thumbnailArr[i] = thumbnail;
        }

        return thumbnailArr;
    }

    protected static JSONObject parseAuthorJSON(JSONObject json) {
        return (JSONObject) json.getJSONObject("title").getJSONArray("runs").get(0);
    }
    protected static String getDescription(JSONArray descArr) {
        JSONObject json = ((JSONObject) descArr.get(0)).getJSONObject("snippetText");
        StringBuilder desc = new StringBuilder();

        for (Object snip : json.getJSONArray("runs")) {
            JSONObject snippet = (JSONObject) snip;
            desc.append(snippet.getString("text"));
        }

        return desc.toString();
    }

    protected static Channel getChannel(JSONObject json, Thumbnail icon) {
        JSONObject channelJSON = (JSONObject) json.getJSONArray("runs").get(0);
        JSONObject navMeta = channelJSON.getJSONObject("navigationEndpoint");

        return new Channel(
                channelJSON.getString("text"),
                navMeta.getJSONObject("browseEndpoint").getString("browseId"),
                "https://youtube.com" + SerializerUtil.parseJSONObject(navMeta, "commandMetadata.webCommandMetadata").getString("url"),
                null,
                icon
        );
    }

    protected static String getDurationFromSeconds(int time) {
        int seconds = time % 60;
        int minutesRaw = time / 60;
        int minutes = minutesRaw % 60;
        int hours = (minutesRaw - minutes) / 60;

        StringBuilder durationString = new StringBuilder();

        if (hours != 0) {
            durationString.append(hours).append(":");
        }

        boolean hasHours = hours > 0;

        if (minutes > 0) {
            if (hasHours && minutes < 10) durationString.append("0").append(minutes);
            else durationString.append(minutes);
        } else {
            if (hasHours) durationString.append("00");
            else durationString.append("0");
        }
        durationString.append(":");

        if (seconds == 0) durationString.append("00");
        else durationString.append(seconds);

        return durationString.toString();
    }
}
