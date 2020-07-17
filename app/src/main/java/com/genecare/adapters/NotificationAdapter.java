package com.genecare.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.genecare.R;
import com.genecare.activities_fragments.activity_notification.NotificationActivity;
import com.genecare.databinding.LoadMoreRowBinding;
import com.genecare.databinding.NotificationRowBinding;
import com.genecare.models.NotificationDataModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int DATA = 1;
    private final int LOAD = 2;

    private Context context;
    private List<NotificationDataModel.NotificationModel>  notificationModelList;
    private NotificationActivity activity;

    public NotificationAdapter(Context context, List<NotificationDataModel.NotificationModel>  notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
        activity = (NotificationActivity) context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==DATA) {
            NotificationRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.notification_row, parent, false);
            return new Holder1(binding);


        }else
            {
                LoadMoreRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.load_more_row,parent,false);
                return new LoadHolder(binding);
            }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotificationDataModel.NotificationModel notificationModel = notificationModelList.get(position);

        if (holder instanceof Holder1)
        {
            Holder1 holder1 = (Holder1) holder;
            holder1.binding.setNotificationModel(notificationModel);

            holder1.itemView.setOnClickListener(view -> {
                NotificationDataModel.NotificationModel notificationModel2 = notificationModelList.get(position);
                activity.setItemData(notificationModel2,holder1.getAdapterPosition());
            });

        }else if (holder instanceof LoadHolder)
        {
            LoadHolder loadHolder = (LoadHolder) holder;
            loadHolder.binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            loadHolder.binding.progBar.setIndeterminate(true);

        }




    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class Holder1 extends RecyclerView.ViewHolder {
        private NotificationRowBinding binding;

        public Holder1(@NonNull NotificationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    public class LoadHolder extends RecyclerView.ViewHolder {
        private LoadMoreRowBinding binding;

        public LoadHolder(@NonNull LoadMoreRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (notificationModelList.get(position)==null)
        {
            return LOAD;
        }else
        {
            return DATA;
        }
    }
}
