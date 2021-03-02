package com.xlteam.givelove.external.repository;

import android.content.Context;

import static com.xlteam.givelove.external.utility.utils.Constant.RepositoryType.COMMON_REPOSITORY;
import static com.xlteam.givelove.external.utility.utils.Constant.RepositoryType.USER_REPOSITORY;

public class RepositoryFactory {
    public static <T extends AbsRepository> T createRepository(Context context, ILoader callback, int repositoryType) {
        if (repositoryType == COMMON_REPOSITORY)
            return (T) new CommonCaptionRepository(context, callback);
        else if (repositoryType == USER_REPOSITORY)
            return (T) new UserCaptionRepository(context, callback);
        throw new IllegalArgumentException("No repository was created - repositoryType = " + repositoryType);
    }
}
