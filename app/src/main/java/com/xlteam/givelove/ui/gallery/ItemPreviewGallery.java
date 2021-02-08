package com.xlteam.givelove.ui.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xlteam.givelove.R;
import com.xlteam.givelove.external.utility.logger.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemPreviewGallery extends RecyclerView.Adapter<ItemPreviewGallery.ViewHolder> {
    private List<String> mGalleryPaths;
    private ItemPreviewGallery.GallerySelectCallback mCallback;

    public interface GallerySelectCallback {
        void onItemGallerySelected(int position, String path);
    }

    public ItemPreviewGallery(List<String> galleryPaths, ItemPreviewGallery.GallerySelectCallback callBack) {
        mGalleryPaths = galleryPaths;
        mCallback = callBack;
    }

    @NonNull
    @Override
    public ItemPreviewGallery.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_image, parent, false);
            return new ItemPreviewGallery.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull com.xlteam.givelove.ui.gallery.ItemPreviewGallery.ViewHolder holder, int position) {
        String path = mGalleryPaths.get(position);
        Log.e("onBindViewHolder", "file://" + path);
        File imgFile = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        holder.imgGallery.setImageBitmap(bitmap);
        holder.imgGallery.setScaleType(ImageView.ScaleType.CENTER_CROP);

        holder.imgGallery.setOnClickListener(v -> {
            mCallback.onItemGallerySelected(position, path);
        });
    }

    @Override
    public int getItemCount() {
        return mGalleryPaths.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mItemView;
        ImageView imgGallery;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            imgGallery = itemView.findViewById(R.id.img_item_gallery);
        }
    }

    public void updateList(List<String> galleryPaths) {
        mGalleryPaths = new ArrayList<>(galleryPaths);
    }
}