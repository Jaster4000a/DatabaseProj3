package com.example.library_database

class BorrowerInfo {
    companion object {
        private var borrower_id_in_use: Int = 0

        fun setBorrowerId(id: Int) {
            borrower_id_in_use = id
        }

        fun getBorrowerId(): Int {
            return borrower_id_in_use
        }
    }
}