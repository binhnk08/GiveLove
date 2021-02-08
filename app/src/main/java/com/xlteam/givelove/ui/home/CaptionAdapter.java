package com.xlteam.givelove.ui.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xlteam.givelove.R;
import com.xlteam.givelove.external.datasource.GradientDataSource;
import com.xlteam.givelove.external.utility.customview.SpannableTextView;
import com.xlteam.givelove.external.utility.utils.Utility;
import com.xlteam.givelove.model.CommonCaption;

import java.util.List;

public class CaptionAdapter extends RecyclerView.Adapter<CaptionAdapter.ViewHolder> {
    private List<CommonCaption> mCaptions;
    private Context mContext;
    private Callback mCallback;
    private String mQueryText;
    private boolean mIsSearch;

    public interface Callback {
        void onBookmarkClick(long id, boolean saved, int positionRemove);

        void updateTotalCaptions(int total);
    }

    public CaptionAdapter(Context context, List<CommonCaption> captions, Callback callback,
                          String queryText, boolean isSearch) {
        mContext = context;
        mCaptions = captions;
        mCallback = callback;
        mQueryText = queryText;
        mIsSearch = isSearch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_caption, parent, false);
        return new CaptionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommonCaption caption = mCaptions.get(position);
        setCaptionContent(holder, caption);
        int[] numberGradient = GradientDataSource.getInstance().getAllData().get(position % 9);
        Utility.setColorGradient(holder.layoutBg, numberGradient);
        holder.imgSaved.setActivated(caption.isSaved());
        holder.imgSaved.setOnClickListener(v -> {
            holder.imgSaved.setActivated(!holder.imgSaved.isActivated());
            mCallback.onBookmarkClick(caption.getId(), holder.imgSaved.isActivated(), position);
        });

        holder.imgCopy.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", caption.getContent());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, R.string.copied, Toast.LENGTH_SHORT).show();
            }
        });
        holder.imgShare.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, caption.getContent());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, mContext.getString(R.string.send_friend));
            mContext.startActivity(shareIntent);
        });
        holder.layoutBg.setOnLongClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", caption.getContent());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, R.string.copied, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void setCaptionContent(@NonNull ViewHolder holder, @NonNull CommonCaption caption) {
        String content = caption.getContent();
        if (mIsSearch) {
            holder.tvCaptionContent.setText(content, mQueryText);
        } else {
            holder.tvCaptionContent.setText(content);
        }
    }

    public void removeCaption(int position) {
        mCaptions.remove(position);
        notifyDataSetChanged();
        mCallback.updateTotalCaptions(mCaptions.size());
    }

    @Override
    public int getItemCount() {
        return mCaptions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final SpannableTextView tvCaptionContent;
        private RelativeLayout layoutBg;
        private ImageView imgSaved, imgCopy, imgShare;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCaptionContent = itemView.findViewById(R.id.tv_content_of_caption);
            layoutBg = itemView.findViewById(R.id.layout_background);
            imgSaved = itemView.findViewById(R.id.image_saved);
            imgCopy = itemView.findViewById(R.id.image_copy);
            imgShare = itemView.findViewById(R.id.image_share);
        }
    }
}
