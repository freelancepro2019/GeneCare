package com.genecare.activities_fragments.activity_edit_profile;

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
import com.genecare.databinding.FragmentClientEditProfileBinding;
import com.genecare.interfaces.Listeners;
import com.genecare.models.EditProfileClientModel;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;
import com.genecare.remote.Api;
import com.genecare.share.Common;
import com.genecare.tags.Tags;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Client_EditProfile extends Fragment implements Listeners.ShowCountryDialogListener, OnCountryPickerListener , Listeners.UpdateProfileListener {
    private FragmentClientEditProfileBinding binding;
    private EditProfileActivity activity;
    private String lang;
    private CountryPicker countryPicker;
    private EditProfileClientModel  editProfileClientModel;
    private Preferences preferences;
    private UserModel userModel;
    private Uri uri = null;
    private final String camera_perm = Manifest.permission.CAMERA;
    private final String write_perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int camera_req = 1;


    public static Fragment_Client_EditProfile newInstance() {
        return new Fragment_Client_EditProfile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_edit_profile, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        editProfileClientModel = new EditProfileClientModel();
        activity = (EditProfileActivity) getActivity();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setShowCountryListener(this);
        binding.setUpdateListener(this);
        binding.setEditModel(editProfileClientModel);
        createCountryDialog();
        updateUI();

        binding.rbMale.setOnClickListener(view ->

            editProfileClientModel.setGender(1)
        );

        binding.rbFemale.setOnClickListener(view ->

                editProfileClientModel.setGender(2)
        );


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

    }

    private void updateUI()
    {
        binding.edtName.setText(userModel.getName());
        binding.edtEmail.setText(userModel.getEmail());
        binding.tvCode.setText(userModel.getPhone_code().replaceFirst("00","+"));
        binding.edtPhone.setText(userModel.getPhone());

        editProfileClientModel.setName(userModel.getName());
        editProfileClientModel.setEmail(userModel.getEmail());
        editProfileClientModel.setPhone_code(userModel.getPhone_code());
        editProfileClientModel.setPhone(userModel.getPhone());

        if (userModel.getLogo()!=null&&!userModel.getLogo().isEmpty()&&!userModel.getLogo().equals("0"))
        {
            binding.iconUpload.setVisibility(View.GONE);
            Picasso.with(activity).load(Uri.parse(Tags.IMAGE_AVATAR+userModel.getLogo())).fit().into(binding.image);

        }

        if (userModel.getGender().equals("1"))
        {
            binding.rbMale.setChecked(true);
            editProfileClientModel.setGender(1);
        }else if (userModel.getGender().equals("2"))
            {
                binding.rbFemale.setChecked(true);
                editProfileClientModel.setGender(2);

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
            binding.image.setImageBitmap(bitmap);

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
                editProfileClientModel.setPhone_code(code.replace("+", "00"));

            }
        } catch (Exception e) {
            String code = "+20";
            binding.tvCode.setText(code);
            editProfileClientModel.setPhone_code(code.replace("+", "00"));
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
        editProfileClientModel.setPhone_code(country.getDialCode().replace("+", "00"));

    }

    @Override
    public void updateProfile() {

        if (editProfileClientModel.isDataValid(activity))
        {
            if(uri!=null)
            {
                updateWithImage();
            }else
                {
                    updateWithoutImage();
                }
        }
    }


    private void updateWithoutImage()
    {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody name_part = Common.getRequestBodyText(editProfileClientModel.getName());
        RequestBody phone_part = Common.getRequestBodyText(editProfileClientModel.getPhone());
        RequestBody phone_code_part = Common.getRequestBodyText(editProfileClientModel.getPhone_code());
        RequestBody email_part = Common.getRequestBodyText(editProfileClientModel.getEmail());
        RequestBody gender_part = Common.getRequestBodyText(String.valueOf(editProfileClientModel.getGender()));


        try {
            Api.getService(Tags.base_url)
                    .editClientProfileWithoutImage(lang,userModel.getToken(),name_part,phone_part,phone_code_part,email_part,gender_part)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                preferences.create_update_userData(activity,response.body());
                                Intent intent = activity.getIntent();
                                if (intent!=null)
                                {
                                    activity.setResult(Activity.RESULT_OK,intent);
                                }
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
    private void updateWithImage()
    {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody name_part = Common.getRequestBodyText(editProfileClientModel.getName());
        RequestBody phone_part = Common.getRequestBodyText(editProfileClientModel.getPhone());
        RequestBody phone_code_part = Common.getRequestBodyText(editProfileClientModel.getPhone_code());
        RequestBody email_part = Common.getRequestBodyText(editProfileClientModel.getEmail());
        RequestBody gender_part = Common.getRequestBodyText(String.valueOf(editProfileClientModel.getGender()));
        MultipartBody.Part image_part = Common.getMultiPart(activity,uri,"logo");

        try {
            Api.getService(Tags.base_url)
                    .editClientProfileWithImage(lang,userModel.getToken(),name_part,phone_part,phone_code_part,email_part,gender_part,image_part)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                preferences.create_update_userData(activity,response.body());
                                Intent intent = activity.getIntent();
                                if (intent!=null)
                                {
                                    activity.setResult(Activity.RESULT_OK,intent);
                                }
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
}
