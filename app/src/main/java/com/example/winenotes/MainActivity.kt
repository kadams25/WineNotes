package com.example.winenotes

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winenotes.database.AppDatabase
import com.example.winenotes.database.Note
import com.example.winenotes.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: MyAdapter
    private val notes = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerview.setLayoutManager(layoutManager)

        val dividerItemDecoration = DividerItemDecoration(
            applicationContext, layoutManager.getOrientation()
        )
        binding.recyclerview.addItemDecoration(dividerItemDecoration)

        adapter = MyAdapter()
        binding.recyclerview.setAdapter(adapter)

        loadAllNotes()
    }

    private fun loadAllNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.noteDao()
            val results = dao.getAllNotes()

            withContext(Dispatchers.Main) {
                notes.clear()
                notes.addAll(results)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun sortNotesByTitle() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.noteDao()
            val results = dao.sortAllNotesByTitle()

            withContext(Dispatchers.Main) {
                notes.clear()
                notes.addAll(results)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun sortNotesByDate() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.noteDao()
            val results = dao.sortAllNotesByModified()

            withContext(Dispatchers.Main) {
                notes.clear()
                notes.addAll(results)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.menu_addNote) {
            addNewNote()
            return true
        } else if (item.getItemId() == R.id.menu_sortDate) {
            sortNotesByDate()
            return true
        } else if (item.getItemId() == R.id.menu_sortTtile) {
            sortNotesByTitle()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private val startForAddResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result : ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK) {
                loadAllNotes()
            }
        }

    private val startForUpdateResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result : ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK) {
                loadAllNotes()
            }
        }

    private fun addNewNote() {
        val intent = Intent(applicationContext, NoteActivity::class.java)
        intent.putExtra(
            getString(R.string.intent_purpose_key),
            getString(R.string.intent_purpose_add_note)
        )
        startForAddResult.launch(intent)
    }

    inner class MyViewHolder(val view: TextView) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener, View.OnLongClickListener {

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(view: View?) {
            val intent = Intent(applicationContext, NoteActivity::class.java)

            intent.putExtra(
                getString(R.string.intent_purpose_key),
                getString(R.string.intent_purpose_update_note)
            )

            val note = notes[adapterPosition]
            intent.putExtra(
                getString(R.string.intent_key_note_id),
                note.id
            )

            startForUpdateResult.launch(intent)
        }

        override fun onLongClick(view: View?): Boolean {
            val note = notes[adapterPosition]
            val builder = AlertDialog.Builder(view!!.context)
                .setTitle("Confirm delete")
                .setMessage("Are you sure you want to delete ${note.title}?")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) {
                    dialogInterface, whichButton ->

                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getDatabase(applicationContext)
                            .noteDao()
                            .deleteNote(note)

                        loadAllNotes()
                    }
                }
            builder.show()
            return true
        }
    }

    inner class MyAdapter :
        RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_view, parent, false) as TextView
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val note = notes[position]
            holder.view.setText(note.title)
        }

        override fun getItemCount(): Int {
            return notes.size
        }
    }
}