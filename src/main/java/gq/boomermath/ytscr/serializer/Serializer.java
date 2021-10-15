package gq.boomermath.ytscr.serializer;

import gq.boomermath.ytscr.serializable.Channel;
import gq.boomermath.ytscr.serializable.Playlist;
import gq.boomermath.ytscr.serializable.Thumbnail;
import gq.boomermath.ytscr.serializable.Video;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Serializer {
    public static Video[] processQuery(JSONObject initialData, int limit) {
        JSONObject content = (JSONObject) SerializerUtil.parseJSONObject(initialData, "contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer").getJSONArray("contents").get(0);
        JSONArray videoArr = content.getJSONObject("itemSectionRenderer").getJSONArray("contents");

        int lim = limit == 0 || limit > videoArr.length() ? videoArr.length() : limit;
        ArrayList<Video> videos = new ArrayList<>();

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

            videos.add(video);
        }

        return videos.toArray(Video[]::new);

    }

    public static Playlist processPlaylist(JSONObject initialData) {
        JSONObject content = (JSONObject) SerializerUtil.parseJSONObject(initialData, "contents.twoColumnBrowseResultsRenderer").getJSONArray("tabs").get(0);
        JSONObject tabRenderer = (JSONObject) SerializerUtil.parseJSONObject(content, "tabRenderer.content.sectionListRenderer").getJSONArray("contents").get(0);
        JSONObject videoContents = (JSONObject) tabRenderer.getJSONObject("itemSectionRenderer").getJSONArray("contents").get(0);
        JSONArray videoArr =  videoContents.getJSONObject("playlistVideoListRenderer").getJSONArray("contents");

        Video[] videos = SerializerUtil.getPlaylistVideos(videoArr.length())

        JSONArray playlistItems = initialData.getJSONObject("sidebar").getJSONObject("playlistSidebarRenderer").getJSONArray("items");

        JSONObject primaryRenderer = ((JSONObject) playlistItems.get(0)).getJSONObject("playlistSidebarPrimaryInfoRenderer");
        JSONObject secondaryRenderer = ((JSONObject) playlistItems.get(1)).getJSONObject("playlistSidebarSecondaryInfoRenderer");

        JSONObject title = (JSONObject) primaryRenderer.getJSONObject("title").getJSONArray("runs").get(0);
        JSONObject videoOwnerRenderer = SerializerUtil.parseJSONObject(secondaryRenderer, "videoOwner.videoOwnerRenderer");
        JSONObject author = SerializerUtil.parseAuthorJSON(videoOwnerRenderer);

        String metaURL = SerializerUtil.parseJSONObject(author, "navigationEndpoint.commandMetadata.webCommandMetadata").getString("url");
        String finalAuthorURL = metaURL != null ? metaURL : author.getJSONObject("browseEndpoint").getString("canonicalBaseUrl");

        return new Playlist(
                SerializerUtil.parseJSONObject(title, "navigationEndpoint.watchEndpoint").getString("playlistId"),
                SerializerUtil.parseAuthorJSON(primaryRenderer).getString("text"),
                SerializerUtil.parseThumbnails(SerializerUtil.parseJSONObject(primaryRenderer, "thumbnailRenderer.playlistVideoThumbnailRenderer.thumbnail")),
                new Channel(author.getString("text"), finalAuthorURL.split("/")[1], "https://www.youtube.com" + finalAuthorURL, null, SerializerUtil.parseThumbnails(videoOwnerRenderer.getJSONObject("thumbnail"))[0]),
                videos
        );
    }

    public static Video processVideo(JSONObject playerData, JSONObject initialData) {
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
