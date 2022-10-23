package iot.empiaurhouse.bayardte.utils

import okhttp3.Interceptor
import okhttp3.Response

class JSONHeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain) : Response {
        val request = chain.request().newBuilder()
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}