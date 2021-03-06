package gq.boomermath.ytscr.internal;

import gq.boomermath.ytscr.entities.YoutubeChannel;
import gq.boomermath.ytscr.entities.YoutubeThumbnail;
import gq.boomermath.ytscr.entities.YoutubeVideo;
import org.json.JSONArray;
import org.json.JSONObject;

class SerializerUtil {
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

    protected static YoutubeThumbnail[] parseThumbnails(JSONObject json) {
        JSONArray thumbnailsJSON = json.getJSONArray("thumbnails");
        YoutubeThumbnail[] thumbnailArr = new YoutubeThumbnail[thumbnailsJSON.length()];

        for (int i = 0; i < thumbnailsJSON.length(); i++) {
            JSONObject thumbnailJSON = (JSONObject) thumbnailsJSON.get(i);
            YoutubeThumbnail thumbnail = new YoutubeThumbnail(thumbnailJSON.getString("url"), thumbnailJSON.getInt("width"), thumbnailJSON.getInt("height"));
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

    protected static YoutubeVideo[] getPlaylistVideos(JSONArray videoArr, boolean hasContinuation) {
        int iterations = hasContinuation ? videoArr.length() - 1 : videoArr.length();
        YoutubeVideo[] videos = new YoutubeVideo[iterations];

        for (int i = 0; i < iterations; i++) {
            JSONObject videoJSON = ((JSONObject) videoArr.get(i)).optJSONObject("playlistVideoRenderer");

            YoutubeVideo video = new YoutubeVideo(
                    videoJSON.getString("videoId"),
                    SerializerUtil.parseAuthorJSON(videoJSON).getString("text"),
                    videoJSON.has("detailedMetadataSnippets") ? SerializerUtil.getDescription(videoJSON.getJSONArray("detailedMetadataSnippets")) : null,
                    videoJSON.getJSONObject("lengthText").getString("simpleText"),
                    videoJSON.has("publishedTimeText") ? videoJSON.getJSONObject("publishedTimeText").getString("simpleText") : null,
                    SerializerUtil.parseThumbnails(videoJSON.getJSONObject("thumbnail")),
                    SerializerUtil.getQueryChannel(videoJSON.getJSONObject("shortBylineText"), null),
                    0
            );

            videos[i] = video;
        }

        return videos;
    }

    protected static YoutubeChannel getQueryChannel(JSONObject json, YoutubeThumbnail icon) {
        JSONObject channelJSON = (JSONObject) json.getJSONArray("runs").get(0);
        JSONObject navMeta = channelJSON.getJSONObject("navigationEndpoint");

        return new YoutubeChannel(
                channelJSON.getString("text"),
                navMeta.getJSONObject("browseEndpoint").getString("browseId"),
                "https://youtube.com" + SerializerUtil.parseJSONObject(navMeta, "commandMetadata.webCommandMetadata").getString("url"),
                null,
                icon
        );
    }

    protected static YoutubeChannel getPlaylistChannel(JSONObject secondaryRendererRaw) {
        if (secondaryRendererRaw == null) return null;

        JSONObject secondaryRenderer = secondaryRendererRaw.getJSONObject("playlistSidebarSecondaryInfoRenderer");

        JSONObject videoOwnerRenderer = SerializerUtil.parseJSONObject(secondaryRenderer, "videoOwner.videoOwnerRenderer");
        JSONObject author = SerializerUtil.parseAuthorJSON(videoOwnerRenderer);
        String metaURL = SerializerUtil.parseJSONObject(author, "navigationEndpoint.commandMetadata.webCommandMetadata").getString("url");
        String finalAuthorURL = metaURL != null ? metaURL : author.getJSONObject("browseEndpoint").getString("canonicalBaseUrl");

        return new YoutubeChannel(
                author.getString("text"),
                finalAuthorURL.split("/")[1], "https://www.youtube.com" + finalAuthorURL,
                null,
                SerializerUtil.parseThumbnails(videoOwnerRenderer.getJSONObject("thumbnail"))[0]
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
