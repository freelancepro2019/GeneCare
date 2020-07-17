package com.genecare.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.genecare.R;
import com.genecare.activities_fragments.activity_home.HomeActivity;
import com.genecare.models.NotFireIds;
import com.genecare.models.NotFireModel;
import com.genecare.preferences.Preferences;
import com.genecare.tags.Tags;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class FireBaseMessaging extends FirebaseMessagingService {

    private Preferences preferences = Preferences.newInstance();
    private Map<String,String> map;
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            sendNotification(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        map = remoteMessage.getData();

        for (String key:map.keySet())
        {
            Log.e("key",key+"    value "+map.get(key));
        }

        if (getSession().equals(Tags.session_login))
        {
            String to_many_users = map.get("to_many_users");
            String my_id = getCurrentUser_id();

            if (to_many_users!=null&&isNotificationToMe(my_id,to_many_users))
            {
                manageNotification(map);
            }
        }
    }

    private void manageNotification(Map<String, String> map) {

        String image = map.get("from_image");
        if (image!=null&&!image.isEmpty()&&!image.equals("0"))
        {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Picasso.with(FireBaseMessaging.this).load(Uri.parse(Tags.IMAGE_AVATAR+image)).into(target);
            },100);
        }else
            {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Picasso.with(FireBaseMessaging.this).load(R.drawable.logo).into(target);
                },100);
            }
    }

    private void sendNotification(Bitmap bitmap) {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createNewNotificationVersion(bitmap);
        }else
        {
            createOldNotificationVersion(bitmap);

        }
    }



    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createNewNotificationVersion(Bitmap bitmap) {
        boolean send = true;
        String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;

        String not_type = map.get("notification_type");
        String title = map.get("from_name");
        String body = "";

        Log.e("not_type",not_type+"__");
        if (not_type!=null&&not_type.equals("new_order"))
        {
            body =getString(R.string.new_order);

        }else if (not_type!=null&&not_type.equals("order_accept"))
        {

            body =getString(R.string.order_accepted);

        }else if (not_type!=null&&not_type.equals("order_blocked"))
        {

            body =getString(R.string.ord_pen);

        }else if (not_type!=null&&not_type.equals("client_order_cancel"))
        {

            body =getString(R.string.client_order_cancel);

        }else if (not_type!=null&&not_type.equals("provider_order_cancel"))
        {

            body =getString(R.string.provider_order_cancel);

        }else if (not_type!=null&&not_type.equals("rate_provider"))
        {

            body =getString(R.string.rate_provider);

        }else if (not_type!=null&&not_type.equals("have_rating"))
        {

            body =getString(R.string.have_rating);

        }else if (not_type!=null&&not_type.equals("refresh_note"))
        {
            send = false;

        }

        Log.e("body",body+"__");
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        String CHANNEL_ID = "my_channel_02";
        CharSequence CHANNEL_NAME = "my_channel_name";
        int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

        final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);

        if (send)
        {
            channel.setShowBadge(true);
            channel.setSound(Uri.parse(sound_Path), new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                    .build()
            );
            builder.setChannelId(CHANNEL_ID);
            builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
            builder.setSmallIcon(R.mipmap.ic_launcher_round);

            builder.setLargeIcon(bitmap);
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("not",true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addNextIntent(intent);

            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);
            builder.setContentTitle(title);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
            //builder.setContentText(body);

        }



        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {

            if (send)
            {
                manager.createNotificationChannel(channel);
                manager.notify(78887, builder.build());
            }
            EventBus.getDefault().post(new NotFireModel(true));

        }


    }

    private void createOldNotificationVersion(Bitmap bitmap) {

        boolean send = true;

        String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;

        String not_type = map.get("notification_type");
        String title = map.get("from_name");
        String body = "";

        if (not_type!=null&&not_type.equals("new_order"))
        {
            body =getString(R.string.new_order);

        }else if (not_type!=null&&not_type.equals("order_accept"))
        {
            body =getString(R.string.order_accepted);

        }else if (not_type!=null&&not_type.equals("order_blocked"))
        {
            body =getString(R.string.ord_pen);

        }else if (not_type!=null&&not_type.equals("client_order_cancel"))
        {
            body =getString(R.string.client_order_cancel);

        }else if (not_type!=null&&not_type.equals("provider_order_cancel"))
        {
            body =getString(R.string.provider_order_cancel);

        }else if (not_type!=null&&not_type.equals("rate_provider"))
        {
            body =getString(R.string.rate_provider);

        }else if (not_type!=null&&not_type.equals("have_rating"))
        {
            body =getString(R.string.have_rating);

        }else if (not_type!=null&&not_type.equals("refresh_note"))
        {
            send = false;

        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


        if (send)
        {
            builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
            builder.setSmallIcon(R.mipmap.ic_launcher_round);

            builder.setLargeIcon(bitmap);
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("not",true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addNextIntent(intent);

            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);

            builder.setContentTitle(title);
           // builder.setContentText(body);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        }


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            if (send)
            {
                manager.notify(78887, builder.build());
            }


            EventBus.getDefault().post(new NotFireModel(true));

        }
    }


    private boolean isNotificationToMe(String my_id,String ids)
    {
        NotFireIds data = new Gson().fromJson(ids, NotFireIds.class);

        for (String id :data.getIds())
        {
            if (my_id.equals(id))
            {
                return true;
            }
        }

        return false;
    }

    private String getCurrentUser_id()
    {
        return preferences.getUserData(this).getUser_id();

    }

    private String getSession()
    {
        return preferences.getSession(this);
    }
}
