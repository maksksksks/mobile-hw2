package com.example.spacex

import android.annotation.SuppressLint
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.spacex.database.AppDatabase
import android.content.Intent


@SuppressLint("StaticFieldLeak")
object SpaceXApp {
    lateinit var context: Context
}

actual val driverFactory: SqlDriver by lazy {
    AndroidSqliteDriver(AppDatabase.Schema, SpaceXApp.context, "spacex.db")
}


lateinit var appContext: Context

fun initAndroidContext(context: Context) {
    appContext = context.applicationContext
}

actual fun shareText(text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    appContext.startActivity(shareIntent)
}