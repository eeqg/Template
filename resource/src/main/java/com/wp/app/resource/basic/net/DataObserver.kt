package com.wp.app.resource.basic.net


abstract class DataObserver<T> protected constructor(private val dataListener: DataListener?) {
    private val TAG = DataObserver::class.java.simpleName

    open fun dataStart() {
        if (this.dataListener != null) {
            this.dataListener.dataStart()
        }
    }

    open fun dataStop() {
        if (this.dataListener != null) {
            this.dataListener.dataStop()
        }
    }

    fun dataSuccess(resultBean: T) {
        // Log.d(TAG, "-----dataSuccess()-----");
        if (resultBean is BasicBean) {
            val basicBean = resultBean as BasicBean
            // Log.d(TAG, "-----dataSuccess()-----statusCode = " + basicBean.statusInfo.statusCode);
            if (basicBean.statusInfo!!.isSuccessful) {
                dataResult(resultBean)
                dataStatus(basicBean.statusInfo)
            } else if (basicBean.statusInfo!!.isOther) {
                if (this.dataListener != null) {
                    this.dataListener.dataOther(basicBean.statusInfo)
                }
            } else {
                dataStatus(basicBean.statusInfo)
            }
        } else {
            dataResult(resultBean)
            dataStatus(StatusInfo(StatusInfo.STATUS_SUCCESS))
        }
    }

    fun dataError(throwable: Throwable?) {
        dataStatus(null)
    }

    protected abstract fun dataResult(basicBean: T)

    protected abstract fun dataStatus(statusInfo: StatusInfo?)
}
