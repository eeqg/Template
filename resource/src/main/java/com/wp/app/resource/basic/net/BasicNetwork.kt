package com.wp.app.resource.basic.net

import android.util.Log
import com.google.gson.Gson
import com.wp.app.resource.BuildConfig
import com.wp.app.resource.utils.LogUtils
import okhttp3.*
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

open class BasicNetwork(private val baseUrl: String?) {
    private val TAG = javaClass.simpleName
    private val retrofit: Retrofit

    private val LogInterceptor = Interceptor { chain ->
        // Request request = chain.request();
        // long t1 = System.nanoTime();
        // printLog(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
        // Response response = chain.proceed(request);
        // long t2 = System.nanoTime();
        // printLog(String.format(Locale.CHINA, "Received response for %s in %.1fms%n%s",
        // 		response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        // return response;

        val oldRequest = chain.request()
        var newRequestBuild: Request.Builder? = null
        val method = oldRequest.method()
        var postBodyString = ""
        if ("POST" == method) {
            val oldBody = oldRequest.body()
            if (oldBody is FormBody) {
                val formBodyBuilder = FormBody.Builder()
                // formBodyBuilder.add("deviceOs", iCommon.DEVICE_OS);
                // formBodyBuilder.add("appVersion", Utils.instance().getAppVersionName());
                // formBodyBuilder.add("appName", Utils.instance().getAppNameNew());
                newRequestBuild = oldRequest.newBuilder()

                val formBody = formBodyBuilder.build()
                postBodyString = bodyToString(oldRequest.body())
                postBodyString += (if (postBodyString.length > 0) "&" else "") + bodyToString(
                    formBody
                )
                newRequestBuild!!.post(
                    RequestBody.create(
                        MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"),
                        postBodyString
                    )
                )
            } else if (oldBody is MultipartBody) {
                val oldBodyMultipart = oldBody as MultipartBody?
                val oldPartList = oldBodyMultipart!!.parts()
                val builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)
                // RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), iCommon.DEVICE_OS);
                // RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), Utils.instance().getAppNameNew());
                // RequestBody requestBody3 = RequestBody.create(MediaType.parse("text/plain"), Utils.instance().getAppVersionName());
                for (part in oldPartList) {
                    builder.addPart(part)
                    postBodyString += bodyToString(part.body()) + "\n"
                }
                // postBodyString += (bodyToString(requestBody1) + "\n");
                // postBodyString += (bodyToString(requestBody2) + "\n");
                // postBodyString += (bodyToString(requestBody3) + "\n");
                //              builder.addPart(oldBody);  //不能用这个方法，因为不知道oldBody的类型，可能是PartMap过来的，也可能是多个Part过来的，所以需要重新逐个加载进去
                // builder.addPart(requestBody1);
                // builder.addPart(requestBody2);
                // builder.addPart(requestBody3);
                newRequestBuild = oldRequest.newBuilder()
                newRequestBuild!!.post(builder.build())
                Log.e(TAG, "MultipartBody," + oldRequest.url())
            } else {
                newRequestBuild = oldRequest.newBuilder()
            }
        } else {
            // 添加新的参数
            val commonParamsUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())// .addQueryParameter("deviceOs", iCommon.DEVICE_OS)
            // .addQueryParameter("appVersion", Utils.instance().getAppVersionName())
            // .addQueryParameter("appName", Utils.instance().getAppNameNew())
            newRequestBuild = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(commonParamsUrlBuilder.build())
        }
        val newRequest = newRequestBuild!!
            .addHeader("Accept", "application/json")
            .addHeader("Accept-Language", "zh")
            .build()

        val startTime = System.currentTimeMillis()
        val response = chain.proceed(newRequest)
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        val mediaType = response.body()!!.contentType()
        val content = response.body()!!.string()
        val httpStatus = response.code()
        val logSB = StringBuilder()
        logSB.append("-------start:$method|")
        logSB.append("$newRequest\n|")
        logSB.append(
            if (method.equals(
                    "POST",
                    ignoreCase = true
                )
            ) "post参数{$postBodyString}\n|" else ""
        )
        logSB.append("httpCode=$httpStatus;Response:$content\n|")
        logSB.append("----------End:" + duration + "毫秒----------")
        Log.d(TAG, logSB.toString())
        response.newBuilder()
            .body(ResponseBody.create(mediaType, content))
            .build()
    }

    init {
        if (baseUrl == null) {
            throw NullPointerException("networkListener can't be null!")
        }

        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(CustomGsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        this.retrofit = builder.build()
    }

    fun <T> createService(service: Class<T>): T {
        return this.retrofit.create(service)
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request = chain.request()
                val requestUrl = request.url().toString()
                // requestUrl = buildRequestUrl(requestUrl);
                val httpUrl = buildHttpUrl(request.method(), request.url().newBuilder(requestUrl)!!)
//                LogUtils.d("-----$httpUrl")
                request = convertRequest(request, httpUrl)

                convertResponse(chain.proceed(request))
            }
            // .addInterceptor(LogInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 构建请求地址
     *
     * @param method  请求方式
     * @param builder 请求地址构建
     * @return 请求地址
     */
    protected fun buildHttpUrl(method: String, builder: HttpUrl.Builder): HttpUrl {
        return builder.build()
    }

    /**
     * 转换请求实例
     *
     * @param request 请求实例
     * @param httpUrl 请求地址
     * @return 请求实例
     */
    protected fun convertRequest(request: Request, httpUrl: HttpUrl): Request {
        if (request.method() == "GET") {
            return convertGetRequest(request, httpUrl)
        } else if (request.method() == "POST") {
            return convertPostRequest(request, httpUrl)
        }
        return request.newBuilder()
            .url(httpUrl)
            .build()
    }

    /**
     * 转换GET请求实例
     *
     * @param request 请求实例
     * @param httpUrl 请求地址
     * @return 请求实例
     */
    protected open fun convertGetRequest(request: Request, httpUrl: HttpUrl): Request {
        return request.newBuilder()
            .url(httpUrl)
            .headers(buildHeaders(request.body()))
            .build()
    }

    /**
     * 转换POST请求实例
     *
     * @param request 请求实例
     * @param httpUrl 请求地址
     * @return 请求实例
     */
    protected fun convertPostRequest(request: Request, httpUrl: HttpUrl): Request {
        val requestBody = request.body()
        if (requestBody is FormBody) {
            return request.newBuilder()
                .url(httpUrl)
                .headers(buildHeaders(requestBody))
                .method(request.method(), convertBody(requestBody))
                .build()
        } else if (requestBody is MultipartBody) {
            return request.newBuilder()
                .url(httpUrl)
                .headers(buildHeaders(requestBody))
                .method(request.method(), convertBody(requestBody))
                .build()
        }
        return request.newBuilder()
            .url(httpUrl)
            .headers(buildHeaders(requestBody))
            .method(request.method(), convertBody(requestBody))
            .build()
    }

    /**
     * 构建HEADER实例
     *
     * @param requestBody 请求实体
     * @return HEADER实例
     */
    open fun buildHeaders(requestBody: RequestBody?): Headers {
        return Headers.Builder().build()
    }

    /**
     * 转换表单实体
     *
     * @param formBody 表单实体
     * @return 表单实体
     */
    protected open fun convertBody(formBody: FormBody): RequestBody {
        return formBody
    }

    /**
     * 转换多表单实体
     *
     * @param multipartBody 多表单实体
     * @return 多表单实体
     */
    protected open fun convertBody(multipartBody: MultipartBody): RequestBody {
        return multipartBody
    }

    /**
     * 转换请求实体
     *
     * @param requestBody 请求实体
     * @return 请求实体
     */
    protected open fun convertBody(requestBody: RequestBody?): RequestBody? {
        return requestBody
    }

    /**
     * 转换结果实例
     *
     * @param response 结果实例
     * @return 结果实例
     */
    @Throws(IOException::class)
    protected fun convertResponse(response: Response): Response {
        var response = response
        if (BuildConfig.DEBUG) {
            val builder = StringBuilder()
            builder.append("=========== Request ===========")
                .append("\n")
            builder.append(response.request().method())
                .append(" ")
                // .append(response.request().url().encodedPath())
                .append(response.request().url())
            val queryStr = response.request().url().encodedQuery()
            if (queryStr != null) {
                builder.append("?").append(queryStr)
            }
            builder.append(" ")
                .append(response.protocol())
                .append("\n")
            builder.append("Host: ")
                .append(response.request().url().host())
                .append(":")
                .append(response.request().url().port())
                .append("\n")
            val requestHeaders = response.request().headers()
            for (index in 0 until requestHeaders.size()) {
                builder.append(requestHeaders.name(index))
                    .append(": ")
                    .append(requestHeaders.value(index))
                    .append("\n")
            }
            builder.append("\n")

            val requestBody = response.request().body()
            if (requestBody is FormBody) {
                val formBody = requestBody as FormBody?
                for (index in 0 until formBody!!.size()) {
                    builder.append(formBody.name(index))
                        .append("=")
                        .append(formBody.value(index))
                        .append("&")
                }
                if (formBody.size() > 0) {
                    builder.setLength(builder.length - 1)
                }
            } else if (requestBody != null) {
                builder.append(requestBody.toString())
                //print params
                // Buffer buffer = new Buffer();
                // requestBody.writeTo(buffer);
                // Charset charset = Charset.forName("UTF-8");
                // MediaType contentType = requestBody.contentType();
                // if (contentType != null) {
                // 	charset = contentType.charset(UTF_8);
                // }
                // String paramsStr = buffer.readString(charset);
                val paramsStr = bodyToString(requestBody)
                builder.append("\nparams: ").append(paramsStr)
            }

            builder.append("\n")
                .append("=========== Request ===========")

            builder.append("\n")
                .append("=========== Response ===========")
                .append("\n")
            builder.append(response.code())
                .append(" ")
                .append(response.message())
                .append("\n")
            val responseHeaders = response.headers()
            for (index in 0 until responseHeaders.size()) {
                builder.append(responseHeaders.name(index))
                    .append(": ")
                    .append(responseHeaders.value(index))
                    .append("\n")
            }
            builder.append("\n")

            val responseBody = response.body()
            if (responseBody != null) {
                val responseStr = responseBody.string()
                builder.append(responseStr).append("\n")
                response = response.newBuilder()
                    .body(ResponseBody.create(responseBody.contentType(), responseStr))
                    .build()
            }
            builder.append("=========== Response ===========")
            println(builder)
        }
        return response
    }

    protected fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }

    }

    companion object {
        val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
        val UTF_8 = Charset.forName("UTF-8")

        fun mapToRequestBody(params: Any): RequestBody {
            val paramsStr = Gson().toJson(params)
            // if (BasicApp.isDebug()) {
            // 	LogUtils.d("BasicNetwork-----" + paramsStr);
            // }
            return RequestBody.create(MEDIA_TYPE, paramsStr)
        }
    }
}
