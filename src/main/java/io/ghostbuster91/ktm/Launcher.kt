package io.ghostbuster91.ktm

import com.xenomachina.argparser.ArgParser
import io.reactivex.Observable
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class ParsedArgs(parser: ArgParser) {
    val name by parser.positional("name of the repository")
    val version by parser.positional("version")
}

val logger: Logger = LineWrappingLogger()

fun main(args: Array<String>) {
    ArgParser(args)
            .parseInto(::ParsedArgs)
            .run {
                logger.info("Installing $name $version")
                val homeDirProvider = { File(System.getProperty("user.home")) }
                val downloaderReal = JitPackImpl(Observable.interval(100, TimeUnit.MILLISECONDS).doOnNext { logger.append(".") })
                executeInstallCommand(name, version, downloaderReal, homeDirProvider)
            }
}

interface Logger : HttpLoggingInterceptor.Logger {
    fun error(msg: String, e: Throwable)
    override fun log(msg: String)
    fun append(msg: String)
    fun info(msg: String)
}

typealias GetHomeDir = () -> File

