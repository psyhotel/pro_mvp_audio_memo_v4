package com.voicenotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    val repository = remember { NoteRepository(AppDatabase.getDatabase().noteDao()) }
    val viewModel = remember { NoteViewModel(repository) }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            val notes = viewModel.notes.collectAsState().value
            val categories = viewModel.categories.collectAsState().value
            MainScreen(
                notes = notes,
                categories = categories,
                onAddAudioClick = { navController.navigate("add") },
                onAddTextClick = { navController.navigate("add_text") },
                onNoteClick = { id -> navController.navigate("detail/$id") },
                onDelete = { note -> viewModel.deleteNote(note) },
                onRenameCategory = { old, new -> viewModel.renameCategory(old, new) },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("add") {
            AddNoteScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("add_text") {
            com.voicenotes.ui.screens.AddTextNoteScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("settings") {
            com.voicenotes.ui.screens.SettingsScreen(
                onBack = { navController.popBackStack() },
                onTasks = { navController.navigate("tasks") }
            )
        }
        composable("tasks") {
            com.voicenotes.ui.screens.TasksScreen {
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