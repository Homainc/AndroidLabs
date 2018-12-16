package com.homa_inc.androidlabs.Interfaces

import com.homa_inc.androidlabs.Models.RSSObject

interface NewsReceiver {
    fun onNewsLoadPreExecuted()
    fun onNewsLoadPostExecuted(rssObject: RSSObject?, cached: Boolean)
}