package hinl.android.kotlin.databasehelper.example.schema

import hinl.kotlin.database.helper.Database
import hinl.kotlin.database.helper.Schema
import java.util.*


@Database(tableName = "Example")
data class Example(
        @Schema(generatedId = true, field = "Id", autoIncrease = true, nonNullable = true) val id: Int? = 0,
        @Schema(field = "columnOne") var columnOne: String?,
        @Schema(field = "columnTwo") var columnTwo: Int?,
        @Schema(field = "columnThree") var columnThree: Date? = null) {
}