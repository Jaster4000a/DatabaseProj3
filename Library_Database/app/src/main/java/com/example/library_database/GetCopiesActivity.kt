package com.example.library_database

import android.R.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.database.sqlite.SQLiteDatabase
import android.widget.TableLayout
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TableRow
import android.widget.TextView


class GetCopiesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_copies_activity)

        val bookCopySpinner = findViewById<Spinner>(R.id.book_copy_spinner)
        val bookCopiesTable = findViewById<TableLayout>(R.id.book_copies_table)
        var bookSelected = ""
        val bookQueryResult = mutableListOf<String>()

        val db = SQLiteDatabase.openDatabase(
            "/data/data/com.example.library_database/databases/LMS.db",
            null,
            SQLiteDatabase.OPEN_READWRITE
        )

        //----------------------------------------------------------------
        bookCopySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // code to handle when an item is selected
                bookSelected = parent.getItemAtPosition(position) as String
                Log.v("HELLO",bookSelected)

                //----------
                bookCopiesTable.removeAllViews()
                val cursor = db.rawQuery(
                    """SELECT b.Title, bl.Branch_Id, COUNT(*) AS Num_Copies_Loaned_Out
FROM BOOK_LOANS bl
INNER JOIN BOOK b ON bl.Book_Id = b.Book_Id
WHERE b.Title = '${bookSelected.replace("'", "''")}'
GROUP BY b.Title, bl.Branch_Id;""", null
                )
                var Data = Array(3) { arrayOf("", "", "") }
                Data += arrayOf(
                    cursor.getColumnName(0) + "     ",
                    cursor.getColumnName(1) + "     ",
                    cursor.getColumnName(2)
                )
                if (cursor.moveToFirst()) {
                    do {
                        val book = cursor.getString(0)
                        val branch = cursor.getString(1)
                        val copies = cursor.getString(2)
                        Data += arrayOf(book, branch, copies)
                    } while (cursor.moveToNext())
                }

                    for (i in Data.indices) {
                        val tableRow = TableRow(this@GetCopiesActivity)
                        for (j in Data[i].indices) {
                            val textView = TextView(this@GetCopiesActivity)
                            textView.text = Data[i][j] + " "
                            tableRow.addView(textView)
                        }
                        bookCopiesTable.addView(tableRow)
                    }


                //----------
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // code to handle when nothing is selected
            }
        }
        val bookCursor=db.rawQuery("""SELECT title FROM BOOK""",null)
        if (bookCursor.moveToFirst()) {
            do {
                val branch = bookCursor.getString(0)
                bookQueryResult.add(branch)
            } while (bookCursor.moveToNext())
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bookQueryResult)
        bookCopySpinner.adapter=adapter


    }
}