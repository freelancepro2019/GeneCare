package com.genecare.models;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.genecare.R;

public class SignUpClientModel extends BaseObservable {

    private String image_url;
    private String name;
    private String email;
    private String phone_code;
    private String phone;
    private String password;
    private int gender;
    private boolean isAccept;

    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_phone_code = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_password = new ObservableField<>();



    public boolean isDataValid(Context context)
    {

        Log.e("name",name);
        Log.e("email",email);
        Log.e("phone_code",phone_code);
        Log.e("phone",phone);
        Log.e("gender",gender+"__");
        Log.e("password",password);
        Log.e("isAccept",isAccept+"_");


        if (!TextUtils.isEmpty(name)&&
                !TextUtils.isEmpty(email)&&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()&&
                !TextUtils.isEmpty(phone_code)&&
                !TextUtils.isEmpty(phone)&&
                gender!=0&&
                password.length()>=6&&
                isAccept
        )
        {



            error_name.set(null);
            error_email.set(null);
            error_phone_code.set(null);
            error_phone.set(null);
            error_password.set(null);

            return true;
        }else
        {

            Log.e("ddd","ffff");

            if (name.isEmpty())
            {
                error_name.set(context.getString(R.string.field_req));
            }else
            {
                error_name.set(null);
            }

            if (email.isEmpty())
            {
                error_email.set(context.getString(R.string.field_req));

            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                error_email.set(context.getString(R.string.inv_email));
            }
            else
            {
                error_email.set(null);
            }

            if (phone_code.isEmpty())
            {
                error_phone_code.set(context.getString(R.string.field_req));
            }else
            {
                error_phone_code.set(null);
            }

            if (phone.isEmpty())
            {
                error_phone.set(context.getString(R.string.field_req));
            }else
            {
                error_phone.set(null);
            }

            if (password.isEmpty())
            {
                error_password.set(context.getString(R.string.field_req));
            }else if (password.length()<6)
            {
                error_password.set(context.getString(R.string.pass_short));
            }else
            {
                error_password.set(null);
            }

            if (gender==0)
            {
                Toast.makeText(context, R.string.ch_gender, Toast.LENGTH_SHORT).show();

            }
            if (!isAccept)
            {
                Toast.makeText(context, R.string.accept_terms_and_conditions, Toast.LENGTH_SHORT).show();

            }

            return false;
        }
    }

    public SignUpClientModel() {
        gender = 0;
        isAccept = false;
        this.phone_code = "";
        notifyPropertyChanged(BR.phone_code);
        this.phone="";
        notifyPropertyChanged(BR.phone);
        this.password = "";
        notifyPropertyChanged(BR.password);
        this.name = "";
        notifyPropertyChanged(BR.name);
        this.email = "";
        notifyPropertyChanged(BR.email);
        this.image_url ="";




    }


    @Bindable
    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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
        notifyPropertyChanged(BR.email);

    }

    @Bindable
    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
        notifyPropertyChanged(BR.phone_code);

    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);

    }
    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);

    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }
}
