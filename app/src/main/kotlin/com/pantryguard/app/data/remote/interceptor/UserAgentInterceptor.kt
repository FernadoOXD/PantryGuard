package com.pantryguard.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(
                "User-Agent",
                "PantryGuardApp - Android - Version 1.0 - (student.project@domain.com)"
            )
            .build()
        return chain.proceed(request)
    }
}
