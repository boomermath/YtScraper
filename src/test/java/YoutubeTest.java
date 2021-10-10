import gq.boomermath.ytscr.Youtube;
import gq.boomermath.ytscr.serializable.Video;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class YoutubeTest {
    @Test
    public void testSearch() {
        Video[] videos = Youtube.search("Undertale - Megalovania", 10);
        Assertions.assertEquals(10, videos.length);

        Video megalovaniaVideo = videos[0];

        Assertions.assertEquals("Undertale - Megalovania", megalovaniaVideo.name());
        Assertions.assertEquals("wDgQdr8ZkTw", megalovaniaVideo.id());
        Assertions.assertTrue(megalovaniaVideo.description().startsWith("The song megalovania"));
        Assertions.assertEquals("5:14", megalovaniaVideo.duration());
        Assertions.assertEquals("https://www.youtube.com/watch?v=wDgQdr8ZkTw", megalovaniaVideo.url());
        Assertions.assertEquals("https://www.youtube.com/embed/wDgQdr8ZkTw", megalovaniaVideo.embedURL());
        Assertions.assertEquals("2 years ago", megalovaniaVideo.uploaded());
        Assertions.assertEquals("https://i.ytimg.com/vi/wDgQdr8ZkTw/hq720.jpg?sqp=-oaymwEcCOgCEMoBSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLB_AcbbbqJa5VCmkfjBfjkPrB3peg", megalovaniaVideo.thumbnails()[0].url());
        Assertions.assertEquals("UCrBJGj-Vi8Z9XTftw-PEgCA", megalovaniaVideo.author().id());
        Assertions.assertEquals("Game Guard", megalovaniaVideo.author().title());
    }
}
