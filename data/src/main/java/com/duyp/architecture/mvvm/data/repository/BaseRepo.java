package com.duyp.architecture.mvvm.data.repository;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.Nullable;

import com.duyp.androidutils.rx.functions.PlainConsumer;
import com.duyp.architecture.mvvm.data.local.RealmDatabase;
import com.duyp.architecture.mvvm.data.remote.GithubService;
import com.duyp.architecture.mvvm.data.source.Resource;
import com.duyp.architecture.mvvm.data.source.SimpleRemoteSourceMapper;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import lombok.Getter;
import retrofit2.Response;

/**
 * Created by duypham on 9/15/17.
 *
 */

@Getter
public abstract class BaseRepo {

    protected static final String TAG = "repo";

    private final GithubService githubService;

    private final LifecycleOwner owner;

    private final RealmDatabase realmDatabase;

    public BaseRepo(LifecycleOwner owner, GithubService githubService, RealmDatabase realmDatabase) {
        this.githubService = githubService;
        this.owner = owner;
        this.realmDatabase = realmDatabase;
    }


    protected <T> Flowable<Resource<T>> createRemoteSourceMapper(@Nullable Single<Response<T>> remote,
                                                       @Nullable PlainConsumer<T> onSave) {
        return Flowable.create(emitter -> {
            new SimpleRemoteSourceMapper<T>(emitter) {

                @Override
                public Single<Response<T>> getRemote() {
                    return remote;
                }

                @Override
                public void saveCallResult(T data) {
                    if (onSave != null) {
                        onSave.accept(data);
                    }
                }
            };
        }, BackpressureStrategy.BUFFER);
    }
}