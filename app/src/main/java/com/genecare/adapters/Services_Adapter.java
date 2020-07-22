package com.genecare.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.genecare.R;
import com.genecare.activities_fragments.activity_home.fragment.Fragment_Main;
import com.genecare.databinding.ServiceRowBinding;
import com.genecare.models.ServicesDataModel;

import java.util.List;

import io.paperdb.Paper;

public class Services_Adapter extends RecyclerView.Adapter<Services_Adapter.Service_Holder> {
    private List<ServicesDataModel.ServiceModel> serviceModelList;
    private Context context;
    private Fragment_Main fragment_main;
    private int pos_color = 0;
    private int []  colors = {R.color.color1,R.color.color2,R.color.color5,R.color.color6,R.color.color7,R.color.color8,R.color.color9};
    private String lang;

    public Services_Adapter(List<ServicesDataModel.ServiceModel> serviceModelList, Context context, Fragment_Main fragment_main) {
        this.serviceModelList = serviceModelList;
        this.context = context;
        this.fragment_main = fragment_main;
        Paper.init(context);
        lang = Paper.book().read("lang","ar");

    }

    @Override
    public Service_Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        ServiceRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.service_row, viewGroup, false);
        return new Service_Holder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull final Service_Holder holder, final int position) {
        ServicesDataModel.ServiceModel serviceModel = serviceModelList.get(position);
        holder.binding.setServiceModel(serviceModel);
        holder.binding.image.setColorFilter(ContextCompat.getColor(context, R.color.white));
        holder.binding.setLang(lang);
        holder.binding.image.setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary));
       /* int pos = position%colors.length;
        holder.binding.image.setBackgroundResource(colors[pos]);
        holder.binding.tvName.setTextColor(ContextCompat.getColor(context,colors[pos]));
        holder.binding.arrow.setColorFilter(ContextCompat.getColor(context,colors[pos]));*/

        holder.itemView.setOnClickListener(v -> {
            ServicesDataModel.ServiceModel model1 = serviceModelList.get(holder.getAdapterPosition());

            fragment_main.setItemData(model1,R.color.colorPrimary);


        });


    }

    @Override
    public int getItemCount() {
        return serviceModelList.size();
    }

    public class Service_Holder extends RecyclerView.ViewHolder {
        private ServiceRowBinding binding;

        public Service_Holder(@NonNull ServiceRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }

}
