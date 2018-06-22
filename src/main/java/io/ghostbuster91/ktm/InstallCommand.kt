package io.ghostbuster91.ktm

import io.ghostbuster91.ktm.identifier.VersionedIdentifier
import org.apache.commons.vfs2.AllFileSelector
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.VFS
import java.io.File

fun executeInstallCommand(identifier: VersionedIdentifier.Parsed, jitPack: JitPack, ktmDirectoryManager: KtmDirectoryManager) {
    val libraryDir = ktmDirectoryManager.getLibraryDir(identifier)
    if (!libraryDir.exists()) {
        libraryDir.mkdirs()
        libraryDir.deleteOnError {
            installImpl(jitPack, libraryDir, identifier, ktmDirectoryManager)
        }
    } else {
        logger.info("Library already installed in given version!")
    }
}

private fun installImpl(jitPack: JitPack, libraryDir: File, identifier: VersionedIdentifier.Parsed, directoryManager: KtmDirectoryManager) {
    val artifacts = jitPack.getBuildArtifacts(identifier)
    val tarFile = artifacts.findArchive()
    val binaryFile = tarFile.extractBinaryFile(libraryDir).markAsExecutable()
    directoryManager.linkToBinary(identifier, binaryFile)
}

private fun List<String>.findArchive(): String {
    logger.info("Found $size files:")
    forEach(logger::info)
    val archive = firstOrNull { it.substringAfterLast(".") == "tar" }
    require(archive != null, { "No tar archives found!" })
    return archive!!
}

private fun String.extractBinaryFile(libraryDir: File): FileObject {
    logger.info("Found archive: ${this}")
    val files = decompress(this, libraryDir)
    logger.info("Looking for binary file")
    val binaryFile = files.firstOrNull { it.name.extension.isEmpty() }
    require(binaryFile != null, { "No binary files found!" })
    logger.info("Found: ${binaryFile!!.name.baseName}")
    return binaryFile
}

private fun FileObject.markAsExecutable(): FileObject {
    logger.info("Making executable")
    setExecutable(true, true)
    return this
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
    logger.append("Downloading...")
    val waiter = createWaitingIndicator().subscribe()
    val archive = manager.resolveFile("tar:$url")
    waiter.dispose()
    val allFileSelector = AllFileSelector()
    manager.resolveFile(out.absolutePath).copyFrom(archive, allFileSelector)
    val files = manager.resolveFile(out.absolutePath).findFiles(allFileSelector)
    return files!!.toList()
}

private fun JitPack.getBuildArtifacts(identifier: VersionedIdentifier.Parsed): List<String> {
    logger.append("Fetching build log from JitPack...")
    val buildLog = fetchBuildLog(identifier)
    val files = buildLog.substringAfterLast("Files:").split("\n").filter { it.isNotBlank() }.drop(1)
    require(files.isNotEmpty(), { "Didn't find any artifacts!" })
    return files.map { getFileUrl(it) }
}