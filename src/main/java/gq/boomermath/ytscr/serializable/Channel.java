package gq.boomermath.ytscr.serializable;

public record Channel(String title, String id, String url, String subscribers, Thumbnail icon) {
    public String toString() {
        return title;
    }

    public String iconURL(int size) {
        if (size < 0) throw new IllegalArgumentException("Size cannot be less than 0!");
        String num = icon.url().split("=s")[1].split("-c")[0];
        return icon.url().replaceFirst("=s" + num + "-c", "=s" + size + "-c");
    }
}
