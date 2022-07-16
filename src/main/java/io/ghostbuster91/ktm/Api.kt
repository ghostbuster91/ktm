package io.ghostbuster91.ktm

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ghostbuster91.ktm.components.jitpack.BuildLogApi
import io.ghostbuster91.ktm.components.jitpack.JitPackApi
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

private val retrofit = Retrofit.Builder()
        .client(okHttpClient())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
        .baseUrl("https://jitpack.io/")
        .build()

val jitPackApi = retrofit.create(JitPackApi::class.java)
val buildApi = retrofit.create(BuildLogApi::class.java)

private fun okHttpClient() = OkHttpClient.Builder()
        .certificatePinner(certificatePinner())
        .readTimeout(5, TimeUnit.MINUTES)
        .build()

fun certificatePinner(): CertificatePinner {
 return CertificatePinner.Builder().add("jitpack.io"," sha256/tkGMAXB+eaYdk6EtfZzzDsBsWkMehGX3myHGTd8dBEg=").build()
}
