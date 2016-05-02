package com.begin.player.bean;

/**
 * Created by Begin on 16/4/9.
 */
public class Music {
    private String name;
    private String url;
    private Boolean player;

    public Music() {

    }

    public Music(String name, String url, Boolean player) {
        this.name = name;
        this.url = url;
        this.player = player;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayer(Boolean player) {
        this.player = player;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
