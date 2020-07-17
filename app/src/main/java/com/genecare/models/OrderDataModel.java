package com.genecare.models;

import java.io.Serializable;
import java.util.List;

public class OrderDataModel implements Serializable {
    private Meta meta;
    private List<OrderModel> data;

    public Meta getMeta() {
        return meta;
    }

    public List<OrderModel> getData() {
        return data;
    }

    public class OrderModel implements Serializable
    {
        private String order_id;
        private String user_id;
        private String provider_id;
        private String main_service_id;
        private String sub_service_id;
        private String order_date;
        private String order_time;
        private String order_status;
        private String age;
        private String gender;
        private String address;
        private String google_lat;
        private String google_long;
        private String phone;
        private String other_phone;
        private String payment;
        private String desc;
        private String price;
        private String num_times;
        private String num_patients;
        private String created_at;
        private String deleted;
        private Client client;
        private Provider provider;
        private Service service_titles;

        public String getOrder_id() {
            return order_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getProvider_id() {
            return provider_id;
        }

        public String getMain_service_id() {
            return main_service_id;
        }

        public String getSub_service_id() {
            return sub_service_id;
        }

        public String getOrder_date() {
            return order_date;
        }

        public String getOrder_time() {
            return order_time;
        }

        public String getOrder_status() {
            return order_status;
        }

        public String getAge() {
            return age;
        }

        public String getGender() {
            return gender;
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

        public String getPhone() {
            return phone;
        }

        public String getOther_phone() {
            return other_phone;
        }

        public String getPayment() {
            return payment;
        }

        public String getDesc() {
            return desc;
        }

        public String getPrice() {
            return price;
        }

        public String getNum_times() {
            return num_times;
        }

        public String getNum_patients() {
            return num_patients;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getDeleted() {
            return deleted;
        }

        public Client getClient() {
            return client;
        }

        public Provider getProvider() {
            return provider;
        }

        public Service getService_titles() {
            return service_titles;
        }
    }
    public class Client implements Serializable
    {
        private String user_id;
        private String user_type;
        private String be_provider;
        private String phone;
        private String phone_code;
        private String name;
        private String email;
        private String about;
        private String address;
        private String google_lat;
        private String google_long;
        private String logo;
        private String service_id;
        private String gender;
        private String exper;
        private String is_active;
        private String soft_type;
        private String available;
        private String deleted;

        public String getUser_id() {
            return user_id;
        }

        public String getUser_type() {
            return user_type;
        }

        public String getBe_provider() {
            return be_provider;
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

        public String getService_id() {
            return service_id;
        }

        public String getGender() {
            return gender;
        }

        public String getExper() {
            return exper;
        }

        public String getIs_active() {
            return is_active;
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
    }

    public class Provider implements Serializable
    {
        private String user_id;
        private String user_type;
        private String be_provider;
        private String phone;
        private String phone_code;
        private String name;
        private String email;
        private String about;
        private String address;
        private String google_lat;
        private String google_long;
        private String logo;
        private String service_id;
        private String gender;
        private String exper;
        private String is_active;
        private String soft_type;
        private String available;
        private String deleted;

        public String getUser_id() {
            return user_id;
        }

        public String getUser_type() {
            return user_type;
        }

        public String getBe_provider() {
            return be_provider;
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

        public String getService_id() {
            return service_id;
        }

        public String getGender() {
            return gender;
        }

        public String getExper() {
            return exper;
        }

        public String getIs_active() {
            return is_active;
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
    }

    public class Service implements Serializable
    {
        private String id;
        private String service_id_fk;
        private String title;
        private String content;

        public String getId() {
            return id;
        }

        public String getService_id_fk() {
            return service_id_fk;
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
