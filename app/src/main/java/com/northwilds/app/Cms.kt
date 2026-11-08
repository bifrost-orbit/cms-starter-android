package com.northwilds.app

import com.bifrostorbit.cms.CmsOrbitClient
import okhttp3.OkHttpClient

object Cms {
    val client = CmsOrbitClient(BuildConfig.CMS_PUBLIC_KEY) {
        baseUrl = BuildConfig.CMS_API_URL
        defaultRegion = BuildConfig.CMS_REGION
        if (BuildConfig.DRAFTS_TOKEN.isNotEmpty()) {
            okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    chain.proceed(chain.request().newBuilder().header("X-Drafts-Token", BuildConfig.DRAFTS_TOKEN).build())
                }
                .build()
        }
    }
}
