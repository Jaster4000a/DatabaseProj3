package com.example.library_database

import android.R.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.database.sqlite.SQLiteDatabase
import android.widget.TableLayout
import android.widget.AdapterView
import android.widget.TableRow
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import android.util.TypedValue
import java.text.DecimalFormat

class BookInfoActivity : AppCompatActivity() {
    fun updateBorrowerListTable(db: SQLiteDatabase, bookId:String, bookName:String, bookListTable:TableLayout){
        val cursor = db.rawQuery(
            """SELECT b.Book_Id, b.Title, lb.Branch_Id, lb.Branch_Name,
CASE WHEN bl.Returned_date IS NULL OR julianday(bl.Returned_date) <= julianday(bl.Due_Date) THEN 'Non-Applicable' ELSE '${'$'} ' || printf('%.2f',CAST((julianday(bl.Returned_date) - julianday(bl.Due_Date)) * lb.LateFee AS DECIMAL(10,2))) END AS Late_Fee_Amount,
CASE WHEN bl.Returned_date IS NULL OR julianday(bl.Returned_date) <= julianday(bl.Due_Date) THEN 0 ELSE CAST((julianday(bl.Returned_date) - julianday(bl.Due_Date)) * lb.LateFee AS DECIMAL(10,2)) END AS Late_Fee_Amount_hidden
FROM BOOK_LOANS bl
INNER JOIN BOOK b ON bl.Book_Id = b.Book_Id
INNER JOIN LIBRARY_BRANCH lb ON bl.Branch_Id = lb.Branch_Id
INNER JOIN BORROWER bo ON bl.Card_No = bo.Card_No
WHERE b.Book_Id LIKE '%$bookId%' AND b.Title LIKE '%$bookName%'
GROUP BY b.Book_Id, lb.Branch_Id
HAVING Late_Fee_Amount IS NOT NULL
ORDER BY Late_Fee_Amount_hidden DESC;""", null
        )
        bookListTable.removeAllViews()
        var data = Array(5) { arrayOf("", "", "","","") }
        data += arrayOf(
            cursor.getColumnName(0) + "     ",
            cursor.getColumnName(1) + "     ",
            cursor.getColumnName(2) + "     ",
            cursor.getColumnName(3) + "     ",
            cursor.getColumnName(4)
        )

        if (cursor.moveToFirst()) {
            do {
                val cardNo = cursor.getString(0)
                val BorrowerID = cursor.getString(1)
                val BorrowerName = cursor.getString(2)
                val branch = cursor.getString(3)
                val fees = cursor.getString(4)
                data += arrayOf(cardNo, BorrowerID, BorrowerName,branch,fees)
            } while (cursor.moveToNext())

            for (i in data.indices) {
                val tableRow = TableRow(this)
                for (j in data[i].indices) {
                    val textView = TextView(this)
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9.toFloat())
                    textView.text = data[i][j]
                    tableRow.addView(textView)
                }
                bookListTable.addView(tableRow)
            }

        }
        cursor.close()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.book_info)

        val bookIdSearch = findViewById<TextView>(R.id.book_id_query)
        val bookNameSearch = findViewById<TextView>(R.id.book_name_query)
        val bookListTable = findViewById<TableLayout>(R.id.book_info_table)
        val bookInfoSearchButton=findViewById<View>(R.id.book_info_search)
        val db = SQLiteDatabase.openDatabase(
            "/data/data/com.example.library_database/databases/LMS.db",
            null,
            SQLiteDatabase.OPEN_READWRITE
        )

        bookInfoSearchButton.setOnClickListener {
            updateBorrowerListTable(db,bookIdSearch.text.toString(),bookNameSearch.text.toString(),bookListTable)
        }
        updateBorrowerListTable(db,bookIdSearch.text.toString(),bookNameSearch.text.toString(),bookListTable)
    }
}