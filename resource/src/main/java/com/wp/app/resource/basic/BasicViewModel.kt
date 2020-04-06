package com.wp.app.resource.basic

import androidx.lifecycle.ViewModel

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by wp on 2019/3/23.
 */
open class BasicViewModel : ViewModel() {
    private val TAG = javaClass.simpleName
    private var mCompositeDisposable: CompositeDisposable? = null

    fun registerDisposable(d: Disposable) {
        if (this.mCompositeDisposable == null || this.mCompositeDisposable!!.isDisposed) {
            this.mCompositeDisposable = CompositeDisposable()
        }
        this.mCompositeDisposable!!.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable!!.dispose()
        }
    }
}
