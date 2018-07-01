package io.ghostbuster91.ktm.components

import io.ghostbuster91.ktm.Downloader
import io.ghostbuster91.ktm.logger
import io.reactivex.Observable
import org.apache.commons.vfs2.AllFileSelector
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.VFS
import java.io.File

class TarFileDownloader(private val observable: Observable<out Any>) : Downloader {
    override fun download(url: String, destination: File): FileObject {
        logger.info("Found archive: $url")
        val files = decompress(url, destination)
        logger.info("Looking for binary file")
        val binaryFile = files.firstOrNull { it.name.extension.isEmpty() && it.isFile }
        require(binaryFile != null, { "No binary files found!" })
        logger.info("Found: ${binaryFile!!.name.baseName}")
        return binaryFile
    }

    private fun decompress(url: String, out: File): List<FileObject> {
        val manager = VFS.getManager()
        logger.append("Downloading...")
        val waiter = observable.subscribe()
        val archive = manager.resolveFile("tar:$url")
        waiter.dispose()
        val allFileSelector = AllFileSelector()
        manager.resolveFile(out.absolutePath).copyFrom(archive, allFileSelector)
        val files = manager.resolveFile(out.absolutePath).findFiles(allFileSelector)
        return files!!.toList()
    }
}