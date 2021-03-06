package com.xlteam.givelove.external.utility.utils;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.Nullable;
import timber.log.Timber;

public class MyCustomLogDebugTree extends Timber.DebugTree {
    private static final String TAG = "GiveLove";

    @Override
    protected @Nullable
    String createStackElementTag(@NotNull StackTraceElement element) {
        return makeClickableLineNumber(element);
    }

    private String makeClickableLineNumber(@NotNull StackTraceElement element) {
        StringBuilder stringBuilder = new StringBuilder(TAG)
                .append(": (")
                .append(element.getFileName())
                .append(":")
                .append(element.getLineNumber())
                .append(") [")
                .append(element.getMethodName())
                .append("]");
        return stringBuilder.toString();
    }
}
