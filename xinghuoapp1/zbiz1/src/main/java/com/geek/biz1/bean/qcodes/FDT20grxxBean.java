package com.geek.biz1.bean.qcodes;

import java.io.Serializable;
import java.util.List;

public class FDT20grxxBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean implements Serializable {
        private boolean wxState;
        private String orgName;
        private String idCardHash;
        private String telephone;
        private String userId;
        private String orgId;
        private String joinPartyDate;
        private String identity;
        private String orgCode;
        private String name;
        private String logo;
        private String hash;
        private String username;
        private List<RolesBean> roles;
        private List<AuthoritiesBean> authorities;

        public boolean isWxState() {
            return wxState;
        }

        public void setWxState(boolean wxState) {
            this.wxState = wxState;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getIdCardHash() {
            return idCardHash;
        }

        public void setIdCardHash(String idCardHash) {
            this.idCardHash = idCardHash;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
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

        public String getJoinPartyDate() {
            return joinPartyDate;
        }

        public void setJoinPartyDate(String joinPartyDate) {
            this.joinPartyDate = joinPartyDate;
        }

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }

        public String getOrgCode() {
            return orgCode;
        }

        public void setOrgCode(String orgCode) {
            this.orgCode = orgCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<RolesBean> getRoles() {
            return roles;
        }

        public void setRoles(List<RolesBean> roles) {
            this.roles = roles;
        }

        public List<AuthoritiesBean> getAuthorities() {
            return authorities;
        }

        public void setAuthorities(List<AuthoritiesBean> authorities) {
            this.authorities = authorities;
        }

        public static class RolesBean implements Serializable {
            private String clientId;
            private String roleName;
            private String userId;

            public String getClientId() {
                return clientId;
            }

            public void setClientId(String clientId) {
                this.clientId = clientId;
            }

            public String getRoleName() {
                return roleName;
            }

            public void setRoleName(String roleName) {
                this.roleName = roleName;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }
        }

        public static class AuthoritiesBean implements Serializable {
            private String authority;

            public String getAuthority() {
                return authority;
            }

            public void setAuthority(String authority) {
                this.authority = authority;
            }
        }
    }
}
