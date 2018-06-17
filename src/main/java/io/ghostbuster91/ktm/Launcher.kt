package io.ghostbuster91.ktm

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import io.reactivex.Observable
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class Tool : NoRunCliktCommand()

class Install : CliktCommand() {
    private val name by argument()
    private val version by argument()

    override fun run() {
        logger.info("Installing $name $version")
        val homeDirProvider = { File(System.getProperty("user.home")) }
        val jitPack = JitPackImpl(createWaitingIndicator())
        executeInstallCommand(name, version, jitPack, homeDirProvider)
    }
}

class Version : CliktCommand() {
    override fun run() {
        TermUi.echo(Build.getVersion())
    }
}

val logger: Logger = LineWrappingLogger()

fun main(args: Array<String>) {
    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
    Tool().subcommands(Install(), Version()).main(args)
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

