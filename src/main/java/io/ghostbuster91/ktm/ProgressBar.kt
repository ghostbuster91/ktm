package io.ghostbuster91.ktm

class ProgressBar {
    private val progress = StringBuilder(60)
    private var isFinished = false

    fun update(done: Long, total: Long) {
        if (isFinished) return
        val workchars = charArrayOf('|', '/', '-', '\\')
        val format = "\r%3d%% %s %c"

        val percent = done * 100 / total
        var extrachars = percent / 2 - this.progress.length

        while (extrachars-- > 0) {
            progress.append('#')
        }

        System.out.printf(format, percent, progress,
                workchars[(done % workchars.size).toInt()])

        if (done == total) {
            isFinished = true
            System.out.flush()
            println()
        }
    }
}