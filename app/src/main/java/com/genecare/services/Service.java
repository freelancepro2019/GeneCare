package com.genecare.services;


import com.genecare.models.CommentDataModel;
import com.genecare.models.NotificationCountModel;
import com.genecare.models.NotificationDataModel;
import com.genecare.models.OrderDataModel;
import com.genecare.models.OrderIdModel;
import com.genecare.models.PlaceGeocodeData;
import com.genecare.models.PlaceMapDetailsData;
import com.genecare.models.ServicesDataModel;
import com.genecare.models.SingleOrderDataModel;
import com.genecare.models.SubServicesModel;
import com.genecare.models.TermsDataModel;
import com.genecare.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);


    @Multipart
    @POST("api/provider-register")
    Call<UserModel> signUpDoctorWithImage(@Header("device-lang") String header,
                                          @Part("name") RequestBody name,
                                          @Part("phone") RequestBody phone,
                                          @Part("phone_code") RequestBody phone_code,
                                          @Part("password") RequestBody password,
                                          @Part("email") RequestBody email,
                                          @Part("gender") RequestBody gender,
                                          @Part("soft_type") RequestBody soft_type,
                                          @Part("department") RequestBody department,
                                          @Part("exper") RequestBody exper,
                                          @Part("about") RequestBody about,
                                          @Part MultipartBody.Part logo

    );

    @Multipart
    @POST("api/provider-register")
    Call<UserModel> signUpDoctorWithoutImage(@Header("device-lang") String header,
                                             @Part("name") RequestBody name,
                                             @Part("phone") RequestBody phone,
                                             @Part("phone_code") RequestBody phone_code,
                                             @Part("password") RequestBody password,
                                             @Part("email") RequestBody email,
                                             @Part("gender") RequestBody gender,
                                             @Part("soft_type") RequestBody soft_type,
                                             @Part("department") RequestBody department,
                                             @Part("exper") RequestBody exper,
                                             @Part("about") RequestBody about

    );


    @Multipart
    @POST("api/user-register")
    Call<UserModel> signUpClientWithImage(@Header("device-lang") String header,
                                          @Part("name") RequestBody name,
                                          @Part("phone") RequestBody phone,
                                          @Part("phone_code") RequestBody phone_code,
                                          @Part("password") RequestBody password,
                                          @Part("email") RequestBody email,
                                          @Part("gender") RequestBody gender,
                                          @Part("soft_type") RequestBody soft_type,
                                          @Part MultipartBody.Part logo

    );

    @Multipart
    @POST("api/user-register")
    Call<UserModel> signUpClientWithoutImage(@Header("device-lang") String header,
                                             @Part("name") RequestBody name,
                                             @Part("phone") RequestBody phone,
                                             @Part("phone_code") RequestBody phone_code,
                                             @Part("password") RequestBody password,
                                             @Part("email") RequestBody email,
                                             @Part("gender") RequestBody gender,
                                             @Part("soft_type") RequestBody soft_type

    );

    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Header("device-lang") String header,
                          @Field("phone_code") String phone_code,
                          @Field("phone") String phone,
                          @Field("password") String password);

    @GET("api/logout")
    Call<ResponseBody> logout(@Header("device-lang") String header,
                              @Header("Authorization") String user_token,
                              @Query("firebase_token") String firebase_token);


    @GET("api/main-services")
    Call<ServicesDataModel> get_services(@Header("device-lang") String header);

    @GET("api/get-sub-services")
    Call<SubServicesModel> get_sub_services(@Header("device-lang") String header,
                                            @Query("main_service_id") String main_service_id);

    @FormUrlEncoded
    @POST("api/contact-us")
    Call<ResponseBody> contactUs(@Header("device-lang") String header,
                                 @Field("name") String name,
                                 @Field("email") String email,
                                 @Field("subject") String subject,
                                 @Field("message") String message
    );


    @GET("api/setting")
    Call<TermsDataModel> getAppData(@Header("device-lang") String header);


    @GET("api/my-notifications")
    Call<NotificationDataModel> getNotifications(@Header("device-lang") String header,
                                                 @Header("Authorization") String user_token,
                                                 @Query("page") int page,
                                                 @Query("limit_per_page") int limit_per_page
    );

    @GET("api/client-orders")
    Call<OrderDataModel> getClientOrders(@Header("device-lang") String header,
                                         @Header("Authorization") String user_token,
                                         @Query("type") String type,
                                         @Query("page") int page,
                                         @Query("limit_per_page") int limit_per_page
    );

    @GET("api/provider-orders")
    Call<OrderDataModel> getProviderOrders(@Header("device-lang") String header,
                                           @Header("Authorization") String user_token,
                                           @Query("type") String type,
                                           @Query("page") int page,
                                           @Query("limit_per_page") int limit_per_page
    );

    @FormUrlEncoded
    @POST("api/create-order")
    Call<OrderIdModel> createOrder(@Header("device-lang") String header,
                                   @Header("Authorization") String user_token,
                                   @Field("main_service_id") String main_service_id,
                                   @Field("sub_service_id") String sub_service_id,
                                   @Field("order_date") String order_date,
                                   @Field("order_time") long order_time,
                                   @Field("age") String age,
                                   @Field("gender") int gender,
                                   @Field("address") String address,
                                   @Field("google_lat") double google_lat,
                                   @Field("google_long") double google_long,
                                   @Field("phone") String phone,
                                   @Field("other_phone") String other_phone,
                                   @Field("payment") int payment,
                                   @Field("desc") String desc,
                                   @Field("price") double price,
                                   @Field("num_times") int num_times,
                                   @Field("num_patients") int num_patients,
                                   @Field("other_details") int num_shift


    );


    @FormUrlEncoded
    @POST("api/update-firebase-token")
    Call<ResponseBody> updateToken(@Header("device-lang") String header,
                                   @Header("Authorization") String user_token,
                                   @Field("firebase_token") String token,
                                   @Field("soft_type") int soft_type

    );


    @GET("api/one-order")
    Call<SingleOrderDataModel> getOrderDetails(@Header("device-lang") String header,
                                               @Header("Authorization") String user_token,
                                               @Query("order_id") String order_id
    );

    @GET("api/unread-notifications")
    Call<NotificationCountModel> getNotificationCount(@Header("device-lang") String header,
                                                      @Header("Authorization") String user_token
    );


    @FormUrlEncoded
    @POST("api/accept-order")
    Call<ResponseBody> providerAcceptOrder(@Header("device-lang") String header,
                                           @Header("Authorization") String user_token,
                                           @Field("notification_id") String notification_id,
                                           @Field("from_user_id") String from_user_id,
                                           @Field("process_id_fk") String process_id_fk


    );

    @FormUrlEncoded
    @POST("api/refuse-order")
    Call<ResponseBody> providerRefuseOrder(@Header("device-lang") String header,
                                           @Header("Authorization") String user_token,
                                           @Field("notification_id") String notification_id,
                                           @Field("from_user_id") String from_user_id,
                                           @Field("process_id_fk") String process_id_fk

    );

    @FormUrlEncoded
    @POST("api/provider-end-order")
    Call<ResponseBody> providerEndOrder(@Header("device-lang") String header,
                                        @Header("Authorization") String user_token,
                                        @Field("order_id") String order_id

    );

    @FormUrlEncoded
    @POST("api/cancel-order")
    Call<ResponseBody> cancelOrder(@Header("device-lang") String header,
                                   @Header("Authorization") String user_token,
                                   @Field("order_id") String order_id

    );


    @FormUrlEncoded
    @POST("api/client-end-order")
    Call<ResponseBody> clientEndOrder(@Header("device-lang") String header,
                                      @Header("Authorization") String user_token,
                                      @Field("from_user_id") String from_user_id,
                                      @Field("process_id_fk") String process_id_fk,
                                      @Field("notification_id") String notification_id,
                                      @Field("rate_num") float rate_num,
                                      @Field("rate_comment") String rate_comment


    );


    @GET("api/my-rating")
    Call<CommentDataModel> getComments(@Header("device-lang") String header,
                                       @Header("Authorization") String user_token,
                                       @Query("page") int page
    );


    @Multipart
    @POST("api/update-user-profile")
    Call<UserModel> editClientProfileWithImage(@Header("device-lang") String header,
                                               @Header("Authorization") String user_token,
                                               @Part("name") RequestBody name,
                                               @Part("phone") RequestBody phone,
                                               @Part("phone_code") RequestBody phone_code,
                                               @Part("email") RequestBody email,
                                               @Part("gender") RequestBody gender,
                                               @Part MultipartBody.Part logo

    );

    @Multipart
    @POST("api/update-user-profile")
    Call<UserModel> editClientProfileWithoutImage(@Header("device-lang") String header,
                                                  @Header("Authorization") String user_token,
                                                  @Part("name") RequestBody name,
                                                  @Part("phone") RequestBody phone,
                                                  @Part("phone_code") RequestBody phone_code,
                                                  @Part("email") RequestBody email,
                                                  @Part("gender") RequestBody gender

    );


    @Multipart
    @POST("api/update-provider-profile")
    Call<UserModel> editDoctorProfileWithImage(@Header("device-lang") String header,
                                               @Header("Authorization") String user_token,
                                               @Part("name") RequestBody name,
                                               @Part("phone") RequestBody phone,
                                               @Part("phone_code") RequestBody phone_code,
                                               @Part("email") RequestBody email,
                                               @Part("gender") RequestBody gender,
                                               @Part("department") RequestBody department,
                                               @Part("exper") RequestBody exper,
                                               @Part("about") RequestBody about,
                                               @Part MultipartBody.Part logo

    );

    @Multipart
    @POST("api/update-provider-profile")
    Call<UserModel> editDoctorProfileWithoutImage(@Header("device-lang") String header,
                                                  @Header("Authorization") String user_token,
                                                  @Part("name") RequestBody name,
                                                  @Part("phone") RequestBody phone,
                                                  @Part("phone_code") RequestBody phone_code,
                                                  @Part("email") RequestBody email,
                                                  @Part("gender") RequestBody gender,
                                                  @Part("department") RequestBody department,
                                                  @Part("exper") RequestBody exper,
                                                  @Part("about") RequestBody about

    );


    @FormUrlEncoded
    @POST("api/visit-app")
    Call<ResponseBody> updateVisit(@Header("device-lang") String header,
                                   @Field("day_date") String day_date,
                                   @Field("type") String type



    );

}


