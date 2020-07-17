package com.genecare.models;

import java.io.Serializable;

public class UserModel implements Serializable {

    private String user_id;
    private String user_type;
    private String name;
    private String email;
    private String phone_code;
    private String phone;
    private String about;
    private String address;
    private String google_lat;
    private String google_long;
    private String logo;
    private String banner;
    private String gender;
    private String exper;
    private String be_provider;
    private String is_active;
    private String is_login;
    private String soft_type;
    private String available;
    private String deleted;
    private String token;
    private double user_rating;
    private Service service;



    public String getUser_id() {
        return user_id;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public String getPhone() {
        return phone;
    }

    public String getAbout() {
        return about;
    }

    public String getAddress() {
        return address;
    }

    public String getGoogle_lat() {
        return google_lat;
    }

    public String getGoogle_long() {
        return google_long;
    }

    public String getLogo() {
        return logo;
    }

    public String getBanner() {
        return banner;
    }


    public String getGender() {
        return gender;
    }

    public String getExper() {
        return exper;
    }

    public String getBe_provider() {
        return be_provider;
    }

    public String getIs_active() {
        return is_active;
    }

    public String getIs_login() {
        return is_login;
    }

    public String getSoft_type() {
        return soft_type;
    }

    public String getAvailable() {
        return available;
    }

    public String getDeleted() {
        return deleted;
    }

    public String getToken() {
        return token;
    }

    public double getUser_rating() {
        return user_rating;
    }

    public Service getService() {
        return service;
    }

    public class Service implements Serializable
    {
        private String service_id_fk;
        private String title;
        private String content;

        public String getId() {
            return service_id_fk;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }
}
