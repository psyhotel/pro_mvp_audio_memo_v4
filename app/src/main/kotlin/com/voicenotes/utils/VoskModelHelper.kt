package com.voicenotes.utils

import android.content.Context
import java.io.File

object VoskModelHelper {
    fun ensureModelExists(context: Context, lang: String): File? {
        val base = context.getExternalFilesDir(null) ?: return null
        val modelDir = File(base, "vosk/${lang}")
        return if (modelDir.exists()) modelDir else null
    }
}

