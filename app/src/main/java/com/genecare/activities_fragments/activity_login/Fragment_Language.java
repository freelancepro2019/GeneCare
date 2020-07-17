package com.genecare.activities_fragments.activity_login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.genecare.R;
import com.genecare.databinding.FragmentLanguageBinding;

import io.paperdb.Paper;

public class Fragment_Language extends Fragment {
    private String selected_language = "ar";
    private LoginActivity activity;
    private FragmentLanguageBinding binding;


    public static Fragment_Language newInstance()
    {
        return new Fragment_Language();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_language,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        activity = (LoginActivity) getActivity();
        Paper.init(activity);

        binding.rbAr.setOnClickListener(v -> selected_language = "ar"
        );

        binding.rbEn.setOnClickListener(v -> selected_language = "en"
        );
        binding.fab.setOnClickListener(v -> activity.refreshActivity(selected_language)

        );
    }
}
