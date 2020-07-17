package com.genecare.models;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.genecare.R;

public class EditProfileDoctorModel extends BaseObservable {

    private String image_url;
    private String name;
    private String email;
    private String phone_code;
    private String phone;
    private String experience_id;
    private String department_id;
    private String about_me;
    private int gender;


    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_phone_code = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_about_me = new ObservableField<>();



    public boolean isDataValid(Context context)
    {
        if (!TextUtils.isEmpty(name)&&
                !TextUtils.isEmpty(email)&&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()&&
                !TextUtils.isEmpty(phone_code)&&
                !TextUtils.isEmpty(phone)&&
                !TextUtils.isEmpty(experience_id)&&
                !TextUtils.isEmpty(department_id)&&
                !TextUtils.isEmpty(about_me)&&
                gender!=0
        )
        {
            error_name.set(null);
            error_email.set(null);
            error_phone_code.set(null);
            error_phone.set(null);
            error_about_me.set(null);

            return true;
        }else
        {

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



            if (about_me.isEmpty())
            {
                error_about_me.set(context.getString(R.string.field_req));
            }else
            {
                error_about_me.set(null);
            }

            if (experience_id.isEmpty())
            {
                Toast.makeText(context, R.string.ch_exper, Toast.LENGTH_SHORT).show();
            }

            if (department_id.isEmpty())
            {
                Toast.makeText(context, R.string.ch_dept, Toast.LENGTH_SHORT).show();

            }

            if (gender==0)
            {
                Toast.makeText(context, R.string.ch_gender, Toast.LENGTH_SHORT).show();

            }



            return false;
        }
    }

    public EditProfileDoctorModel() {
        this.gender =0;

        this.phone_code = "";
        notifyPropertyChanged(BR.phone_code);
        this.phone="";
        notifyPropertyChanged(BR.phone);
        this.name = "";
        notifyPropertyChanged(BR.name);
        this.email = "";
        notifyPropertyChanged(BR.email);
        this.image_url ="";

        this.experience_id ="";

        this.department_id ="";

        this.about_me ="";
        notifyPropertyChanged(BR.about_me);



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


    public String getExperience_id() {
        return experience_id;
    }

    public void setExperience_id(String experience_id) {
        this.experience_id = experience_id;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    @Bindable
    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
        notifyPropertyChanged(BR.about_me);
    }


    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
