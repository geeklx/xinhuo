package com.geek.biz1.bean;

import java.io.Serializable;
import java.util.List;

public class FinitBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title_title;//标题 终端名称
    private String user;//用户协议
    private String privacy;//隐私协议
    private String advertimage;//广告页
    private String advertlinkurl;//广告页地址
    private String short_style_end_time;//置灰结束时间
    private String language_language;//语言
    private String customer_phone;//客服电话
    private List<String> guideimage;//引导页集合
    private String theme_theme;//主题
    private String watermark_contacts_name;//水印名字
    private String watermark_contacts_phone4;//水印电话
    private String authorization_ak;//accessKey
    private String authorization_sk;//accessScrite
    private String authorization_version;//版本
    private String fingerprint_fingerprint;//是否开启指纹
    private String login_login;//是否开启强制登录
    private String short_style_style;//置灰
    private String short_style_start_time;//置灰结束时间
    private String project_name;//项目名称
    private String project_code;//项目编号

    public FinitBean() {
    }

    public FinitBean(String title_title, String user, String privacy, String advertimage, String advertlinkurl, String short_style_end_time, String language_language, String customer_phone, List<String> guideimage, String theme_theme, String watermark_contacts_name, String watermark_contacts_phone4, String authorization_ak, String authorization_sk, String authorization_version, String fingerprint_fingerprint, String login_login, String short_style_style, String short_style_start_time, String project_name, String project_code) {
        this.title_title = title_title;
        this.user = user;
        this.privacy = privacy;
        this.advertimage = advertimage;
        this.advertlinkurl = advertlinkurl;
        this.short_style_end_time = short_style_end_time;
        this.language_language = language_language;
        this.customer_phone = customer_phone;
        this.guideimage = guideimage;
        this.theme_theme = theme_theme;
        this.watermark_contacts_name = watermark_contacts_name;
        this.watermark_contacts_phone4 = watermark_contacts_phone4;
        this.authorization_ak = authorization_ak;
        this.authorization_sk = authorization_sk;
        this.authorization_version = authorization_version;
        this.fingerprint_fingerprint = fingerprint_fingerprint;
        this.login_login = login_login;
        this.short_style_style = short_style_style;
        this.short_style_start_time = short_style_start_time;
        this.project_name = project_name;
        this.project_code = project_code;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTitle_title() {
        return title_title;
    }

    public void setTitle_title(String title_title) {
        this.title_title = title_title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getAdvertimage() {
        return advertimage;
    }

    public void setAdvertimage(String advertimage) {
        this.advertimage = advertimage;
    }

    public String getAdvertlinkurl() {
        return advertlinkurl;
    }

    public void setAdvertlinkurl(String advertlinkurl) {
        this.advertlinkurl = advertlinkurl;
    }

    public String getShort_style_end_time() {
        return short_style_end_time;
    }

    public void setShort_style_end_time(String short_style_end_time) {
        this.short_style_end_time = short_style_end_time;
    }

    public String getLanguage_language() {
        return language_language;
    }

    public void setLanguage_language(String language_language) {
        this.language_language = language_language;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public List<String> getGuideimage() {
        return guideimage;
    }

    public void setGuideimage(List<String> guideimage) {
        this.guideimage = guideimage;
    }

    public String getTheme_theme() {
        return theme_theme;
    }

    public void setTheme_theme(String theme_theme) {
        this.theme_theme = theme_theme;
    }

    public String getWatermark_contacts_name() {
        return watermark_contacts_name;
    }

    public void setWatermark_contacts_name(String watermark_contacts_name) {
        this.watermark_contacts_name = watermark_contacts_name;
    }

    public String getWatermark_contacts_phone4() {
        return watermark_contacts_phone4;
    }

    public void setWatermark_contacts_phone4(String watermark_contacts_phone4) {
        this.watermark_contacts_phone4 = watermark_contacts_phone4;
    }

    public String getAuthorization_ak() {
        return authorization_ak;
    }

    public void setAuthorization_ak(String authorization_ak) {
        this.authorization_ak = authorization_ak;
    }

    public String getAuthorization_sk() {
        return authorization_sk;
    }

    public void setAuthorization_sk(String authorization_sk) {
        this.authorization_sk = authorization_sk;
    }

    public String getAuthorization_version() {
        return authorization_version;
    }

    public void setAuthorization_version(String authorization_version) {
        this.authorization_version = authorization_version;
    }

    public String getFingerprint_fingerprint() {
        return fingerprint_fingerprint;
    }

    public void setFingerprint_fingerprint(String fingerprint_fingerprint) {
        this.fingerprint_fingerprint = fingerprint_fingerprint;
    }

    public String getLogin_login() {
        return login_login;
    }

    public void setLogin_login(String login_login) {
        this.login_login = login_login;
    }

    public String getShort_style_style() {
        return short_style_style;
    }

    public void setShort_style_style(String short_style_style) {
        this.short_style_style = short_style_style;
    }

    public String getShort_style_start_time() {
        return short_style_start_time;
    }

    public void setShort_style_start_time(String short_style_start_time) {
        this.short_style_start_time = short_style_start_time;
    }
}
