package com.example.winenotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winenotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val adapter = MyAdapter()

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
        binding.myRecyclerView.setAdapter(adapter)
    }

    inner class MyViewholder(val itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            itemView.findViewById<View>(R.id.note_ConstraintLayout).setOnClickListener(this)
        }

        fun setText(text: String) {
            itemView.findViewById<TextView>(R.id.note_textView).setText(text)
        }

        override fun onClick(view: View?) {
            TODO("Not yet implemented")
        }
    }

    inner class MyAdapter() : RecyclerView.Adapter<MyViewholder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: MyViewholder, position: Int) {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            return 0
        }

    }
}