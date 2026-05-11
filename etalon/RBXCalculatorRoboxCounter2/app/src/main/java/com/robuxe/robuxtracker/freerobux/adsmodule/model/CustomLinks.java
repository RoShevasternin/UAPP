package com.robuxe.robuxtracker.freerobux.adsmodule.model;

import java.util.List;

public class CustomLinks {
    private String linkColor;
    private List<String> openRedirectLink;
    private List<String> interRedirectLink;
    private List<String> nativeRedirectLink;
    private List<String> bannerRedirectLink;

    public String getLinkColor() {
        return linkColor;
    }

    public void setLinkColor(String linkColor) {
        this.linkColor = linkColor;
    }

    public List<String> getOpenRedirectLink() {
        return openRedirectLink;
    }

    public void setOpenRedirectLink(List<String> list) {
        this.openRedirectLink = list;
    }

    public List<String> getInterRedirectLink() {
        return interRedirectLink;
    }

    public void setInterRedirectLink(List<String> list) {
        this.interRedirectLink = list;
    }

    public List<String> getNativeRedirectLink() {
        return nativeRedirectLink;
    }

    public void setNativeRedirectLink(List<String> list) {
        this.nativeRedirectLink = list;
    }

    public List<String> getBannerRedirectLink() {
        return bannerRedirectLink;
    }

    public void setBannerRedirectLink(List<String> list) {
        this.bannerRedirectLink = list;
    }
}
