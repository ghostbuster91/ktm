package io.ghostbuster91.ktm

import org.apache.commons.vfs2.FileSelectInfo
import org.apache.commons.vfs2.FileSelector

class ExecutableFilesSelector : FileSelector{
    override fun traverseDescendents(fileInfo: FileSelectInfo?): Boolean {
        return true
    }

    override fun includeFile(fileInfo: FileSelectInfo): Boolean {
        return fileInfo.file.isExecutable
    }
}