package io.ghostbuster91.ktm

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JitPackApi {
    //TODO: dynamic parsing
    @GET("api/builds/{groupId}/{artifactId}")
    fun builds(@Path("groupId") groupId: String, @Path("artifactId") artifactId: String): Observable<String>

    @GET("api/search")
    fun search(@Query("q") query: String): Observable<Map<String, List<String>>>

    @GET("api/builds/{groupId}/{artifactId}/{version}")
    fun details(@Path("groupId") groupId: String, @Path("artifactId") artifactId: String, @Path("version") version: String): Observable<DetailsResults>

    @GET("api/builds/{groupId}/{artifactId}/latestOk")
    fun latestRelease(@Path("groupId") groupId: String, @Path("artifactId") artifactId: String): Observable<VersionResponse>

    data class VersionResponse(val version: String?)

    data class DetailsResults(val status: String,
                              val message: String,
                              val time: Long,
                              val isTag: Boolean,
                              val commit: String,
                              val isPrivate: Boolean)
}