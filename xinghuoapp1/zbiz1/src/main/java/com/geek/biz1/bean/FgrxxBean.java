package com.geek.biz1.bean;

import java.io.Serializable;

public class FgrxxBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;//唯一表示
    private String username;//用户名称
    private String userId;//用户id
    private String orgId;//组织id
    private String identityId;//表示id
    private String code;
    private String name;//名称
    private String orgCode;//组织code
    private String orgName;//组织名称
    private String takeOrgName;
    private String position;
    private String phonenum;//手机号
    private String landline;//座机
    private String fixedTelephone;//固定电话
    private String hostmail;//邮箱
    private String sex;//性别
    private String photo;//头像
    private String orgTitile;//组织标题
    private String organization;
    private String myCollection;
    private String myMsg;
    private String signature;
    private String fingerprint;
    private String cityName;//城市名称


    public FgrxxBean() {
    }

    public FgrxxBean(String username, String userId, String orgId, String identityId, String code, String name, String orgCode, String orgName, String takeOrgName, String position, String phonenum, String landline, String hostmail, String sex, String photo, String orgTitile, String organization, String myCollection, String myMsg, String signature, String fingerprint, String cityName) {
        this.username = username;
        this.userId = userId;
        this.orgId = orgId;
        this.identityId = identityId;
        this.code = code;
        this.name = name;
        this.orgCode = orgCode;
        this.orgName = orgName;
        this.takeOrgName = takeOrgName;
        this.position = position;
        this.phonenum = phonenum;
        this.landline = landline;
        this.hostmail = hostmail;
        this.sex = sex;
        this.photo = photo;
        this.orgTitile = orgTitile;
        this.organization = organization;
        this.myCollection = myCollection;
        this.myMsg = myMsg;
        this.signature = signature;
        this.fingerprint = fingerprint;
        this.cityName = cityName;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getTakeOrgName() {
        return takeOrgName;
    }

    public void setTakeOrgName(String takeOrgName) {
        this.takeOrgName = takeOrgName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getFixedTelephone() {
        return fixedTelephone;
    }

    public void setFixedTelephone(String fixedTelephone) {
        this.fixedTelephone = fixedTelephone;
    }

    public String getHostmail() {
        return hostmail;
    }

    public void setHostmail(String hostmail) {
        this.hostmail = hostmail;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOrgTitile() {
        return orgTitile;
    }

    public void setOrgTitile(String orgTitile) {
        this.orgTitile = orgTitile;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getMyCollection() {
        return myCollection;
    }

    public void setMyCollection(String myCollection) {
        this.myCollection = myCollection;
    }

    public String getMyMsg() {
        return myMsg;
    }

    public void setMyMsg(String myMsg) {
        this.myMsg = myMsg;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
