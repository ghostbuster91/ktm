package io.ghostbuster91.ktm

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.validate
import io.reactivex.Observable
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit

private val directoryManager = KtmDirectoryManager({ File(System.getProperty("user.home")) })

class KTM : NoRunCliktCommand()

class Install : CliktCommand() {
    private val identifier by argument().convert { Identifier.parse(it) }

    override fun run() {
        logger.info("Installing $identifier")
        val jitPack = JitPackImpl(createWaitingIndicator())
        executeInstallCommand(identifier, jitPack, directoryManager)
        logger.info("Done")
    }
}

class Use : CliktCommand() {
    private val identifier by argument().convert { Identifier.parse(it) }.validate {
        require(directoryManager.getLibraryDir(it).exists(), { "Library not found. Use \"ktm install $it\" to install it." })
    }

    override fun run() {
        val binary = directoryManager.getBinary(identifier)
        directoryManager.linkToBinary(identifier, binary)
        logger.info("Done")
    }
}

class Version : CliktCommand() {
    override fun run() {
        TermUi.echo(Build.getVersion())
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
    private val identifier by argument().convert { Identifier.parse(it) }

    override fun run() {
        TermUi.echo(URL("https://jitpack.io/api/builds/${identifier.name}/${identifier.shortVersion}").readText())
    }
}

val logger: Logger = LineWrappingLogger()

fun main(args: Array<String>) {
    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
    KTM().subcommands(Install(), Version(), Info(), Search(), Details(), Use()).main(args)
}

fun createWaitingIndicator() = Observable.interval(100, TimeUnit.MILLISECONDS)
        .doOnNext { logger.append(".") }
        .doOnDispose { logger.info("") }

interface Logger : HttpLoggingInterceptor.Logger {
    fun error(msg: String, e: Throwable)
    override fun log(msg: String)
    fun append(msg: String)
    fun info(msg: String)
}

typealias GetHomeDir = () -> File

