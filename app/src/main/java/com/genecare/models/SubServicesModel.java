package com.genecare.models;


import java.io.Serializable;
import java.util.List;

public class SubServicesModel implements Serializable {
    private List<SubServiceModel> services;

    public class SubServiceModel implements Serializable {
        private String service_id;
        private String logo;
        private String cost;
        private WordsModel words;
        private List<Integer> other_details;// maybe null

        public WordsModel getWords() {
            return words;
        }

        public String getId() {
            return service_id;
        }

        public String getLogo() {
            return logo;
        }

        public String getCost() {
            return cost;
        }

        public String getService_id() {
            return service_id;
        }

        public List<Integer> getOther_details() {
            return other_details;
        }

        public class WordsModel implements Serializable {
            private String title;
            private String content;

            public String getTitle() {
                return title;
            }

            public String getContent() {
                return content;
            }
        }
    }

    public List<SubServiceModel> getServices() {
        return services;
    }

}
