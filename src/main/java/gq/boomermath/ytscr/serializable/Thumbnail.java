package gq.boomermath.ytscr.serializable;

public final class Thumbnail {
    private final String url;
    private final int width;
    private final int height;

    public Thumbnail(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String toString() {
        return url;
    }

    public String url() {
        return url;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
