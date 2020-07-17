package com.genecare.activities_fragments.activity_notification;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genecare.R;
import com.genecare.activities_fragments.activity_order_details.OrderDetailsActivity;
import com.genecare.adapters.NotificationAdapter;
import com.genecare.databinding.ActivityNotificationBinding;
import com.genecare.databinding.DialogRateBinding;
import com.genecare.interfaces.Listeners;
import com.genecare.language.LanguageHelper;
import com.genecare.models.NotFireModel;
import com.genecare.models.NotificationDataModel;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;
import com.genecare.remote.Api;
import com.genecare.share.Common;
import com.genecare.tags.Tags;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity implements Listeners.BackListener {

    private ActivityNotificationBinding binding;
    private String lang;
    private LinearLayoutManager manager;
    private List<NotificationDataModel.NotificationModel> notificationModelList;
    private NotificationAdapter adapter;
    private Preferences preferences;
    private UserModel userModel;
    private boolean isLoading = false;
    private int current_page = 1;
    private int selectedPos = -1;
    private float rate = 0.0f;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        initView();
    }

    private void initView() {
        EventBus.getDefault().register(this);
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        notificationModelList = new ArrayList<>();
        manager = new LinearLayoutManager(this);
        binding.recView.setLayoutManager(manager);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        adapter = new NotificationAdapter(this, notificationModelList);
        binding.recView.setAdapter(adapter);
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int total = binding.recView.getAdapter().getItemCount();

                    int lastVisibleItem = ((LinearLayoutManager) binding.recView.getLayoutManager()).findLastCompletelyVisibleItemPosition();


                    if (total > 6 && (total - lastVisibleItem) == 2 && !isLoading) {
                        isLoading = true;
                        int page = current_page + 1;
                        notificationModelList.add(null);
                        adapter.notifyDataSetChanged();
                        loadMore(page);
                    }
                }
            }
        });
        getNotification();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenForNotification(NotFireModel notFireModel)
    {
        getNotification();
    }

    public void getNotification() {

        try {

            Api.getService(Tags.base_url)
                    .getNotifications(lang, userModel.getToken(), 1, 20)
                    .enqueue(new Callback<NotificationDataModel>() {
                        @Override
                        public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                notificationModelList.clear();
                                notificationModelList.addAll(response.body().getData());
                                if (notificationModelList.size() > 0) {
                                    adapter.notifyDataSetChanged();
                                    binding.llNoNotification.setVisibility(View.GONE);
                                } else {
                                    binding.llNoNotification.setVisibility(View.VISIBLE);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(NotificationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(NotificationActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(NotificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void loadMore(int page) {
        try {


            Api.getService(Tags.base_url)
                    .getNotifications(lang, userModel.getToken(), page, 20)
                    .enqueue(new Callback<NotificationDataModel>() {
                        @Override
                        public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                            isLoading = false;
                            notificationModelList.remove(notificationModelList.size() - 1);
                            adapter.notifyItemRemoved(notificationModelList.size() - 1);

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                notificationModelList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();
                                if (response.body().getData().size() > 0) {
                                    current_page = response.body().getMeta().getCurrent_page();

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(NotificationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                            try {
                                if (notificationModelList.get(notificationModelList.size() - 1) == null) {
                                    isLoading = false;
                                    notificationModelList.remove(notificationModelList.size() - 1);
                                    adapter.notifyItemRemoved(notificationModelList.size() - 1);

                                }
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(NotificationActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(NotificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    public void setItemData(NotificationDataModel.NotificationModel notificationModel, int adapterPosition) {

        if (notificationModel.getAction_type().equals("1")) {
            this.selectedPos = adapterPosition;
            Intent intent = new Intent(this, OrderDetailsActivity.class);
            intent.putExtra("order_id", notificationModel.getProcess_id_fk());
            intent.putExtra("from", "notification");
            intent.putExtra("notification_id", notificationModel.getNotification_id());
            intent.putExtra("from_user_id", notificationModel.getFrom_user_id());

            startActivityForResult(intent, 100);
        } else if (notificationModel.getAction_type().equals("2")) {
            CreateRateDialogAlert(notificationModel, adapterPosition);

        }

    }


    private void CreateRateDialogAlert(NotificationDataModel.NotificationModel notificationModel, int pos) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogRateBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_rate, null, false);
        Picasso.with(this).load(Uri.parse(Tags.IMAGE_AVATAR + notificationModel.getFrom_user().getLogo())).fit().into(binding.image);
        binding.tvName.setText(notificationModel.getFrom_user().getName());
        binding.ratingBar.setOnRatingBarChangeListener((simpleRatingBar, rating, fromUser) -> {
            rate = rating;
            binding.tvRate.setText("(" + rating + ")");
        });
        binding.btnCancel.setOnClickListener(v -> dialog.dismiss()

        );
        binding.btnAdd.setOnClickListener(view -> {
            dialog.dismiss();
            String comment = binding.edtComment.getText().toString().trim();
            addRate(notificationModel, comment,pos);

        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    private void addRate(NotificationDataModel.NotificationModel notificationModel, String comment, int pos) {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .clientEndOrder(lang,userModel.getToken(),notificationModel.getFrom_user_id(),notificationModel.getProcess_id_fk(),notificationModel.getNotification_id(),rate,comment)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null )
                        {
                            Toast.makeText(NotificationActivity.this, R.string.suc, Toast.LENGTH_SHORT).show();
                            notificationModelList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                        } else {
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
                                    Toast.makeText(NotificationActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(NotificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            if (selectedPos != -1) {
                notificationModelList.remove(this.selectedPos);
                adapter.notifyItemRemoved(this.selectedPos);
                this.selectedPos = -1;

                if (notificationModelList.size()>0)
                {
                    binding.llNoNotification.setVisibility(View.GONE);
                }else
                    {
                        binding.llNoNotification.setVisibility(View.VISIBLE);

                    }
            }

        }
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
    }
}
