package io.ghostbuster91.ktm

import org.apache.commons.vfs2.FileObject
import java.io.File

interface Downloader {
    fun download(url: String, destination: File): FileObject

    companion object {
        operator fun invoke(f: (String, File) -> FileObject) = object : Downloader {
            override fun download(url: String, destination: File) = f(url, destination)
        }
    }
}