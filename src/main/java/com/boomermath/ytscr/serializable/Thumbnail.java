package com.boomermath.ytscr.serializable;

public record Thumbnail(String url, int width, int height) {
    public String toString() {
        return url;
    }
}
