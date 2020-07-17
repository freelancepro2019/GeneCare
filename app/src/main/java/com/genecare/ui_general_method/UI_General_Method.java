package com.genecare.ui_general_method;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.genecare.R;
import com.genecare.share.Time_Ago;
import com.genecare.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UI_General_Method {

    @BindingAdapter("error")
    public static void setErrorUi(View view, String error)
    {
        if (view instanceof EditText)
        {
            EditText editText = (EditText) view;
            editText.setError(error);

        }else if (view instanceof TextView)
        {
            TextView textView = (TextView) view;
            textView.setError(error);

        }
    }



    @BindingAdapter("avatarUser")
    public static void avatarUser(View view,String endPoint)
    {
        if (view instanceof CircleImageView)
        {
            CircleImageView imageView = (CircleImageView) view;

            if (endPoint!=null)
            {

                Picasso.with(imageView.getContext()).load(Uri.parse(Tags.IMAGE_AVATAR+endPoint)).placeholder(R.drawable.ic_user).fit().into(imageView);
            }else
            {
                Picasso.with(imageView.getContext()).load(R.drawable.ic_user).fit().into(imageView);

            }
        }else if (view instanceof RoundedImageView)
        {
            RoundedImageView roundedImageView = (RoundedImageView) view;

            if (endPoint!=null)
            {

                Picasso.with(roundedImageView.getContext()).load(Uri.parse(Tags.IMAGE_AVATAR+endPoint)).fit().into(roundedImageView);
            }else
            {
                Picasso.with(roundedImageView.getContext()).load(R.drawable.ic_user).fit().into(roundedImageView);

            }
        }
        else if (view instanceof ImageView)
        {
            ImageView imageView = (ImageView) view;

            if (endPoint!=null)
            {

                Picasso.with(imageView.getContext()).load(Uri.parse(Tags.IMAGE_AVATAR+endPoint)).fit().into(imageView);
            }else
                {
                    Log.e("fff","fff");
                    Picasso.with(imageView.getContext()).load(R.drawable.ic_user).fit().into(imageView);

                }
        }

    }

    @BindingAdapter("avatarUserNotification")
    public static void avatarUserNotification(View view,String endPoint)
    {
        RoundedImageView roundedImageView = (RoundedImageView) view;

        if (endPoint!=null)
        {

            Picasso.with(roundedImageView.getContext()).load(Uri.parse(Tags.IMAGE_AVATAR+endPoint)).placeholder(R.drawable.logo).fit().into(roundedImageView);
        }else
        {
            Picasso.with(roundedImageView.getContext()).load(R.drawable.logo).fit().into(roundedImageView);

        }

    }

    @BindingAdapter("serviceImage")
    public static void ServiceImage(View view,String endPoint)
    {
        if (view instanceof CircleImageView)
        {
            CircleImageView imageView = (CircleImageView) view;

            if (endPoint!=null)
            {

                Picasso.with(imageView.getContext()).load(Uri.parse(Tags.IMAGE_AVATAR+endPoint)).placeholder(R.drawable.logo).fit().into(imageView);
            }else
            {
                Picasso.with(imageView.getContext()).load(R.drawable.logo).fit().into(imageView);

            }
        }else if (view instanceof ImageView)
        {
            ImageView imageView = (ImageView) view;

            if (endPoint!=null)
            {

                Picasso.with(imageView.getContext()).load(Uri.parse(Tags.IMAGE_AVATAR+endPoint)).placeholder(R.drawable.logo).fit().into(imageView);
            }else
            {
                Picasso.with(imageView.getContext()).load(R.drawable.logo).fit().into(imageView);

            }
        }

    }



    @BindingAdapter({"date"})
    public static void displayTime(TextView textView,long time)
    {
        textView.setText(Time_Ago.getTimeAgo(time,textView.getContext()));
    }


    @BindingAdapter({"orderDate","orderTime"})
    public static void displayDateTime(TextView textView,String dateLong,String timeLong)
    {

        if (dateLong!=null&&timeLong!=null)
        {
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date(Long.parseLong(dateLong)*1000));
            String time = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(new Date(Long.parseLong(timeLong)*1000));

            textView.setText(date+" "+time);
        }

    }



    @BindingAdapter("rate")
    public static void rate (SimpleRatingBar simpleRatingBar, String rate)
    {
        SimpleRatingBar.AnimationBuilder builder = simpleRatingBar.getAnimationBuilder()
                .setRatingTarget(Float.parseFloat(rate))
                .setDuration(1000)
                .setRepeatCount(0)
                .setInterpolator(new LinearInterpolator());
        builder.start();
    }






}
