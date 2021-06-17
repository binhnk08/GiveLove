package com.xlteam.givelove.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xlteam.givelove.R;
import com.xlteam.givelove.external.utility.logger.Log;

import java.util.List;

public class XongThiXoaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> strings;
    private Context mContext;
    private boolean isAllOpened = false;
    private int sizeHienThi = 10;

    public XongThiXoaAdapter(Context context, List<String> list) {
        mContext = context;
        strings = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == -1) {
            //type của nút more
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yyy, parent, false);
            return new MoreViewHolder(view);
        } else {
            //type thường
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_xxx, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("binhnk08", " viewtype = " + holder.getItemViewType());
        if (holder.getItemViewType() == -1) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isAllOpened = true;
                    notifyItemRangeChanged(position, strings.size());
                }
            });
        } else {
            ((ViewHolder) holder).t.setText(strings.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!isAllOpened && position == sizeHienThi) return -1; //-1 và 0 tùy ý, là type thôi
        return 0;
    }

    @Override
    public int getItemCount() {
        if (!isAllOpened && strings.size() > sizeHienThi) {
            return sizeHienThi + 1;
        }
        return strings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView t;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            t = itemView.findViewById(R.id.tv_content);
        }
    }

    static class MoreViewHolder extends RecyclerView.ViewHolder {

        public MoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
