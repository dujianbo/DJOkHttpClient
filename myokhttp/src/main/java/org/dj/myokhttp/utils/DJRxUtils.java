package org.dj.myokhttp.utils;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * 作者：DuJianBo on 2017/11/27 14:13
 * 邮箱：jianbo_du@foxmail.com
 */

public class DJRxUtils {

    private static volatile DJRxUtils sInstance;
    private final FlowableProcessor<Object> flowableBus;
    private final Subject<Object> obervableBus;

    private DJRxUtils() {
        flowableBus = PublishProcessor.create().toSerialized();
        obervableBus = PublishSubject.create().toSerialized();
    }

    public static DJRxUtils getInstance() {
        if (sInstance == null) {
            synchronized (DJRxUtils.class) {
                if (sInstance == null) {
                    sInstance = new DJRxUtils();
                }
            }
        }

        return sInstance;
    }

    public static <T> FlowableTransformer<T, T> flowableDefaultSchedulers() {
        return new FlowableTransformer<T, T>() {

            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T>FlowableTransformer<T, T> flowableAllInIo() {
        return new FlowableTransformer<T, T>() {

            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io());
            }
        };
    }

    public static <T>ObservableTransformer<T, T> observableDefaultSchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public Observable<T> apply(Observable<T> tObservable) {
                return tObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T>ObservableTransformer<T, T> observableAllInIo() {
        return new ObservableTransformer<T, T>() {
            @Override
            public Observable<T> apply(Observable<T> tObservable) {
                return tObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io());
            }
        };
    }

    public void postFlowable(Object o) {
        flowableBus.onNext(o);
    }

    public <T> Flowable<T> toFlowable(Class<T> tClass) {
        return flowableBus.ofType(tClass);
    }

    public void postObservable(Object o) {
        obervableBus.onNext(o);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return obervableBus.ofType(tClass);
    }
}
