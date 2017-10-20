package hinl.android.kotlin.databasehelper

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import hinl.kotlin.database.helper.Database
import hinl.kotlin.database.helper.ISelectionOperator
import hinl.kotlin.database.helper.SQLiteDatabaseHelper
import hinl.kotlin.database.helper.Schema
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}