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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.genecare.R;
import com.genecare.adapters.Spinner_Department_Adapter;
import com.genecare.adapters.Spinner_Experiment_Adapter;
import com.genecare.databinding.FragmentDoctorEditProfileBinding;
import com.genecare.interfaces.Listeners;
import com.genecare.models.EditProfileDoctorModel;
import com.genecare.models.ServicesDataModel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Doctor_EditProfile extends Fragment implements Listeners.ShowCountryDialogListener, OnCountryPickerListener, Listeners.UpdateProfileListener {
    private FragmentDoctorEditProfileBinding binding;
    private EditProfileActivity activity;
    private String lang;
    private CountryPicker countryPicker;
    private EditProfileDoctorModel editProfileDoctorModel;
    private Preferences preferences;
    private UserModel userModel;
    private Uri uri = null;
    private List<String> experimentList;
    private Spinner_Experiment_Adapter spinner_experiment_adapter;
    private final String camera_perm = Manifest.permission.CAMERA;
    private final String write_perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int camera_req = 1;
    private Spinner_Department_Adapter adapter;
    private List<ServicesDataModel.ServiceModel> serviceModelList;

    public static Fragment_Doctor_EditProfile newInstance() {
        return new Fragment_Doctor_EditProfile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_doctor_edit_profile, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        serviceModelList  = new ArrayList<>();
        ServicesDataModel.ServiceModel model = new ServicesDataModel.ServiceModel();
        ServicesDataModel.ServiceModel.WordsModel wordsModel = new ServicesDataModel.ServiceModel.WordsModel();
        wordsModel.setTitle(getString(R.string.ch));
        model.setWords(wordsModel);
        serviceModelList.add(model);
        binding.setUpdateListener(this);

        experimentList = new ArrayList<>();
        editProfileDoctorModel = new EditProfileDoctorModel();
        preferences = Preferences.newInstance();
        activity = (EditProfileActivity) getActivity();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

        binding.setLang(lang);
        binding.setShowCountryListener(this);
        binding.setEditModel(editProfileDoctorModel);


        createCountryDialog();

        addExperiment();

        spinner_experiment_adapter = new Spinner_Experiment_Adapter(experimentList,activity);
        binding.spinnerExperience.setAdapter(spinner_experiment_adapter);

        adapter = new Spinner_Department_Adapter(serviceModelList,activity);
        binding.spinnerDepartment.setAdapter(adapter);

        binding.spinnerExperience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==0)
                {
                    editProfileDoctorModel.setExperience_id("");
                }else
                    {
                        editProfileDoctorModel.setExperience_id(experimentList.get(i));

                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==0)
                {
                    editProfileDoctorModel.setDepartment_id("");
                }else
                {
                    editProfileDoctorModel.setDepartment_id(serviceModelList.get(i).getService_id());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.flImage.setOnClickListener((v)->checkCameraPermission());
        binding.rbMale.setOnClickListener(view -> editProfileDoctorModel.setGender(1));
        binding.rbFemale.setOnClickListener(view -> editProfileDoctorModel.setGender(2));


        getData();
        updateUI();
    }

    private void updateUI()
    {

        binding.edtName.setText(userModel.getName());
        binding.edtEmail.setText(userModel.getEmail());
        binding.tvCode.setText(userModel.getPhone_code().replaceFirst("00","+"));
        binding.edtPhone.setText(userModel.getPhone());

        editProfileDoctorModel.setName(userModel.getName());
        editProfileDoctorModel.setEmail(userModel.getEmail());
        editProfileDoctorModel.setPhone_code(userModel.getPhone_code());
        editProfileDoctorModel.setPhone(userModel.getPhone());

        if (userModel.getLogo()!=null&&!userModel.getLogo().isEmpty()&&!userModel.getLogo().equals("0"))
        {
            binding.iconUpload.setVisibility(View.GONE);
            Picasso.with(activity).load(Uri.parse(Tags.IMAGE_AVATAR+userModel.getLogo())).fit().into(binding.image);

        }

        if (userModel.getGender().equals("1"))
        {
            binding.rbMale.setChecked(true);
            editProfileDoctorModel.setGender(1);

        }else if (userModel.getGender().equals("2"))
        {
            binding.rbFemale.setChecked(true);
            editProfileDoctorModel.setGender(2);


        }
        binding.edtAbout.setText(userModel.getAbout());
        editProfileDoctorModel.setAbout_me(userModel.getAbout());
        if (getExpPos()!=-1)
        {
            binding.spinnerExperience.setSelection(getExpPos());

        }
        editProfileDoctorModel.setExperience_id(experimentList.get(getExpPos()));

    }

    private int getExpPos()
    {
        int pos = 1;

        for (int i =0;i<experimentList.size();i++)
        {
            if (experimentList.get(i).equals(userModel.getExper()))
            {
                pos = i;
                break;
            }
        }

        return pos;
    }

    private int getServicePos()
    {
        int pos = -1;

        for (int i =0;i<serviceModelList.size();i++)
        {


            if (serviceModelList.get(i).getService_id()!=null&&serviceModelList.get(i).getService_id().equals(userModel.getService().getId()))
            {
                pos = i;
                break;
            }
        }

        return pos;
    }

    private void addExperiment()
    {
        experimentList.add(getString(R.string.ch));
        for (int i =1;i<16;i++)
        {
            experimentList.add(String.valueOf(i));
        }
    }

    private void getData()
    {

        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        try {
            Api.getService(Tags.base_url).get_services(lang).enqueue(new Callback<ServicesDataModel>() {
                @Override
                public void onResponse(Call<ServicesDataModel> call, Response<ServicesDataModel> response) {
                    dialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getServices().size() > 0) {
                            serviceModelList.addAll(response.body().getServices());
                            adapter.notifyDataSetChanged();
                            int pos = getServicePos();
                            if (pos!=-1)
                            {
                                binding.spinnerDepartment.setSelection(pos);

                            }

                            if (pos!=-1)
                            {
                                editProfileDoctorModel.setDepartment_id(serviceModelList.get(pos).getService_id());

                            }

                        }
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
                public void onFailure(Call<ServicesDataModel> call, Throwable t) {

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

        }catch (Exception e)
        {
           dialog.dismiss();
        }
    }

    private void createCountryDialog()
    {
        countryPicker = new CountryPicker.Builder()
                .canSearch(true)
                .listener(this)
                .theme(CountryPicker.THEME_NEW)
                .with(activity)
                .build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (countryPicker.getCountryFromSIM()!=null)
            {
                updatePhoneCode(countryPicker.getCountryFromSIM());
            }else if (telephonyManager!=null&&countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso())!=null)
            {
                updatePhoneCode(countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()));
            }else if (countryPicker.getCountryByLocale(Locale.getDefault())!=null)
            {
                updatePhoneCode(countryPicker.getCountryByLocale(Locale.getDefault()));
            }else
            {
                String code = "+20";
                binding.tvCode.setText(code);
                editProfileDoctorModel.setPhone_code(code.replace("+","00"));

            }
        }catch (Exception e)
        {
            String code = "+20";
            binding.tvCode.setText(code);
            editProfileDoctorModel.setPhone_code(code.replace("+","00"));
        }


    }
    private void checkCameraPermission()
    {
        if (ContextCompat.checkSelfPermission(activity, camera_perm) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, write_perm) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{camera_perm, write_perm}, camera_req);
        }
    }
    private void selectImage()
    {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, camera_req);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == camera_req && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            binding.iconUpload.setVisibility(View.GONE);
            binding.image.setImageBitmap(bitmap);
            uri = getUriFromBitmap(bitmap);
            binding.image.setImageBitmap(bitmap);
        }
    }



    private Uri getUriFromBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "", ""));
    }
    @Override
    public void showDialog() {
        countryPicker.showDialog(activity);
    }

    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);
    }

    private void updatePhoneCode(Country country)
    {
        binding.tvCode.setText(country.getDialCode());
        editProfileDoctorModel.setPhone_code(country.getDialCode().replace("+","00"));

    }





    @Override
    public void updateProfile() {
        if (editProfileDoctorModel.isDataValid(activity))
        {
            if (uri==null)
            {
                editProfileWithoutImage();
            }else
                {
                    editProfileWithImage();
                }
        }
    }


    private void editProfileWithoutImage()
    {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody name_part = Common.getRequestBodyText(editProfileDoctorModel.getName());
        RequestBody phone_part = Common.getRequestBodyText(editProfileDoctorModel.getPhone());
        RequestBody phone_code_part = Common.getRequestBodyText(editProfileDoctorModel.getPhone_code());
        RequestBody email_part = Common.getRequestBodyText(editProfileDoctorModel.getEmail());
        RequestBody gender_part = Common.getRequestBodyText(String.valueOf(editProfileDoctorModel.getGender()));
        RequestBody department_part = Common.getRequestBodyText(editProfileDoctorModel.getDepartment_id());
        RequestBody exper_part = Common.getRequestBodyText(editProfileDoctorModel.getExperience_id());
        RequestBody about_part = Common.getRequestBodyText(editProfileDoctorModel.getAbout_me());


        try {
            Api.getService(Tags.base_url)
                    .editDoctorProfileWithoutImage(lang,userModel.getToken(),name_part,phone_part,phone_code_part,email_part,gender_part,department_part,exper_part,about_part)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                preferences.create_update_userData(activity, response.body());
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
    private void editProfileWithImage()
    {



        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        RequestBody name_part = Common.getRequestBodyText(editProfileDoctorModel.getName());
        RequestBody phone_part = Common.getRequestBodyText(editProfileDoctorModel.getPhone());
        RequestBody phone_code_part = Common.getRequestBodyText(editProfileDoctorModel.getPhone_code());
        RequestBody email_part = Common.getRequestBodyText(editProfileDoctorModel.getEmail());
        RequestBody gender_part = Common.getRequestBodyText(String.valueOf(editProfileDoctorModel.getGender()));
        RequestBody department_part = Common.getRequestBodyText(editProfileDoctorModel.getDepartment_id());
        RequestBody exper_part = Common.getRequestBodyText(editProfileDoctorModel.getExperience_id());
        RequestBody about_part = Common.getRequestBodyText(editProfileDoctorModel.getAbout_me());
        MultipartBody.Part image_part = Common.getMultiPart(activity,uri,"logo");


        try {
            Api.getService(Tags.base_url)
                    .editDoctorProfileWithImage(lang,userModel.getToken(),name_part,phone_part,phone_code_part,email_part,gender_part,department_part,exper_part,about_part,image_part)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                preferences.create_update_userData(activity, response.body());
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
