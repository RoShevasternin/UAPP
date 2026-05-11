package com.robuxe.robuxtracker.freerobux.adsmodule.model;

import java.util.List;

public class CustomAdsConfig {
    private List<String> mainHeadline;
    private List<String> headline;
    private List<String> description;
    private List<String> buttonText;
    private List<String> nativeImageLarge;
    private List<String> nativeImageMedium;
    private List<String> nativeImageSmall;
    private List<String> roundImage;
    private List<String> bannerImage;

    public List<String> getMainHeadline() {
        return mainHeadline;
    }

    public void setMainHeadline(List<String> list) {
        this.mainHeadline = list;
    }

    public List<String> getHeadline() {
        return headline;
    }

    public void setHeadline(List<String> list) {
        this.headline = list;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> list) {
        this.description = list;
    }

    public List<String> getButtonText() {
        return buttonText;
    }

    public void setButtonText(List<String> list) {
        this.buttonText = list;
    }

    public List<String> getNativeImageLarge() {
        return nativeImageLarge;
    }

    public void setNativeImageLarge(List<String> list) {
        this.nativeImageLarge = list;
    }

    public List<String> getNativeImageMedium() {
        return nativeImageMedium;
    }

    public void setNativeImageMedium(List<String> list) {
        this.nativeImageMedium = list;
    }

    public List<String> getNativeImageSmall() {
        return nativeImageSmall;
    }

    public void setNativeImageSmall(List<String> list) {
        this.nativeImageSmall = list;
    }

    public List<String> getRoundImage() {
        return roundImage;
    }

    public void setRoundImage(List<String> list) {
        this.roundImage = list;
    }

    public List<String> getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(List<String> list) {
        this.bannerImage = list;
    }
}
