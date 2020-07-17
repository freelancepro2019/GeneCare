package com.genecare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.genecare.R;
import com.genecare.activities_fragments.activity_home.HomeActivity;
import com.genecare.databinding.SubServiceRowBinding;
import com.genecare.models.SubServicesModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class SubServiceAdapter extends RecyclerView.Adapter<SubServiceAdapter.MyViewHolder> {

    private List<SubServicesModel.SubServiceModel> subServiceModelList;
    private Context context;
    private HomeActivity activity;
    private String lang;

    public SubServiceAdapter(Context context, List<SubServicesModel.SubServiceModel> subServiceModelList){

        this.context=context;
        this.subServiceModelList = subServiceModelList;
        activity = (HomeActivity) context;
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
    }

    @Override
    public SubServiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        SubServiceRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.sub_service_row,parent,false);
         return  new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(SubServiceAdapter.MyViewHolder holder, int position) {
        SubServicesModel.SubServiceModel model = subServiceModelList.get(position);
        holder.binding.setLang(lang);
        holder.binding.setSubModel(model);
        holder.itemView.setOnClickListener(view -> {
            SubServicesModel.SubServiceModel subServiceModel = subServiceModelList.get(holder.getAdapterPosition());
            activity.setSubServiceItemData(subServiceModel);
        });
    }

    @Override
    public int getItemCount() {
        return subServiceModelList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private SubServiceRowBinding binding;

        public MyViewHolder(SubServiceRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }


        }



}