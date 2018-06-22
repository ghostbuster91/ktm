package io.ghostbuster91.ktm

import io.ghostbuster91.ktm.identifier.VersionedIdentifier
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.net.URL

interface JitPack {

    fun fetchBuildLog(identifier: VersionedIdentifier.Parsed): String

    fun getFileUrl(fileName: String): String
}

class JitPackImpl(private val waitingIndicator: Observable<Long>) : JitPack {

    override fun fetchBuildLog(identifier: VersionedIdentifier.Parsed): String {
        val waiter = waitingIndicator.subscribe()
        return URL("$jitPackUrl/${identifier.groupId.replace(".", "/")}/${identifier.artifactId}/${identifier.shortVersion}/build.log").readText().also { waiter.dispose() }
    }

    override fun getFileUrl(fileName: String): String {
        return "$jitPackUrl/$fileName"
    }

    companion object {
        private const val jitPackUrl = "https://jitpack.io"
    }
}

interface JitPackApi {
    //TODO: dynamic parsing
    @GET("/builds/{groupId}/{artifactId}")
    fun builds(@Path("groupId") groupId: String, @Path("artifactId") artifactId: String): Observable<String>

    @GET("/search")
    fun search(@Query("q") query: String): Observable<List<SearchResult>>

    @GET("/builds/{groupId}/{artifactId}/{version}")
    fun details(@Path("groupId") groupId: String, @Path("artifactId") artifactId: String, @Path("version") version: String): Observable<DetailsResults>

    @GET("/builds/{groupId}/{artifactId}/latestOk")
    fun latestRelease(@Path("groupId") groupId: String, @Path("artifactId") artifactId: String): Observable<VersionResponse>

    data class VersionResponse(val version: String?)

    data class DetailsResults(val status: String,
                              val message: String,
                              val time: Long,
                              val isTag: Boolean,
                              val commit: String,
                              val isPrivate: Boolean)

}

typealias SearchResult = Pair<String, List<String>>