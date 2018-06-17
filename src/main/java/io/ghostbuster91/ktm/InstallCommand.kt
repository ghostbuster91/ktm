package io.ghostbuster91.ktm

import org.apache.commons.vfs2.AllFileSelector
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.VFS
import java.io.File
import java.nio.file.Files

fun executeInstallCommand(name: String, version: String, jitPack: JitPack, getHomeDir: GetHomeDir) {
    val ktmDir = getHomeDir().createChild(".ktm")
    val unifiedName = name.replace("/", ".")
    val libraryDir = ktmDir.getLibraryDir(unifiedName, version)
    if (!libraryDir.exists()) {
        libraryDir.mkdirs()
        libraryDir.deleteOnError {
            installImpl(jitPack, name, version, libraryDir, ktmDir, unifiedName)
        }
    } else {
        println("Library already installed in given version!")
    }
}

private fun installImpl(jitPack: JitPack, name: String, version: String, libraryDir: File, ktmDir: File, unifiedName: String) {
    val artifacts = jitPack.getArtifactsNames(name, version)
    val tarFile = artifacts.first { it.substringAfterLast(".") == "tar" }
    println("Decompressing files from $tarFile")
    val files = decompress(tarFile, libraryDir)
    println("Looking for binary file...")
    val binaryFile = files.first { it.name.extension.isEmpty() }
    println("Found ${binaryFile.name.baseName}")
    println("Making executable")
    binaryFile.setExecutable(true, true)
    val symlink = ktmDir.createChild("bin").apply { mkdir() }.createChild(unifiedName.substringAfterLast("."))
    println("Linking as ${symlink.name}")
    if (symlink.exists()) {
        symlink.delete()
    }
    Files.createSymbolicLink(symlink.toPath(), File(binaryFile.name.path).toPath())
}

private fun File.deleteOnError(function: () -> Unit) {
    try {
        function()
    } catch (ex: Exception) {
        deleteRecursively()
    }
}

private fun JitPack.getArtifactsNames(name: String, version: String): List<String> {
    return try {
        getRelatedFiles(name, version)
    } catch (ex: Exception) {
        logger.error("Error during downloading library", ex)
        throw ex
    }
}

private fun decompress(url: String, out: File): List<FileObject> {
    val manager = VFS.getManager()
    val archive = manager.resolveFile("tar:$url")
    val allFileSelector = AllFileSelector()
    manager.resolveFile(out.absolutePath).copyFrom(archive, allFileSelector)
    val files = manager.resolveFile(out.absolutePath).findFiles(allFileSelector)
    return files!!.toList()
}

private fun File.getLibraryDir(name: String, version: String) =
        createChild("modules")
                .createChild(name)
                .createChild(version)

private fun JitPack.getRelatedFiles(name: String, version: String): List<String> {
    logger.append("Fetching build log from JitPack...")
    val buildLog = fetchBuildLog(name, version)
    val files = buildLog.substringAfterLast("Files:").split("\n").filter { it.isNotBlank() }.drop(1)
    logger.info("")
    logger.info("Found ${files.size} files:")
    files.forEach(::println)
    return files.map { "${JitPack.jitPackUrl}/$it" }
}

private fun File.createChild(childName: String) = File(this, childName)
