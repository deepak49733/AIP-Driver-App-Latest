package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev on 23/4/19.
 */

public class FormDetails {

    @SerializedName("ContactName")
        @Expose
        private String contactName;
        @SerializedName("CellPhone")
        @Expose
        private String cellPhone;
        @SerializedName("NumberOfBagsLoaded")
        @Expose
        private String numberOfBagsLoaded;
        @SerializedName("TemperatureOfSemenLoaded")
        @Expose
        private String temperatureOfSemenLoaded;
        @SerializedName("CreatedDate")
        @Expose
        private String createdDate;
        @SerializedName("NumberOfBagsDelivered")
        @Expose
        private String numberOfBagsDelivered;
        @SerializedName("TemperatureOfSemenDelivered")
        @Expose
        private String temperatureOfSemenDelivered;
        @SerializedName("DeliveryDate")
        @Expose
        private String deliveryDate;

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getCellPhone() {
            return cellPhone;
        }

        public void setCellPhone(String cellPhone) {
            this.cellPhone = cellPhone;
        }

        public String getNumberOfBagsLoaded() {
            return numberOfBagsLoaded;
        }

        public void setNumberOfBagsLoaded(String numberOfBagsLoaded) {
            this.numberOfBagsLoaded = numberOfBagsLoaded;
        }

        public String getTemperatureOfSemenLoaded() {
            return temperatureOfSemenLoaded;
        }

        public void setTemperatureOfSemenLoaded(String temperatureOfSemenLoaded) {
            this.temperatureOfSemenLoaded = temperatureOfSemenLoaded;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getNumberOfBagsDelivered() {
            return numberOfBagsDelivered;
        }

        public void setNumberOfBagsDelivered(String numberOfBagsDelivered) {
            this.numberOfBagsDelivered = numberOfBagsDelivered;
        }

        public String getTemperatureOfSemenDelivered() {
            return temperatureOfSemenDelivered;
        }

        public void setTemperatureOfSemenDelivered(String temperatureOfSemenDelivered) {
            this.temperatureOfSemenDelivered = temperatureOfSemenDelivered;
        }

        public String getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(String deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

    }


