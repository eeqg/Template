package com.wp.app.resource.basic.net

import android.util.Log

import com.wp.app.resource.BuildConfig

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class DataDisposable<T> internal constructor(private val modelLiveData: ModelLiveData<T>?) :
    Observer<T>, Disposable {
    private val TAG = DataDisposable::class.java.simpleName
    private var disposable: Disposable? = null

    init {
        if (modelLiveData == null) {
            throw RuntimeException("modelLiveData can't be null!")
        }
    }

    override fun onSubscribe(disposable: Disposable) {
        this.disposable = disposable

        val liveDataWrapper = ModelLiveData.LiveDataWrapper<T>()
            .dataStart()

        this.modelLiveData!!.setValue(liveDataWrapper)
    }

    override fun onNext(basicBean: T) {
        // Log.d(TAG, "-----onNext()-----");
        var liveDataWrapper: ModelLiveData.LiveDataWrapper<*>

        liveDataWrapper = ModelLiveData.LiveDataWrapper<T>().dataStop()

        this.modelLiveData!!.setValue(liveDataWrapper)

        liveDataWrapper = ModelLiveData.LiveDataWrapper<T>()
            .dataSuccess(basicBean)

        this.modelLiveData.setValue(liveDataWrapper)
    }

    override fun onError(throwable: Throwable) {
        Log.d(TAG, "-----onError()-----")
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace()
        }

        var liveDataWrapper: ModelLiveData.LiveDataWrapper<*>

        liveDataWrapper = ModelLiveData.LiveDataWrapper<T>().dataStop()

        this.modelLiveData!!.setValue(liveDataWrapper)

        liveDataWrapper = ModelLiveData.LiveDataWrapper<T>()
            .dataError(throwable)

        this.modelLiveData.setValue(liveDataWrapper)
    }

    override fun onComplete() {
        // NO-OP
    }

    override fun dispose() {
        if (this.disposable != null) {
            this.disposable!!.dispose()
        }
    }

    override fun isDisposed(): Boolean {
        return this.disposable != null && this.disposable!!.isDisposed
    }
}
