package com.example.library_database

import android.R.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.example.library_database.BorrowerInfo

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val checkoutBookButton=findViewById<View>(R.id.checkout_book_button)
        val createBorrowerButton=findViewById<View>(R.id.create_borrower_button)
        val addBookButton=findViewById<View>(R.id.add_book_button)
        val getCopiesButton=findViewById<View>(R.id.get_copies_button)
        val getLateButton=findViewById<View>(R.id.get_late_button)
        val listBorrowers=findViewById<View>(R.id.list_borrower_button)
        val bookInfo=findViewById<View>(R.id.book_info_button)


        checkoutBookButton.isEnabled=false


        checkoutBookButton.setOnClickListener{
            val intent = Intent(this, CheckoutBookActivity::class.java)
            startActivity(intent)
        }
        createBorrowerButton.setOnClickListener{
            val intent = Intent(this, CreateBorrowerActivity::class.java)
            startActivity(intent)
            checkoutBookButton.isEnabled=true
        }
        addBookButton.setOnClickListener{
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }
        getCopiesButton.setOnClickListener{
            val intent = Intent(this, GetCopiesActivity::class.java)
            startActivity(intent)
        }
        getLateButton.setOnClickListener{
            val intent = Intent(this, GetLateActivity::class.java)
            startActivity(intent)
        }
        listBorrowers.setOnClickListener{
            val intent = Intent(this, ListBorrowersActivity::class.java)
            startActivity(intent)
        }
        bookInfo.setOnClickListener{
            val intent = Intent(this, BookInfoActivity::class.java)
            startActivity(intent)
        }
    }


}