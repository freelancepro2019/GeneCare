package com.genecare.activities_fragments.activity_home.fragment.fragment_settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


import com.genecare.R;
import com.genecare.activities_fragments.activity_home.HomeActivity;
import com.genecare.models.AppDataModel;
import com.genecare.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;


public class Fragment_About extends Fragment {
    private Preferences preferences;
    private ImageView back;
    private TextView tv_content;
    private HomeActivity activity;
    private String cuurent_language;

    public static Fragment_About newInstance() {

Fragment_About about=new Fragment_About();
    return about;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        intitview(view);
        // Inflate the layout for this fragment
        return view;
    }

    private void intitview(View view) {
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        cuurent_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        preferences=Preferences.getInstance();
        tv_content =view.findViewById(R.id.tv_content);
        back = (ImageView) view.findViewById(R.id.arrow_back);
        preferences = Preferences.getInstance();

        if (cuurent_language.equals("en")) {

            back.setRotation(180);
        }
        getAppData(cuurent_language);



    }
    private void getAppData(String cuurent_language) {

  /*      Api.getService(Tags.base_url)
                .getabout()
                .enqueue(new Callback<AppDataModel>() {
                    @Override
                    public void onResponse(Call<AppDataModel> call, Response<AppDataModel> response) {
                      //  smoothprogressbar.setVisibility(View.GONE);

                        if (response.isSuccessful()&&response.body()!=null)
                        {
                            updateTermsContent(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<AppDataModel> call, Throwable t) {
                        try {
                           // smoothprogressbar.setVisibility(View.GONE);
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });*/

    }

    private void updateTermsContent(AppDataModel appDataModel) {

        if(cuurent_language.equals("ar")){
            tv_content.setText(appDataModel.getData().getAbout().getAr_content());}
        else {
            tv_content.setText(appDataModel.getData().getAbout().getEn_content());
        }

    }
}
