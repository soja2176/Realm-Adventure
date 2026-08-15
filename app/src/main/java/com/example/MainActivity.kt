package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.GameProgressRepository
import com.example.data.GameViewModel
import com.example.ui.EldoriaMainContainer
import com.example.ui.design.Eldoria
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val database by lazy {
    Room.databaseBuilder(
      applicationContext,
      AppDatabase::class.java,
      "eldoria_rpg.db"
    )
    .addMigrations(AppDatabase.MIGRATION_7_8, AppDatabase.MIGRATION_8_9)
    .fallbackToDestructiveMigrationOnDowngrade()
    .fallbackToDestructiveMigration()
    .build()
  }

  private val repository by lazy {
    GameProgressRepository(database.gameProgressDao(), applicationContext)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
          @Suppress("UNCHECKED_CAST")
          return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
      }
    })[GameViewModel::class.java]

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Eldoria.Abyss
        ) {
          EldoriaMainContainer(viewModel = viewModel)
        }
      }
    }
  }
}
