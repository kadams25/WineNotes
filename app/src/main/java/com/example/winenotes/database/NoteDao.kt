package com.example.winenotes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {

    @Insert
    fun addNote(note : Note) : Long

    @Update
    fun updateNote(note: Note)

    @Query("SELECT * FROM note")
    fun getAllNotes(): List<Note>

    @Query("SELECT * FROM NOTE ORDER BY title")
    fun sortAllNotesByTitle(): List<Note>

    @Query("SELECT * FROM NOTE ORDER BY last_modified DESC")
    fun sortAllNotesByModified(): List<Note>

    @Query("SELECT * FROM note WHERE id = :noteId")
    fun getNote(noteId : Long) : Note
}