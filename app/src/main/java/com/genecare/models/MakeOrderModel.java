package com.genecare.models;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.genecare.R;

import java.io.Serializable;

public class MakeOrderModel extends BaseObservable implements Serializable {

    private String user_id;
    private String main_service_id;
    private String sub_service_id;
    private int num_time;
    private int num_patient;
    private String age;
    private long date;
    private long time;
    private String address;
    private double lat;
    private double lng;
    private String phone;
    private String another_phone;
    private int type;
    private int payment;
    private String description;
    private double total;
    private boolean hasShift;
    private int shift_num;

    public ObservableField<String> error_age = new ObservableField<>();
    public ObservableField<String> error_address = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_description = new ObservableField<>();


    public boolean isDataValid(Context context)
    {
        if (!user_id.isEmpty()&&
                !main_service_id.isEmpty()&&
                !sub_service_id.isEmpty()&&
                num_time!=0&&
                num_patient!=0&&
                !age.isEmpty()&&
                date!=0&&
                time!=0&&
                !address.isEmpty()&&
                !phone.isEmpty()&&
                type!=0&&
                payment!=0&&
                !description.isEmpty()
        )
        {

            if (hasShift)
            {
                if (shift_num==0)
                {
                    return false;
                }else
                    {
                        return true;
                    }
            }else
                {
                   return true;
                }

        }else
            {
                if (num_time==0)
                {
                    Toast.makeText(context, R.string.ch_num_tim, Toast.LENGTH_SHORT).show();
                }

                if (num_patient==0)
                {
                    Toast.makeText(context, R.string.ch_num_pat, Toast.LENGTH_SHORT).show();
                }

                if (age.isEmpty())
                {
                    error_age.set(context.getString(R.string.field_req));
                }else
                    {
                        error_age.set(null);

                    }

                if (date==0)
                {
                    Toast.makeText(context, R.string.ch_date, Toast.LENGTH_SHORT).show();
                }

                if (time==0)
                {
                    Toast.makeText(context, R.string.ch_type, Toast.LENGTH_SHORT).show();
                }

                if (address.isEmpty())
                {
                    error_address.set(context.getString(R.string.field_req));
                }else
                {
                    error_address.set(null);

                }

                if (phone.isEmpty())
                {
                    error_phone.set(context.getString(R.string.field_req));
                }else
                {
                    error_phone.set(null);

                }

                if (type==0)
                {
                    Toast.makeText(context, R.string.ch_prov_gen, Toast.LENGTH_SHORT).show();

                }

                if (payment==0)
                {
                    Toast.makeText(context, R.string.ch_pay_way, Toast.LENGTH_SHORT).show();

                }

                if (description.isEmpty())
                {
                    error_description.set(context.getString(R.string.field_req));
                }else
                {
                    error_description.set(null);

                }


                if (hasShift)
                {
                    if (shift_num==0)
                    {
                        Toast.makeText(context, R.string.ch_num_shift, Toast.LENGTH_SHORT).show();

                    }
                }
                return false;
            }
    }

    public MakeOrderModel() {
        hasShift = false;
        user_id = "0";
        shift_num = 0;
        main_service_id ="0";
        sub_service_id="0";
        num_time = 0;
        num_patient = 0;
        age = "";
        date = 0;
        time = 0;
        address = "";
        lat = 0.0;
        lng = 0.0;
        phone = "";
        another_phone = "";
        type = 0;
        payment = 0;
        description = "";
        total = 0;

    }


    @Bindable
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
        notifyPropertyChanged(BR.user_id);
    }

    @Bindable
    public String getMain_service_id() {
        return main_service_id;
    }

    public void setMain_service_id(String main_service_id) {
        this.main_service_id = main_service_id;
        notifyPropertyChanged(BR.main_service_id);

    }

    @Bindable
    public String getSub_service_id() {
        return sub_service_id;
    }

    public void setSub_service_id(String sub_service_id) {
        this.sub_service_id = sub_service_id;
        notifyPropertyChanged(BR.sub_service_id);

    }

    @Bindable
    public int getNum_time() {
        return num_time;
    }

    public void setNum_time(int num_time) {
        this.num_time = num_time;
        notifyPropertyChanged(BR.num_time);

    }

    @Bindable
    public int getNum_patient() {
        return num_patient;
    }

    public void setNum_patient(int num_patient) {
        this.num_patient = num_patient;
        notifyPropertyChanged(BR.num_patient);

    }

    @Bindable
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
        notifyPropertyChanged(BR.age);

    }

    @Bindable
    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
        notifyPropertyChanged(BR.date);

    }

    @Bindable
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        notifyPropertyChanged(BR.time);

    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);

    }

    @Bindable
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Bindable
    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
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
    public String getAnother_phone() {
        return another_phone;
    }

    public void setAnother_phone(String another_phone) {
        this.another_phone = another_phone;
        notifyPropertyChanged(BR.another_phone);

    }

    @Bindable
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Bindable
    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Bindable
    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isHasShift() {
        return hasShift;
    }

    public void setHasShift(boolean hasShift) {
        this.hasShift = hasShift;
    }

    public int getShift_num() {
        return shift_num;
    }

    public void setShift_num(int shift_num) {
        this.shift_num = shift_num;
    }
}
