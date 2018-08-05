package io.ghostbuster91.ktm

import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption
import io.ghostbuster91.ktm.commands.*
import io.ghostbuster91.ktm.components.KtmDirectoryManager
import io.ghostbuster91.ktm.components.LineWrappingLogger
import io.ghostbuster91.ktm.components.TarFileDownloader
import io.ghostbuster91.ktm.components.jitpack.JitPackArtifactToLinkTranslator
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import io.ghostbuster91.ktm.identifier.artifact.*
import io.ghostbuster91.ktm.identifier.version.DefaultVersionResolver
import io.ghostbuster91.ktm.identifier.version.LatestVersionFetchingIdentifierResolver
import io.ghostbuster91.ktm.identifier.version.SimpleVersionResolver
import io.ghostbuster91.ktm.identifier.version.VersionSolverDispatcher
import io.ghostbuster91.ktm.utils.NullPrintStream
import io.reactivex.Observable
import jline.internal.Log
import java.io.File
import java.util.concurrent.TimeUnit

typealias GetHomeDir = () -> File

var logger: Logger = LineWrappingLogger()

private val directoryManager = KtmDirectoryManager { File(System.getProperty("user.home")) }

private val aliasRepository = AliasFileRepository(directoryManager)

private val artifactSolverDispatcher = ArtifactSolverDispatcher(listOf(AliasArtifactResolver(aliasRepository), SearchingArtifactResolver {
    { jitPackApi.search(it).blockingFirst() }.withWaiter()
}, SimpleArtifactResolver()))

private val identifierSolver = IdentifierResolver(artifactSolverDispatcher, versionSolverDispatcher())

private val jitPackArtifactToLinkTranslator = JitPackArtifactToLinkTranslator { g, a, v ->
    { buildApi.getBuildLog(g, a, v).blockingFirst() }.withWaiter()
}

private val tarFileDownloader = TarFileDownloader(createWaitingIndicator())

fun main(args: Array<String>) {
    Log.setOutput(NullPrintStream())
    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
    KTM().subcommands(
            Install(directoryManager, jitPackArtifactToLinkTranslator, tarFileDownloader, identifierSolver),
            Aliases(aliasRepository),
            Info(jitPackApi, artifactSolverDispatcher),
            Search(jitPackApi),
            Use(directoryManager, identifierSolver),
            io.ghostbuster91.ktm.commands.List(directoryManager)
    ).main(args)
}

private class KTM : NoRunCliktCommand() {

    init {
        versionOption(Build.getVersion())
    }
}
private fun <T> (() -> T).withWaiter(): T {
    val waiter = createWaitingIndicator().subscribe()
    return invoke().also { waiter.dispose() }
}

private fun createWaitingIndicator(): Observable<out Any> = Observable.interval(100, TimeUnit.MILLISECONDS)
        .doOnNext { logger.append(".") }
        .doOnDispose { logger.info("") }

private fun versionSolverDispatcher(): VersionSolverDispatcher {
    return VersionSolverDispatcher(listOf(SimpleVersionResolver(),
            LatestVersionFetchingIdentifierResolver { g, a ->
                { jitPackApi.latestRelease(g, a).blockingFirst() }.withWaiter()
            }, DefaultVersionResolver()))
}
