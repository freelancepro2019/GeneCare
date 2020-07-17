package com.genecare.activities_fragments.activity_make_order;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.genecare.R;
import com.genecare.activities_fragments.activity_payment.PaymentActivity;
import com.genecare.adapters.Spinner_Adapter;
import com.genecare.databinding.ActivityMakeOrderBinding;
import com.genecare.databinding.DialogAlertBinding;
import com.genecare.interfaces.Listeners;
import com.genecare.language.LanguageHelper;
import com.genecare.models.MakeOrderModel;
import com.genecare.models.OrderIdModel;
import com.genecare.models.PlaceGeocodeData;
import com.genecare.models.PlaceMapDetailsData;
import com.genecare.models.SubServicesModel;
import com.genecare.models.UserModel;
import com.genecare.preferences.Preferences;
import com.genecare.remote.Api;
import com.genecare.share.Common;
import com.genecare.tags.Tags;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeOrderActivity extends AppCompatActivity implements Listeners.BackListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private ActivityMakeOrderBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String fineLocPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 1225;
    private GoogleMap mMap;
    private double lat, lng;
    private Marker marker;
    private final float zoom = 15.6f;

    private FragmentMapTouchListener fragment;
    private Spinner_Adapter adapter_times, adapter_patient;
    private List<String> times, patient;

    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private long date = 0, time = 0;
    private int color;
    private String main_service_id;
    private SubServicesModel.SubServiceModel subServiceModel = null;
    private MakeOrderModel makeOrderModel;
    private List<Integer> shifts;
    private int paymentType=0;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_make_order);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            color = intent.getIntExtra("color", R.color.colorPrimary);
            main_service_id = intent.getStringExtra("main_service_id");
            subServiceModel = (SubServicesModel.SubServiceModel) intent.getSerializableExtra("data");
        }
    }

    private void initView() {
        shifts = new ArrayList<>();
        makeOrderModel = new MakeOrderModel();

        times = new ArrayList<>();
        patient = new ArrayList<>();

        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        makeOrderModel.setUser_id(userModel.getUser_id());
        makeOrderModel.setMain_service_id(main_service_id);
        makeOrderModel.setSub_service_id(subServiceModel.getId());
        binding.setModel(makeOrderModel);
        binding.setSubServiceModel(subServiceModel);
        createDatePickerDialog();
        createTimePickerDialog();


        adapter_times = new Spinner_Adapter(times, this);
        binding.spinnerNumberOfTime.setAdapter(adapter_times);

        adapter_patient = new Spinner_Adapter(patient, this);
        binding.spinnerNumberOfPatient.setAdapter(adapter_patient);
        binding.tvName.setText(subServiceModel.getWords().getTitle());
        binding.tvPrice.setText(String.format("%s %s", subServiceModel.getCost(), getString(R.string.le)));
        binding.toolBar.setBackgroundResource(color);
        binding.btnSend.setBackgroundResource(color);


        if (subServiceModel.getOther_details() != null) {
            shifts.addAll(subServiceModel.getOther_details());

            makeOrderModel.setHasShift(true);
            List<String> shiftsList = new ArrayList<>();
            shiftsList.add(getString(R.string.ch));

            for (Integer shift : subServiceModel.getOther_details()) {
                shiftsList.add(shift + " " + getString(R.string.hour));
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shiftsList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerNumberOfShifts.setAdapter(arrayAdapter);

        }

        addTimes_Patients();

        initMap();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeStatusBarColor(color);
        }


        binding.spinnerNumberOfTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    makeOrderModel.setNum_time(0);
                    binding.tvTotal.setText(String.format("%s %s", "0.0", getString(R.string.le)));
                    makeOrderModel.setTotal(0);
                } else {
                    makeOrderModel.setNum_time(Integer.parseInt(times.get(i)));
                    calculateTotal();

                }
                binding.setModel(makeOrderModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerNumberOfPatient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    binding.tvTotal.setText(String.format("%s %s", "0.0", getString(R.string.le)));
                    makeOrderModel.setNum_patient(0);
                    makeOrderModel.setTotal(0);

                } else {
                    makeOrderModel.setNum_patient(Integer.parseInt(patient.get(i)));
                    calculateTotal();

                }
                binding.setModel(makeOrderModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerNumberOfPatient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    binding.tvTotal.setText(String.format("%s %s", "0.0", getString(R.string.le)));
                    makeOrderModel.setNum_patient(0);
                    makeOrderModel.setTotal(0);

                } else {
                    makeOrderModel.setNum_patient(Integer.parseInt(patient.get(i)));
                    calculateTotal();

                }
                binding.setModel(makeOrderModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerNumberOfShifts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    makeOrderModel.setShift_num(0);

                } else {
                    makeOrderModel.setShift_num(shifts.get(i));

                }
                binding.setModel(makeOrderModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.rbMale.setOnClickListener(view ->

                makeOrderModel.setType(1)
        );

        binding.rbFemale.setOnClickListener(view ->

                makeOrderModel.setType(2)
        );

        binding.rbAny.setOnClickListener(view ->

                makeOrderModel.setType(3)
        );

        binding.rbCashMoney.setOnClickListener(view ->

                {
                    paymentType = 1;
                    makeOrderModel.setPayment(1);
                    binding.rbVodafoneCash.setChecked(false);
                    binding.rbFawry.setChecked(false);
                    binding.rbMasterCard.setChecked(false);


                }
        );

        binding.rbVodafoneCash.setOnClickListener(view ->

                {
                    paymentType = 2;
                    makeOrderModel.setPayment(2);

                    binding.rbCashMoney.setChecked(false);
                    binding.rbFawry.setChecked(false);
                    binding.rbMasterCard.setChecked(false);

                }
        );

        binding.rbMasterCard.setOnClickListener(view ->

                {
                    paymentType = 3;
                    makeOrderModel.setPayment(3);

                    binding.rbCashMoney.setChecked(false);
                    binding.rbFawry.setChecked(false);
                    binding.rbVodafoneCash.setChecked(false);

                }
        );

        binding.rbFawry.setOnClickListener(view ->

                {
                    makeOrderModel.setPayment(4);

                    binding.rbCashMoney.setChecked(false);
                    binding.rbVodafoneCash.setChecked(false);
                    binding.rbMasterCard.setChecked(false);

                }
        );

        binding.btnSend.setOnClickListener(view ->
        {
            if (makeOrderModel.isDataValid(this)) {
                Common.CloseKeyBoard(this, binding.edtAge);
                makeOrder();
            }
        });
        binding.imageSearch.setOnClickListener(view ->
        {
            String address = binding.edtAddress.getText().toString().trim();
            if (!address.isEmpty()) {
                Common.CloseKeyBoard(this, binding.edtAddress);
                binding.edtAddress.setError(null);
                Search(address);
            } else {
                binding.edtAddress.setError(getString(R.string.field_req));

            }
        });

        binding.llDate.setOnClickListener(view ->
                datePickerDialog.show(getFragmentManager(), "")
        );
        binding.llTime.setOnClickListener(view ->
                timePickerDialog.show(getFragmentManager(), "")
        );
    }

    private void makeOrder() {

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy", Locale.ENGLISH);
            String d = dateFormat.format(new Date(date));
            Api.getService(Tags.base_url)
                    .createOrder(lang, userModel.getToken(), main_service_id, subServiceModel.getId(), d, makeOrderModel.getTime(), makeOrderModel.getAge(), makeOrderModel.getType(), makeOrderModel.getAddress(), makeOrderModel.getLat(), makeOrderModel.getLng(), makeOrderModel.getPhone(), makeOrderModel.getAnother_phone(), makeOrderModel.getPayment(), makeOrderModel.getDescription(), makeOrderModel.getTotal(), makeOrderModel.getNum_time(), makeOrderModel.getNum_patient(), makeOrderModel.getShift_num())
                    .enqueue(new Callback<OrderIdModel>() {
                        @Override
                        public void onResponse(Call<OrderIdModel> call, Response<OrderIdModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {

                                if (paymentType==3)
                                {
                                    Log.e("order_id",response.body().getId()+"___");
                                    Intent intent = new Intent(MakeOrderActivity.this, PaymentActivity.class);
                                    intent.putExtra("order_id",response.body());
                                    startActivityForResult(intent,200);
                                }else
                                    {
                                        CreateAlert();

                                    }

                            } else {

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(MakeOrderActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else if (response.code() == 409) {

                                    Common.CreateDialogAlert(MakeOrderActivity.this, getString(R.string.sorry_no_prov), color);
                                } else {
                                    Toast.makeText(MakeOrderActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderIdModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(MakeOrderActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MakeOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void CreateAlert() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogAlertBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_alert, null, false);
        binding.btnCancel.setBackgroundResource(color);
        binding.llContainer.setBackgroundResource(color);
        binding.tvMsg.setText(R.string.order_sent);
        binding.btnCancel.setOnClickListener(v ->
                {
                    dialog.dismiss();
                    finish();
                }


        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, color));
    }

    private void calculateTotal() {
        double total = Double.parseDouble(subServiceModel.getCost()) * makeOrderModel.getNum_time() * makeOrderModel.getNum_patient();
        binding.tvTotal.setText(String.format("%s %s", total, getString(R.string.le)));
        makeOrderModel.setTotal(total);
        binding.setModel(makeOrderModel);
    }

    private void createDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);

        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setAccentColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        datePickerDialog.setCancelColor(ActivityCompat.getColor(this, R.color.gray4));
        datePickerDialog.setOkColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        datePickerDialog.setOkText(getString(R.string.select));
        datePickerDialog.setCancelText(getString(R.string.cancel));
        datePickerDialog.setLocale(new Locale(lang));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setMinDate(calendar);


    }

    private void createTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.dismissOnPause(true);
        timePickerDialog.setAccentColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        timePickerDialog.setCancelColor(ActivityCompat.getColor(this, R.color.gray4));
        timePickerDialog.setOkColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        timePickerDialog.setOkText(getString(R.string.select));
        timePickerDialog.setCancelText(getString(R.string.cancel));
        timePickerDialog.setLocale(new Locale(lang));
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);


    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        binding.tvDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        date = calendar.getTimeInMillis();

        makeOrderModel.setDate(date / 1000);
        binding.setModel(makeOrderModel);

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String t = dateFormat.format(new Date(calendar.getTimeInMillis()));
        binding.tvTime.setText(t);

        time = calendar.getTimeInMillis();
        makeOrderModel.setTime(time / 1000);
        binding.setModel(makeOrderModel);

    }

    private void addTimes_Patients() {
        times.add(getString(R.string.no_time));
        patient.add(getString(R.string.no_patients));
        for (int i = 1; i < 31; i++) {
            times.add(String.valueOf(i));
        }

        for (int i = 1; i < 6; i++) {
            patient.add(String.valueOf(i));
        }
        adapter_times.notifyDataSetChanged();
        adapter_patient.notifyDataSetChanged();


    }

    private void initMap() {

        fragment = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment != null) {
            fragment.getMapAsync(this);

        }

    }

    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, fineLocPerm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{fineLocPerm}, loc_req);
        } else {

            initGoogleApi();
        }
    }

    private void initGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            CheckPermission();

            mMap.setOnMapClickListener(latLng -> {
                marker.setPosition(latLng);
                lat = latLng.latitude;
                lng = latLng.longitude;
                getGeoData(lat, lng);
            });

            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));

        }
    }

    private void AddMarker(double lat, double lng) {

        this.lat = lat;
        this.lng = lng;
        makeOrderModel.setLat(lat);
        makeOrderModel.setLng(lng);
        binding.setModel(makeOrderModel);

        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));

        } else {
            marker.setPosition(new LatLng(lat, lng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));


        }


    }

    private void getGeoData(double lat, double lng) {


        String location = lat + "," + lng;
        Api.getService("https://maps.googleapis.com/maps/api/")
                .getGeoData(location, lang, getString(R.string.map_api_key))
                .enqueue(new Callback<PlaceGeocodeData>() {
                    @Override
                    public void onResponse(Call<PlaceGeocodeData> call, Response<PlaceGeocodeData> response) {
                        if (response.isSuccessful() && response.body() != null) {


                            if (response.body().getResults().size() > 0) {
                                String address = response.body().getResults().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                makeOrderModel.setAddress(address);
                                binding.setModel(makeOrderModel);
                                binding.edtAddress.setText(address);


                            }
                        } else {

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceGeocodeData> call, Throwable t) {
                        try {


                            // Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void Search(String query) {

        String fields = "id,place_id,name,geometry,formatted_address";
        Api.getService("https://maps.googleapis.com/maps/api/")
                .searchOnMap("textquery", query, fields, lang, getString(R.string.map_api_key))
                .enqueue(new Callback<PlaceMapDetailsData>() {
                    @Override
                    public void onResponse(Call<PlaceMapDetailsData> call, Response<PlaceMapDetailsData> response) {
                        if (response.isSuccessful() && response.body() != null) {


                            if (response.body().getCandidates().size() > 0) {
                                String address = response.body().getCandidates().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                lat = response.body().getCandidates().get(0).getGeometry().getLocation().getLat();
                                lng = response.body().getCandidates().get(0).getGeometry().getLocation().getLng();
                                binding.edtAddress.setText(address);
                                makeOrderModel.setAddress(address);
                                makeOrderModel.setLat(lat);
                                makeOrderModel.setLng(lng);
                                binding.setModel(makeOrderModel);
                                AddMarker(response.body().getCandidates().get(0).getGeometry().getLocation().getLat(), response.body().getCandidates().get(0).getGeometry().getLocation().getLng());
                            }
                        } else {


                            try {
                                Log.e("error_code", response.code() + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceMapDetailsData> call, Throwable t) {
                        try {


                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    private void initLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(60000);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(locationRequest);
        request.setAlwaysShow(false);


        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, request.build());
        result.setResultCallback(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(MakeOrderActivity.this, 100);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        AddMarker(lat, lng);
        getGeoData(location.getLatitude(), location.getLongitude());

        if (googleApiClient != null) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
            googleApiClient = null;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == loc_req) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGoogleApi();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            startLocationUpdate();
        }else if (requestCode == 200) {

            if (resultCode == Activity.RESULT_OK)
            {
                CreateAlert();

            }else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void back() {
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }

        if (locationRequest != null && locationCallback != null) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        }

    }
}
