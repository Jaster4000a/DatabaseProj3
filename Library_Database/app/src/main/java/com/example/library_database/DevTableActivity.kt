package com.example.library_database

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import android.util.Log
import android.widget.Button
import android.database.sqlite.SQLiteDatabase
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.util.TypedValue
import kotlin.random.Random

class DevTableActivity: AppCompatActivity() {
//    fun updateListTable(db: SQLiteDatabase, tableName:String, ListTable:TableLayout){
//        val cursor = db.rawQuery(
//            """SELECT * FROM $tableName""", null
//        )
//        ListTable.removeAllViews()
//        val columnCount = cursor.columnCount
//        val data = Array(columnCount) { arrayOfNulls<String>((columnCount)}
//        for (i in 0 until columnCount) {
//            data[i] = Array(columnCount) { cursor.getColumnName(i) }
//        }
//
//        if (cursor.moveToFirst()) {
//            do {
//                val cardNo = cursor.getString(0)
//                val BorrowerID = cursor.getString(1)
//                val BorrowerName = cursor.getString(2)
//                data += arrayOf(cardNo, BorrowerID, BorrowerName)
//            } while (cursor.moveToNext())
//
//            for (i in data.indices) {
//                val tableRow = TableRow(this)
//                for (j in data[i].indices) {
//                    val textView = TextView(this)
//                    textView.text = data[i][j]
//                    tableRow.addView(textView)
//                }
//                ListTable.addView(tableRow)
//            }
//
//        }
//    }
fun updateListTable(db: SQLiteDatabase, tableName: String, listTable: TableLayout) {
    val cursor = db.rawQuery("SELECT * FROM $tableName", null)
    listTable.removeAllViews()

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
        val tableRow = TableRow(listTable.context)
        for (j in 0 until columnCount) {
            val textView = TextView(listTable.context)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7.toFloat())
            textView.text = data[i][j] ?: ""
            tableRow.addView(textView)
        }
        listTable.addView(tableRow)
    }
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_tables)
        val tabs = findViewById<TabLayout>(R.id.table_tabs)
        val listTableOutput=findViewById<TableLayout>(R.id.output_dev_table)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val db = SQLiteDatabase.openDatabase("/data/data/com.example.library_database/databases/LMS.db", null, SQLiteDatabase.OPEN_READWRITE)

                // check if the selected tab is the one you want to trigger the function for
                if (tab.text == "Borrower") {
                    updateListTable(db,"BORROWER",listTableOutput)
                }else if (tab.text == "Book_Loans"){
                    updateListTable(db,"BOOK_LOANS",listTableOutput)
                }else if (tab.text == "Library_branch"){
                    updateListTable(db,"LIBRARY_BRANCH",listTableOutput)
                }else if (tab.text == "Book"){
                    updateListTable(db,"BOOK",listTableOutput)
                }else if (tab.text == "Book_authors"){
                    updateListTable(db,"BOOK_AUTHORS",listTableOutput)
                }else if (tab.text == "Book_copies"){
                    updateListTable(db,"BOOK_COPIES",listTableOutput)
                }else if (tab.text == "Publisher"){
                    updateListTable(db,"PUBLISHER",listTableOutput)
                }
                db.close()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // empty implementation
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // empty implementation
            }
        })
    }
}