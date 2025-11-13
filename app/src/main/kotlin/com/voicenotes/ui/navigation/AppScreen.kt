package com.voicenotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.voicenotes.data.local.AppDatabase
import com.voicenotes.data.repository.NoteRepository
import com.voicenotes.ui.screens.MainScreen
import com.voicenotes.ui.screens.AddNoteScreen
import com.voicenotes.ui.screens.NoteDetailScreen
import com.voicenotes.ui.viewmodel.NoteViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun AppScreen() {
    val navController = rememberNavController()
    val repository = NoteRepository(AppDatabase.getDatabase().noteDao())
    val viewModel = NoteViewModel(repository)

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            val notes = viewModel.notes.collectAsState().value
            MainScreen(
                notes = notes,
                onAddClick = { navController.navigate("add") },
                onNoteClick = { id -> navController.navigate("detail/$id") }
            )
        }
        composable("add") {
            AddNoteScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable(
            "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("id") ?: return@composable
            NoteDetailScreen(noteId = noteId, viewModel = viewModel) {
                navController.popBackStack()
            }
        }
    }
}