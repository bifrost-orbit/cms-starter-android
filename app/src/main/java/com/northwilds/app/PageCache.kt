package com.northwilds.app

import android.os.SystemClock
import com.bifrostorbit.cms.CmsResolution
import com.bifrostorbit.cms.CmsServerException
import java.util.concurrent.ConcurrentHashMap

object PageCache {
    private const val LIFETIME_MILLIS = 60_000L

    private class Kept(val result: CmsResolution, val at: Long)

    private val kept = ConcurrentHashMap<String, Kept>()

    suspend fun resolve(path: String, fresh: Boolean = false): CmsResolution {
        val now = SystemClock.elapsedRealtime()
        val entry = kept[path]
        if (!fresh && entry != null && now - entry.at < LIFETIME_MILLIS) return entry.result
        return try {
            Cms.client.resolve(path).also { kept[path] = Kept(it, now) }
        } catch (error: CmsServerException) {
            entry?.result ?: throw error
        }
    }

    fun clear() = kept.clear()
}
