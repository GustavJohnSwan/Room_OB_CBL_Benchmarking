package com.bignerdranch.android.room_ob_cbl_benchmarking.ui_composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
                Button(onClick = { viewModel.findEntriesById() }) {
                    Text("Find 100 IDs in ObjectBox database")
                }
            }

            item {
                Button(onClick = { viewModel.deleteAllEntries_ObjectBox() }) {
                    Text("Delete all entries from ObjectBox")
                }
            }

            item {
                Button(onClick = { viewModel.resetDataBase_ObjectBox() }) {
                    Text("Reset ObjectBox database")
                }
            }
        }
    }
}