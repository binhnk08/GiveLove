package com.xlteam.givelove.external.firebase;

public interface FirebaseListener<T> {

    void onResponse(T t);

    void onError();
}
