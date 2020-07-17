package com.genecare.activities_fragments.activity_sign_up;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.genecare.R;
import com.genecare.activities_fragments.activity_login.LoginActivity;
import com.genecare.databinding.ActivitySignUpBinding;
import com.genecare.language.LanguageHelper;
import com.genecare.preferences.Preferences;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private int fragment_count = 0;
    private FragmentManager manager;
    private Fragment_Client_SignUp fragment_client_signUp;
    private Fragment_Doctor_SignUp fragment_doctor_signUp;
    private Preferences preferences;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        manager = getSupportFragmentManager();
        preferences = Preferences.newInstance();
        if (savedInstanceState == null) {
            displayFragmentClientSignUp();
        }
    }




    public void displayFragmentClientSignUp() {

        fragment_count ++;
        if (fragment_client_signUp ==null)
        {
            fragment_client_signUp = Fragment_Client_SignUp.newInstance();

        }

        if (fragment_client_signUp.isAdded())
        {
            manager.beginTransaction().show(fragment_client_signUp).commit();
        }else
        {
            manager.beginTransaction().add(R.id.fragment_sign_up_container, fragment_client_signUp, "fragment_client_signUp").addToBackStack("fragment_client_signUp").commit();

        }
    }

    public void displayFragmentDoctorSignUp() {
        fragment_count ++;
        if (fragment_doctor_signUp ==null)
        {
            fragment_doctor_signUp = Fragment_Doctor_SignUp.newInstance();

        }

        if (fragment_doctor_signUp.isAdded())
        {
            manager.beginTransaction().show(fragment_doctor_signUp).commit();
        }else
            {
                manager.beginTransaction().add(R.id.fragment_sign_up_container, fragment_doctor_signUp, "fragment_doctor_signUp").addToBackStack("fragment_doctor_signUp").commit();

            }


    }





    @Override
    public void onBackPressed() {
        back();
    }


    public void back() {
        if (fragment_count > 1) {
            fragment_count--;
            super.onBackPressed();
        } else {
            FinishActivity();
        }
    }


    public void FinishActivity()
    {
        Log.e("ddd","fff");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = manager.getFragments();
        for (Fragment fragment :fragments)
        {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = manager.getFragments();
        for (Fragment fragment :fragments)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}


