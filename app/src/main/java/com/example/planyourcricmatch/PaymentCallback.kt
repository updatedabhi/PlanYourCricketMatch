package com.example.planyourcricmatch


interface PaymentCallback {
    fun onInitiatePayment(match: Match)
    fun onPaymentSuccess(match: Match)
}

