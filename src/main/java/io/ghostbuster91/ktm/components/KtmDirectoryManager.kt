package io.ghostbuster91.ktm.components

import io.ghostbuster91.ktm.GetHomeDir
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.logger
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSelectInfo
import org.apache.commons.vfs2.FileSelector
import org.apache.commons.vfs2.VFS
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class KtmDirectoryManager(homeDir: GetHomeDir) {
    private val ktmDir = KtmDir(homeDir().createChild(".ktm"))

    fun getLibraryDir(identifier: Identifier.Parsed): File {
        return ktmDir
                .modules
                .createChild(identifier.groupId)
                .createChild(identifier.artifactId)
                .createChild(identifier.version)
    }

    fun linkToBinary(identifier: Identifier.Parsed, binaryFile: FileObject) {
        val symbolicLink = ktmDir
                .binaries
                .apply { mkdir() }
                .createChild(identifier.artifactId)
        logger.info("Linking $identifier as ${symbolicLink.name}")
        symbolicLink.linkTo(binaryFile)
    }

    fun getBinary(identifier: Identifier.Parsed): FileObject {
        val binaryFile = VFS.getManager().resolveFile(getLibraryDir(identifier).absolutePath).findFiles(ExecutableFilesSelector()).single()
        return binaryFile!!
    }

    fun getAliasFile(): File {
        return ktmDir.createChild("aliases")
    }

    fun getBinaries(): List<FileObject> {
        return VFS.getManager()
                .resolveFile(ktmDir.binaries.absolutePath)
                .findFiles(ExecutableFilesSelector())
                ?.toList() ?: emptyList()
    }

    fun getActiveModules(): List<Identifier.Parsed> {
        return getBinaries()
                .map {
                    val unwrapedBinery = Files.readSymbolicLink(Paths.get(it.name.path))
                    val reversed = unwrapedBinery.reversed()
                    Identifier.Parsed(reversed.drop(4).first().toString(), reversed.drop(3).first().toString(), reversed.drop(2).first().toString())
                }
    }

    fun getAllModules(): List<Identifier.Parsed> {
        return VFS.getManager()
                .resolveFile(ktmDir.modules.absolutePath)
                .children
                .flatMap { group ->
                    group.children.flatMap { artifact ->
                        artifact.children.map { versionFile -> Identifier.Parsed(group.name.baseName, artifact.name.baseName, versionFile.name.baseName) }
                    }
                }
    }

    private fun File.linkTo(fileObject: FileObject) {
        if (exists()) {
            delete()
        }
        Files.createSymbolicLink(toPath(), File(fileObject.name.path).toPath())
    }

}

private fun File.createChild(childName: String) = File(this, childName)


private class ExecutableFilesSelector : FileSelector {
    override fun traverseDescendents(fileInfo: FileSelectInfo?): Boolean {
        return true
    }

    override fun includeFile(fileInfo: FileSelectInfo): Boolean {
        return fileInfo.file.isExecutable && fileInfo.file.isFile
    }
}

private class KtmDir(private val ktmDir: File) {
    val binaries = ktmDir.createChild("bin")
    val modules = ktmDir.createChild("modules")
    fun createChild(name: String) = ktmDir.createChild(name)
}

class Module(val group: String, val artifact: String, val version: String)