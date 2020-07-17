package com.genecare.activities_fragments.activity_feedback;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genecare.R;
import com.genecare.adapters.CommentAdapter;
import com.genecare.databinding.ActivityFeedbackBinding;
import com.genecare.interfaces.Listeners;
import com.genecare.language.LanguageHelper;
import com.genecare.models.CommentDataModel;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;
import com.genecare.remote.Api;
import com.genecare.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity implements Listeners.BackListener  {
    private ActivityFeedbackBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private List<CommentDataModel.CommentModel> commentModelList;
    private CommentAdapter  adapter;
    private LinearLayoutManager manager;
    private int current_page = 1;
    private boolean isLoading = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);
        initView();

    }


    private void initView() {
        commentModelList = new ArrayList<>();

        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        binding.setUserModel(userModel);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        manager = new LinearLayoutManager(this);
        binding.recView.setLayoutManager(manager);
        adapter = new CommentAdapter(commentModelList,this);
        binding.recView.setAdapter(adapter);

        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int total_items = adapter.getItemCount();
                int last_item_pos = manager.findLastCompletelyVisibleItemPosition();

                if (dy>0)
                {
                    if ((total_items-last_item_pos)==5&&!isLoading)
                    {
                        commentModelList.add(null);
                        adapter.notifyItemInserted(commentModelList.size()-1);
                        int page = current_page+1;
                        loadMore(page);

                    }
                }

            }
        });

        SimpleRatingBar.AnimationBuilder builder = binding.ratingBar.getAnimationBuilder()
                .setRatingTarget((float)userModel.getUser_rating())
                .setDuration(2000)
                .setRepeatCount(0)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        builder.start();

        getComments();

    }


    private void getComments()
    {
        try {

            Api.getService(Tags.base_url)
                    .getComments(lang,userModel.getToken(),1)
                    .enqueue(new Callback<CommentDataModel>() {
                        @Override
                        public void onResponse(Call<CommentDataModel> call, Response<CommentDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
                            {
                                commentModelList.clear();
                                if (response.body().getData().size()>0)
                                {
                                    commentModelList.addAll(response.body().getData());
                                    adapter.notifyDataSetChanged();
                                    binding.tvNoComments.setVisibility(View.GONE);
                                }else
                                {
                                    binding.tvNoComments.setVisibility(View.VISIBLE);

                                }

                            }else
                            {
                                if (response.code() == 500) {
                                    Toast.makeText(FeedbackActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                }else if (response.code()==401)
                                {
                                    Toast.makeText(FeedbackActivity.this,"User Unauthenticated", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(FeedbackActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CommentDataModel> call, Throwable t) {
                            try {
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(FeedbackActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(FeedbackActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });

        }catch (Exception e){
            Log.e("ddd",e.getMessage()+"_");
        }


    }
    private void loadMore(int page)
    {

        try {

            Api.getService(Tags.base_url)
                    .getComments(lang,userModel.getToken(),page)
                    .enqueue(new Callback<CommentDataModel>() {
                        @Override
                        public void onResponse(Call<CommentDataModel> call, Response<CommentDataModel> response) {

                            commentModelList.remove(commentModelList.size()-1);
                            adapter.notifyItemRemoved(commentModelList.size()-1);

                            if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
                            {
                                if (response.body().getData().size()>0)
                                {
                                    commentModelList.addAll(response.body().getData());
                                    adapter.notifyDataSetChanged();
                                    current_page = response.body().getMeta().getCurrent_page();
                                    isLoading  =false;
                                }

                            }else
                            {
                                commentModelList.remove(commentModelList.size()-1);
                                adapter.notifyItemRemoved(commentModelList.size()-1);


                                isLoading  =false;

                                if (response.code() == 500) {
                                    Toast.makeText(FeedbackActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                }else if (response.code()==401)
                                {
                                    Toast.makeText(FeedbackActivity.this,"User Unauthenticated", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(FeedbackActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CommentDataModel> call, Throwable t) {
                            try {

                                commentModelList.remove(commentModelList.size()-1);
                                adapter.notifyItemRemoved(commentModelList.size()-1);

                                isLoading  =false;

                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(FeedbackActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(FeedbackActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){

        }
    }

    @Override
    public void back() {
        finish();
    }

}
