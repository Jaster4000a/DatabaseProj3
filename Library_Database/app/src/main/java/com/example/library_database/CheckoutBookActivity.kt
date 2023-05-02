package com.example.library_database

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

class CheckoutBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout_book_activity)

        val db = SQLiteDatabase.openDatabase(
            "/data/data/com.example.library_database/databases/LMS.db",
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
        val bookQueryResult = mutableListOf<String>()
        val branchQueryResult = mutableListOf<String>()

        val branchListSpinner = findViewById<Spinner>(R.id.branch_list_spinner)
        val bookListSpinner = findViewById<Spinner>(R.id.book_list_spinner)
        val confirmCheckoutButton = findViewById<View>(R.id.confirm_checkout_button)
        var data = Array(3) { arrayOf("", "", "") }
        var branchSelected=""
        var bookSelected=""

        //------------------------------------------------------------------------
        branchListSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // code to handle when an item is selected
                branchSelected = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // code to handle when nothing is selected
            }
        }
        bookListSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // code to handle when an item is selected
                bookSelected = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // code to handle when nothing is selected
            }
        }
        confirmCheckoutButton.setOnClickListener {
            val db = SQLiteDatabase.openDatabase(
                "/data/data/com.example.library_database/databases/LMS.db",
                null,
                SQLiteDatabase.OPEN_READWRITE
            )

            db.execSQL("""UPDATE BOOK_COPIES SET No_Of_Copies = No_Of_Copies - 1 WHERE Book_Id = book_id AND Branch_Id = branch_id;""")

            var confirmationCursor = db.rawQuery("""SELECT b.Book_Id, lb.Branch_Id, bc.no_of_copies
                        FROM BOOK b
                        JOIN BOOK_COPIES bc ON b.Book_Id = bc.Book_Id
                        JOIN LIBRARY_BRANCH lb ON bc.Branch_Id = lb.Branch_Id
                        WHERE b.Title = '${bookSelected.replace("'", "''")}' AND lb.Branch_Name = '$branchSelected';
            """, null)

            val confirmationTableLayout = findViewById<TableLayout>(R.id.borrower_table)
            confirmationTableLayout.removeAllViews()
            var confirmationData = Array(3) { arrayOf("", "", "") }
            confirmationData += arrayOf(
                confirmationCursor.getColumnName(0) + "     ",
                confirmationCursor.getColumnName(1) + "     ",
                confirmationCursor.getColumnName(2)
            )

            if (confirmationCursor.moveToFirst()) {
                do {

                    val book = confirmationCursor.getString(0)
                    val branch = confirmationCursor.getString(1)
                    val copies = confirmationCursor.getString(2)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val currentDate = Date()
                    val dateString = dateFormat.format(currentDate) + " 00:00:00.000000"
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, 14) // add 14 days to the current date
                    val futureDate = calendar.time
                    val futureDateString = dateFormat.format(futureDate) + " 00:00:00.000000"
                    val returnedQuery = """INSERT INTO BOOK_LOANS (Book_Id, Branch_Id, Card_No, Date_Out, Due_Date, Returned_date) VALUES (${book.replace("'", "''")}, $branch, '${BorrowerInfo.getBorrowerId().toString().replace("'", "''")}', '$dateString', '$futureDateString', NULL);"""
                    db.execSQL(returnedQuery)
                    confirmationData += arrayOf(book, branch, copies)
                } while (confirmationCursor.moveToNext())


                for (i in confirmationData.indices) {
                    val tableRow = TableRow(this)
                    for (j in confirmationData[i].indices) {
                        val textView = TextView(this)
                        textView.text = confirmationData[i][j]
                        tableRow.addView(textView)
                    }
                    confirmationTableLayout.addView(tableRow)
                }
            }
            confirmationCursor.close()
            db.close()

        }

        //------------------------------------------------------------------------
        var cursor = db.rawQuery("SELECT branch_name FROM LIBRARY_BRANCH", null)
        branchQueryResult.add("Select Branch")
        if (cursor.moveToFirst()) {
            do {
                val branch = cursor.getString(0)
                branchQueryResult.add(branch)
            } while (cursor.moveToNext())
        }
        val branchList = ArrayAdapter(this, android.R.layout.simple_spinner_item, branchQueryResult)
        branchListSpinner.adapter = branchList
        //------------------------------------------------------------------------

        cursor = db.rawQuery("SELECT title FROM BOOK", null)
        bookQueryResult.add("Select Book")
        if (cursor.moveToFirst()) {
            do {
                val book = cursor.getString(0)
                bookQueryResult.add(book)
            } while (cursor.moveToNext())
        }

        val bookList = ArrayAdapter(this, android.R.layout.simple_spinner_item, bookQueryResult)
        bookListSpinner.adapter = bookList
        //------------------------------------------------------------------------
        val tableLayout = findViewById<TableLayout>(R.id.borrower_table)

        cursor = db.rawQuery(
            """SELECT b.Title, lb.Branch_Name, bc.no_of_copies
FROM BOOK b
JOIN BOOK_COPIES bc ON b.Book_Id = bc.Book_Id
JOIN LIBRARY_BRANCH lb ON bc.Branch_Id = lb.Branch_Id
LEFT JOIN BOOK_LOANS bl ON b.Book_Id = bl.Book_Id AND bc.Branch_Id = bl.Branch_Id AND bl.Returned_date IS NULL
WHERE bl.Book_Id IS NULL OR bc.No_Of_Copies > (SELECT COUNT(*) FROM BOOK_LOANS WHERE Book_Id = b.Book_Id AND Branch_Id = bc.Branch_Id AND Returned_date IS NULL)
ORDER BY lb.Branch_Name, b.Title;""", null
        )
        data += arrayOf(cursor.getColumnName(0)+"     ",cursor.getColumnName(1)+"     ",cursor.getColumnName(2))

        if (cursor.moveToFirst()) {
            do {
                val book = cursor.getString(0)
                val branch = cursor.getString(1)
                val copies = cursor.getString(2)
                data += arrayOf(book, branch,copies)
            } while (cursor.moveToNext())

            for (i in data.indices) {
                val tableRow = TableRow(this)
                for (j in data[i].indices) {
                    val textView = TextView(this)
                    textView.text = data[i][j]
                    tableRow.addView(textView)
                }
                tableLayout.addView(tableRow)
            }
            //------------------------------------------------------------------------
            cursor.close()
            db.close()
        }
    }
}