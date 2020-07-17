package com.genecare.interfaces;

import com.genecare.models.ContactUsModel;

public interface Listeners {


    interface LoginListener {
        void checkDataLogin();
    }
    interface SkipListener
    {
        void skip();
    }
    interface BackListener
    {
        void back();
    }
    interface ShowCountryDialogListener
    {
        void showDialog();
    }

    interface SignUpListener {
        void checkDataSignUp();
    }

    interface MoreActions
    {
        void aboutApp();
        void changeLanguage();
        void contactUs();
        void rateApp();
        void terms();
        void share();
        void logout();
    }

    interface UpdateProfileListener
    {
        void updateProfile();
    }
    interface ContactListener
    {
        void sendContact(ContactUsModel contactUsModel);
    }
}
