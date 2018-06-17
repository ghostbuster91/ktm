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
        logger.info("Library already installed in given version!")
    }
}

private fun installImpl(jitPack: JitPack, name: String, version: String, libraryDir: File, ktmDir: File, unifiedName: String) {
    val artifacts = jitPack.getRelatedFiles(name, version)
    require(artifacts.isNotEmpty(), { "Didn't find any artifacts!" })
    logger.info("Found ${artifacts.size} files:")
    artifacts.forEach(logger::info)
    val tarFile = artifacts.firstOrNull { it.substringAfterLast(".") == "tar" }
    require(tarFile != null, { "No tar archives found!" })
    logger.info("Decompressing files from $tarFile")
    val files = decompress(tarFile!!, libraryDir)
    logger.info("Looking for binary file...")
    val binaryFile = files.firstOrNull { it.name.extension.isEmpty() }
    require(binaryFile != null, { "No binary files found!" })
    logger.info("Found ${binaryFile!!.name.baseName}")
    logger.info("Making executable")
    binaryFile.setExecutable(true, true)
    val symlink = ktmDir.createChild("bin").apply { mkdir() }.createChild(unifiedName.substringAfterLast("."))
    logger.info("Linking as ${symlink.name}")
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
    return files.map { getFileUrl(it) }
}

private fun File.createChild(childName: String) = File(this, childName)
