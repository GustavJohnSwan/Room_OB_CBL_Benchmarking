package com.bignerdranch.android.room_ob_cbl_benchmarking.buisness_logic.helper_classes.ObjectBox

import com.bignerdranch.android.room_ob_cbl_benchmarking.database.EntryOb_B
import com.bignerdranch.android.room_ob_cbl_benchmarking.database.ExtraDataOb_B

data class OB_EntryWithExtraData(
    val entry: EntryOb_B,
    val extraData: ExtraDataOb_B?
)