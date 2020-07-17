package com.genecare.activities_fragments.activity_home.fragment.fragment_settings;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.genecare.BuildConfig;
import com.genecare.R;
import com.genecare.activities_fragments.activity_contact_us.ContactUsActivity;
import com.genecare.activities_fragments.activity_home.HomeActivity;
import com.genecare.activities_fragments.activity_terms.TermsActivity;
import com.genecare.databinding.DialogLanguageBinding;
import com.genecare.databinding.FragmentSettingsBinding;
import com.genecare.interfaces.Listeners;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Settings extends Fragment implements Listeners.MoreActions {
    private FragmentSettingsBinding binding;
    private HomeActivity activity;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private String [] language_array;


    public static Fragment_Settings newInstance() {
        return new Fragment_Settings();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(activity);

        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        language_array = new String[]{"English","العربية"};
        binding.setAction(this);
        binding.tvVersion.setText(String.format("%s %s","Version : ",BuildConfig.VERSION_NAME));


    }

    private void CreateLangDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .create();

        DialogLanguageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_language, null, false);
        String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        if (lang.equals("ar")) {
            binding.rbAr.setChecked(true);
        } else {
            binding.rbEn.setChecked(true);

        }
        binding.btnCancel.setOnClickListener((v) ->
                dialog.dismiss()

        );
        binding.rbAr.setOnClickListener(view -> {
            dialog.dismiss();
           new Handler()
                   .postDelayed(() -> {
                       if (!lang.equals("ar"))
                       {
                           activity.RefreshActivity("ar");
                       }
                   },200);
        });
        binding.rbEn.setOnClickListener(view -> {
            dialog.dismiss();
            new Handler()
                    .postDelayed(() -> {
                        if (!lang.equals("en"))
                        {
                            activity.RefreshActivity("en");
                        }
                    },200);
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }


    @Override
    public void aboutApp() {
        Intent intent = new Intent(activity, TermsActivity.class);
        intent.putExtra("type",4);
        startActivity(intent);
    }

    @Override
    public void changeLanguage() {
        CreateLangDialog();
    }

    @Override
    public void contactUs() {
        Intent intent = new Intent(activity, ContactUsActivity.class);
        startActivity(intent);
    }

    @Override
    public void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));

        }
    }

    @Override
    public void terms() {
        Intent intent = new Intent(activity, TermsActivity.class);
        intent.putExtra("type",3);
        startActivity(intent);
    }

    @Override
    public void share() {

        String app_url = "https://play.google.com/store/apps/details?id=" + activity.getPackageName();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "Home Care App ");
        intent.putExtra(Intent.EXTRA_TEXT, app_url);
        startActivity(intent);
    }

    @Override
    public void logout() {
        if (userModel!=null)
        {
            activity.logout();

        }else
        {
            activity.navigateToSignInActivity();
        }
    }
}
