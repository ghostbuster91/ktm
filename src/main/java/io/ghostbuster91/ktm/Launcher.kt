package io.ghostbuster91.ktm

import com.xenomachina.argparser.ArgParser
import java.io.File

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
                val homeDirProvider = { File(System.getProperty("user.home")) }
                executeInstallCommand(name, version, RealJitPackDownloader(), homeDirProvider, updateProgressBar)
            }
}

fun executeInstallCommand(name: String, version: String, downloaderReal: JitPackDownloader, getHomeDir: GetHomeDir, updateProgressBar: (Int) -> Unit) {
    val versionedLibDir = getVersionedLibDir(getHomeDir, name, version)
    if (!versionedLibDir.exists()) {
        versionedLibDir.mkdirs()
        log("Versioned lib dir created at ${versionedLibDir.path}")
    }
    downloadArtifacts(name, version, versionedLibDir, downloaderReal, updateProgressBar)
}

private fun getVersionedLibDir(getHomeDir: GetHomeDir, name: String, version: String) =
        getHomeDir()
                .createChild(".ktm")
                .createChild(name.replace("/", "."))
                .createChild(version)


private fun downloadArtifacts(
        name: String,
        version: String,
        versionedLibDir: File,
        jitPackDownloader: JitPackDownloader,
        updateProgressBar: (Int) -> Unit
) {
    val buildLog = jitPackDownloader.fetchBuildLog(name, version)
    val files = buildLog.substringAfterLast("Files:")
    log("files : $files")
    files.split("\n")
            .filter { it.isNotBlank() }
            .drop(1)
            .forEach { file ->
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
    println("Processing path $destination")
    jitPackDownloader.downloadFile(name, version, file, destination, updateProgressBar)
}

typealias logger = (String) -> Unit
typealias GetHomeDir = () -> File

private fun File.createChild(childName: String) = File(this, childName)
