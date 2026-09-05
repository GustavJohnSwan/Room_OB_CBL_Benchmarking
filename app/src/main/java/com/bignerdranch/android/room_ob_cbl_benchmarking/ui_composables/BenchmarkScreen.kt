package com.bignerdranch.android.room_ob_cbl_benchmarking.ui_composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.BenchmarkViewModel

@Composable
fun BenchmarkScreen(
    viewModel: BenchmarkViewModel = viewModel()
) {

    var benchmarkStatus by remember { mutableStateOf("Ready") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Text(
                    text = benchmarkStatus,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                // The UI observes the ViewModel state directly
                Text(
                    text = viewModel.benchmarkStatus,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                Button(onClick = { viewModel.insertDataSet_ObjectBox(1) }) {
                    Text("Insert 100 entries into ObjectBox")
                }
            }

            item {
                Button(onClick = { viewModel.insertDataSet_ObjectBox(2) }) {
                    Text("Insert 1k entries into ObjectBox")
                }
            }

            item {
                Button(onClick = { viewModel.insertDataSet_ObjectBox(3) }) {
                    Text("Insert 10k entries into ObjectBox")
                }
            }

            item {
                Button(onClick = { viewModel.insertDataSet_ObjectBox(4) }) {
                    Text("Insert 50k entries into ObjectBox")
                }
            }

            item {
                Button(onClick = { viewModel.insertDataSet_ObjectBox(5) }) {
                    Text("Insert 100k entries into ObjectBox")
                }
            }

            item {
                FilledTonalButton(onClick = { viewModel.findEntriesById(1) }) {
                    Text("Find 100 IDs in ObjectBox database")
                }
            }

            item {
                FilledTonalButton(onClick = { viewModel.findEntriesById(2) }) {
                    Text("Find 1k IDs in ObjectBox database")
                }
            }

            item {
                FilledTonalButton(onClick = { viewModel.findEntriesById(3) }) {
                    Text("Find 10k IDs in ObjectBox database")
                }
            }

            item {
                FilledTonalButton(onClick = { viewModel.findEntriesById(4) }) {
                    Text("Find 50k IDs in ObjectBox database")
                }
            }

            item {
                FilledTonalButton(onClick = { viewModel.findEntriesById(5) }) {
                    Text("Find 100k IDs in ObjectBox database")
                }
            }

            item {
                FilledTonalButton(
                    onClick = { viewModel.updateEntriesById(100) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.Green,
                        contentColor = Color.Black
                )
                ) {
                    Text("Update 100 entities by IDs in ObjectBox database")
                }
            }

            item {
                FilledTonalButton(
                    onClick = { viewModel.deleteEntriesById(100) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Delete 100 entities by IDs in ObjectBox database")
                }
            }

            item {
                FilledTonalButton(
                    onClick = { viewModel.findEntriesByDateRange() },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.Yellow,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Find entries in DATE RANGE, order by time in ObjectBox database")
                }
            }

            item {
                FilledTonalButton(
                    onClick = { viewModel.findEntriesByDate() },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.Yellow,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Find entries in DATE, order by time in ObjectBox database")
                }
            }

            item {
                FilledTonalButton(
                    onClick = { viewModel.findNextEntryFromTodayToDate() },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.Yellow,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Find NEXT ENTRY from TODAY to DATE in ObjectBox database")
                }
            }


            item {
                OutlinedButton(onClick = { viewModel.deleteAllEntries_ObjectBox() }) {
                    Text("Delete all entries from ObjectBox")
                }
            }

            item {
                OutlinedButton(onClick = { viewModel.resetDataBase_ObjectBox() }) {
                    Text("Reset ObjectBox database")
                }
            }
        }
    }
}