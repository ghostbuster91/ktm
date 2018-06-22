package io.ghostbuster91.ktm

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.pair
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.options.versionOption
import io.ghostbuster91.ktm.identifier.*
import io.reactivex.Observable
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit

typealias GetHomeDir = () -> File

val logger: Logger = LineWrappingLogger()
private val directoryManager = KtmDirectoryManager({ File(System.getProperty("user.home")) })
private val aliasController = AliasFileRepository(directoryManager)
private val identifierSolver = IdentifierSolverDispatcher(AliasIdentifierResolver(aliasController), SimpleIdentifierResolver())

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
            .convert { Identifier.Unparsed(it).let { identifierSolver.resolverIdentifier(it) } }

    override fun run() {
        logger.info("Installing $identifier")
        val jitPack = JitPackImpl(createWaitingIndicator())
        executeInstallCommand(identifier, jitPack, directoryManager)
        logger.info("Done")
    }
}

class Use : CliktCommand() {
    private val identifier by argument().convert { Identifier.Unparsed(it).let { identifierSolver.resolverIdentifier(it) } }.validate {
        require(directoryManager.getLibraryDir(it).exists(), { "Library not found. Use \"ktm install $it\" to install it first." })
    }

    override fun run() {
        val binary = directoryManager.getBinary(identifier)
        directoryManager.linkToBinary(identifier, binary)
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
    private val identifier by argument().convert { Identifier.Unparsed(it).let { identifierSolver.resolverIdentifier(it) } }

    override fun run() {
        TermUi.echo(URL("https://jitpack.io/api/builds/${identifier.name}/${identifier.shortVersion}").readText())
    }
}

class Aliases : CliktCommand() {
    private val artifactRegex = "([\\w.]+):([\\w.]+)".toRegex()
    private val aliasRegex = "(\\w)".toRegex()
    private val isAdd by option("--add").pair().validate { (alias, artifact) -> (alias.matches(aliasRegex) && artifact.matches(artifactRegex)) || fail("Wrong input!") }

    override fun run() {
        if (isAdd != null) {
            aliasController.addAlias(isAdd!!.first, isAdd!!.second)
        } else {
            aliasController.getAliases().forEach { TermUi.echo(it) }
        }
    }
}


fun createWaitingIndicator() = Observable.interval(100, TimeUnit.MILLISECONDS)
        .doOnNext { logger.append(".") }
        .doOnDispose { logger.info("") }
