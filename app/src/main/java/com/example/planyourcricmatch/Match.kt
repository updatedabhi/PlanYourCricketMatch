package com.example.planyourcricmatch

data class Match(
    val id: Int,
    val matchName: String,
    val team: String,
    val againstTeam: String,
    val date: String,
    val location: String,
    val format: String,
    val stadium: String,
    val slot: String,
    val weather: String,
    var status: String,
)
