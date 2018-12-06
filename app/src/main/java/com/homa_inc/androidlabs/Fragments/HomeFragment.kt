package com.homa_inc.androidlabs.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.homa_inc.androidlabs.Adapter.FeedAdapter
import com.homa_inc.androidlabs.Adapter.FeedViewHolder
import com.homa_inc.androidlabs.Models.RSSObject
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.HTTPUtil
import com.homa_inc.androidlabs.Utils.ThumbnailDownloader
import java.lang.StringBuilder
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap


class HomeFragment : Fragment() {
    companion object {
        private const val RSS_link = "http://rss.nytimes.com/services/xml/rss/nyt/Science.xml"
        private const val RSS_to_JSON_API = "https://api.rss2json.com/v1/api.json?rss_url="
    }

    private var newsRecyclerView: RecyclerView? = null
    private lateinit var thumbnailDownloader: ThumbnailDownloader<FeedViewHolder>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        newsRecyclerView = v.findViewById(R.id.newsRecyclerView)
        val linearLayoutManager = LinearLayoutManager(activity?.baseContext, RecyclerView.VERTICAL, false)
        newsRecyclerView?.layoutManager = linearLayoutManager
        loadRSS()
        return v
    }

    fun setupThumbnailDownloader(){
        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler)
        thumbnailDownloader.setThumbnailDownloadListener(
            object : ThumbnailDownloader.ThumbnailDownloadListener<FeedViewHolder> {
                override fun onThumbnailDownloaded(
                    target: FeedViewHolder,
                    thumbnail: Bitmap
                ) {
                    val drawable = BitmapDrawable(resources, thumbnail)
                    target.bindDrawable(drawable)
                }
            }
        )
        thumbnailDownloader.start()
        thumbnailDownloader.looper
    }

    private fun loadRSS(){
        val loadRSSAsync = @SuppressLint("StaticFieldLeak")
        object:AsyncTask<String, String, String>(){
            internal var mDialog = ProgressDialog(activity)

            override fun onPreExecute() {
                mDialog.setMessage("Please wait...")
                mDialog.show()
            }

            override fun onPostExecute(result: String?) {
                mDialog.dismiss()
                val rssObject: RSSObject = Gson().fromJson<RSSObject>(result, RSSObject::class.java)
                val adapter = FeedAdapter(thumbnailDownloader , rssObject, activity?.baseContext as Context)
                newsRecyclerView?.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun doInBackground(vararg params: String?): String {
                val result: String
                val http = HTTPUtil()
                result = http.GetHTTPDataHandler(params[0])
                return  result
            }
        }

        val urlGetData = StringBuilder(RSS_to_JSON_API)
        urlGetData.append(RSS_link)
        loadRSSAsync.execute(urlGetData.toString())
        setupThumbnailDownloader()
    }

    override fun onDestroy() {
        super.onDestroy()
        thumbnailDownloader.quit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        thumbnailDownloader.clearQueue()
    }
}