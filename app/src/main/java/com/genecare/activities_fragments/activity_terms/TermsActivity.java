package com.genecare.activities_fragments.activity_terms;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.genecare.R;
import com.genecare.databinding.ActivityTermsBinding;
import com.genecare.interfaces.Listeners;
import com.genecare.language.LanguageHelper;
import com.genecare.models.TermsDataModel;
import com.genecare.remote.Api;
import com.genecare.tags.Tags;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TermsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityTermsBinding binding;
    private String lang;
    private int type;





    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("type"))
        {
            type = intent.getIntExtra("type",0);

        }
    }


    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        if (type==1||type==2||type==3)
        {
            binding.setTitle(getString(R.string.terms_and_conditions));
        }else if (type ==4)
        {
            binding.setTitle(getString(R.string.about_app));

        }


        getAppData();

    }

    private void getAppData() {
        Api.getService(Tags.base_url).
                getAppData(lang).
                enqueue(new Callback<TermsDataModel>() {
                    @Override
                    public void onResponse(Call<TermsDataModel> call, Response<TermsDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {

                            if (lang.equals("ar"))
                            {
                                if (type==1)
                                {
                                    binding.setContent(response.body().getSettings().getAr_client_condition());

                                }else if (type ==2)
                                {
                                    binding.setContent(response.body().getSettings().getAr_provider_condition());

                                }else if (type ==3)
                                {
                                    binding.setContent(response.body().getSettings().getAr_termis_condition());

                                }else if (type ==4)
                                {
                                    binding.setContent(response.body().getSettings().getAr_about());

                                }
                            }else
                                {
                                    if (type==1)
                                    {
                                        binding.setContent(response.body().getSettings().getEn_client_condition());

                                    }else if (type ==2)
                                    {
                                        binding.setContent(response.body().getSettings().getEn_provider_condition());

                                    }else if (type ==3)
                                    {
                                        binding.setContent(response.body().getSettings().getEn_termis_condition());

                                    }else if (type ==4)
                                    {
                                        binding.setContent(response.body().getSettings().getEn_about());

                                    }
                                }

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(TermsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(TermsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TermsDataModel> call, Throwable t) {

                        try {
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(TermsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TermsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
