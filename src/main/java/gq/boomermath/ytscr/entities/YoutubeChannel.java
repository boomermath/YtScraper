package gq.boomermath.ytscr.serializable;

public final class YoutubeChannel {
    private final String title;
    private final String id;
    private final String url;
    private final String subscribers;
    private final Thumbnail icon;

    public YoutubeChannel(String title, String id, String url, String subscribers, Thumbnail icon) {
        this.title = title;
        this.id = id;
        this.url = url;
        this.subscribers = subscribers;
        this.icon = icon;
    }

    public String toString() {
        return title;
    }

    public String iconURL(int size) {
        if (icon == null || icon.url() == null) return null;
        if (size < 0) throw new IllegalArgumentException("Size cannot be less than 0!");
        String num = icon.url().split("=s")[1].split("-c")[0];
        return icon.url().replaceFirst("=s" + num + "-c", "=s" + size + "-c");
    }

    public String title() {
        return title;
    }

    public String id() {
        return id;
    }

    public String url() {
        return url;
    }

    public String subscribers() {
        return subscribers;
    }

    public Thumbnail icon() {
        return icon;
    }
}
