package com.genecare.activities_fragments.activity_order_details;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.genecare.R;
import com.genecare.activities_fragments.activity_make_order.FragmentMapTouchListener;
import com.genecare.databinding.ActivityOrderDetailsBinding;
import com.genecare.interfaces.Listeners;
import com.genecare.language.LanguageHelper;
import com.genecare.models.SingleOrderDataModel;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;
import com.genecare.remote.Api;
import com.genecare.share.Common;
import com.genecare.tags.Tags;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity implements Listeners.BackListener , OnMapReadyCallback {
    private ActivityOrderDetailsBinding binding;
    private String lang;
    private UserModel userModel;
    private Preferences preferences;
    private String order_id;
    private String notification_id;
    private String from_user_id;
    private String from;
    private SingleOrderDataModel.OrderModel orderModel= null;
    private FragmentMapTouchListener fragment;
    private GoogleMap mMap;
    private Marker marker;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("order_id"))
        {
            order_id = intent.getStringExtra("order_id");
            from = intent.getStringExtra("from");
            notification_id = intent.getStringExtra("notification_id");
            from_user_id = intent.getStringExtra("from_user_id");

        }
    }


    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);
        binding.setUserId(userModel.getUser_id());
        binding.setFrom(from);
        binding.btnCall.setOnClickListener(view ->
        {
            String phone;

            if (userModel.getUser_type().equals("1"))
            {
                if (orderModel.getProvider()==null)
                {
                    Toast.makeText(this,R.string.order_is_pending, Toast.LENGTH_SHORT).show();
                }else
                    {
                        phone = orderModel.getProvider().getPhone_code()+orderModel.getProvider().getPhone();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
                        startActivity(intent);
                    }

            }else
                {
                    phone = orderModel.getClient().getPhone_code()+orderModel.getClient().getPhone();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
                    startActivity(intent);
                }


        });

        binding.btnWhats.setOnClickListener(view ->
        {

            String phone;

            if (userModel.getUser_type().equals("1"))
            {
                if (orderModel.getProvider()==null)
                {
                    Toast.makeText(this,R.string.order_is_pending, Toast.LENGTH_SHORT).show();
                }else
                {
                    phone = orderModel.getProvider().getPhone_code().replaceFirst("00","+")+orderModel.getProvider().getPhone();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+phone));
                    startActivity(intent);
                }

            }else
            {
                phone = orderModel.getProvider().getPhone_code().replaceFirst("00","+")+orderModel.getProvider().getPhone();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+phone));
                startActivity(intent);
            }






        });

        binding.btnAccept.setOnClickListener(view ->
                accept()
                );

        binding.btnRefuse.setOnClickListener(view ->
                refuse()
        );

        binding.btnEndOrder.setOnClickListener(view ->
                providerEndOrder()
        );

        binding.btnCancel.setOnClickListener(view ->
                cancelOrder()
        );

        initMap();
        getOrderData();


    }

    private void initMap()
    {

        fragment = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment!=null)
        {
            fragment.getMapAsync(this);

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);



            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));

        }
    }
    private void AddMarker()
    {


        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(orderModel.getGoogle_lat()),Double.parseDouble(orderModel.getGoogle_long()))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(orderModel.getGoogle_lat()),Double.parseDouble(orderModel.getGoogle_long())),15.6f));



    }

    private void accept() {
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .providerAcceptOrder(lang,userModel.getToken(),notification_id,from_user_id,order_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null )
                        {
                            Toast.makeText(OrderDetailsActivity.this, R.string.suc, Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            if (intent!=null)
                            {
                                setResult(RESULT_OK,intent);
                            }

                            finish();
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void refuse() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .providerRefuseOrder(lang,userModel.getToken(),notification_id,from_user_id,order_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null )
                        {
                            Toast.makeText(OrderDetailsActivity.this, R.string.suc, Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            if (intent!=null)
                            {
                                setResult(RESULT_OK,intent);
                            }

                            finish();
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void cancelOrder() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .cancelOrder(lang,userModel.getToken(),order_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null )
                        {
                            Toast.makeText(OrderDetailsActivity.this, R.string.suc, Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            if (intent!=null)
                            {
                                setResult(RESULT_OK,intent);
                            }

                            finish();
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void providerEndOrder() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .providerEndOrder(lang,userModel.getToken(),order_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null )
                        {
                            Toast.makeText(OrderDetailsActivity.this, R.string.suc, Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            if (intent!=null)
                            {
                                intent.putExtra("end",true);
                                setResult(RESULT_OK,intent);
                            }

                            finish();
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void getOrderData()
    {

        Api.getService(Tags.base_url).
                getOrderDetails(lang,userModel.getToken(),order_id).
                enqueue(new Callback<SingleOrderDataModel>() {
                    @Override
                    public void onResponse(Call<SingleOrderDataModel> call, Response<SingleOrderDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            orderModel = response.body().getOrder();
                            binding.setOrderModel(response.body().getOrder());
                            binding.llContainer.setVisibility(View.VISIBLE);
                            AddMarker();
                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(OrderDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(OrderDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleOrderDataModel> call, Throwable t) {

                        try {
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }



                    }
                });
    }


    @Override
    public void back() {
        finish();
    }


}
