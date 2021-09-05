package com.boomermath.ytscr.serializer;

import com.boomermath.ytscr.serializable.Channel;
import com.boomermath.ytscr.serializable.Playlist;
import com.boomermath.ytscr.serializable.Thumbnail;
import com.boomermath.ytscr.serializable.Video;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class Serializer {
    public static Video[] processQuery(JSONObject initialData, int limit) {
        JSONObject content = (JSONObject) SerializerUtil.parseJSONObject(initialData, "contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer").getJSONArray("contents").get(0);
        JSONArray videoArr = content.getJSONObject("itemSectionRenderer").getJSONArray("contents");

        int lim = limit == 0 ? videoArr.length() : limit;
        Video[] videos = new Video[lim];

        for (int i = 0; i < lim; i++) {
            JSONObject videoJSON = ((JSONObject) videoArr.get(i)).optJSONObject("videoRenderer");

            if (videoJSON == null) continue;

            JSONObject titleJSON = SerializerUtil.parseAuthorJSON(videoJSON);
            String videoTitle = titleJSON.getString("text");

            Video video = new Video(
                    videoJSON.getString("videoId"),
                    videoTitle,
                    videoJSON.optJSONArray("detailedMetadataSnippets") != null ? SerializerUtil.getDescription(videoJSON.getJSONArray("detailedMetadataSnippets")) : null,
                    videoJSON.getJSONObject("lengthText").getString("simpleText"),
                    videoJSON.optJSONObject("publishedTimeText") != null ? videoJSON.getJSONObject("publishedTimeText").getString("simpleText") : null,
                    SerializerUtil.parseThumbnails(videoJSON.getJSONObject("thumbnail")),
                    SerializerUtil.getChannel(videoJSON.getJSONObject("ownerText"), SerializerUtil.parseThumbnails(Objects.requireNonNull(SerializerUtil.parseJSONObject(videoJSON, "channelThumbnailSupportedRenderers.channelThumbnailWithLinkRenderer.thumbnail")))[0]),
                    Long.parseLong(videoJSON.getJSONObject("viewCountText").getString("simpleText").replaceAll(" views", "").replaceAll(",", ""))
            );

            videos[i] = video;
        }

        return videos;
    }

    public static Playlist processPlaylist(JSONObject initialData) {
        JSONObject content = (JSONObject) SerializerUtil.parseJSONObject(initialData, "contents.twoColumnBrowseResultsRenderer").getJSONArray("tabs").get(0);
        JSONObject c = (JSONObject) SerializerUtil.parseJSONObject(content, "tabRenderer.content.sectionListRenderer").getJSONArray("contents").get(0);
        JSONObject d = (JSONObject) c.getJSONObject("itemSectionRenderer").getJSONArray("contents").get(0);
        JSONArray videoArr =  d.getJSONObject("playlistVideoListRenderer").getJSONArray("contents");

        Video[] videos = new Video[videoArr.length()];

        for (int i = 0; i < videos.length; i++) {
            JSONObject videoJSON = ((JSONObject) videoArr.get(i)).optJSONObject("playlistVideoRenderer");

            if (videoJSON == null) continue;

            Video video = new Video(
                    videoJSON.getString("videoId"),
                    SerializerUtil.parseAuthorJSON(videoJSON).getString("text"),
                    videoJSON.optJSONArray("detailedMetadataSnippets") != null ? SerializerUtil.getDescription(videoJSON.getJSONArray("detailedMetadataSnippets")) : null,
                    videoJSON.getJSONObject("lengthText").getString("simpleText"),
                    videoJSON.optJSONObject("publishedTimeText") != null ? videoJSON.getJSONObject("publishedTimeText").getString("simpleText") : null,
                    SerializerUtil.parseThumbnails(videoJSON.getJSONObject("thumbnail")),
                    SerializerUtil.getChannel(videoJSON.getJSONObject("shortBylineText"), new Thumbnail(null, 0, 0)),
                    0
            );

            videos[i] = video;
        }

        JSONArray playlistItems = initialData.getJSONObject("sidebar").getJSONObject("playlistSidebarRenderer").getJSONArray("items");

        JSONObject primaryRenderer = ((JSONObject) playlistItems.get(0)).getJSONObject("playlistSidebarPrimaryInfoRenderer");
        JSONObject secondaryRenderer = ((JSONObject) playlistItems.get(1)).getJSONObject("playlistSidebarSecondaryInfoRenderer");

        JSONObject title = (JSONObject) primaryRenderer.getJSONObject("title").getJSONArray("runs").get(0);
        JSONObject author = SerializerUtil.parseAuthorJSON(SerializerUtil.parseJSONObject(secondaryRenderer, "videoOwner.videoOwnerRenderer"));

        String metaURL = SerializerUtil.parseJSONObject(author, "navigationEndpoint.commandMetadata.webCommandMetadata").getString("url");
        String finalAuthorURL = metaURL != null ? metaURL : author.getJSONObject("browseEndpoint").getString("canonicalBaseUrl");

        return new Playlist(
                SerializerUtil.parseJSONObject(title, "navigationEndpoint.watchEndpoint").getString("playlistId"),
                primaryRenderer.getString("text"),
                SerializerUtil.parseThumbnails(SerializerUtil.parseJSONObject(primaryRenderer, "thumbnailRenderer.playlistVideoThumbnailRenderer.thumbnail")),
                new Channel(author.getString("text"), finalAuthorURL.split("/")[1], "https://www.youtube.com" + finalAuthorURL, null, SerializerUtil.parseThumbnails(author.getJSONObject("thumbnail"))[0]),
                videos
        );
    }

    public static Video processVideo(JSONObject initialData, JSONObject playerData) {
        JSONObject videoDetails = initialData.getJSONObject("videoDetails");

        JSONObject resultsRenderer = SerializerUtil.parseJSONObject(playerData, "contents.twoColumnWatchNextResults.results.results");
        JSONObject contents = (JSONObject) resultsRenderer.getJSONArray("contents").get(1);
        JSONObject owner = SerializerUtil.parseJSONObject(contents, "videoSecondaryInfoRenderer.owner.videoOwnerRenderer");

        return new Video(
                videoDetails.getString("videoId"),
                videoDetails.getString("title"),
                videoDetails.getString("shortDescription"),
                SerializerUtil.getDurationFromSeconds(videoDetails.getInt("lengthSeconds")),
                initialData.getJSONObject("microformat").getJSONObject("playerMicroformatRenderer").getString("publishDate"),
                SerializerUtil.parseThumbnails(videoDetails.getJSONObject("thumbnail")),
                new Channel(
                        videoDetails.getString("author"),
                        videoDetails.getString("channelId"),
                        "https://youtube.com" + SerializerUtil.parseJSONObject(SerializerUtil.parseAuthorJSON(owner), "navigationEndpoint.commandMetadata.webCommandMetadata").getString("url"),
                        owner.getJSONObject("subscriberCountText").getString("simpleText").replaceFirst(" subscribers", ""),
                        SerializerUtil.parseThumbnails(owner.getJSONObject("thumbnail"))[0]
                ),
                videoDetails.getInt("viewCount")
        );
    }
}
