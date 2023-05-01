package com.example.library_database

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.database.sqlite.SQLiteDatabase
import android.util.TypedValue


class GetLateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_late_activity)

        val startRange = findViewById<TextView>(R.id.book_id_field)
        val endRange = findViewById<TextView>(R.id.book_name_field)
        val lateTable = findViewById<TableLayout>(R.id.get_borrower_table)
        val lateButton = findViewById<View>(R.id.get_books_list_button)
        //--------------------------------------------------------------------
        lateButton.setOnClickListener {

            val db = SQLiteDatabase.openDatabase(
                "/data/data/com.example.library_database/databases/LMS.db",
                null,
                SQLiteDatabase.OPEN_READWRITE
            )
            val cursor = db.rawQuery(
                """SELECT bl.Book_Id, b.Title, bl.Branch_Id, lb.Branch_Name, bl.Card_No, bo.Name AS Borrower_Name, bl.Date_Out, bl.Due_Date, bl.Returned_date,
CASE WHEN bl.Returned_date IS NULL THEN 0 ELSE julianday(bl.Returned_date) - julianday(bl.Due_Date) END AS Days_Late,
CASE WHEN bl.Returned_date IS NULL OR julianday(bl.Returned_date) <= julianday(bl.Due_Date) THEN 0 ELSE (julianday(bl.Returned_date) - julianday(bl.Due_Date)) * lb.LateFee END AS Late_Fee
FROM BOOK_LOANS bl
INNER JOIN BOOK b ON bl.Book_Id = b.Book_Id
INNER JOIN LIBRARY_BRANCH lb ON bl.Branch_Id = lb.Branch_Id
INNER JOIN BORROWER bo ON bl.Card_No = bo.Card_No
WHERE bl.Due_Date > '${startRange.text.toString()}' and bl.Due_Date < '${endRange.text.toString()}';""",
                null
            )
            lateTable.removeAllViews()
            var data = Array(7) { arrayOf("", "", "", "", "", "","") }
            data += arrayOf(
                cursor.getColumnName(0) + " ",
                cursor.getColumnName(1) + " ",
                cursor.getColumnName(2) + " ",
                cursor.getColumnName(3) + " ",
                cursor.getColumnName(4) + " ",
                cursor.getColumnName(5) + " "
            )

            if (cursor.moveToFirst()) {
                do {
                    val book_id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val branch_id = cursor.getString(2)
                    val branch_name = cursor.getString(3)
                    val Card_No = cursor.getString(4)
                    val name = cursor.getString(5)
                    data += arrayOf(book_id, title, branch_id, branch_name, Card_No, name)
                    Log.v("HELLO",name)
                } while (cursor.moveToNext())

                for (i in data.indices) {
                    val tableRow = TableRow(this)
                    for (j in data[i].indices) {
                        val textView = TextView(this)
                        textView.text = data[i][j]
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,9.toFloat())
                        tableRow.addView(textView)
                    }
                    lateTable.addView(tableRow)
                }
            }
        }
    }
}