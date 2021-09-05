# YtScraper

Scrapes youtube for video metadata.
Returns data (if applicable) including a video's:
- ID
- Title
- Description
- Duration
- Upload Date
- Thumbnails (URL, width, height)
- Channel (URL, id, name, subscribers, and channel icon)
- Views

Example
````java
   Video video = Youtube.searchOne("Megalovania"); //returns a single video
   Video[] video = Youtube.search("Megalovania"); //returns all videos on the first page
   Video video = Youtube.getVideo("https://www.youtube.com/watch?v=wDgQdr8ZkTw") //returns video metadata from link
   Playlist playlist = Youtube.getPlaylist("https://www.youtube.com/playlist?list=PLAuXvMFaTiZxNllvtCAObLD2tq31W0tgk"); //returns playlist metadata and videos
````

Made by [boomermath](https://github.com/boomermath)