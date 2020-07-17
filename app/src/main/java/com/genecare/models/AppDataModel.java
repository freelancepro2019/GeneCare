package com.genecare.models;

import java.io.Serializable;

public class AppDataModel implements Serializable {
private Data data;

    public Data getData() {
        return data;
    }

    public class Data implements Serializable {
        private About about;
        private Conditions conditions;

        public About getAbout() {
            return about;
        }

        public Conditions getConditions() {
            return conditions;
        }

        public class About implements Serializable {
            private int id;
                   private String link;
            private String en_title;
            private String ar_title;
            private String ar_content;
            private String en_content;

            public int getId() {
                return id;
            }

            public String getLink() {
                return link;
            }

            public String getEn_title() {
                return en_title;
            }

            public String getAr_title() {
                return ar_title;
            }

            public String getAr_content() {
                return ar_content;
            }

            public String getEn_content() {
                return en_content;
            }
        }
       public class Conditions implements Serializable {
            private int id;
            private String link;
            private String en_title;
            private String ar_title;
            private String ar_content;
            private String en_content;

           public int getId() {
               return id;
           }

           public String getLink() {
               return link;
           }

           public String getEn_title() {
               return en_title;
           }

           public String getAr_title() {
               return ar_title;
           }

           public String getAr_content() {
               return ar_content;
           }

           public String getEn_content() {
               return en_content;
           }
       }
    }
}
