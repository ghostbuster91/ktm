package io.ghostbuster91.ktm

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.pair
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.options.versionOption
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ghostbuster91.ktm.components.*
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import io.ghostbuster91.ktm.identifier.artifact.AliasArtifactResolver
import io.ghostbuster91.ktm.identifier.artifact.AliasFileRepository
import io.ghostbuster91.ktm.identifier.artifact.SearchingArtifactResolver
import io.ghostbuster91.ktm.identifier.artifact.SimpleArtifactResolver
import io.ghostbuster91.ktm.identifier.version.DefaultVersionResolver
import io.ghostbuster91.ktm.identifier.version.LatestVersionFetchingIdentifierResolver
import io.ghostbuster91.ktm.identifier.version.SimpleVersionResolver
import io.ghostbuster91.ktm.utils.NullPrintStream
import io.reactivex.Observable
import jline.internal.Log
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit

typealias GetHomeDir = () -> File

val logger: Logger = LineWrappingLogger()
private val retrofit = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
        .baseUrl("https://jitpack.io/")
        .build()
private val jitPackApi = retrofit.create(JitPackApi::class.java)
private val buildApi = retrofit.create(BuildLogApi::class.java)
private val directoryManager = KtmDirectoryManager({ File(System.getProperty("user.home")) })
private val aliasRepository = AliasFileRepository(directoryManager)
private val identifierSolver = IdentifierResolver(
        artifactResolvers = listOf(AliasArtifactResolver(aliasRepository), SearchingArtifactResolver({
            { jitPackApi.search(it).blockingFirst() }.withWaiter()
        }), SimpleArtifactResolver()),
        versionResolvers = listOf(SimpleVersionResolver(), LatestVersionFetchingIdentifierResolver(
                { g, a -> { jitPackApi.latestRelease(g, a).blockingFirst() }.withWaiter() }), DefaultVersionResolver()))
private val jitPackArtifactToLinkTranslator = JitPackArtifactToLinkTranslator({ g, a, v ->
    { buildApi.getBuildLog(g, a, v).blockingFirst() }.withWaiter()
})
private val tarFileDownloader = TarFileDownloader(createWaitingIndicator())

fun main(args: Array<String>) {
    Log.setOutput(NullPrintStream())
    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
    KTM().subcommands(Install(), Aliases(), Info(), Search(), Details(), Use()).main(args)
}

private class KTM : NoRunCliktCommand() {
    init {
        versionOption(Build.getVersion())
    }
}

private class Install : CliktCommand() {
    private val identifier by argument().convert { Identifier.Unparsed(it) }
    private val version by option()

    override fun run() {
        logger.info("Installing $identifier")
        installer(identifierSolver, directoryManager, jitPackArtifactToLinkTranslator, tarFileDownloader)(identifier, version)
        logger.info("Done")
    }
}

private class Use : CliktCommand() {
    private val identifier by argument()
    private val version by option()

    override fun run() {
        val parsed = Identifier.Unparsed(identifier)
                .let { identifierSolver.resolve(it, version) }
        require(directoryManager.getLibraryDir(parsed).exists(), { "Library not found. Use \"ktm install $parsed\" to install it first." })
        val binary = directoryManager.getBinary(parsed)
        directoryManager.linkToBinary(parsed, binary)
        logger.info("Done")
    }
}

private class Info : CliktCommand() {
    private val name by argument()

    override fun run() {
        TermUi.echo(URL("https://jitpack.io/api/builds/$name").readText())
    }
}

private class Search : CliktCommand() {
    private val query by argument()

    override fun run() {
        TermUi.echo(URL("https://jitpack.io/api/search?q=$query").readText())
    }
}

private class Details : CliktCommand() {
    private val identifier by argument()
    private val version by option()
    override fun run() {
        val parsed = Identifier.Unparsed(identifier)
                .let { identifierSolver.resolve(it, version) }
        TermUi.echo(URL("https://jitpack.io/api/builds/${parsed.name}/${parsed.shortVersion}").readText())
    }
}

private class Aliases : CliktCommand() {
    private val artifactRegex = "([\\w.]+):([\\w.]+)".toRegex()
    private val aliasRegex = "(\\w)".toRegex()
    private val add by option().pair().validate { (alias, artifact) -> (alias.matches(aliasRegex) && artifact.matches(artifactRegex)) || fail("Wrong input!") }

    override fun run() {
        if (add != null) {
            aliasRepository.addAlias(add!!.first, add!!.second)
        } else {
            aliasRepository.getAliases().forEach { TermUi.echo(it) }
        }
    }
}

private fun <T> (() -> T).withWaiter(): T {
    val waiter = createWaitingIndicator().subscribe()
    return invoke().also { waiter.dispose() }
}

private fun createWaitingIndicator(): Observable<out Any> = Observable.interval(100, TimeUnit.MILLISECONDS)
        .doOnNext { logger.append(".") }
        .doOnDispose { logger.info("") }
