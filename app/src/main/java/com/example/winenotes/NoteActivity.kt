package com.example.winenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.winenotes.database.AppDatabase
import com.example.winenotes.database.Note
import com.example.winenotes.databinding.ActivityNoteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding

    private var purpose : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        purpose = intent.getStringExtra(
            getString(R.string.intent_purpose_key)
        )

        setTitle("${purpose} Name")
    }

    override fun onBackPressed() {
        val note = binding.noteEditText.getText().toString().trim()
        val title = binding.titleEditText.getText().toString().trim()
        val lastModified = binding.modDateTextView.getText().toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(applicationContext,
            "Title cannot be empty", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val noteDao = AppDatabase.getDatabase(applicationContext)
                .noteDao()

            var noteId : Long

            if (purpose.equals(getString(R.string.intent_purpose_add_note))) {
                // add new note
                val noteInfo = Note(0, title, note, lastModified)
                noteId = noteDao.addNote(noteInfo)
                Log.i("STATUS", "inserted new note ${noteInfo}")
            } else {
                //update current note
                TODO("Not implemented")
            }

            val intent = Intent()

            intent.putExtra(
                getString(R.string.intent_key_note_id),
                noteId
            )

            withContext(Dispatchers.Main) {
                setResult(RESULT_OK, intent)
                super.onBackPressed()
            }
        }
    }
}