package com.xlteam.givelove.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xlteam.givelove.R;
import com.xlteam.givelove.external.utility.animation.ViManager;
import com.xlteam.givelove.external.utility.thread.AsyncLayoutInflateManager;

public class HomeFragment extends Fragment {
    private Context mContext;
    private TabLayout tabLayoutCategory;
    private ViewPager viewPager;
    private SlidePagerAdapter mAdapter;

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
        tabLayoutCategory = root.findViewById(R.id.tab_layout_category);
        viewPager = root.findViewById(R.id.viewPager);
        mAdapter = new SlidePagerAdapter(getChildFragmentManager(), mContext);
        viewPager.setAdapter(mAdapter);
        tabLayoutCategory.setupWithViewPager(viewPager);
        return root;
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

    public class SlidePagerAdapter extends FragmentStatePagerAdapter {
        private Context mContext;

        SlidePagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
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
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.all);
                case 1:
                    return mContext.getString(R.string.trending);
                case 2:
                    return mContext.getString(R.string.give_love);
                case 3:
                    return mContext.getString(R.string.life);
                case 4:
                    return mContext.getString(R.string.friend);
                case 5:
                    return mContext.getString(R.string.family);
                case 6:
                    return mContext.getString(R.string.other);
            }
            return mContext.getString(R.string.all);
        }
    }
}
