package com.geek.biz1.bean;

import java.io.Serializable;
import java.util.List;


public class SNewListBean1 implements Serializable {

    private int totalelements;
    private int totalpages;
    private List<ListBean> list;

    public int getTotalelements() {
        return totalelements;
    }

    public void setTotalelements(int totalelements) {
        this.totalelements = totalelements;
    }

    public int getTotalpages() {
        return totalpages;
    }

    public void setTotalpages(int totalpages) {
        this.totalpages = totalpages;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable {
        private String id;
        private String categoryId;
        private String title;
        private String subTitle;
        private long releaseTime;
        private String status;
        private String type;
        private int pv;
        private Object address;
        private Object contactPerson;
        private Object contactPhone;
        private Object partyServiceCenter;
        private ExtBean ext;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public long getReleaseTime() {
            return releaseTime;
        }

        public void setReleaseTime(long releaseTime) {
            this.releaseTime = releaseTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getPv() {
            return pv;
        }

        public void setPv(int pv) {
            this.pv = pv;
        }

        public Object getAddress() {
            return address;
        }

        public void setAddress(Object address) {
            this.address = address;
        }

        public Object getContactPerson() {
            return contactPerson;
        }

        public void setContactPerson(Object contactPerson) {
            this.contactPerson = contactPerson;
        }

        public Object getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(Object contactPhone) {
            this.contactPhone = contactPhone;
        }

        public Object getPartyServiceCenter() {
            return partyServiceCenter;
        }

        public void setPartyServiceCenter(Object partyServiceCenter) {
            this.partyServiceCenter = partyServiceCenter;
        }

        public ExtBean getExt() {
            return ext;
        }

        public void setExt(ExtBean ext) {
            this.ext = ext;
        }

        public static class ExtBean implements Serializable {
            private String url;
            private String appUrl;
            private Object icon;
            private String thumbnail;
            private Object sourceApp;
            private Object resourceHour;
            private int resourcePv;
            private Object openTime;
            private Object teacher;
            private Object studyHour;
            private Object lessonOrganize;
            private Object lessonProduceOrganize;
            private Object lessonCount;
            private Object classType;
            private String origin;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getAppUrl() {
                return appUrl;
            }

            public void setAppUrl(String appUrl) {
                this.appUrl = appUrl;
            }

            public Object getIcon() {
                return icon;
            }

            public void setIcon(Object icon) {
                this.icon = icon;
            }

            public String getThumbnail() {
                return thumbnail;
            }

            public void setThumbnail(String thumbnail) {
                this.thumbnail = thumbnail;
            }

            public Object getSourceApp() {
                return sourceApp;
            }

            public void setSourceApp(Object sourceApp) {
                this.sourceApp = sourceApp;
            }

            public Object getResourceHour() {
                return resourceHour;
            }

            public void setResourceHour(Object resourceHour) {
                this.resourceHour = resourceHour;
            }

            public int getResourcePv() {
                return resourcePv;
            }

            public void setResourcePv(int resourcePv) {
                this.resourcePv = resourcePv;
            }

            public Object getOpenTime() {
                return openTime;
            }

            public void setOpenTime(Object openTime) {
                this.openTime = openTime;
            }

            public Object getTeacher() {
                return teacher;
            }

            public void setTeacher(Object teacher) {
                this.teacher = teacher;
            }

            public Object getStudyHour() {
                return studyHour;
            }

            public void setStudyHour(Object studyHour) {
                this.studyHour = studyHour;
            }

            public Object getLessonOrganize() {
                return lessonOrganize;
            }

            public void setLessonOrganize(Object lessonOrganize) {
                this.lessonOrganize = lessonOrganize;
            }

            public Object getLessonProduceOrganize() {
                return lessonProduceOrganize;
            }

            public void setLessonProduceOrganize(Object lessonProduceOrganize) {
                this.lessonProduceOrganize = lessonProduceOrganize;
            }

            public Object getLessonCount() {
                return lessonCount;
            }

            public void setLessonCount(Object lessonCount) {
                this.lessonCount = lessonCount;
            }

            public Object getClassType() {
                return classType;
            }

            public void setClassType(Object classType) {
                this.classType = classType;
            }

            public String getOrigin() {
                return origin;
            }

            public void setOrigin(String origin) {
                this.origin = origin;
            }
        }
    }
}
