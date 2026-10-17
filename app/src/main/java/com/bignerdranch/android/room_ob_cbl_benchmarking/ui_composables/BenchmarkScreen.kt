package com.bignerdranch.android.room_ob_cbl_benchmarking.ui_composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.OB_ViewModel
import com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.Room_ViewModel
import kotlinx.coroutines.launch

@Composable
fun BenchmarkScreen(
    viewModel: OB_ViewModel = viewModel(),
    roomViewModel: Room_ViewModel = viewModel()
) {

    var showRoomButtons by remember { mutableStateOf(false) }

    var benchmarkStatus by remember { mutableStateOf("Ready") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(top = 40.dp, bottom = 40.dp, start = 16.dp, end = 16.dp)
        ) {

            item {
                Button(
                    onClick = { showRoomButtons = !showRoomButtons }
                ) {
                    Text(
                        if (showRoomButtons) "Switch to ObjectBox"
                        else "Switch to Room"
                    )
                }
            }

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



            if (!showRoomButtons) {

                // _________________________________________________________________________________

                // ObjectBox Buttons

                // _________________________________________________________________________________

                item {
                    OutlinedButton(onClick = { viewModel.createFreshDatabase_ObjectBox() }) {
                        Text("Reset ObjectBox database")
                    }
                }

                item {
                    OutlinedButton(onClick = { viewModel.countEntriesBenchmark_ObjectBox() }) {
                        Text("Count entries in database")
                    }
                }

                // _________________________________________________________________________________


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

                // _________________________________________________________________________________

                item {
                    Button(onClick = { viewModel.insertMainEntryBenchmark_ObjectBox() }) {
                        Text("(Main Only) Insert 1 entry into ObjectBox")
                    }
                }

                // _________________________________________________________________________________


                item {
                    Button(onClick = { viewModel.insertMainDataSet_ObjectBox(1) }) {
                        Text("(Main Only) Insert 100 entries into ObjectBox")
                    }
                }

                item {
                    Button(onClick = { viewModel.insertMainDataSet_ObjectBox(2) }) {
                        Text("(Main Only) Insert 1k entries into ObjectBox")
                    }
                }

                item {
                    Button(onClick = { viewModel.insertMainDataSet_ObjectBox(3) }) {
                        Text("(Main Only) Insert 10k entries into ObjectBox")
                    }
                }

                item {
                    Button(onClick = { viewModel.insertMainDataSet_ObjectBox(4) }) {
                        Text("(Main Only) Insert 50k entries into ObjectBox")
                    }
                }

                item {
                    Button(onClick = { viewModel.insertMainDataSet_ObjectBox(5) }) {
                        Text("(Main Only) Insert 100k entries into ObjectBox")
                    }
                }

                // _________________________________________________________________________________

                item {
                    FilledTonalButton(onClick = { viewModel.getAllEntries_ObjectBox() }) {
                        Text("(Main Only) Find ALL entries in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(onClick = { viewModel.getEntryByIdBenchmark_ObjectBox() }) {
                        Text("(Main Only) Find 1 ENTRY BY ID in ObjectBox database")
                    }
                }

                // _________________________________________________________________________________

                item {
                    FilledTonalButton(onClick = { viewModel.getEntriesByIdsBenchmark_ObjectBox(1) }) {
                        Text("(Main Only) Find 100 ENTRIES BY IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(onClick = { viewModel.getEntriesByIdsBenchmark_ObjectBox(2) }) {
                        Text("(Main Only) Find 1k ENTRIES BY IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(onClick = { viewModel.getEntriesByIdsBenchmark_ObjectBox(3) }) {
                        Text("(Main Only) Find 10k ENTRIES BY IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(onClick = { viewModel.getEntriesByIdsBenchmark_ObjectBox(4) }) {
                        Text("(Main Only) Find 50k ENTRIES BY IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(onClick = { viewModel.getEntriesByIdsBenchmark_ObjectBox(5) }) {
                        Text("(Main Only) Find 100k ENTRIES BY IDs in ObjectBox database")
                    }
                }

                // _________________________________________________________________________________

                item {
                    FilledTonalButton(
                        onClick = { viewModel.updateEntryBenchmark_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Green,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Update 1 ENTRY by ID in ObjectBox database")
                    }
                }

                // _________________________________________________________________________________

                item {
                    FilledTonalButton(
                        onClick = { viewModel.updateEntriesBenchmark_ObjectBox(1) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Green,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Update 100 ENTRIES by IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.updateEntriesBenchmark_ObjectBox(2) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Green,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Update 1000 ENTRIES by IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.updateEntriesBenchmark_ObjectBox(3) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Green,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Update 10 000 ENTRIES by IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.updateEntriesBenchmark_ObjectBox(4) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Green,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Update 50 000 ENTRIES by IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.updateEntriesBenchmark_ObjectBox(5) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Green,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Update 100 000 ENTRIES by IDs in ObjectBox database")
                    }
                }

                // _________________________________________________________________________________


                item {
                    FilledTonalButton(
                        onClick = { viewModel.deleteAllEntriesBenchmark_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Delete all entries in ObjectBox database")
                    }
                }


                item {
                    FilledTonalButton(
                        onClick = { viewModel.deleteEntryByIdBenchmark_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Delete 1 entry by ID in ObjectBox database")
                    }
                }

                // _________________________________________________________________________________

                item {
                    FilledTonalButton(
                        onClick = { viewModel.deleteEntriesByIdsBenchmark_ObjectBox(1) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Delete 100 entities by IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.deleteEntriesByIdsBenchmark_ObjectBox(2) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Delete 1000 entities by IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.deleteEntriesByIdsBenchmark_ObjectBox(3) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Delete 10 000 entities by IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.deleteEntriesByIdsBenchmark_ObjectBox(4) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Delete 50 000 entities by IDs in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.deleteEntriesByIdsBenchmark_ObjectBox(5) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("(Main Only) Delete 100 000 entities by IDs in ObjectBox database")
                    }
                }

                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________

                // MEDIUM QUERIES
                item {
                    FilledTonalButton(
                        onClick = { viewModel.findEntriesInSpecificDateBenchmarking_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Yellow,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Find entries in date, order by time in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.findEntriesInDateRangeBenchmarking_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Yellow,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Find entries in date range, order by time in ObjectBox database")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.findNextEntriesBenchmarking_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Yellow,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("FIND next X entries from date to date with reminder (not null)")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.findEntriesWithSpecificReminderBenchmarking_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Yellow,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Find entries with a specific reminder")
                    }
                }



                item {
                    FilledTonalButton(
                        onClick = { viewModel.findEntriesWithRecurranceBenchmarking_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Yellow,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Find entries with any recurrence")
                    }
                }

                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // _____________________________________________________________________________________________
                // ADVANCED QUERIES

                item {
                    FilledTonalButton(
                        onClick = { viewModel.findEntriesInDateRangeReminderRepeat1OrRepeat2Benchmarking_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("FIND Entries in Date Range with specific Reminder and specific Repeat1 or Repeat2")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.findEntriesReminderNullRepeatNotNullLimitOffsetBenchmarking_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("FIND Entries with Reminder is Null and Repeat is Not Null + Limit + Offset")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.countEntriesByRepeatTypeBenchmarking_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Find all repeat types and count them")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.findEarliestLatestEventTimesInRangeWithReminderBenchmarking_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Earliest and latest event time among events in a date range with a specific reminder")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { viewModel.findEntriesContainsSpecificTextTimeIsLaterThanSpecifiedTimeBenchmarking_ObjectBox() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Find all entries whose title contains a specified text fragment and whose event time is later than a specified time.")
                    }
                }

            } else {


                // ROOM BUTTONS


                item {
                    Button(onClick = { scope.launch { roomViewModel.insertEntry_Room() } }) {
                        Text("Insert 1 entry into Room")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { scope.launch { roomViewModel.getAllEntry_Room() } },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Green,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Get All Entries from Room")
                    }
                }

                item {
                    FilledTonalButton(
                        onClick = { scope.launch { roomViewModel.deleteEntry_Room() } },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Delete first entry from Room")
                    }
                }


            }
        }
    }
}