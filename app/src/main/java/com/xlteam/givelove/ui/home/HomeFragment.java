package com.xlteam.givelove.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.xlteam.givelove.R;
import com.xlteam.givelove.external.utility.animation.ViManager;
import com.xlteam.givelove.external.utility.thread.AsyncLayoutInflateManager;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private ViewPager viewPager;
    private SlidePagerAdapter mAdapter;
    private RelativeLayout layoutTitle, layoutCategory;
    private TextView tvTitle;
    private LinearLayout btnTatCaThinh, btnThinhDangHot, btnChoiChu, btnThoCa, btnVanHoc, btnCaDao;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = AsyncLayoutInflateManager.getInstance(mContext).inflateView(inflater, container, R.layout.fragment_home);
        viewPager = root.findViewById(R.id.viewPager);
        tvTitle = root.findViewById(R.id.tvTitle);
        layoutCategory = root.findViewById(R.id.layout_category);
        layoutTitle = root.findViewById(R.id.layout_title);
        mAdapter = new SlidePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCategoryName(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        layoutTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutCategory.getVisibility() == View.VISIBLE) {
                    layoutCategory.setVisibility(View.GONE);
                } else {
                    layoutCategory.setVisibility(View.VISIBLE);
                }
            }
        });

        initMenuCategory(root);
        return root;
    }

    private void initMenuCategory(View view) {
        btnTatCaThinh = view.findViewById(R.id.btn_tat_ca_thinh);
        btnThinhDangHot = view.findViewById(R.id.btn_thinh_dang_hot);
        btnChoiChu = view.findViewById(R.id.btn_choi_chu);
        btnThoCa = view.findViewById(R.id.btn_tho_ca);
        btnVanHoc = view.findViewById(R.id.btn_van_hoc);
        btnCaDao = view.findViewById(R.id.btn_ca_dao);
        layoutCategory = view.findViewById(R.id.layout_category);

        btnTatCaThinh.setOnClickListener(this);
        btnThinhDangHot.setOnClickListener(this);
        btnChoiChu.setOnClickListener(this);
        btnThoCa.setOnClickListener(this);
        btnVanHoc.setOnClickListener(this);
        btnCaDao.setOnClickListener(this);
        layoutCategory.setOnTouchListener((v, motionEvent) -> true);
    }

    @Override
    public void onClick(View view) {
        int position = 0;
        switch (view.getId()) {
            case R.id.btn_tat_ca_thinh:
                position = 0;
                break;
            case R.id.btn_thinh_dang_hot:
                position = 1;
                break;
            case R.id.btn_choi_chu:
                position = 2;
                break;
            case R.id.btn_tho_ca:
                position = 3;
                break;
            case R.id.btn_van_hoc:
                position = 4;
                break;
            case R.id.btn_ca_dao:
                position = 5;
                break;
        }
        viewPager.setCurrentItem(position);
        layoutCategory.setVisibility(View.GONE);
    }

    private void setCategoryName(int numberCategory) {
        switch (numberCategory) {
            case 0:
                tvTitle.setText(mContext.getString(R.string.tat_ca_thinh));
                break;
            case 1:
                tvTitle.setText(mContext.getString(R.string.thinh_dang_hot));
                break;
            case 2:
                tvTitle.setText(mContext.getString(R.string.choi_chu));
                break;
            case 3:
                tvTitle.setText(mContext.getString(R.string.tho_ca));
                break;
            case 4:
                tvTitle.setText(mContext.getString(R.string.van_hoc));
                break;
            case 5:
                tvTitle.setText(mContext.getString(R.string.ca_dao));
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ViManager.getInstance().setFragmentDefaultAnimation(mContext, fragmentTransaction);
            SearchDialogFragment searchDialogFragment = new SearchDialogFragment(() -> mAdapter.notifyDataSetChanged());
            searchDialogFragment.show(fragmentTransaction, "dialog");
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SlidePagerAdapter extends FragmentStatePagerAdapter {

        SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return new CaptionListFragment(position);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 6;
        }
    }
}
