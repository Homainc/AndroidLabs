package com.homa_inc.androidlabs.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.homa_inc.androidlabs.Adapter.FeedAdapter
import com.homa_inc.androidlabs.Adapter.FeedViewHolder
import com.homa_inc.androidlabs.Models.RSSObject
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.ThumbnailDownloader
import java.lang.StringBuilder
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.view.*
import android.widget.Toast
import com.google.gson.stream.JsonReader
import com.homa_inc.androidlabs.Utils.HttpUtil
import com.homa_inc.androidlabs.Utils.UserUtil
import java.io.StringReader


class HomeFragment : Fragment() {
    companion object {
        private const val RSS_API_KEY =
            "&api_key=jfsknxx6un3fzrasevu6vzyeva8j2zkse3p2u52p&count=30"
        private const val RSS_to_JSON_API =
            "https://api.rss2json.com/v1/api.json?rss_url="
    }

    private var newsRecyclerView: RecyclerView? = null
    private var thumbnailDownloader: ThumbnailDownloader<FeedViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        newsRecyclerView = v.findViewById(R.id.newsRecyclerView)
        val linearLayoutManager = LinearLayoutManager(activity?.baseContext, RecyclerView.VERTICAL, false)
        newsRecyclerView?.layoutManager = linearLayoutManager
        if(UserUtil.instance.isFirstLogIn)
            showToast(resources.getString(R.string.text_logged)+UserUtil.instance.currentUser?.email)
        loadRSS()
        return v
    }

    private fun setupThumbnailDownloader(){
        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler)
        thumbnailDownloader?.setThumbnailDownloadListener(
            object : ThumbnailDownloader.ThumbnailDownloadListener<FeedViewHolder> {
                override fun onThumbnailDownloaded(
                    target: FeedViewHolder,
                    thumbnail: Bitmap?
                ) {
                    if(thumbnail != null) {
                        val drawable = BitmapDrawable(resources, thumbnail)
                        target.bindDrawable(drawable)
                    }
                }
            }
        )
        thumbnailDownloader?.start()
        thumbnailDownloader?.looper
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
                if(result == null) {
                    showToast(resources.getString(R.string.text_connection_error))
                    return
                }
                val reader = JsonReader(StringReader(result))
                reader.isLenient = true
                val rssObject: RSSObject = Gson().fromJson<RSSObject>(reader, RSSObject::class.java)
                val adapter = FeedAdapter(thumbnailDownloader as ThumbnailDownloader<FeedViewHolder> , rssObject, activity?.baseContext as Context)
                newsRecyclerView?.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun doInBackground(vararg params: String?): String? {
                return HttpUtil.dataHandler(params[0])
            }
        }
        if(UserUtil.instance.getRSSLink() == null){
            showToast(resources.getString(R.string.rss_link_required))
        }
        else if(HttpUtil.hasConnection(context as Context)) {
            val urlGetData = StringBuilder(RSS_to_JSON_API)
            urlGetData.append(UserUtil.instance.getRSSLink())
            urlGetData.append(RSS_API_KEY)
            loadRSSAsync.execute(urlGetData.toString())
            setupThumbnailDownloader()
        }
        else {
            showToast(resources.getString(R.string.no_internet))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        thumbnailDownloader?.quit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        thumbnailDownloader?.clearQueue()
    }

    private fun showToast(text: String){
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.appbar_item_refresh ->{
                loadRSS()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}