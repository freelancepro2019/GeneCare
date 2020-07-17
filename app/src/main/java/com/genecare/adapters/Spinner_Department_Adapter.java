package com.genecare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.genecare.R;
import com.genecare.databinding.SpinnerDepartmentRowBinding;
import com.genecare.models.ServicesDataModel;

import java.util.List;

public class Spinner_Department_Adapter extends BaseAdapter {
    private List<ServicesDataModel.ServiceModel> data;
    private Context context;

    public Spinner_Department_Adapter(List<ServicesDataModel.ServiceModel> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") SpinnerDepartmentRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.spinner_department_row,viewGroup,false);
        binding.setData(data.get(i).getWords().getTitle());
        return binding.getRoot();
    }
}
