package io.ghostbuster91.ktm

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.pair
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.options.versionOption
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ghostbuster91.ktm.identifier.*
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit

typealias GetHomeDir = () -> File

val logger: Logger = LineWrappingLogger()
private val jitPackApi = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
        .baseUrl("https://jitpack.io/api/")
        .build()
        .create(JitPackApi::class.java)
private val directoryManager = KtmDirectoryManager({ File(System.getProperty("user.home")) })
private val aliasController = AliasFileRepository(directoryManager)
private val identifierSolver = VersionSolverDispatcher(listOf(LatestVersionFetchingIdentifierResolver(jitPackApi)),
        IdentifierSolverDispatcher(listOf(SearchingIdentifierResolver(jitPackApi), AliasIdentifierResolver(aliasController), SimpleIdentifierResolver())))

fun main(args: Array<String>) {
    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
    KTM().subcommands(Install(), Aliases(), Info(), Search(), Details(), Use()).main(args)
}

class KTM : NoRunCliktCommand() {
    init {
        versionOption(Build.getVersion())
    }
}

class Install : CliktCommand() {
    private val identifier by argument()
    private val version by option()

    override fun run() {
        val parsed = Identifier.Unparsed(identifier).let { identifierSolver.resolverVersionedIdentifier(it, version) }
        logger.info("Installing $identifier")
        val jitPack = JitPackImpl(createWaitingIndicator())
        executeInstallCommand(parsed, jitPack, directoryManager)
        logger.info("Done")
    }
}

class Use : CliktCommand() {
    private val identifier by argument()
    private val version by option()

    override fun run() {
        val parsed = Identifier.Unparsed(identifier).let { identifierSolver.resolverVersionedIdentifier(it, version) }
        require(directoryManager.getLibraryDir(parsed).exists(), { "Library not found. Use \"ktm install $parsed\" to install it first." })
        val binary = directoryManager.getBinary(parsed)
        directoryManager.linkToBinary(parsed, binary)
        logger.info("Done")
    }
}

class Info : CliktCommand() {
    private val name by argument()

    override fun run() {
        TermUi.echo(URL("https://jitpack.io/api/builds/$name").readText())
    }
}

class Search : CliktCommand() {
    private val query by argument()

    override fun run() {
        TermUi.echo(URL("https://jitpack.io/api/search?q=$query").readText())
    }
}

class Details : CliktCommand() {
    private val identifier by argument()
    private val version by option()
    override fun run() {
        val parsed = Identifier.Unparsed(identifier).let { identifierSolver.resolverVersionedIdentifier(it, version) }
        TermUi.echo(URL("https://jitpack.io/api/builds/${parsed.name}/${parsed.shortVersion}").readText())
    }
}

class Aliases : CliktCommand() {
    private val artifactRegex = "([\\w.]+):([\\w.]+)".toRegex()
    private val aliasRegex = "(\\w)".toRegex()
    private val add by option().pair().validate { (alias, artifact) -> (alias.matches(aliasRegex) && artifact.matches(artifactRegex)) || fail("Wrong input!") }

    override fun run() {
        if (add != null) {
            aliasController.addAlias(add!!.first, add!!.second)
        } else {
            aliasController.getAliases().forEach { TermUi.echo(it) }
        }
    }
}


fun createWaitingIndicator() = Observable.interval(100, TimeUnit.MILLISECONDS)
        .doOnNext { logger.append(".") }
        .doOnDispose { logger.info("") }
