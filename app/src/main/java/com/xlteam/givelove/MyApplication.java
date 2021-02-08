package com.xlteam.givelove;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.xlteam.givelove.external.repository.CommonCaptionRepository;
import com.xlteam.givelove.external.repository.ILoader;
import com.xlteam.givelove.external.utility.logger.Log;
import com.xlteam.givelove.external.utility.logger.LogcatLogWriter;
import com.xlteam.givelove.external.utility.thread.BitmapLruCache;
import com.xlteam.givelove.external.utility.thread.ThreadExecutor;
import com.xlteam.givelove.external.utility.thread.ThreadExecutorWithPool;
import com.xlteam.givelove.external.utility.utils.MyCustomLogDebugTree;
import com.xlteam.givelove.external.utility.utils.PrefUtils;
import com.xlteam.givelove.external.utility.utils.Utility;
import com.xlteam.givelove.model.CommonCaption;

import java.io.File;
import java.util.List;

import timber.log.Timber;


public class MyApplication extends Application implements ILoader<CommonCaption> {

    public MyApplication() {
        super();
        ThreadExecutorWithPool.getInstance().execute(() -> insertDatabase());
        ThreadExecutor.runOnMainThread(() -> saveImageToLruCache());
        Log.init(new LogcatLogWriter());
        if (Timber.treeCount() == 0) {
            if(BuildConfig.DEBUG) Timber.plant(new MyCustomLogDebugTree());
        }
    }

    private void saveImageToLruCache() {
        List<String> pathImage = PrefUtils.getListItemGallery(this);
        if (!Utility.isEmpty(pathImage)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                BitmapLruCache cacheBitmapLru = BitmapLruCache.getInstance();
                for (String path : pathImage) {
                    File imgFile = new File(path);
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    cacheBitmapLru.saveBitmapToCache(path, bitmap);
                }
            }
        }
    }

    private void insertDatabase() {
        CommonCaptionRepository mRepository = new CommonCaptionRepository(this, this);
        mRepository.insertToDatabase();
    }
}
