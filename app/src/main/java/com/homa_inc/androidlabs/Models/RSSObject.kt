package com.homa_inc.androidlabs.Models

data class RSSObject(
    val status: String,
    val feed: Feed,
    val items: List<NewsItem>
)