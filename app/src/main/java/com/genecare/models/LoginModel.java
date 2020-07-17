package com.genecare.models;

import android.content.Context;
import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.genecare.R;

public class LoginModel extends BaseObservable {

    private String phone_code;
    private String phone;
    private String password;

    public ObservableField<String> error_phone_code = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_password = new ObservableField<>();



    public LoginModel() {
        this.phone_code = "";
        notifyPropertyChanged(BR.phone_code);
        this.phone="";
        notifyPropertyChanged(BR.phone);
        this.password = "";
        notifyPropertyChanged(BR.password);

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

    public boolean isDataValid(Context context)
    {
        if (!TextUtils.isEmpty(phone_code)&&
                !TextUtils.isEmpty(phone)&&
                password.length()>=6
        )
        {
            error_phone_code.set(null);
            error_phone.set(null);
            error_password.set(null);

            return true;
        }else
        {
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


            return false;
        }
    }


}
