package com.example.library_database

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import android.widget.AdapterView
import android.database.sqlite.SQLiteDatabase
import android.widget.ArrayAdapter


class AddBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_book_activity)

        //val bookQueryResult = mutableListOf<String>()
        val publisherQueryResult = mutableListOf<String>()

        val newBookName=findViewById<EditText>(R.id.book_id_search)
        val newBookPublisherSpinner = findViewById<Spinner>(R.id.new_book_publisher)
        val newBookAuthor=findViewById<EditText>(R.id.new_book_author)
        val confirmCheckoutButton = findViewById<View>(R.id.confirm_new_book)
        var publisherSelected=""
        //---------------------------------------------------------------------------
        val db = SQLiteDatabase.openDatabase(
            "/data/data/com.example.library_database/databases/LMS.db",
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
        var cursor = db.rawQuery("SELECT publisher_name FROM PUBLISHER", null)
        publisherQueryResult.add("Select Publisher")
        if (cursor.moveToFirst()) {
            do {
                val publisher = cursor.getString(0)
                publisherQueryResult.add(publisher)
            } while (cursor.moveToNext())
        }
        val branchList = ArrayAdapter(this, android.R.layout.simple_spinner_item, publisherQueryResult)
        newBookPublisherSpinner.adapter = branchList
        cursor.close()
        db.close()


        //---------------------------------------------------------------------------
        newBookPublisherSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // code to handle when an item is selected
                publisherSelected = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // code to handle when nothing is selected

            }
        }

        confirmCheckoutButton.setOnClickListener {
            val db2 = SQLiteDatabase.openDatabase(
                "/data/data/com.example.library_database/databases/LMS.db",
                null,
                SQLiteDatabase.OPEN_READWRITE
            )
            //db2.beginTransaction()
            var newcursor = db2.rawQuery("""SELECT MAX(book_id) FROM BOOK;""", null)
            newcursor.moveToFirst()
            var book_id_query=newcursor.getString(0).toInt()+1

            db2.execSQL("""INSERT INTO BOOK (book_id, Title, Book_publisher) VALUES ($book_id_query, '${newBookName.text.toString()}', '$publisherSelected');""")
            db2.execSQL("""INSERT INTO BOOK_AUTHORS (book_id, Author_Name) VALUES ($book_id_query, '${newBookAuthor.text.toString()}');""")
            db2.execSQL("""INSERT INTO BOOK_COPIES (Book_Id, Branch_Id, No_Of_Copies)
                                    VALUES ($book_id_query, 1, 5),
                                          ($book_id_query, 2, 5),
                                          ($book_id_query, 3, 5),
                                          ($book_id_query, 4, 5),
                                          ($book_id_query, 5, 5);""")

            //db2.setTransactionSuccessful()
            //db2.endTransaction()
            newcursor.close()
            db2.close()

        }
    }

}