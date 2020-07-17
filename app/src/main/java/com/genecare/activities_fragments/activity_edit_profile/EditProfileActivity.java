package com.genecare.activities_fragments.activity_edit_profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.genecare.R;
import com.genecare.databinding.ActivityEditProfileBinding;
import com.genecare.language.LanguageHelper;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private int fragment_count = 0;
    private FragmentManager manager;
    private Fragment_Client_EditProfile fragment_client_editProfile;
    private Fragment_Doctor_EditProfile fragment_doctor_editProfile;

    private Preferences preferences;
    private UserModel userModel;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        manager = getSupportFragmentManager();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);

        if (savedInstanceState == null) {
            if (userModel.getUser_type().equals("1"))
            {
                displayFragmentClientEditProfile();
            }else
                {
                    displayFragmentDoctorSignUp();
                }
        }
    }
    public void displayFragmentClientEditProfile()
    {

        fragment_count ++;
        fragment_client_editProfile = Fragment_Client_EditProfile.newInstance();


        if (fragment_client_editProfile.isAdded())
        {
            manager.beginTransaction().show(fragment_client_editProfile).commit();
        }else
        {
            manager.beginTransaction().add(R.id.fragment_edit_container, fragment_client_editProfile, "fragment_client_editProfile").addToBackStack("fragment_client_editProfile").commit();

        }
    }
    public void displayFragmentDoctorSignUp()
    {
        fragment_count ++;
        fragment_doctor_editProfile =  Fragment_Doctor_EditProfile.newInstance();

        if (fragment_doctor_editProfile.isAdded())
        {
            manager.beginTransaction().show(fragment_doctor_editProfile).commit();
        }else
        {
            manager.beginTransaction().add(R.id.fragment_edit_container, fragment_doctor_editProfile, "fragment_doctor_editProfile").addToBackStack("fragment_doctor_editProfile").commit();

        }


    }


    @Override
    public void onBackPressed() {
        back();
    }


    public void back()
    {
        if (fragment_count > 1) {
            fragment_count--;
            super.onBackPressed();
        } else {
            finish();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = manager.getFragments();
        for (Fragment fragment :fragments)
        {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = manager.getFragments();
        for (Fragment fragment :fragments)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}



