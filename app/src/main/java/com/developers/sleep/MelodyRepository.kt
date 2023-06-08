package com.developers.sleep

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.net.toUri
import com.developers.sleep.dataModels.Melody
import com.developers.sleep.dataModels.Playlist
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Inject

class MelodyRepository @Inject constructor(
    private val downloadManager: DownloadManager,
    private val application: Application
) {
    val alarmPlayerPrefs: SharedPreferences =
        application.getSharedPreferences(AlarmPlayerPrefs.PREFS_NAME, Context.MODE_PRIVATE)

    fun getLastSavedMelody(): Melody? {
        val melodyJsonString = alarmPlayerPrefs.getString(AlarmPlayerPrefs.SELECTED_MELODY, null)
        val gson = Gson()
        return   gson.fromJson(melodyJsonString, Melody::class.java)
    }

    fun getLastSavedPlaylist(): Playlist? {
        val playlistName = alarmPlayerPrefs.getString(AlarmPlayerPrefs.SELECTED_PLAYLIST_NAME, null)
        return PLAYLIST_LIST.findLast { it.name == playlistName }
    }

    fun getLastSavedDuration(): Int {
        return alarmPlayerPrefs.getInt(AlarmPlayerPrefs.MUSIC_DURATION, 30)
    }

    fun <T> toJsonString(obj: T): String {
        val gson = Gson()
        return gson.toJson(obj)
    }

    fun saveCurrentPlayerSet(
        currentMelody: Melody,
        currentPlaylist: Playlist,
        musicDurationInMinutes: Int,
    ) {
        val melodyJsonString = toJsonString(currentMelody)
        alarmPlayerPrefs.edit()
            .putString(AlarmPlayerPrefs.SELECTED_MELODY, melodyJsonString)
            .putInt(AlarmPlayerPrefs.MUSIC_DURATION, musicDurationInMinutes)
            .putString(AlarmPlayerPrefs.SELECTED_PLAYLIST_NAME, currentPlaylist.name)
            .apply()
    }

    fun downloadMelody(fileName: String) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (!file.exists()) {
            val request = DownloadManager.Request((BASE_URL + fileName).toUri())
                .setMimeType("audio/mp3")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE) //TODO Hide it
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            downloadManager.enqueue(request)
        }
    }

}


@Module
@InstallIn(SingletonComponent::class)
object MelodyRepositoryModule {
    @Provides
    fun provideMelodyRepository(
        downloadManager: DownloadManager,
        application: Application
    ): MelodyRepository {
        return MelodyRepository(downloadManager, application)
    }
}
