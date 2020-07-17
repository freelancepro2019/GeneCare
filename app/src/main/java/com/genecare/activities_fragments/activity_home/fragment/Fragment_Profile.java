package com.genecare.activities_fragments.activity_home.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.genecare.R;
import com.genecare.activities_fragments.activity_edit_profile.EditProfileActivity;
import com.genecare.activities_fragments.activity_feedback.FeedbackActivity;
import com.genecare.activities_fragments.activity_home.HomeActivity;
import com.genecare.databinding.FragmentProfileBinding;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;
import com.genecare.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Profile extends Fragment  {
    private FragmentProfileBinding binding;
    private HomeActivity activity;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;

    public static Fragment_Profile newInstance() {
        return new Fragment_Profile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }
    private void initView()
    {
        preferences = Preferences.newInstance();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        userModel = preferences.getUserData(activity);
        binding.setLang(lang);
        binding.setUserModel(userModel);

        Picasso.with(activity).load(Uri.parse(Tags.IMAGE_AVATAR+userModel.getLogo())).placeholder(R.drawable.logo).fit().into(binding.image);

        binding.imageEdit.setOnClickListener(view ->
        {
            Intent intent = new Intent(activity, EditProfileActivity.class);
            startActivityForResult(intent,100);
        });

        binding.llFeedback.setOnClickListener(view ->
        {
            if (userModel.getUser_type().equals("1"))
            {
                Toast.makeText(activity, R.string.ser_av_prov, Toast.LENGTH_SHORT).show();
            }else
                {
                    Intent intent = new Intent(activity, FeedbackActivity.class);
                    startActivity(intent);
                }

        });





    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode== Activity.RESULT_OK&&data!=null)
        {
            userModel = preferences.getUserData(activity);
            binding.setUserModel(userModel);
            activity.updateUserModel();
        }
    }
}
