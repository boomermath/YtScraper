package gq.boomermath.ytscr.internal;

import gq.boomermath.ytscr.entities.YoutubeChannel;
import gq.boomermath.ytscr.entities.YoutubePlaylist;
import gq.boomermath.ytscr.entities.YoutubeVideo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Serializer {
    public static YoutubeVideo[] processQuery(JSONObject initialData, int limit) {
        JSONObject content = SerializerUtil.parseJSONObject(initialData, "contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer").getJSONArray("contents").getJSONObject(0);
        JSONArray videoArr = content.getJSONObject("itemSectionRenderer").getJSONArray("contents");

        int lim = limit == 0 || limit > videoArr.length() ? videoArr.length() : limit;
        ArrayList<YoutubeVideo> videos = new ArrayList<>();

        for (int i = 0; i < lim; i++) {
            JSONObject videoJSON = videoArr.getJSONObject(i).optJSONObject("videoRenderer");

            if (videoJSON == null) continue;

            JSONObject titleJSON = SerializerUtil.parseAuthorJSON(videoJSON);
            String videoTitle = titleJSON.getString("text");

            YoutubeVideo video = new YoutubeVideo(
                    videoJSON.getString("videoId"),
                    videoTitle,
                    videoJSON.has("detailedMetadataSnippets") ? SerializerUtil.getDescription(videoJSON.getJSONArray("detailedMetadataSnippets")) : null,
                    videoJSON.getJSONObject("lengthText").getString("simpleText"),
                    videoJSON.has("publishedTimeText") ? videoJSON.getJSONObject("publishedTimeText").getString("simpleText") : null,
                    SerializerUtil.parseThumbnails(videoJSON.getJSONObject("thumbnail")),
                    SerializerUtil.getQueryChannel(videoJSON.getJSONObject("ownerText"), SerializerUtil.parseThumbnails(Objects.requireNonNull(SerializerUtil.parseJSONObject(videoJSON, "channelThumbnailSupportedRenderers.channelThumbnailWithLinkRenderer.thumbnail")))[0]),
                    Long.parseLong(videoJSON.getJSONObject("viewCountText").getString("simpleText").replaceAll(" views", "").replaceAll(",", ""))
            );

            videos.add(video);
        }

        return videos.toArray(YoutubeVideo[]::new);
    }

    public static YoutubePlaylist processPlaylist(JSONObject initialData) {
        JSONObject content = SerializerUtil.parseJSONObject(initialData, "contents.twoColumnBrowseResultsRenderer").getJSONArray("tabs").getJSONObject(0);
        JSONObject tabRenderer = SerializerUtil.parseJSONObject(content, "tabRenderer.content.sectionListRenderer").getJSONArray("contents").getJSONObject(0);
        JSONObject videoContents = tabRenderer.getJSONObject("itemSectionRenderer").getJSONArray("contents").getJSONObject(0);
        JSONArray videoArr = videoContents.getJSONObject("playlistVideoListRenderer").getJSONArray("contents");

        boolean hasContinuation = videoArr.getJSONObject(videoArr.length() - 1).optJSONObject("continuationItemRenderer") != null;

        YoutubeVideo[] videos = SerializerUtil.getPlaylistVideos(videoArr, hasContinuation);

        JSONArray playlistItems = initialData.getJSONObject("sidebar").getJSONObject("playlistSidebarRenderer").getJSONArray("items");

        JSONObject primaryRenderer = playlistItems.getJSONObject(0).getJSONObject("playlistSidebarPrimaryInfoRenderer");
        JSONObject secondaryRenderer = playlistItems.optJSONObject(1);

        JSONObject title = primaryRenderer.getJSONObject("title").getJSONArray("runs").getJSONObject(0);

        return new YoutubePlaylist(
                SerializerUtil.parseJSONObject(title, "navigationEndpoint.watchEndpoint").getString("playlistId"),
                SerializerUtil.parseAuthorJSON(primaryRenderer).getString("text"),
                SerializerUtil.parseThumbnails(SerializerUtil.parseJSONObject(primaryRenderer, "thumbnailRenderer.playlistVideoThumbnailRenderer.thumbnail")),
                SerializerUtil.getPlaylistChannel(secondaryRenderer),
                videos
        );
    }

    public static YoutubeVideo processVideo(JSONObject playerData, JSONObject initialData) {
        JSONObject videoDetails = initialData.getJSONObject("videoDetails");

        JSONObject contents = SerializerUtil.parseJSONObject(playerData, "contents.twoColumnWatchNextResults.results.results").getJSONArray("contents").getJSONObject(1);
        JSONObject owner = SerializerUtil.parseJSONObject(contents, "videoSecondaryInfoRenderer.owner.videoOwnerRenderer");

        return new YoutubeVideo(
                videoDetails.getString("videoId"),
                videoDetails.getString("title"),
                videoDetails.getString("shortDescription"),
                SerializerUtil.getDurationFromSeconds(videoDetails.getInt("lengthSeconds")),
                initialData.getJSONObject("microformat").getJSONObject("playerMicroformatRenderer").getString("publishDate"),
                SerializerUtil.parseThumbnails(videoDetails.getJSONObject("thumbnail")),
                new YoutubeChannel(
                        videoDetails.getString("author"),
                        videoDetails.getString("channelId"),
                        "https://www.youtube.com" + SerializerUtil.parseJSONObject(SerializerUtil.parseAuthorJSON(owner), "navigationEndpoint.commandMetadata.webCommandMetadata").getString("url"),
                        owner.getJSONObject("subscriberCountText").getString("simpleText").replaceFirst(" subscribers", ""),
                        SerializerUtil.parseThumbnails(owner.getJSONObject("thumbnail"))[0]
                ),
                videoDetails.getLong("viewCount")
        );
    }
}
