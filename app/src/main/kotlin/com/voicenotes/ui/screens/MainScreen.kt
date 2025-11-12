package com.voicenotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.voicenotes.data.local.entities.NoteEntity
import com.voicenotes.ui.viewmodel.NoteViewModel

@Composable
fun MainScreen(nav: NavController, vm: NoteViewModel) {
    val notes = vm.notes.collectAsState().value
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(notes) { n ->
                Card(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(n.title ?: "(no title)")
                            Text(n.text ?: "", maxLines = 1)
                        }
                        Button(onClick = { nav.navigate("detail/${n.id}") }) {
                            Text("Open")
                        }
                    }
                }
            }
        }
        FloatingActionButton(onClick = { nav.navigate("add") }, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Text("+")
        }
    }
}
