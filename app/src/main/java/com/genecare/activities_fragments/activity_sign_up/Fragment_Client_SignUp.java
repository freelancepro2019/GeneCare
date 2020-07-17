package com.genecare.activities_fragments.activity_sign_up;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.genecare.R;
import com.genecare.activities_fragments.activity_home.HomeActivity;
import com.genecare.activities_fragments.activity_terms.TermsActivity;
import com.genecare.databinding.FragmentClientSignUpBinding;
import com.genecare.interfaces.Listeners;
import com.genecare.models.SignUpClientModel;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;
import com.genecare.remote.Api;
import com.genecare.share.Common;
import com.genecare.tags.Tags;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Client_SignUp extends Fragment implements Listeners.ShowCountryDialogListener, OnCountryPickerListener, Listeners.BackListener, Listeners.SignUpListener {
    private FragmentClientSignUpBinding binding;
    private SignUpActivity activity;
    private String lang;
    private CountryPicker countryPicker;
    private SignUpClientModel signUpClientModel;
    private Preferences preferences;
    private Uri uri = null;
    private final String camera_perm = Manifest.permission.CAMERA;
    private final String write_perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int camera_req = 1;


    public static Fragment_Client_SignUp newInstance() {
        return new Fragment_Client_SignUp();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_sign_up, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        signUpClientModel = new SignUpClientModel();
        preferences = Preferences.newInstance();
        activity = (SignUpActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setShowCountryListener(this);
        binding.setSignUpListener(this);
        binding.setSignUpModel(signUpClientModel);
        createCountryDialog();
        binding.imageSwitchUser.setOnClickListener((v) -> activity.displayFragmentDoctorSignUp());

        binding.checkbox.setOnClickListener(view -> {
            if (binding.checkbox.isChecked())
            {
                signUpClientModel.setAccept(true);
                Intent intent = new Intent(activity, TermsActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
            }else
            {
                signUpClientModel.setAccept(false);

            }
        });

        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().startsWith("0")) {
                    binding.edtPhone.setText("");
                }
            }
        });

        binding.flImage.setOnClickListener(view -> checkCameraPermission());

        binding.rbMale.setOnClickListener(view -> signUpClientModel.setGender(1));
        binding.rbFemale.setOnClickListener(view -> signUpClientModel.setGender(2));

    }


    @Override
    public void checkDataSignUp() {

        if (signUpClientModel.isDataValid(activity)) {
            Common.CloseKeyBoard(activity,binding.edtName);
            if (uri==null)
            {
                signUpWithoutImage();
            }else
            {
                signUpWithImage();
            }

        }
    }


    private void signUpWithoutImage()
    {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody name_part = Common.getRequestBodyText(signUpClientModel.getName());
        RequestBody phone_part = Common.getRequestBodyText(signUpClientModel.getPhone());
        RequestBody phone_code_part = Common.getRequestBodyText(signUpClientModel.getPhone_code());
        RequestBody password_part = Common.getRequestBodyText(signUpClientModel.getPassword());
        RequestBody email_part = Common.getRequestBodyText(signUpClientModel.getEmail());
        RequestBody gender_part = Common.getRequestBodyText(String.valueOf(signUpClientModel.getGender()));
        RequestBody soft_type = Common.getRequestBodyText("1");


        try {
            Api.getService(Tags.base_url)
                    .signUpClientWithoutImage(lang,name_part,phone_part,phone_code_part,password_part,email_part,gender_part,soft_type)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                preferences.create_update_userData(activity, response.body());
                                preferences.createSession(activity, Tags.session_login);
                                Intent intent = new Intent(activity, HomeActivity.class);
                                startActivity(intent);
                                activity.finish();

                            } else {

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
            dialog.dismiss();

        }
    }
    private void signUpWithImage()
    {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody name_part = Common.getRequestBodyText(signUpClientModel.getName());
        RequestBody phone_part = Common.getRequestBodyText(signUpClientModel.getPhone());
        RequestBody phone_code_part = Common.getRequestBodyText(signUpClientModel.getPhone_code());
        RequestBody password_part = Common.getRequestBodyText(signUpClientModel.getPassword());
        RequestBody email_part = Common.getRequestBodyText(signUpClientModel.getEmail());
        RequestBody gender_part = Common.getRequestBodyText(String.valueOf(signUpClientModel.getGender()));
        RequestBody soft_type = Common.getRequestBodyText("1");
        MultipartBody.Part image = Common.getMultiPart(activity,uri,"logo");

        try {
            Api.getService(Tags.base_url)
                    .signUpClientWithImage(lang,name_part,phone_part,phone_code_part,password_part,email_part,gender_part,soft_type,image)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                preferences.create_update_userData(activity, response.body());
                                preferences.createSession(activity, Tags.session_login);
                                Intent intent = new Intent(activity, HomeActivity.class);
                                startActivity(intent);
                                activity.finish();

                            } else {

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
            dialog.dismiss();

        }

    }
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(activity, camera_perm) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, write_perm) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{camera_perm, write_perm}, camera_req);
        }
    }

    private void selectImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, camera_req);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == camera_req && grantResults.length > 0) {
            boolean isGranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    isGranted = true;
                } else {
                    isGranted = false;
                }
            }
            if (isGranted) {
                selectImage();

            }
            else{
                Toast.makeText(activity, "access images denied", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == camera_req && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            binding.iconUpload.setVisibility(View.GONE);
            binding.image.setImageBitmap(bitmap);
            uri = getUriFromBitmap(bitmap);
        }
    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "", ""));
    }

    private void createCountryDialog() {
        countryPicker = new CountryPicker.Builder()
                .canSearch(true)
                .listener(this)
                .theme(CountryPicker.THEME_NEW)
                .with(activity)
                .build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (countryPicker.getCountryFromSIM() != null) {
                updatePhoneCode(countryPicker.getCountryFromSIM());
            } else if (telephonyManager != null && countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()) != null) {
                updatePhoneCode(countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()));
            } else if (countryPicker.getCountryByLocale(Locale.getDefault()) != null) {
                updatePhoneCode(countryPicker.getCountryByLocale(Locale.getDefault()));
            } else {
                String code = "+966";
                binding.tvCode.setText(code);
                signUpClientModel.setPhone_code(code.replace("+", "00"));

            }
        } catch (Exception e) {
            String code = "+20";
            binding.tvCode.setText(code);
            signUpClientModel.setPhone_code(code.replace("+", "00"));
        }


    }

    @Override
    public void showDialog() {
        countryPicker.showDialog(activity);
    }

    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);
    }

    private void updatePhoneCode(Country country) {
        binding.tvCode.setText(country.getDialCode());
        signUpClientModel.setPhone_code(country.getDialCode().replace("+", "00"));

    }


    @Override
    public void back() {
        activity.FinishActivity();
    }


}
