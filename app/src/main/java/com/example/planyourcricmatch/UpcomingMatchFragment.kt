package com.example.planyourcricmatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UpcomingMatchFragment : Fragment() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: MatchesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upcoming_match, container, false)
        dbHelper = DatabaseHelper(requireContext())
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MatchesAdapter(requireContext(), fetchMatches(), activity as PaymentCallback)
        recyclerView.adapter = adapter

        return view
    }

    private fun fetchMatches(): MutableList<Match> { // Adjusted to use MatchesAdapter.Match
        val matches = mutableListOf<Match>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_NAME}"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val match = Match(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                    matchName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MATCH_NAME)) ?: "",
                    team = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TEAM)) ?: "",
                    againstTeam = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AGAINST_TEAM)) ?: "",
                    date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE)) ?: "",
                    location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LOCATION)) ?: "",
                    format = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FORMAT)) ?: "",
                    stadium = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STADIUM)) ?: "",
                    slot = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT)) ?: "",
                    weather = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WEATHER)) ?: "",
                    status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS)) ?: "",
                )

                matches.add(match)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return matches
    }
}
