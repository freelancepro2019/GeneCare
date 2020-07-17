package com.genecare.models;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.genecare.R;

import java.io.Serializable;

public class ContactUsModel extends BaseObservable implements Serializable {

    private String name;
    private String email;
    private String subject;
    private String message;
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_subject = new ObservableField<>();
    public ObservableField<String> error_message = new ObservableField<>();


    public ContactUsModel() {
        this.name = "";
        this.email = "";
        this.subject = "";
        this.message = "";
    }

    public ContactUsModel(String name, String email, String subject, String message) {
        this.name = name;
        notifyPropertyChanged(BR.name);
        this.email = email;
        notifyPropertyChanged(BR.email);

        this.subject = subject;
        notifyPropertyChanged(BR.subject);

        this.message = message;
        notifyPropertyChanged(BR.message);

    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);


    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Bindable
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDataValid(Context context) {
        if (!TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(email) &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                !TextUtils.isEmpty(subject) &&
                !TextUtils.isEmpty(message)

        ) {
            error_name.set(null);
            error_email.set(null);
            error_subject.set(null);
            error_message.set(null);

            return true;
        } else {
            if (name.isEmpty()) {
                error_name.set(context.getString(R.string.field_req));
            } else {
                error_name.set(null);
            }


            if (email.isEmpty()) {
                error_email.set(context.getString(R.string.field_req));
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                error_email.set(context.getString(R.string.inv_email));
            } else {
                error_email.set(null);
            }

            if (subject.isEmpty()) {
                error_subject.set(context.getString(R.string.field_req));
            } else {
                error_subject.set(null);
            }


            if (message.isEmpty()) {
                error_message.set(context.getString(R.string.field_req));
            }  else {
                error_message.set(null);

            }


            return false;
        }
    }
}
