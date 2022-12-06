package com.geek.appcommon.xpop;

import java.io.Serializable;

public class PopulationLabelListItemBean extends BaseShowBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tagId;
    private String tagName;
    private String categoryCode;
    private boolean check;

    public PopulationLabelListItemBean(String tagId, String tagName, String categoryCode, boolean check) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.categoryCode = categoryCode;
        this.check = check;
    }

    public PopulationLabelListItemBean(String shwoContent, String tagId, String tagName, String categoryCode, boolean check) {
        super(shwoContent);
        this.tagId = tagId;
        this.tagName = tagName;
        this.categoryCode = categoryCode;
        this.check = check;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }


    @Override
    protected String showCommonContent() {
        return tagName == null ? "" : tagName;
    }

}
