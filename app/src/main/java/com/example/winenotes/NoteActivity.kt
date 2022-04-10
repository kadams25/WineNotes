package com.example.winenotes

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.winenotes.database.AppDatabase
import com.example.winenotes.database.Note
import com.example.winenotes.databinding.ActivityNoteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding

    private var purpose : String? = ""
    private var noteId : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        purpose = intent.getStringExtra(
            getString(R.string.intent_purpose_key)
        )

        if (purpose.equals(getString(R.string.intent_purpose_update_note))) {
            noteId = intent.getLongExtra(
                getString(R.string.intent_key_note_id),
            -1
            )

            CoroutineScope(Dispatchers.IO).launch {
                val note = AppDatabase.getDatabase(applicationContext)
                    .noteDao()
                    .getNote(noteId)

                withContext(Dispatchers.Main) {
                    binding.titleEditText.setText(note.title)
                    binding.noteEditText.setText(note.notes)
                    binding.modDateTextView.setText(note.lastModified)
                }
            }
        } else {
            binding.modDateTextView.visibility = View.GONE
        }

        setTitle("$purpose Note")
    }

    override fun onBackPressed() {

//        val now : Date = Date()
//
//        val databaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//        databaseDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
//
//        var dateString : String = databaseDateFormat.format(now)

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

            if (purpose.equals(getString(R.string.intent_purpose_add_note))) {
                // add new note
                val noteInfo = Note(0, title, note, lastModified)
                noteId = noteDao.addNote(noteInfo)
                Log.i("STATUS", "inserted new note ${noteInfo}")
            } else {
                //update current note
                val noteInfo = Note(noteId, title, note, lastModified)

                noteDao.updateNote(noteInfo)
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