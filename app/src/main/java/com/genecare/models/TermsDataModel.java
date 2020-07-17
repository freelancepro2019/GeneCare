package com.genecare.models;

import java.io.Serializable;

public class TermsDataModel implements Serializable {

    private Setting settings;

    public Setting getSettings() {
        return settings;
    }

    public class Setting implements Serializable
    {
        private String website;
        private String ar_address;
        private String en_address;
        private String phones;
        private String emails;
        private String ar_about;
        private String en_about;
        private String facebook;
        private String twitter;
        private String instagram;
        private String whatsapp;
        private String ar_client_condition;
        private String en_client_condition;
        private String ar_provider_condition;
        private String en_provider_condition;
        private String ar_termis_condition;
        private String en_termis_condition;


        public String getWebsite() {
            return website;
        }

        public String getAr_address() {
            return ar_address;
        }

        public String getEn_address() {
            return en_address;
        }

        public String getPhones() {
            return phones;
        }

        public String getEmails() {
            return emails;
        }

        public String getAr_about() {
            return ar_about;
        }

        public String getEn_about() {
            return en_about;
        }

        public String getFacebook() {
            return facebook;
        }

        public String getTwitter() {
            return twitter;
        }

        public String getInstagram() {
            return instagram;
        }

        public String getWhatsapp() {
            return whatsapp;
        }

        public String getAr_client_condition() {
            return ar_client_condition;
        }

        public String getEn_client_condition() {
            return en_client_condition;
        }

        public String getAr_provider_condition() {
            return ar_provider_condition;
        }

        public String getEn_provider_condition() {
            return en_provider_condition;
        }

        public String getAr_termis_condition() {
            return ar_termis_condition;
        }

        public String getEn_termis_condition() {
            return en_termis_condition;
        }
    }
}
