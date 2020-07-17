package com.genecare.activities_fragments.activity_home.fragment.fragment_settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.genecare.R;
import com.genecare.activities_fragments.activity_home.HomeActivity;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;
import com.genecare.share.Common;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Contact_Us extends Fragment {
    private ImageView image_back;
    private EditText edt_name, edt_email, edt_msg, edt_subject;
    private Button btn_send;
    private String current_language;
    private HomeActivity activity;
    private Preferences preferences;
    private UserModel userModel;

    public static Fragment_Contact_Us newInstance() {
        return new Fragment_Contact_Us();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        image_back = view.findViewById(R.id.arrow);

        if (current_language.equals("en") ) {
            image_back.setRotation(180.0f);
        }

        image_back = view.findViewById(R.id.arrow);
        edt_name = view.findViewById(R.id.edt_name);
        edt_email = view.findViewById(R.id.edt_email);
        edt_subject = view.findViewById(R.id.edtSubject);
        edt_msg = view.findViewById(R.id.edt_msg);
        btn_send = view.findViewById(R.id.btn_send);

        updateUI();




        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });


    }

    private void updateUI() {

        if (userModel != null) {

           // edt_name.setText(userModel.getName());
            //  edt_email.setText(userModel.getEmail);

        }
    }

    private void CheckData() {
        String m_name = edt_name.getText().toString().trim();
        String m_email = edt_email.getText().toString().trim();
        String m_msg = edt_msg.getText().toString().trim();
        String subject = edt_subject.getText().toString();

        if (!TextUtils.isEmpty(m_name) &&
                !TextUtils.isEmpty(m_email) &&
                Patterns.EMAIL_ADDRESS.matcher(m_email).matches() &&
                !TextUtils.isEmpty(m_msg)&&!TextUtils.isEmpty(subject)
        ) {
            edt_name.setError(null);
            edt_email.setError(null);
            edt_msg.setError(null);
            edt_subject.setError(null);
            Common.CloseKeyBoard(activity, edt_name);
            Send(m_name, m_email, m_msg,subject);
        } else {
            if (TextUtils.isEmpty(m_name)) {
                edt_name.setError(getString(R.string.field_req));
            } else {
                edt_name.setError(null);

            }
if(TextUtils.isEmpty(subject)){
    edt_subject.setError(getResources().getString(R.string.field_req));
}
            if (TextUtils.isEmpty(m_email)) {
                edt_email.setError(getString(R.string.field_req));
            } else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches()) {
                edt_email.setError(getString(R.string.inv_email));

            } else {
                edt_email.setError(null);

            }

            if (TextUtils.isEmpty(m_msg)) {
                edt_msg.setError(getString(R.string.field_req));
            } else {
                edt_msg.setError(null);

            }

        }
    }

    private void Send(String m_name, String m_email, String m_msg, String subject) {


        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();

     /*   Api.getService(Tags.base_url)
                .contact_us(m_name, m_email, m_msg,subject)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            Toast.makeText(activity, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                         //   activity.Back();
                        } else {
                            Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });*/
    }
}
