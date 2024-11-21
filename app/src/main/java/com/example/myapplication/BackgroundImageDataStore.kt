import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit

val Context.dataStore by preferencesDataStore(name = "background_image_prefs")

object BackgroundImageDataStore {
    private val BACKGROUND_IMAGE_KEY = stringPreferencesKey("background_image_uri")

    // Lưu URI hình nền
    suspend fun saveBackgroundImageUri(context: Context, uri: String) {
        context.dataStore.edit { prefs ->
            prefs[BACKGROUND_IMAGE_KEY] = uri
        }
    }

    // Lấy URI hình nền
    fun getBackgroundImageUri(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[BACKGROUND_IMAGE_KEY]
        }
    }
}
