package com.example.library_database

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import kotlin.random.Random


class CreateBorrowerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_borrower_activity)

        val db = SQLiteDatabase.openDatabase("/data/data/com.example.library_database/databases/LMS.db", null, SQLiteDatabase.OPEN_READWRITE)

        val borrowerNameField=findViewById<EditText>(R.id.book_name_field)
        val borrowerAddressField=findViewById<EditText>(R.id.borrower_address_field)
        val borrowerPhoneField=findViewById<EditText>(R.id.borrower_phone_field)
        val confirmBorrowerButton=findViewById<Button>(R.id.confirm_borrower_button)
        val borrowerTable=findViewById<TableLayout>(R.id.borrower_table)
        ///--------------------------------------------------------------------------------
        confirmBorrowerButton.setOnClickListener {
            var Data = Array(4) { arrayOf("","","","") }
            val random = Random
            var randomSixDigitNumber = random.nextInt(900000)+100000
            //Log.v("HELLO",randomSixDigitNumber.toString())
            var continueGenerateRandomNumber=true
            do {
                randomSixDigitNumber = random.nextInt(900000)+100000
                var cursor = db.rawQuery("""SELECT NOT EXISTS(SELECT card_no FROM BORROWER WHERE card_no='123456')""", null)
                cursor.moveToFirst()
                continueGenerateRandomNumber=if (cursor.getString(0)=="1") true else false
                //Log.v("HELLO",continueGenerateRandomNumber.toString())
            }while(continueGenerateRandomNumber)

            var cursor = db.rawQuery("""INSERT INTO BORROWER (Card_No, Name, Address, Phone) VALUES ($randomSixDigitNumber, '$borrowerNameField.text.toString()', '$borrowerAddressField.text.toString()', '$borrowerPhoneField.text.toString()');""", null)
            Data = arrayOf(arrayOf(randomSixDigitNumber.toString(),borrowerNameField.text.toString(),borrowerAddressField.text.toString(),borrowerPhoneField.text.toString()))
            BorrowerInfo.setBorrowerId(randomSixDigitNumber)
            Log.v("HELLO",BorrowerInfo.getBorrowerId().toString())
            for (i in Data.indices) {
                val tableRow = TableRow(this)
                for (j in Data[i].indices) {
                    val textView = TextView(this)
                    textView.text = Data[i][j] + "   "
                    tableRow.addView(textView)
                }
                borrowerTable.addView(tableRow)
            }
        }
    }
}