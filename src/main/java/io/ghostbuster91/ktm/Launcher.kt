package io.ghostbuster91.ktm

import com.xenomachina.argparser.ArgParser
import java.io.File
import java.net.URL

class ParsedArgs(parser: ArgParser) {
    val name by parser.positional("name of the repository")
    val version by parser.positional("version")
}

val log: logger = { println(it) }

fun main(args: Array<String>) {
    ArgParser(args)
            .parseInto(::ParsedArgs)
            .run {
                println("Hello, $name! $version")
                val updateProgressBar: (Int) -> Unit = { println(it) }
                val okHttpFileDownloader = OkHttpFileDownloader()
                executeInstallCommand(name, version, okHttpFileDownloader::downloadFile.rcurry(updateProgressBar))
            }
}

fun executeInstallCommand(name: String, version: String, fileDownloader: (String, File) -> Unit) {
    val homeDir = System.getProperty("user.home")
    val ktmDir = File(homeDir, ".ktm")
    val libDir = File(ktmDir, name.replace("/", "."))
    val versionedLibDir = File(libDir, version)
    if (!versionedLibDir.exists()) {
        versionedLibDir.mkdirs()
    }
    log(libDir.toString())
    downloadArtifacts(name, version, versionedLibDir, fileDownloader)
}

private fun downloadArtifacts(name: String, version: String, versionedLibDir: File, fileDownloader: (String, File) -> Unit) {
    val jitPackUrl = "https://jitpack.io"
    val buildLog = URL("$jitPackUrl/$name/$version/build.log").readText()
    val files = buildLog.substringAfterLast("Files:")
    log("files : $files")
    files.split("\n")
            .filter { it.isNotBlank() }
            .drop(1)
            .map {
                val source = "$jitPackUrl/$name/$version/$it"
                val destination = File(versionedLibDir, it.substringAfterLast("$version/"))
                source to destination
            }
            .forEach { (source, destination) ->
                println("Processing file $destination")
                fileDownloader(source, destination)
            }
}

typealias logger = (String) -> Unit
typealias progressBarUpdater = (Int) -> Unit

fun <T1, T2, T3, R> ((T1, T2, T3) -> R).rcurry(t3: T3): (T1, T2) -> R = { t1, t2 -> this(t1, t2, t3) }