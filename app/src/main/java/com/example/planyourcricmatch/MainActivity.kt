package com.example.planyourcricmatch

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject


class MainActivity : AppCompatActivity(), PaymentResultWithDataListener, PaymentCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load the default fragment (Add Match)
        if (savedInstanceState == null) {
            loadFragment(AddMatchFragment())
        }

        // Set up the tab navigation logic
        findViewById<TextView>(R.id.f1).setOnClickListener {
            loadFragment(AddMatchFragment()) // Switch to AddMatchFragment
        }

        findViewById<TextView>(R.id.f2).setOnClickListener {
            loadFragment(UpcomingMatchFragment()) // Switch to UpcomingMatchesFragment
        }
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private var currentMatch: Match? = null

    override fun onInitiatePayment(match: Match) {
        currentMatch = match
        initPayment(match)
    }

    private fun initPayment(match: Match) {
        val activity: Activity = this
        val co = Checkout()
        co.setKeyID("rzp_test_Nqy8gmPWtyPySL") // Use your Razorpay test key

        try {
            val options = JSONObject()
            options.put("name", "Cricket Match Booking")
            options.put("description", "Booking for ${match.team} vs ${match.againstTeam}")
            options.put("currency", "INR")
            options.put("amount", "200000") // Amount in paise (₹2000.00)
            options.put("theme.color", "#3399cc")

            val prefill = JSONObject()
            prefill.put("email", "test.user@example.com")
            prefill.put("contact", "6203554162")
            options.put("prefill", prefill)

            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        currentMatch?.let { match ->
            Toast.makeText(this, "Payment Successful! ID: $razorpayPaymentId", Toast.LENGTH_SHORT).show()
            onPaymentSuccess(match)
        }
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        Toast.makeText(this, "Payment Failed! Error: $response", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentSuccess(match: Match) {
        // Notify the adapter about the successful payment
        val adapter = (findViewById<RecyclerView>(R.id.recyclerView)).adapter as MatchesAdapter
        adapter.updateMatchAfterPayment(match)
    }
}

