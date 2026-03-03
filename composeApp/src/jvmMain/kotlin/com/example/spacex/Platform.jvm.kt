package com.example.spacex

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.spacex.database.AppDatabase
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual val driverFactory: SqlDriver by lazy {
    val driver = JdbcSqliteDriver("jdbc:sqlite:spacex.db")
    AppDatabase.Schema.create(driver)
    driver
}



actual fun shareText(text: String) {
    val selection = StringSelection(text)
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(selection, null)
}