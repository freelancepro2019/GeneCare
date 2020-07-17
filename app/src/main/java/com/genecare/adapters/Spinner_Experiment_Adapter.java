package com.genecare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.genecare.R;
import com.genecare.databinding.SpinnerExperimentRowBinding;

import java.util.List;

public class Spinner_Experiment_Adapter extends BaseAdapter {
    private List<String> data;
    private Context context;

    public Spinner_Experiment_Adapter(List<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") SpinnerExperimentRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.spinner_experiment_row,viewGroup,false);
        binding.setData(data.get(i));
        return binding.getRoot();
    }
}
