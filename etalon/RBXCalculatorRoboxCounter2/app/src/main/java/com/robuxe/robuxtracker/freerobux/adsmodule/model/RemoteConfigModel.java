package com.robuxe.robuxtracker.freerobux.adsmodule.model;

public class RemoteConfigModel {
    private boolean is_splash_show;
    private int is_rewarded_ad_on;
    private int after_x_number_ads_listview;

    private String banner_ad_category_id;
    private String int_ad_category_id;
    private String reward_ad_category_id;
    private String native_ad_category_id;
    private String app_open_ad_category_id;

    private Admob admob;

    private boolean next_double_on_off;
    private boolean back_double_on_off;
    private boolean intro_on_off;
    private boolean reward_timer_on_off;
    private boolean back_screen_on_off;

    private long reward_timer_time;

    private boolean nativeadsoff;
    private boolean banneradsoff;
    private boolean interadsoff;
    private boolean backadsoff;

    private String reward_button_text;
    private String Privacypolicy;
    private String baseurl;

    private int after_x_pages_int_ads;
    private int back_count;
    private String bannerad_type;

    private boolean is_ad_show;

    private CustomLinks customLinks;
    private CustomAdsConfig customAdsConfig;

    // ---------------- getters & setters ----------------
    public boolean getIs_splash_show() {
        return is_splash_show;
    }

    public void setIs_splash_show(boolean value) {
        this.is_splash_show = value;
    }




    public int getIs_rewarded_ad_on() {
        return is_rewarded_ad_on;
    }

    public void setIs_rewarded_ad_on(int value) {
        this.is_rewarded_ad_on = value;
    }

    public int getAfter_x_number_ads_listview() {
        return after_x_number_ads_listview;
    }

    public void setAfter_x_number_ads_listview(int value) {
        this.after_x_number_ads_listview = value;
    }

    public String getBanner_ad_category_id() {
        return banner_ad_category_id;
    }

    public void setBanner_ad_category_id(String value) {
        this.banner_ad_category_id = value;
    }

    public String getInt_ad_category_id() {
        return int_ad_category_id;
    }

    public void setInt_ad_category_id(String value) {
        this.int_ad_category_id = value;
    }

    public String getReward_ad_category_id() {
        return reward_ad_category_id;
    }

    public void setReward_ad_category_id(String value) {
        this.reward_ad_category_id = value;
    }

    public String getNative_ad_category_id() {
        return native_ad_category_id;
    }

    public void setNative_ad_category_id(String value) {
        this.native_ad_category_id = value;
    }

    public String getApp_open_ad_category_id() {
        return app_open_ad_category_id;
    }

    public void setApp_open_ad_category_id(String value) {
        this.app_open_ad_category_id = value;
    }

    public Admob getAdmob() {
        return admob;
    }

    public void setAdmob(Admob admob) {
        this.admob = admob;
    }

    public boolean getNext_double_on_off() {
        return next_double_on_off;
    }

    public void setNext_double_on_off(boolean value) {
        this.next_double_on_off = value;
    }

    public boolean getBack_double_on_off() {
        return back_double_on_off;
    }

    public void setBack_double_on_off(boolean value) {
        this.back_double_on_off = value;
    }

    public boolean getintro_on_off() {
        return intro_on_off;
    }

    public void setintro_on_off(boolean value) {
        this.intro_on_off = value;
    }

    public boolean getIntro_on_off() {
        return intro_on_off;
    }

    public void setIntro_on_off(boolean value) {
        this.intro_on_off = value;
    }

    public boolean getReward_timer_on_off() {
        return reward_timer_on_off;
    }

    public void setReward_timer_on_off(boolean value) {
        this.reward_timer_on_off = value;
    }

    public boolean getBack_screen_on_off() {
        return back_screen_on_off;
    }

    public void setBack_screen_on_off(boolean value) {
        this.back_screen_on_off = value;
    }

    public long getReward_timer_time() {
        return reward_timer_time;
    }

    public void setReward_timer_time(long value) {
        this.reward_timer_time = value;
    }

    public boolean getNativeadsoff() {
        return nativeadsoff;
    }

    public void setNativeadsoff(boolean value) {
        this.nativeadsoff = value;
    }

    public boolean getBanneradsoff() {
        return banneradsoff;
    }

    public void setBanneradsoff(boolean value) {
        this.banneradsoff = value;
    }

    public boolean getInteradsoff() {
        return interadsoff;
    }

    public void setInteradsoff(boolean value) {
        this.interadsoff = value;
    }

    public boolean getBackadsoff() {
        return backadsoff;
    }

    public void setBackadsoff(boolean value) {
        this.backadsoff = value;
    }

    public String getReward_button_text() {
        return reward_button_text;
    }

    public void setReward_button_text(String value) {
        this.reward_button_text = value;
    }

    public String getPrivacypolicy() {
        return Privacypolicy;
    }
    public String getbaseurl() {
        return baseurl;
    }

    public void setPrivacypolicy(String value) {
        this.Privacypolicy = value;
    }   public void setbaseurl(String value) {
        this.baseurl = value;
    }

    public int getAfter_x_pages_int_ads() {
        return after_x_pages_int_ads;
    }

    public void setAfter_x_pages_int_ads(int value) {
        this.after_x_pages_int_ads = value;
    }

    public int getBack_count() {
        return back_count;
    }

    public void setBack_count(int value) {
        this.back_count = value;
    }

    public String getBannerad_type() {
        return bannerad_type;
    }

    public void setBannerad_type(String value) {
        this.bannerad_type = value;
    }

    public boolean getIs_ad_show() {
        return is_ad_show;
    }

    public void setIs_ad_show(boolean value) {
        this.is_ad_show = value;
    }

    public CustomLinks getCustomLinks() {
        return customLinks;
    }

    public void setCustomLinks(CustomLinks value) {
        this.customLinks = value;
    }

    public CustomAdsConfig getCustomAdsConfig() {
        return customAdsConfig;
    }

    public void setCustomAdsConfig(CustomAdsConfig value) {
        this.customAdsConfig = value;
    }
}
