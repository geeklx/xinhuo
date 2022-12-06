package com.geek.appcommon.web.bean;

import java.io.Serializable;
import java.util.List;

public class DTRegionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public RootBean root;
    public List<TreeBean> tree;

    public static class RootBean {

        public String admId;
        public String orgName;
        public Object parentOrgCode;
        public String orgParentId;
        public Integer orderId;
        public String level;
        public Integer hasChildren;
        public Object userDefined;
        public Object admEncoding;
        public String revocationFlag;
        public String ua;
        public String orgId;
        public String admParentId;
        public String admName;
        public String orgCode;
        public String admCode;
        public Boolean enable;
        public String id;
        public String admParentName;
    }

    public static class TreeBean implements Serializable {
        public String admId;
        public String orgName;
        public String parentOrgCode;
        public String orgParentId;
        public Integer orderId;
        public String level;
        public Integer hasChildren;
        public String userDefined;
        public String admEncoding;
        public String revocationFlag;
        public String ua;
        public String orgId;
        public String admParentId;
        public String admName;
        public String orgCode;
        public String admCode;
        public Boolean enable;
        public String id;
        public String admParentName;
    }

}
