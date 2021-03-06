package com.xlteam.givelove.external.repository;

import android.content.Context;

import com.xlteam.givelove.external.database.MyDatabase;
import com.xlteam.givelove.external.utility.thread.ThreadExecutor;

import java.util.List;

import timber.log.Timber;

public class AbsRepository<CaptionType> {
    protected MyDatabase mDatabase;
    protected ILoader mCallback;
    protected Context mContext;

    public AbsRepository(Context context, ILoader callback) {
        mContext = context;
        mDatabase = MyDatabase.getInstance(context);
        mCallback = callback;
    }

    protected void execute(int loaderTaskType, List<CaptionType> result) {
        if (result != null) {
            ThreadExecutor.runOnMainThread(() -> mCallback.loadResult(loaderTaskType, result), true);
            Timber.d("execute done: type = " + loaderTaskType);
        }
    }
}
