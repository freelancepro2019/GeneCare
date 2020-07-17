package com.genecare.activities_fragments.activity_sub_service_details;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.genecare.R;
import com.genecare.activities_fragments.activity_make_order.MakeOrderActivity;
import com.genecare.databinding.ActivitySubServiceDetailsBinding;
import com.genecare.interfaces.Listeners;
import com.genecare.language.LanguageHelper;
import com.genecare.models.SubServicesModel;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;
import com.genecare.share.Common;
import com.genecare.tags.Tags;

import java.util.Locale;

import io.paperdb.Paper;

public class SubServiceDetailsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivitySubServiceDetailsBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private int color;
    private String main_service_id;
    private SubServicesModel.SubServiceModel subServiceModel = null;





    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_service_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            color = intent.getIntExtra("color",R.color.colorPrimary);
            main_service_id = intent.getStringExtra("main_service_id");
            subServiceModel = (SubServicesModel.SubServiceModel) intent.getSerializableExtra("data");
        }
    }


    private void initView()
    {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.toolBar.setBackgroundResource(color);
        binding.setSubModel(subServiceModel);
        binding.btnSend.setBackgroundResource(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeStatusBarColor(color);
        }

        if (userModel!=null&&userModel.getUser_type().equals(Tags.USER_PROVIDER))
        {
            binding.btnSend.setVisibility(View.GONE);
        }else
            {
                binding.btnSend.setVisibility(View.VISIBLE);

            }

        binding.btnSend.setOnClickListener(view ->
        {
            if (userModel==null)
            {
                Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up),color);
            }else
                {
                    Intent intent = new Intent(this, MakeOrderActivity.class);
                    intent.putExtra("main_service_id",main_service_id);
                    intent.putExtra("color",color);
                    intent.putExtra("data",subServiceModel);
                    startActivity(intent);
                }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeStatusBarColor(int color)
    {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,color));
    }


    @Override
    public void back() {
        finish();
    }

}
