package com.genecare.models;

import java.io.Serializable;

public class OrderIdModel implements Serializable {
    private String id;
    private String payment_link;
    private String sucess_link;
    private String error_link;


    public String getId() {
        return id;
    }

    public String getPayment_link() {
        return payment_link;
    }

    public String getSucess_link() {
        return sucess_link;
    }

    public String getError_link() {
        return error_link;
    }
}
