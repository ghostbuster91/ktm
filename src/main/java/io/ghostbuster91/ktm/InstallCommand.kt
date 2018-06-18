package io.ghostbuster91.ktm

import org.apache.commons.vfs2.AllFileSelector
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.VFS
import java.io.File
import java.nio.file.Files

fun executeInstallCommand(name: String, version: String, jitPack: JitPack, getHomeDir: GetHomeDir) {
    val simplifiedVersion = version.take(10)
    val ktmDir = getHomeDir().createChild(".ktm")
    val unifiedName = name.replace("/", ".")
    val libraryDir = ktmDir.getLibraryDir(unifiedName, simplifiedVersion)
    if (!libraryDir.exists()) {
        libraryDir.mkdirs()
        libraryDir.deleteOnError {
            installImpl(jitPack, name, simplifiedVersion, libraryDir, ktmDir, unifiedName)
        }
    } else {
        logger.info("Library already installed in given version!")
    }
}

private fun installImpl(jitPack: JitPack, name: String, version: String, libraryDir: File, ktmDir: File, unifiedName: String) {
    val artifacts = jitPack.getBuildArtifacts(name, version)
    val tarFile = artifacts.findArchive()
    val binaryFile = tarFile.extractBinaryFile(libraryDir).markAsExecutable()
    val symbolicLink = ktmDir.createChild("bin").apply { mkdir() }.createChild(unifiedName.substringAfterLast("."))
    symbolicLink.linkTo(binaryFile)
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

private fun File.linkTo(fileObject: FileObject) {
    logger.info("Linking as $name")
    if (exists()) {
        delete()
    }
    Files.createSymbolicLink(toPath(), File(fileObject.name.path).toPath())
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

private fun File.getLibraryDir(name: String, version: String) =
        createChild("modules")
                .createChild(name)
                .createChild(version)

private fun JitPack.getBuildArtifacts(name: String, version: String): List<String> {
    logger.append("Fetching build log from JitPack...")
    val buildLog = fetchBuildLog(name, version)
    val files = buildLog.substringAfterLast("Files:").split("\n").filter { it.isNotBlank() }.drop(1)
    require(files.isNotEmpty(), { "Didn't find any artifacts!" })
    return files.map { getFileUrl(it) }
}

private fun File.createChild(childName: String) = File(this, childName)
