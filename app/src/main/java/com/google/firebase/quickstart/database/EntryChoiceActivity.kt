package com.google.firebase.quickstart.database

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.example.internal.BaseEntryChoiceActivity
import com.firebase.example.internal.Choice
import com.firebase.example.internal.ChoiceAdapter

class EntryChoiceActivity : BaseEntryChoiceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, com.google.firebase.quickstart.database.kotlin.MainActivity::class.java))
    }

    override fun getChoices(): List<Choice> {
        return listOf(
                Choice(
                        "Java",
                        "Run the Firebase Realtime Database quickstart written in Java.",
                        Intent(this, com.google.firebase.quickstart.database.java.MainActivity::class.java)),
                Choice(
                        "Kotlin",
                        "Run the Firebase Realtime Database quickstart written in Kotlin.",
                        Intent(this, com.google.firebase.quickstart.database.kotlin.MainActivity::class.java))
        )
    }
}
