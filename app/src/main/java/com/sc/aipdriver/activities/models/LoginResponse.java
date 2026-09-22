package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev on 5/3/18.
 */

public class LoginResponse {

        @SerializedName("Version")
        @Expose
        private String version;
        @SerializedName("StatusCode")
        @Expose
        private Integer statusCode;
        @SerializedName("Message")
        @Expose
        private String message;
        @SerializedName("Data")
        @Expose
        private LogInData data;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LogInData getData() {
            return data;
        }

        public void setData(LogInData data) {
            this.data = data;
        }

    }

