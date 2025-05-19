package com.example.lab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = TaskViewModel(applicationContext)
            TaskAppUI(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAppUI(viewModel: TaskViewModel) {
    var newTaskText by remember { mutableStateOf("") }
    val tasks by viewModel.tasks.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "BIRDS' LIVES",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = viewModel.searchQuery.value,
            onValueChange = { viewModel.setSearchQuery(it) },
            label = { Text("Buscar tareas") },
            modifier = Modifier.fillMaxWidth()
        )

        SortSelector(viewModel)

        OutlinedTextField(
            value = newTaskText,
            onValueChange = { newTaskText = it },
            label = { Text("Nueva tarea") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = {
                if (newTaskText.isNotBlank()) {
                    viewModel.addTask(newTaskText)
                    newTaskText = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Agregar tarea")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onToggle = { viewModel.toggleTaskCompletion(task) },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }
        }

        OutlinedButton(
            onClick = { viewModel.deleteAllTasks() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar todo")
            Spacer(Modifier.width(8.dp))
            Text("Eliminar todas las tareas")
        }
    }
}

@Composable
fun SortSelector(viewModel: TaskViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val sortTypes = listOf(
        "Nombre" to SortType.NAME,
        "Fecha" to SortType.DATE,
        "Estado" to SortType.STATUS
    )
    val currentSort by viewModel.sortType.collectAsState()

    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Button(onClick = { expanded = true }) {
            Text("Ordenar: ${currentSort.name}")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sortTypes.forEach { (name, type) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        viewModel.setSortType(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(task.createdDate)),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}



