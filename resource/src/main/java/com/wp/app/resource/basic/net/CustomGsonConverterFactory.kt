package com.wp.app.resource.basic.net

import android.text.TextUtils
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.wp.app.resource.utils.LogUtils
import com.wp.app.resource.BuildConfig
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.*
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * Created by wp on 2018/4/11.
 */

class CustomGsonConverterFactory private constructor(private val gson: Gson?) :
    Converter.Factory() {
    private val TAG = CustomGsonConverterFactory::class.java.simpleName

    init {
        if (gson == null) throw NullPointerException("gson == null")
    }

    override fun requestBodyConverter(
        type: Type?,
        parameterAnnotations: Array<Annotation>?,
        methodAnnotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<*, RequestBody>? {
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
    }

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        val typeToken = TypeToken.get(type!!)
        val adapter = gson?.getAdapter(TypeToken.get(type))
        if (typeToken.rawType == BasicBean::class.java) {
            return BaseResponseBodyConverter<Any>()
        }
        if (BasicBean::class.java.isAssignableFrom(typeToken.rawType)) {
            if (ArrayBean::class.java.isAssignableFrom(typeToken.rawType)) {
                return ArrayResponseBodyConverter<ArrayBean>(type)
            } else {
                return ObjectResponseBodyConverter<BasicBean>(type)
            }
        }
        return this.OtherResponseBodyConverter(gson!!, adapter!!)
    }

    private inner class BaseResponseBodyConverter<T> : Converter<ResponseBody, BasicBean> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): BasicBean {
            val basicBean = gson?.fromJson(value.charStream(), BaseValueBean::class.java)
            // if (BasicApp.isDebug()) {
            // 	LogUtils.json(value.string());
            // 	LogUtils.i(TAG, "code = " + basicBean.statusCode
            // 			+ ", message = " + basicBean.statusMessage
            // 			+ ", data = " + basicBean.resultData);
            // }

            val resultBean = BasicBean()
            resultBean.statusInfo?.statusCode = basicBean?.statusCode!!
            resultBean.statusInfo?.statusMessage = basicBean?.statusMessage
            resultBean.resultData = basicBean?.resultData

            return resultBean
        }
    }

    private inner class ObjectResponseBodyConverter<T : BasicBean> internal constructor(private val type: Type) :
        Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T? {
            // BaseBean basicBean = gson.fromJson(value.charStream(), BaseBean.class);
            // if (BasicApp.isDebug()) {
            // 	LogUtils.i(TAG, "code = " + basicBean.statusCode
            // 			+ ", message = " + basicBean.statusMessage
            // 			+ ", data = " + basicBean.resultData.toString());
            // }
            var resultBean: T? = null
            val parse = JsonParser()
            try {
                val json = parse.parse(value.string()) as JsonObject
                val jsonElement = json.get("data")
//                LogUtils.d("-----jsonElement = $jsonElement");
                val jsonStr: String
                if (jsonElement != null && jsonElement.isJsonObject) {
                    jsonStr = jsonElement.asJsonObject.toString()
                } else {
                    jsonStr = "{}"
                }
//                LogUtils.d("-----jsonStr = $jsonStr");
                resultBean = gson?.fromJson<T>(jsonStr, this.type)
                resultBean!!.statusInfo!!.statusCode = json.get("respCode").asInt
                resultBean.statusInfo!!.statusMessage = json.get("respMsg").asString
                resultBean.resultData = jsonElement?.toString() ?: ""

//                 LogUtils.i(TAG, "statusCode = " + resultBean.statusInfo?.statusCode
//                 		+ ", statusMessage = " + resultBean.statusInfo?.statusMessage
//                 		+ ", resultData = " + resultBean.resultData)
            } catch (e: JsonIOException) {
                e.printStackTrace()
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return resultBean
        }
    }

    private inner class ArrayResponseBodyConverter<T : ArrayBean> internal constructor(private val type: Type) :
        Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T? {
            // BaseListBean basicBean = gson.fromJson(value.charStream(), BaseListBean.class);
            // if (BasicApp.isDebug()) {
            // 	// LogUtils.json(value.string());
            // 	LogUtils.i(TAG, "code = " + basicBean.statusCode
            // 			+ ", message = " + basicBean.statusMessage
            // 			+ ", data = " + basicBean.resultData);
            // }
            // T resultBean = gson.fromJson(
            // 		String.format("{\"result\":%s}", basicBean.resultData == null ? "[]" : gson.toJson(basicBean.resultData)),
            // 		this.type);
            // resultBean.totalCount = basicBean.totalCount;
            // resultBean.statusInfo.statusCode = basicBean.statusCode;
            // resultBean.statusInfo.statusMessage = basicBean.statusMessage;

            var resultBean: T? = null
            val parse = JsonParser()
            try {
                val json = parse.parse(value.string()) as JsonObject
                val jsonElement = json.get("data")
                var jsonStr = "[]"
                if (jsonElement != null && jsonElement.isJsonArray) {
                    jsonStr = jsonElement.asJsonArray.toString()
                }
                resultBean = gson?.fromJson<T>(String.format("{\"result\":%s}", jsonStr), this.type)
                resultBean!!.statusInfo!!.statusCode = json.get("respCode").asInt
                resultBean.statusInfo!!.statusMessage = json.get("respMsg").asString
                val count = json.get("count")
                resultBean.totalCount = if (count != null && !TextUtils.equals(
                        "null",
                        count.toString()
                    )
                ) count.asInt else 0
                resultBean.resultData = jsonElement?.toString() ?: ""

//                LogUtils.i(
//                    TAG, "statusCode = " + resultBean.statusInfo?.statusCode
//                            + ", statusMessage = " + resultBean.statusInfo?.statusMessage
//                            + ", resultData = " + resultBean.resultData
//                )
            } catch (e: JsonIOException) {
                e.printStackTrace()
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return resultBean
        }
    }

    private inner class OtherResponseBodyConverter<T> internal constructor(
        private val gson: Gson,
        private val adapter: TypeAdapter<T>
    ) : Converter<ResponseBody, T> {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
        private val UTF_8 = Charset.forName("UTF-8")

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val response = value.string()
            // BasicBean baseBean = gson.fromJson(response, BasicBean.class);
            // // if (httpStatus.isCodeInvalid()) {
            // // 	value.close();
            // // 	throw new ApiException(httpStatus.getCode(), httpStatus.getMessage());
            // // }
            if (BuildConfig.DEBUG) {
                LogUtils.json(response)
            }

            val contentType = value.contentType()
            val charset = if (contentType != null) contentType.charset(UTF_8) else UTF_8
            val inputStream = ByteArrayInputStream(response.toByteArray())
            var reader: Reader? = null
            var jsonReader: JsonReader? = null
            if (charset != null) {
                reader = InputStreamReader(inputStream, charset)
                jsonReader = gson.newJsonReader(reader)
            }

            try {
                return adapter.read(jsonReader)
            } finally {
                value.close()
            }
        }
    }

    private class BaseBean {
        @SerializedName("respCode")
        internal var statusCode: Int = 0
        @SerializedName("respMsg")
        internal var statusMessage: String? = null
        @SerializedName("data")
        internal var resultData: JsonObject? = null
    }

    private class BaseListBean {
        @SerializedName("respCode")
        internal var statusCode: Int = 0
        @SerializedName("respMsg")
        internal var statusMessage: String? = null
        @SerializedName("data")
        internal var resultData: JsonArray? = null
        @SerializedName("count")
        internal var totalCount: Int = 0
    }

    private class BaseValueBean {
        @SerializedName("respCode")
        internal var statusCode: Int? = 0
        @SerializedName("respMsg")
        internal var statusMessage: String? = null
        @SerializedName("data")
        internal var resultData: String? = null
    }

    companion object {

        @JvmOverloads
        fun create(gson: Gson = Gson()): CustomGsonConverterFactory {
            return CustomGsonConverterFactory(gson)
        }
    }
}
