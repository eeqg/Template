package com.wp.app.resource.basic.net

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.wp.app.resource.basic.BasicViewModel

class ModelLiveData<T> : LiveData<ModelLiveData.LiveDataWrapper<T>>(),
    Observer<ModelLiveData.LiveDataWrapper<T>> {
    private var dataObserver: DataObserver<T>? = null

    fun observe(
        lifecycleOwner: LifecycleOwner,
        dataObserver: DataObserver<T>
    ) {
        // if (this.dataObserver != null) {
        // 	throw new RuntimeException("only can observe one!");
        // }
        this.dataObserver = dataObserver
        super.observe(lifecycleOwner, this)
    }

    fun observeForever(dataObserver: DataObserver<T>) {
        if (this.dataObserver != null) {
            throw RuntimeException("only can observe one!")
        }
        this.dataObserver = dataObserver
        super.observeForever(this)
    }

    public override fun postValue(value: LiveDataWrapper<T>) {
        super.postValue(value)
    }

    public override fun setValue(value: LiveDataWrapper<T>) {
        super.setValue(value)
    }

    override fun onChanged(liveDataWrapper: LiveDataWrapper<T>?) {
        if (liveDataWrapper == null) {
            this.dataObserver?.dataError(null!!)
        } else if (liveDataWrapper.isDataStart) {
            this.dataObserver?.dataStart()
        } else if (liveDataWrapper.isDataStop) {
            this.dataObserver?.dataStop()
        } else if (liveDataWrapper.isDataSuccess) {
            this.dataObserver?.dataSuccess(liveDataWrapper.basicBean!!)
        } else if (liveDataWrapper.isDataError) {
            this.dataObserver?.dataError(liveDataWrapper.throwable)
        }
    }

    @Deprecated("")
    fun dispose(): DataDisposable<T> {
        return DataDisposable(this)
    }

    fun dispose(viewModel: BasicViewModel): DataDisposable<T> {
        val dataDisposable = DataDisposable(this)
        viewModel.registerDisposable(dataDisposable)
        return dataDisposable
    }

    class LiveDataWrapper<T> {

        var taskStatus: Int = 0
        var basicBean: T? = null
        var throwable: Throwable? = null

        val isDataStart: Boolean
            get() = this.taskStatus == DATA_START

        val isDataStop: Boolean
            get() = this.taskStatus == DATA_STOP

        val isDataSuccess: Boolean
            get() = this.taskStatus == DATA_SUCCESS

        val isDataError: Boolean
            get() = this.taskStatus == DATA_ERROR

        fun dataStart(): LiveDataWrapper<T> {
            this.taskStatus = DATA_START
            return this
        }

        fun dataStop(): LiveDataWrapper<T> {
            this.taskStatus = DATA_STOP
            return this
        }

        fun dataSuccess(basicBean: T): LiveDataWrapper<T> {
            // Log.d(TAG, "-----dataSuccess()-----");
            this.taskStatus = DATA_SUCCESS
            this.basicBean = basicBean
            return this
        }

        fun dataError(throwable: Throwable): LiveDataWrapper<T> {
            this.taskStatus = DATA_ERROR
            this.throwable = throwable
            return this
        }

        companion object {
            private val DATA_START = 1
            private val DATA_STOP = 2
            private val DATA_SUCCESS = 3
            private val DATA_ERROR = 4
        }
    }

    companion object {
        private val TAG = ModelLiveData::class.java.simpleName
    }
}
