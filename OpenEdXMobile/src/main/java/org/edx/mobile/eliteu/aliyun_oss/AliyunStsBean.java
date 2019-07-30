package org.edx.mobile.eliteu.aliyun_oss;

import java.io.Serializable;

public class AliyunStsBean implements Serializable {

    private String RequestId;
    private AssumedRoleUserBean AssumedRoleUser;
    private CredentialsBean Credentials;

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String RequestId) {
        this.RequestId = RequestId;
    }

    public AssumedRoleUserBean getAssumedRoleUser() {
        return AssumedRoleUser;
    }

    public void setAssumedRoleUser(AssumedRoleUserBean AssumedRoleUser) {
        this.AssumedRoleUser = AssumedRoleUser;
    }

    public CredentialsBean getCredentials() {
        return Credentials;
    }

    public void setCredentials(CredentialsBean Credentials) {
        this.Credentials = Credentials;
    }

    public static class AssumedRoleUserBean implements Serializable {

        private String AssumedRoleId;
        private String Arn;

        public String getAssumedRoleId() {
            return AssumedRoleId;
        }

        public void setAssumedRoleId(String AssumedRoleId) {
            this.AssumedRoleId = AssumedRoleId;
        }

        public String getArn() {
            return Arn;
        }

        public void setArn(String Arn) {
            this.Arn = Arn;
        }
    }

    public static class CredentialsBean implements Serializable {

        private String AccessKeySecret;
        private String AccessKeyId;
        private String Expiration;
        private String SecurityToken;

        public String getAccessKeySecret() {
            return AccessKeySecret;
        }

        public void setAccessKeySecret(String AccessKeySecret) {
            this.AccessKeySecret = AccessKeySecret;
        }

        public String getAccessKeyId() {
            return AccessKeyId;
        }

        public void setAccessKeyId(String AccessKeyId) {
            this.AccessKeyId = AccessKeyId;
        }

        public String getExpiration() {
            return Expiration;
        }

        public void setExpiration(String Expiration) {
            this.Expiration = Expiration;
        }

        public String getSecurityToken() {
            return SecurityToken;
        }

        public void setSecurityToken(String SecurityToken) {
            this.SecurityToken = SecurityToken;
        }
    }
}
