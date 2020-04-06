package com.wp.app.resource.common;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wp on 2019/11/22.
 */
public class RxTimerHelper implements LifecycleObserver {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public static RxTimerHelper get(LifecycleOwner owner) {
        return new RxTimerHelper(owner);
    }

    private RxTimerHelper(LifecycleOwner owner) {
        // LogUtils.d("-----owner: " + owner);
        owner.getLifecycle().addObserver(this);
    }

    /**
     * milliseconds毫秒后执行next操作
     */
    public void timer(long milliseconds, final IRxNext next) {
        Disposable disposable = Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (next != null) {
                            next.doNext(aLong);
                        }
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 每隔milliseconds毫秒后执行next操作
     */
    public void interval(long milliseconds, final IRxNext next) {
        Disposable disposable = Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (next != null) {
                            next.doNext(aLong);
                        }
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    public <T> void doInBackground(@NotNull final Callback<T> callback) {
        Disposable disposable = Observable.just(1)
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer) {
                        //LogUtils.d("-----1--" + Thread.currentThread().getName());
                        callback.onStart();
                        return integer;
                    }
                })
                .observeOn(Schedulers.newThread())
                .map(new Function<Integer, T>() {
                    @Override
                    public T apply(@NonNull Integer integer) {
                        //LogUtils.d("-----2--" + Thread.currentThread().getName());
                        return callback.doInBackground();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(@NonNull T result) {
                        callback.onResult(result);
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 取消订阅
     */
    private void dispose() {
        mCompositeDisposable.dispose();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        // Log.d("--", "------onDestroy: ");
        dispose();
    }

    public interface IRxNext {
        void doNext(long number);
    }

    public interface Callback<T> {
        void onStart();

        T doInBackground();

        void onResult(T result);
    }
}
