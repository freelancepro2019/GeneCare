package com.genecare.activities_fragments.activity_payment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.genecare.R;
import com.genecare.databinding.ActivityPaymentBinding;
import com.genecare.language.LanguageHelper;
import com.genecare.models.OrderIdModel;

import java.util.Locale;

import io.paperdb.Paper;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private OrderIdModel orderIdModel;
    private String url;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        getDataFromIntent();
        initView();
    }



    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("order_id"))
        {
            orderIdModel = (OrderIdModel) intent.getSerializableExtra("order_id");
        }
    }

    private void initView() {

        url = orderIdModel.getPayment_link();

        binding.webView.loadUrl(url);

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setBuiltInZoomControls(true);
        binding.webView.getSettings().setDisplayZoomControls(false);
        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("URL",url);
                if (url.contains(orderIdModel.getSucess_link()))
                {
                    setResult(RESULT_OK);
                    finish();


                }else if (url.contains(orderIdModel.getError_link()))
                {
                    setResult(RESULT_CANCELED);
                    finish();


                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });
    }
}
