package com.skybird.colorfulscanner.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Date：2022/7/11
 * Describe:
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ColorfulPhoto")

private val SELECTED_NAME = stringPreferencesKey("curSelectedName")
private val SELECTED_ICON = intPreferencesKey("curSelectedIcon")

suspend fun putCurSelectedName(name: String) =
    CSApp.mApp.dataStore.edit { it[SELECTED_NAME] = name }

fun getCurSelectedName() = runBlocking {
    return@runBlocking CSApp.mApp.dataStore.data.map {
        it[SELECTED_NAME] ?: DEFAULT_SERVER_NAME
    }.first()
}

suspend fun putCurSelectedIcon(res: Int) =
    CSApp.mApp.dataStore.edit { it[SELECTED_ICON] = res }

fun getCurSelectedIcon() = runBlocking {
    return@runBlocking CSApp.mApp.dataStore.data.map {
        it[SELECTED_ICON] ?: R.drawable.ic_default_n
    }.first()
}