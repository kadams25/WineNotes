package com.example.winenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winenotes.database.AppDatabase
import com.example.winenotes.database.Note
import com.example.winenotes.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: MyAdapter
    private val notes = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.myRecyclerView.setLayoutManager(layoutManager)
        binding.myRecyclerView.setHasFixedSize(true)

        val divider = DividerItemDecoration(
            applicationContext, layoutManager.orientation
        )
        binding.myRecyclerView.addItemDecoration(divider)

        adapter = MyAdapter()
        binding.myRecyclerView.setAdapter(adapter)

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
            return true
        } else if (item.getItemId() == R.id.menu_sortTtile) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewNote() {
        val intent = Intent(applicationContext, NoteActivity::class.java)

    }

    inner class MyViewHolder(val view: TextView) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

//            fun setText(text: String) {
//                itemView.findViewById<TextView>(R.id.note_textView).setText(text)
//            }

        override fun onClick(view: View?) {
            TODO("Not yet implemented")
        }
    }

    inner class MyAdapter() :
        RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_view, parent, false) as TextView
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.view.text = notes[position].toString()
        }

        override fun getItemCount(): Int {
            return notes.size
        }

    }
}