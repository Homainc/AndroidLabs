package com.homa_inc.androidlabs.Tasks

import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.homa_inc.androidlabs.Interfaces.NewsReceiver
import com.homa_inc.androidlabs.Models.RSSObject
import com.homa_inc.androidlabs.Utils.HttpUtil
import java.io.StringReader

class NewsDownloadingTask(private val receiver: NewsReceiver) : AsyncTask<String, String, String>() {

    override fun onPreExecute() {
        receiver.onNewsLoadPreExecuted()
    }

    override fun onPostExecute(result: String?) {
        if(result == null) {
            receiver.onNewsLoadPostExecuted(null, false)
            return
        }
        val reader = JsonReader(StringReader(result))
        reader.isLenient = true
        receiver.onNewsLoadPostExecuted(Gson().fromJson<RSSObject>(reader, RSSObject::class.java), false)
    }

    override fun doInBackground(vararg params: String?): String? {
        return HttpUtil.dataHandler(params[0])
    }
}