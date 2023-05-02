package com.example.library_database

import android.R.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.TableLayout
import android.widget.TextView
import android.widget.TableRow
import android.widget.ArrayAdapter
import android.database.sqlite.SQLiteDatabase
import android.widget.EditText

class ListBorrowersActivity : AppCompatActivity() {
        fun updateBorrowerListTable(db: SQLiteDatabase, borrowerId:String, borrowerName:String, borrowerListTable:TableLayout){
            val cursor = db.rawQuery(
                """SELECT bo.Card_No AS Borrower_ID, bo.Name AS Borrower_Name,
      COALESCE(SUM(CASE WHEN julianday(bl.Returned_date) > julianday(bl.Due_Date) THEN (julianday(bl.Returned_date) - julianday(bl.Due_Date)) * lb.LateFee ELSE 0 END), 0) AS LateFee_Balance
FROM BORROWER bo
LEFT JOIN BOOK_LOANS bl ON bo.Card_No = bl.Card_No
LEFT JOIN LIBRARY_BRANCH lb ON bl.Branch_Id = lb.Branch_Id
WHERE bo.Card_No LIKE '%$borrowerId%' AND bo.Name LIKE '%$borrowerName%'
GROUP BY bo.Card_No, bo.Name;""", null
            )
            borrowerListTable.removeAllViews()
            var data = Array(3) { arrayOf("", "", "") }
            data += arrayOf(
                cursor.getColumnName(0) + "     ",
                cursor.getColumnName(1) + "     ",
                cursor.getColumnName(2)
            )

            if (cursor.moveToFirst()) {
                do {
                    val cardNo = cursor.getString(0)
                    val BorrowerID = cursor.getString(1)
                    val BorrowerName = cursor.getString(2)
                    data += arrayOf(cardNo, BorrowerID, BorrowerName)
                } while (cursor.moveToNext())

                for (i in data.indices) {
                    val tableRow = TableRow(this)
                    for (j in data[i].indices) {
                        val textView = TextView(this)
                        textView.text = data[i][j]
                        tableRow.addView(textView)
                    }
                    borrowerListTable.addView(tableRow)
                }

            }
            cursor.close()
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_borrowers)

        val borrowerIdSearch = findViewById<TextView>(R.id.borrower_id_search)
        val borrowerNameSearch = findViewById<TextView>(R.id.borrower_name_search)
        val borrowerListTable = findViewById<TableLayout>(R.id.borrower_table)
        val listBorrowerButton=findViewById<View>(R.id.list_borrower_button_go)
        val db = SQLiteDatabase.openDatabase(
            "/data/data/com.example.library_database/databases/LMS.db",
            null,
            SQLiteDatabase.OPEN_READWRITE
        )

        listBorrowerButton.setOnClickListener {
            updateBorrowerListTable(db,borrowerIdSearch.text.toString(),borrowerNameSearch.text.toString(),borrowerListTable)
        }
        updateBorrowerListTable(db,borrowerIdSearch.text.toString(),borrowerNameSearch.text.toString(),borrowerListTable)
    }
}