package io.ghostbuster91.ktm

import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.VFS
import java.io.File
import java.nio.file.Files

class KtmDirectoryManager(private val homeDir: GetHomeDir) {
    private val ktmDir = homeDir().createChild(".ktm")

    fun getLibraryDir(identifier: Identifier): File {
        return ktmDir
                .createChild("modules")
                .createChild("${identifier.groupId}:${identifier.artifactId}")
                .createChild(identifier.shortVersion)
    }

    fun linkToBinary(identifier: Identifier, binaryFile: FileObject) {
        val symbolicLink = ktmDir
                .createChild("bin")
                .apply { mkdir() }
                .createChild(identifier.artifactId)
        logger.info("Linking $identifier as ${symbolicLink.name}")
        symbolicLink.linkTo(binaryFile)
    }

    fun getBinary(identifier: Identifier): FileObject {
        val binaryFile = VFS.getManager().resolveFile(getLibraryDir(identifier).absolutePath).findFiles(ExecutableFilesSelector()).first()
        return binaryFile!!
    }

    private fun File.linkTo(fileObject: FileObject) {
        if (exists()) {
            delete()
        }
        Files.createSymbolicLink(toPath(), File(fileObject.name.path).toPath())
    }

    private fun File.createChild(childName: String) = File(this, childName)
}