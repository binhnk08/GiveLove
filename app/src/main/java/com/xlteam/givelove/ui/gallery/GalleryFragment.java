package com.xlteam.givelove.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.xlteam.givelove.R;
import com.xlteam.givelove.external.utility.logger.Log;
import com.xlteam.givelove.external.utility.thread.AsyncLayoutInflateManager;
import com.xlteam.givelove.external.utility.utils.PrefUtils;
import com.xlteam.givelove.external.utility.utils.Utility;
import com.xlteam.givelove.ui.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.xlteam.givelove.external.utility.utils.Constant.FILE_PROVIDER_PATH;

public class GalleryFragment extends Fragment
        implements GalleryAdapter.GallerySelectCallback,
        DialogPreviewGallery.DialogDismissListenerCallback {
    private RecyclerView rvGallery;
    private Context mContext;
    private TextView mEmptyImage;
    private GalleryAdapter mGalleryAdapter;
    private List<String> mGalleryPaths;
    private RelativeLayout layoutTop;
    private ImageView imgCheckAll;

    private LinearLayout layoutBottom;
    private boolean showed = false;
    private Transition transition;
    private ViewGroup viewGroup;
    private LinearLayout mBtnShareGallery;
    private LinearLayout mBtnDeleteGallery;
    private TextView tvTotalChecked;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGalleryPaths = PrefUtils.getListItemGallery(mContext);
        mGalleryAdapter = new GalleryAdapter(mGalleryPaths, this);

        // set animation for bottom sheet (layout share and delete)
        transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(200);
        transition.addTarget(R.id.bottom_sheet);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(this, "onResume");
        updateUI();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = AsyncLayoutInflateManager.getInstance(mContext).inflateView(inflater, container, R.layout.fragment_gallery);
        this.viewGroup = container;
        mEmptyImage = root.findViewById(R.id.tv_empty_image);
        layoutTop = root.findViewById(R.id.layout_top);
        layoutBottom = root.findViewById(R.id.bottom_sheet);

        tvTotalChecked = root.findViewById(R.id.tv_number_image_checked);

        mBtnDeleteGallery = root.findViewById(R.id.btn_delete_gallery);
        mBtnDeleteGallery.setOnClickListener(v -> {
            deleteImages(mGalleryAdapter.getCheckedList());
        });

        mBtnShareGallery = root.findViewById(R.id.btn_share_gallery);
        mBtnShareGallery.setOnClickListener(v -> {
            shareImages(mGalleryAdapter.getCheckedList());
        });

        imgCheckAll = root.findViewById(R.id.image_check_all);
        imgCheckAll.setOnClickListener(view -> {
            imgCheckAll.setActivated(!imgCheckAll.isActivated());
            mGalleryAdapter.onCheckBoxAllChecked(imgCheckAll.isActivated());
        });

        // init recycler gallery by findViewById
        rvGallery = root.findViewById(R.id.rv_gallery_caption);
        rvGallery.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvGallery.setAdapter(mGalleryAdapter);
        rvGallery.setHasFixedSize(true);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemGallerySelected(int position) {
        DialogPreviewGallery dialogPreview = DialogPreviewGallery.newInstance(
                new ArrayList<>(mGalleryPaths),
                position,
                this);
        dialogPreview.show(getChildFragmentManager(), "DialogPreviewGallery");
    }

    @Override
    public void showCheckBoxAll(boolean isCheckBoxChecked) {
        if (isCheckBoxChecked) {
            ((MainActivity) getActivity()).showToolbarCustom(false);
            layoutTop.setVisibility(View.VISIBLE);
        } else {
            layoutTop.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAllItemChecked(boolean isCheckBoxAllChecked) {
        imgCheckAll.setActivated(isCheckBoxAllChecked);
    }

    @Override
    public void showBottomSheetShareAndDelete(int numberImageChecked) {
        Timber.e("numberImageChecked: " + numberImageChecked);
        boolean isShow = numberImageChecked != 0;
        TransitionManager.beginDelayedTransition(viewGroup, transition);
        if (isShow) {
            tvTotalChecked.setText(mContext.getString(R.string.select_number_image, numberImageChecked));
        } else {
            tvTotalChecked.setText(R.string.select_items);
        }
        if (isShow && !showed) {
            layoutBottom.setVisibility(View.VISIBLE);
            showed = true;
        } else if (!isShow && showed) {
            layoutBottom.setVisibility(View.GONE);
            showed = false;
        }
    }

    private void updateUI() {
        mGalleryPaths = PrefUtils.getListItemGallery(mContext);
        if (mGalleryPaths.size() > 0) {
            Timber.e("updateUI, list path size = " + mGalleryPaths.size());
            mEmptyImage.setVisibility(View.GONE);
            rvGallery.setVisibility(View.VISIBLE);
            Collections.sort(mGalleryPaths, (s1, s2) -> s1.substring(s1.lastIndexOf("/") + 1)
                    .compareTo(s2.substring(s2.lastIndexOf("/") + 1)));
            mGalleryAdapter.updateList(mGalleryPaths);
            mGalleryAdapter.notifyDataSetChanged();
        } else {
            rvGallery.setVisibility(View.GONE);
            mEmptyImage.setVisibility(View.VISIBLE);
            Utility.vibrateAnimation(mContext, mEmptyImage);
        }
    }

    @Override
    public void onDialogPreviewDismissed(Boolean isImageDeleted) {
        if (isImageDeleted) {
            updateUI();
        }
    }

    private void shareImages(ArrayList<Integer> checkedList) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
//        intent.putExtra(Intent.EXTRA_SUBJECT, );
        intent.setType("image/jpeg");
        ArrayList<Uri> uriList = new ArrayList<Uri>();
        for (int i = 0; i < mGalleryPaths.size(); i++) {
            if (checkedList.contains(i)) {
                uriList.add(
                        FileProvider.getUriForFile(getContext(),
                                FILE_PROVIDER_PATH,
                                new File(mGalleryPaths.get(i))));

            }
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        startActivity(Intent.createChooser(intent, "Chia sẻ"));
    }

    private void deleteImages(ArrayList<Integer> checkedList) {

    }

    public void onBackPress() { // cancel selecting image mode
        imgCheckAll.setActivated(false);
        mGalleryAdapter.onCheckBoxAllChecked(false);
        mGalleryAdapter.cancelMultipleMode();
        layoutTop.setVisibility(View.GONE);
    }
}