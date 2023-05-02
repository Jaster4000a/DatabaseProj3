package com.example.library_database

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase
import android.widget.EditText
import android.widget.TableLayout
import android.widget.Button
import android.widget.TableRow
import android.widget.TextView
import android.util.TypedValue

class ManualModeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual)
        val queryEntry=findViewById<EditText>(R.id.manual_query)
        var startQuery=findViewById<Button>(R.id.start_manual_query)
        val queryOutput=findViewById<TableLayout>(R.id.manual_query_table)
        queryEntry.setText("SELECT\nFROM\nWHERE")
        val db = SQLiteDatabase.openDatabase("/data/data/com.example.library_database/databases/LMS.db", null, SQLiteDatabase.OPEN_READWRITE)


        startQuery.setOnClickListener {
            val cursor = db.rawQuery(queryEntry.text.toString(),null)
            queryOutput.removeAllViews()

            val columnCount = cursor.columnCount
            val data = Array(cursor.count + 1) { arrayOfNulls<String>(columnCount) }

            for (i in 0 until columnCount) {
                data[0][i] = cursor.getColumnName(i)+ " "
            }

            var rowIndex = 1
            if (cursor.moveToFirst()) {
                do {
                    for (i in 0 until columnCount) {
                        data[rowIndex][i] = cursor.getString(i) + " "
                    }
                    rowIndex++
                } while (cursor.moveToNext())
            }

            for (i in data.indices) {
                val tableRow = TableRow(queryOutput.context)
                for (j in 0 until columnCount) {
                    val textView = TextView(queryOutput.context)
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7.toFloat())
                    textView.text = data[i][j] ?: ""
                    tableRow.addView(textView)
                }
                queryOutput.addView(tableRow)
            }
            cursor.close()
            db.close()
        }
    }
}