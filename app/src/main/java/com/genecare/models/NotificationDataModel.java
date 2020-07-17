package com.genecare.models;

import java.io.Serializable;
import java.util.List;

public class NotificationDataModel implements Serializable {

    private List<NotificationModel> data;
    private Meta meta;

    public List<NotificationModel> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public class NotificationModel implements Serializable
    {
        private String notification_id;
        private String process_id_fk;
        private String from_user_id;
        private String to_user_id;
        private String notification_type;
        private String action_type;
        private String is_read;
        private String created_at;
        private FromUser from_user;
        private Words words;

        public String getNotification_id() {
            return notification_id;
        }

        public String getProcess_id_fk() {
            return process_id_fk;
        }

        public String getFrom_user_id() {
            return from_user_id;
        }

        public String getTo_user_id() {
            return to_user_id;
        }

        public String getNotification_type() {
            return notification_type;
        }

        public String getAction_type() {
            return action_type;
        }

        public String getIs_read() {
            return is_read;
        }

        public String getCreated_at() {
            return created_at;
        }

        public FromUser getFrom_user() {
            return from_user;
        }

        public Words getWords() {
            return words;
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

    public class Words implements Serializable
    {
        private String id;
        private String notification_id_fk;
        private String title;
        private String content;

        public String getId() {
            return id;
        }

        public String getNotification_id_fk() {
            return notification_id_fk;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
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
