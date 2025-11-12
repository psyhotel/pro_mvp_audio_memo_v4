package com.voicenotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.voicenotes.ui.screens.AddNoteScreen
import com.voicenotes.ui.screens.MainScreen
import com.voicenotes.ui.screens.NoteDetailScreen
import com.voicenotes.ui.viewmodel.NoteViewModel

@Composable
fun AppScreen() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "main") {
        composable("main") { MainScreen(nav = nav, vm = hiltViewModel<NoteViewModel>()) }
        composable("add") { AddNoteScreen(nav = nav, vm = hiltViewModel<NoteViewModel>()) }
        composable("detail/{noteId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("noteId") ?: ""
            NoteDetailScreen(nav = nav, noteId = id, vm = hiltViewModel<NoteViewModel>())
        }
    }
}
