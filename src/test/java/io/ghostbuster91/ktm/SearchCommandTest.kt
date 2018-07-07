package io.ghostbuster91.ktm

import com.github.ajalt.clikt.core.subcommands
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ghostbuster91.ktm.commands.Search
import io.ghostbuster91.ktm.components.jitpack.JitPackApi
import io.ghostbuster91.ktm.utils.TestCommand
import io.ghostbuster91.ktm.utils.readJsonFromFile
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

class SearchCommandTest {

    @Before
    fun setUp() {
        logger = mock()
    }

    @Test
    fun shouldReturnResponseFromJitPackIfArtifactWasResolved() {
        val jitPackApi = mock<JitPackApi>()
        val resource = readJsonFromFile("jitPack/searchResult.json").parseToSearchResult()
        whenever(jitPackApi.search(any())).thenReturn(Observable.just(resource))
        TestCommand().subcommands(Search(jitPackApi)).main(arrayOf("search", "com.github.ghostbuster91"))
        verify(logger).info(
                "com.github.ghostbuster91:ktm --> [0.0.5, 0.0.4, 0.0.2]\n" +
                "com.github.ghostbuster91:solidity-collision-checker --> [1.0.0]"
        )
    }
}

private fun String.parseToSearchResult(): Map<String, List<String>>? {
    val type = Types.newParameterizedType(List::class.java, String::class.java)
    return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            .adapter<Map<String, List<String>>>(Types.newParameterizedType(Map::class.java, String::class.java, type))
            .fromJson(this)
}
