package com.example.planyourcricmatch

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.*

class AddMatchFragment : Fragment() {
    private lateinit var dateEditText: EditText
    private lateinit var stadiumSpinner: Spinner
    private lateinit var slotSpinner: Spinner
    private lateinit var dbHelper: DatabaseHelper

    private val stadiums = listOf("Stadium", "Wankhede", "Arun Jaitley", "M. Chinnaswamy", "Eden Garden")
    private val slots = listOf("Slot","8:00-11:00AM", "12:00-3:00PM", "3:00-6:00PM")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_match, container, false)

        dbHelper = DatabaseHelper(requireContext())
        dateEditText = view.findViewById(R.id.date)
        stadiumSpinner = view.findViewById(R.id.stadium)
        slotSpinner = view.findViewById(R.id.slot)

        setupDatePicker()
        setupStadiumSpinner()
        setupSlotSpinner()

        val saveButton: Button = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener { saveMatch(view) }

        return view
    }

    private fun setupDatePicker() {
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateEditText.setText(formattedDate)
            }, year, month, day).show()
        }
    }

    private fun setupStadiumSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stadiums)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stadiumSpinner.adapter = adapter
    }

    private fun setupSlotSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, slots)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        slotSpinner.adapter = adapter
    }

    private fun saveMatch(view: View) {
        val matchName = view.findViewById<EditText>(R.id.matchName).text.toString()
        val team = view.findViewById<EditText>(R.id.team).text.toString()
        val againstTeam = view.findViewById<EditText>(R.id.againstTeam).text.toString()
        val date = dateEditText.text.toString()
        val location = view.findViewById<EditText>(R.id.location).text.toString()
        val format = view.findViewById<EditText>(R.id.format).text.toString()

        val selectedStadium = stadiumSpinner.selectedItem.toString()
        val selectedSlot = slotSpinner.selectedItem.toString()

        if (matchName.isEmpty() || team.isEmpty() || againstTeam.isEmpty() || date.isEmpty() || location.isEmpty() || format.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_LONG).show()
            return
        }

        fetchWeather(location) { weather ->
            Thread {
                try {
                    val db = dbHelper.writableDatabase
                    val insertQuery = """
                    INSERT INTO ${DatabaseHelper.TABLE_NAME} 
                    (${DatabaseHelper.COL_MATCH_NAME}, ${DatabaseHelper.COL_TEAM}, ${DatabaseHelper.COL_AGAINST_TEAM}, ${DatabaseHelper.COL_DATE}, ${DatabaseHelper.COL_LOCATION}, ${DatabaseHelper.COL_FORMAT}, ${DatabaseHelper.COL_STADIUM}, ${DatabaseHelper.COL_SLOT}, ${DatabaseHelper.COL_WEATHER}) 
                    VALUES ('$matchName', '$team', '$againstTeam', '$date', '$location', '$format', '$selectedStadium', '$selectedSlot', '$weather')
                    """
                    db.execSQL(insertQuery)
                    Log.d("AddMatch", "Match data saved successfully")

                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Match saved successfully", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("AddMatch", "Error saving match data: ${e.message}")
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Error saving match", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }
    }

    private fun fetchWeather(location: String, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=$location&appid=ca17433c1bc055f12cda53edd6ccf84d")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("AddMatch", "Weather request failed: ${e.message}")
                activity?.runOnUiThread { callback("Weather unavailable") }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                try {
                    response.body?.let { responseBody ->
                        val json = JSONObject(responseBody.string())
                        val weather = json.getJSONArray("weather").getJSONObject(0).getString("description")
                        activity?.runOnUiThread { callback(weather) }
                    } ?: activity?.runOnUiThread { callback("Weather unavailable") }
                } catch (e: Exception) {
                    Log.e("AddMatch", "Error parsing weather response: ${e.message}")
                    activity?.runOnUiThread { callback("Weather unavailable") }
                }
            }
        })
    }
}
