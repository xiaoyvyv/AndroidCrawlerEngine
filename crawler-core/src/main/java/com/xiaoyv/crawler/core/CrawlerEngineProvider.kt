package com.xiaoyv.crawler.core

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import java.lang.ref.WeakReference

/**
 * CrawlerEngineProvider
 *
 * @author why
 * @since 11/9/23
 */
class CrawlerEngineProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        CrawlerEngine.context = WeakReference(requireNotNull(context))
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri) = null
    override fun insert(uri: Uri, values: ContentValues?) = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = 0
}