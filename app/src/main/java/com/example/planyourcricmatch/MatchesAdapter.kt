package com.example.planyourcricmatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MatchesAdapter(
    private val context: Context,
    private var matchList: List<Match>,
    private val paymentCallback: PaymentCallback
) : RecyclerView.Adapter<MatchesAdapter.MatchViewHolder>() {

    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val teamVsTeam: TextView = view.findViewById(R.id.team1_vs_team2)
        val venue: TextView = view.findViewById(R.id.venue)
        val stadium: TextView = view.findViewById(R.id.stadium)
        val slot: TextView = view.findViewById(R.id.slot)
        val date: TextView = view.findViewById(R.id.date)
        val weather: TextView = view.findViewById(R.id.weatherStatus)
        val status: TextView = view.findViewById(R.id.status)
        val price: TextView = view.findViewById(R.id.price)
        val bookStadiumButton: Button = view.findViewById(R.id.book)
        val cancelBookingButton: Button = view.findViewById(R.id.cancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.match_item, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matchList[position]

        holder.teamVsTeam.text = "${match.team} vs ${match.againstTeam}"
        holder.venue.text = "Venue: ${match.location}"
        holder.stadium.text = "Stadium: ${match.stadium}"
        holder.slot.text = "Timing: ${match.slot}"
        holder.date.text = "Date: ${match.date}"
        holder.weather.text = "Weather: ${match.weather}"
        holder.status.text = if (match.status == "Booked") "Status: Match Booked" else "Status: Available"
        holder.price.text = when (match.slot) {
            "8:00-11:00AM" -> "Price: ₹3000"
            "12:00-3:00PM" -> "Price: ₹2000"
            "3:00-6:00PM" -> "Price: ₹4000"
            else -> "Price: N/A"
        }

        if (match.status == "Booked") {
            holder.bookStadiumButton.text = "Already booked"
            holder.bookStadiumButton.isEnabled = false
        } else {
            holder.bookStadiumButton.text = "Book Stadium"
            holder.bookStadiumButton.isEnabled = true
        }

        holder.bookStadiumButton.setOnClickListener {
            if (match.status != "Booked") {
                paymentCallback.onInitiatePayment(match)
            }
        }

        holder.cancelBookingButton.setOnClickListener {
            matchList = matchList.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
            deleteMatch(match.id)
            Toast.makeText(context, "Match canceled successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = matchList.size

    fun updateMatchAfterPayment(match: Match) {
        val index = matchList.indexOfFirst { it.id == match.id }
        if (index != -1) {
            matchList[index].status = "Booked"
            notifyItemChanged(index)
            updateMatchStatus(match.id, "Booked")
        }
    }

    private fun updateMatchStatus(matchId: Int, status: String) {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase
        val updateQuery = "UPDATE ${DatabaseHelper.TABLE_NAME} SET ${DatabaseHelper.COL_STATUS} = '$status' WHERE ${DatabaseHelper.COL_ID} = $matchId"
        db.execSQL(updateQuery)
    }

    private fun deleteMatch(matchId: Int) {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase
        val deleteQuery = "DELETE FROM ${DatabaseHelper.TABLE_NAME} WHERE ${DatabaseHelper.COL_ID} = $matchId"
        db.execSQL(deleteQuery)
    }
}
