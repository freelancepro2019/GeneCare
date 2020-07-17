package com.genecare.activities_fragments.activity_home.fragment.fragment_orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.genecare.R;
import com.genecare.activities_fragments.activity_home.HomeActivity;
import com.genecare.adapters.ViewPagerAdapter;
import com.genecare.databinding.FragmentProviderOrdersBinding;
import com.genecare.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Provider_Orders extends Fragment  {
    private FragmentProviderOrdersBinding binding;
    private HomeActivity activity;
    private String lang;
    private Preferences preferences;
    private List<Fragment> fragmentList;
    private List<String> titles;
    private ViewPagerAdapter adapter;


    public static Fragment_Provider_Orders newInstance() {
        return new Fragment_Provider_Orders();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_provider_orders, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        fragmentList = new ArrayList<>();
        titles = new ArrayList<>();

        preferences = Preferences.newInstance();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

        addFragmentAndTitles();
        adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragments(fragmentList);
        adapter.addTitles(titles);
        binding.tab1.setText(titles.get(0));
        binding.tab2.setText(titles.get(1));

        binding.pager.setAdapter(adapter);

        binding.pager.setOffscreenPageLimit(fragmentList.size());

        binding.pager.setCurrentItem(0,false);
        binding.tab1.setTextColor(ContextCompat.getColor(activity,R.color.colorPrimary));
        binding.tab2.setTextColor(ContextCompat.getColor(activity,R.color.white));


        if (lang.equals("ar"))
        {
            binding.tab1.setBackgroundResource(R.drawable.right_white_tab_bg);
            binding.tab2.setBackgroundResource(R.drawable.left_green_tab_bg);

        }else
        {
            binding.tab1.setBackgroundResource(R.drawable.left_white_tab_bg);
            binding.tab2.setBackgroundResource(R.drawable.right_green_tab_bg);

        }

        binding.tab1.setOnClickListener(view -> binding.pager.setCurrentItem(0,true));
        binding.tab2.setOnClickListener(view -> binding.pager.setCurrentItem(1,true));

        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                switch (position) {
                    case 0:
                        binding.tab1.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                        binding.tab2.setTextColor(ContextCompat.getColor(activity, R.color.white));


                        if (lang.equals("ar")) {
                            binding.tab1.setBackgroundResource(R.drawable.right_white_tab_bg);
                            binding.tab2.setBackgroundResource(R.drawable.left_green_tab_bg);

                        } else {
                            binding.tab1.setBackgroundResource(R.drawable.left_white_tab_bg);
                            binding.tab2.setBackgroundResource(R.drawable.right_green_tab_bg);

                        }
                        break;
                    case 1:

                        binding.tab1.setTextColor(ContextCompat.getColor(activity, R.color.white));
                        binding.tab2.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));


                        if (lang.equals("ar")) {
                            binding.tab1.setBackgroundResource(R.drawable.right_green_tab_bg);
                            binding.tab2.setBackgroundResource(R.drawable.left_white_tab_bg);

                        } else {
                            binding.tab1.setBackgroundResource(R.drawable.left_green_tab_bg);
                            binding.tab2.setBackgroundResource(R.drawable.right_white_tab_bg);

                        }

                        break;


                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void addFragmentAndTitles()
    {
        fragmentList.add(Fragment_Provider_Current_Orders.newInstance());
        fragmentList.add(Fragment_Provider_Previous_Orders.newInstance());

        titles.add(getString(R.string.current));
        titles.add(getString(R.string.previous));


    }

    public void refreshData()
    {
        Fragment_Provider_Current_Orders fragment_provider_current_orders = (Fragment_Provider_Current_Orders) adapter.getItem(0);
        Fragment_Provider_Previous_Orders fragment_provider_previous_orders = (Fragment_Provider_Previous_Orders) adapter.getItem(1);


        fragment_provider_current_orders.getOrders(true);
        fragment_provider_previous_orders.getOrders(true);
    }

    public void refreshFragmentPreviousData()
    {
        Fragment_Provider_Previous_Orders fragment_provider_previous_orders = (Fragment_Provider_Previous_Orders) adapter.getItem(1);


        fragment_provider_previous_orders.getOrders(true);
    }


}
