package com.genecare.models;


import java.io.Serializable;
import java.util.List;

public class ServicesDataModel implements Serializable {
    private List<ServiceModel> services;
    private Counter counters;

    public List<ServiceModel> getServices() {
        return services;
    }

    public Counter getCounters() {
        return counters;
    }

    public class Counter implements Serializable
    {
        private int service;
        private int users;
        private int providers;

        public int getService() {
            return service;
        }

        public int getUsers() {
            return users;
        }

        public int getProviders() {
            return providers;
        }
    }
    public static class  ServiceModel implements Serializable {
        private String service_id;
        private String logo;
        private String cost;
        private WordsModel words;

        public WordsModel getWords() {
            return words;
        }

        public String getService_id() {
            return service_id;
        }

        public String getLogo() {
            return logo;
        }

        public String getCost() {
            return cost;
        }

        public void setWords(WordsModel words) {
            this.words = words;
        }

        public static class WordsModel implements Serializable {
            private String title;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }


}
