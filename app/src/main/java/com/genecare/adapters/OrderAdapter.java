package com.genecare.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.genecare.R;
import com.genecare.activities_fragments.activity_home.fragment.fragment_orders.Fragment_Client_Current_Orders;
import com.genecare.activities_fragments.activity_home.fragment.fragment_orders.Fragment_Client_Pending_Orders;
import com.genecare.activities_fragments.activity_home.fragment.fragment_orders.Fragment_Provider_Current_Orders;
import com.genecare.databinding.LoadMoreRowBinding;
import com.genecare.databinding.OrderRowBinding;
import com.genecare.models.OrderDataModel;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int DATA = 1;
    private final int LOAD = 2;
    private Context context;
    private List<OrderDataModel.OrderModel>  list;
    private String user_type;
    private Fragment fragment;

    public OrderAdapter(Context context, List<OrderDataModel.OrderModel>  list, Fragment fragment, String user_type) {
        this.context = context;
        this.list = list;
        this.user_type = user_type;
        this.fragment = fragment;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==DATA) {
            OrderRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.order_row, parent, false);
            return new Holder1(binding);


        }else
            {
                LoadMoreRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.load_more_row,parent,false);
                return new LoadHolder(binding);
            }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OrderDataModel.OrderModel orderModel = list.get(position);


        if (holder instanceof Holder1)
        {
            Holder1 holder1 = (Holder1) holder;
            holder1.binding.setOrderModel(orderModel);
            holder1.binding.setUserType(user_type);


            holder1.itemView.setOnClickListener(view -> {
                {
                    OrderDataModel.OrderModel orderModel2 = list.get(holder1.getAdapterPosition());

                    if (fragment instanceof Fragment_Client_Current_Orders)
                    {
                        Fragment_Client_Current_Orders fragment_client_current_orders = (Fragment_Client_Current_Orders) fragment;
                        fragment_client_current_orders.setItemData(orderModel2);
                    }else if (fragment instanceof Fragment_Client_Pending_Orders)
                    {
                        Fragment_Client_Pending_Orders fragment_client_pending_orders = (Fragment_Client_Pending_Orders) fragment;
                        fragment_client_pending_orders.setItemData(orderModel2,holder1.getAdapterPosition());
                    }
                    else if (fragment instanceof Fragment_Provider_Current_Orders)
                    {
                        Fragment_Provider_Current_Orders fragment_provider_current_orders = (Fragment_Provider_Current_Orders) fragment;
                        fragment_provider_current_orders.setItemData(orderModel2);
                    }
                }
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
        return list.size();
    }

    public class Holder1 extends RecyclerView.ViewHolder {
        private OrderRowBinding binding;

        public Holder1(@NonNull OrderRowBinding binding) {
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

        if (list.get(position)==null)
        {
            return LOAD;
        }else
        {
            return DATA;
        }
    }
}
