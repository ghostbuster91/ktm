package io.ghostbuster91.ktm

import com.xenomachina.argparser.ArgParser
import io.reactivex.Observable
import jline.TerminalFactory
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class ParsedArgs(parser: ArgParser) {
    val name by parser.positional("name of the repository")
    val version by parser.positional("version")
}

val logger: Logger = object : Logger {

    var charsInLine = 0

    override fun error(msg: String, e: Throwable) {
        println("$msg : $e")
    }

    override fun log(msg: String) {
//        println(msg) // TODO write to file
    }

    override fun append(msg: String) {
        charsInLine += msg.length
        if (charsInLine > TerminalFactory.get().width) {
            println()
            charsInLine = msg.length
        }
        print(msg)
    }

    override fun info(msg: String) {
        println(msg)
    }
}

fun main(args: Array<String>) {
    ArgParser(args)
            .parseInto(::ParsedArgs)
            .run {
                logger.info("Installing $name $version")
                val updateProgressBar: (Int) -> Unit = { println(it) }
                val homeDirProvider = { File(System.getProperty("user.home")) }
                executeInstallCommand(name, version, RealJitPackDownloader(logger), homeDirProvider, updateProgressBar)
            }
}

fun executeInstallCommand(name: String, version: String, downloaderReal: JitPackDownloader, getHomeDir: GetHomeDir, updateProgressBar: (Int) -> Unit) {
    val libraryDir = getLibraryDir(getHomeDir, name, version)
    if (!libraryDir.exists()) {
        libraryDir.mkdirs()
        logger.info("Library dir created at ${libraryDir.path}")
    }
    val versionedLib = libraryDir.createChild(version)
    if (versionedLib.exists()) {
        logger.info("$name already installed with version $version")
        logger.info("Skipping")
        return
    } else {
        versionedLib.mkdirs()
    }
    try {
        downloadArtifacts(name, version, libraryDir, downloaderReal, updateProgressBar)
        logger.info("Done")
    } catch (ex: Exception) {
        logger.error("Error during downloading library", ex)
        libraryDir.deleteRecursively()
    }
}

private fun getLibraryDir(getHomeDir: GetHomeDir, name: String, version: String) =
        getHomeDir()
                .createChild(".ktm")
                .createChild("modules")
                .createChild(name.replace("/", "."))


private fun downloadArtifacts(
        name: String,
        version: String,
        versionedLibDir: File,
        jitPackDownloader: JitPackDownloader,
        updateProgressBar: (Int) -> Unit
) {
    logger.append("Fetching build log from JitPack...")
    val waitingIndicator = Observable.interval(100, TimeUnit.MILLISECONDS)
            .doOnNext { logger.append(".") }
    val buildLog = jitPackDownloader.fetchBuildLog(name, version, waitingIndicator)
    val files = buildLog.substringAfterLast("Files:").split("\n").filter { it.isNotBlank() }.drop(1)
    logger.info("")
    logger.info("Found ${files.size} files")
    files.forEachIndexed { index, file ->
        println("Downloading file ${index + 1}/${files.size}: ${file.substringAfterLast("/")}")
        downloadArtifact(versionedLibDir, file, version, jitPackDownloader, name, updateProgressBar)
    }
}

private fun downloadArtifact(
        versionedLibDir: File,
        file: String,
        version: String,
        jitPackDownloader: JitPackDownloader,
        name: String,
        updateProgressBar: (Int) -> Unit
) {
    val destination = versionedLibDir.createChild(file.substringAfterLast("$version/"))
    jitPackDownloader.downloadFile(name, version, file, destination, updateProgressBar)
}

interface Logger : HttpLoggingInterceptor.Logger {
    fun error(msg: String, e: Throwable)
    override fun log(msg: String)
    fun append(msg: String)
    fun info(msg: String)
}
typealias GetHomeDir = () -> File

private fun File.createChild(childName: String) = File(this, childName)
