package com.genecare.activities_fragments.activity_home.fragment.fragment_orders;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genecare.R;
import com.genecare.activities_fragments.activity_home.HomeActivity;
import com.genecare.activities_fragments.activity_order_details.OrderDetailsActivity;
import com.genecare.adapters.OrderAdapter;
import com.genecare.databinding.FragmentPendingCurrentPreviousOrderBinding;
import com.genecare.models.OrderDataModel;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;
import com.genecare.remote.Api;
import com.genecare.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Client_Pending_Orders extends Fragment  {
    private FragmentPendingCurrentPreviousOrderBinding binding;
    private HomeActivity activity;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private LinearLayoutManager manager;
    private List<OrderDataModel.OrderModel> orderModelList;
    private OrderAdapter adapter;
    private boolean isLoading = false;
    private int current_page = 1;
    private int selectedPos=-1;

    public static Fragment_Client_Pending_Orders newInstance() {
        return new Fragment_Client_Pending_Orders();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_pending_current_previous_order, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        orderModelList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary,R.color.color1,R.color.color2,R.color.color3);

        manager = new LinearLayoutManager(activity);
        binding.recView.setLayoutManager(manager);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        adapter = new OrderAdapter(activity,orderModelList,this,userModel.getUser_type());
        binding.recView.setAdapter(adapter);
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {
                    int total = binding.recView.getAdapter().getItemCount();

                    int lastVisibleItem = ((LinearLayoutManager)binding.recView.getLayoutManager()).findLastCompletelyVisibleItemPosition();


                    if (total>6&&(total-lastVisibleItem)==2&&!isLoading)
                    {
                        isLoading = true;
                        int page = current_page+1;
                        orderModelList.add(null);
                        adapter.notifyDataSetChanged();
                        loadMore(page);
                    }
                }
            }
        });

        getOrders(false);

        binding.swipeRefresh.setOnRefreshListener(() ->
                getOrders(false)
        );
    }


    public void getOrders(boolean vis) {

        if (vis)
        {
            binding.swipeRefresh.setRefreshing(true);
        }else
            {
                binding.swipeRefresh.setRefreshing(false);

            }
        try {

            Api.getService(Tags.base_url)
                    .getClientOrders(lang,userModel.getToken(),"pending",1,20)
                    .enqueue(new Callback<OrderDataModel>() {
                        @Override
                        public void onResponse(Call<OrderDataModel> call, Response<OrderDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                orderModelList.clear();
                                orderModelList.addAll(response.body().getData());
                                if (orderModelList.size() > 0) {
                                    adapter.notifyDataSetChanged();
                                    binding.tvNoOrders.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoOrders.setVisibility(View.VISIBLE);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderDataModel> call, Throwable t) {
                            try {
                                binding.swipeRefresh.setRefreshing(false);

                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void loadMore(int page)
    {
        try {
            Api.getService(Tags.base_url)
                    .getClientOrders(lang,userModel.getToken(),"pending", page,20)
                    .enqueue(new Callback<OrderDataModel>() {
                        @Override
                        public void onResponse(Call<OrderDataModel> call, Response<OrderDataModel> response) {
                            isLoading = false;
                            orderModelList.remove(orderModelList.size()-1);
                            adapter.notifyItemRemoved(orderModelList.size()-1);

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                orderModelList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();
                                if (response.body().getData().size()>0)
                                {
                                    current_page = response.body().getMeta().getCurrent_page();

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderDataModel> call, Throwable t) {
                            try {
                                if (orderModelList.get(orderModelList.size()-1)==null)
                                {
                                    isLoading = false;
                                    orderModelList.remove(orderModelList.size()-1);
                                    adapter.notifyItemRemoved(orderModelList.size()-1);

                                }
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }


    public void setItemData(OrderDataModel.OrderModel orderModel, int adapterPosition) {
        this.selectedPos = adapterPosition;

        Intent intent = new Intent(activity, OrderDetailsActivity.class);
        intent.putExtra("order_id",orderModel.getOrder_id());
        intent.putExtra("from","order_client_pending");
        startActivityForResult(intent,100);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode== Activity.RESULT_OK&&data!=null)
        {

            if (selectedPos!=-1)
            {
                orderModelList.remove(this.selectedPos);
                adapter.notifyItemRemoved(this.selectedPos);
                this.selectedPos=-1;

                if (orderModelList.size()>0)
                {
                    binding.tvNoOrders.setVisibility(View.GONE);
                }else {
                    binding.tvNoOrders.setVisibility(View.VISIBLE);

                }
            }

        }
    }
}
