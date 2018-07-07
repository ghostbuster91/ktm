package io.ghostbuster91.ktm

import com.github.ajalt.clikt.core.subcommands
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ghostbuster91.ktm.commands.Info
import io.ghostbuster91.ktm.components.jitpack.JitPackApi
import io.ghostbuster91.ktm.identifier.artifact.ArtifactSolverDispatcher
import io.ghostbuster91.ktm.identifier.artifact.SimpleArtifactResolver
import io.ghostbuster91.ktm.utils.TestCommand
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

class InfoCommandTest {

    @Before
    fun setUp() {
        logger = mock()
    }

    @Test
    fun shouldReturnResponseFromJitPackIfArtifactWasResolved() {
        val jitPackApi = mock<JitPackApi>()
        val resource = readJsonFromFile("jitPack/ktmInfo.json")
        whenever(jitPackApi.builds(any(), any())).thenReturn(Observable.just(resource))
        infoCommand(jitPackApi, "com.github.ghostbuster91:ktm")
        verify(logger).info("8fb07d78d4 --> Error\ne19240a0fb --> ok")
    }

    private fun readJsonFromFile(fileName: String) = javaClass.classLoader.getResource(fileName)
            .readText()
            .let {
                Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                        .adapter<Map<String, Any>>(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
                        .fromJson(it)
            }

    private fun infoCommand(jitPackApi: JitPackApi, artifactName: String) {
        TestCommand()
                .subcommands(Info(jitPackApi, artifactResolver = ArtifactSolverDispatcher(listOf(SimpleArtifactResolver()))))
                .main(arrayOf("info", artifactName))
    }
}