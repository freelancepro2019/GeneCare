package com.genecare.models;

import java.io.Serializable;
import java.util.List;

public class CommentDataModel implements Serializable {

    private Meta meta;
    private List<CommentModel> data;

    public Meta getMeta() {
        return meta;
    }

    public List<CommentModel> getData() {
        return data;
    }

    public class CommentModel implements Serializable
    {
        private String id;
        private String order_id;
        private String from_id;
        private String to_id;
        private String rate_num;
        private String rate_comment;
        private FromUser from_user;

        public String getId() {
            return id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public String getFrom_id() {
            return from_id;
        }

        public String getTo_id() {
            return to_id;
        }

        public String getRate_num() {
            return rate_num;
        }

        public String getRate_comment() {
            return rate_comment;
        }

        public FromUser getFromUser() {
            return from_user;
        }
    }


    public class FromUser implements Serializable
    {
        private String user_id;
        private String user_type;
        private String phone;
        private String phone_code;
        private String name;
        private String email;
        private String about;
        private String address;
        private String google_lat;
        private String google_long;
        private String logo;
        private String gender;

        public String getUser_id() {
            return user_id;
        }

        public String getUser_type() {
            return user_type;
        }

        public String getPhone() {
            return phone;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
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

        public String getGender() {
            return gender;
        }
    }

    public class Meta implements Serializable
    {
        private int current_page;

        public int getCurrent_page() {
            return current_page;
        }
    }
}
