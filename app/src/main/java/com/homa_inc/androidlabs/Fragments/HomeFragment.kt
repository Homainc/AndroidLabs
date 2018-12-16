package com.homa_inc.androidlabs.Fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.homa_inc.androidlabs.Interfaces.NewsReceiver
import com.homa_inc.androidlabs.Tasks.NewsDownloadingTask
import com.homa_inc.androidlabs.Utils.HttpUtil
import com.homa_inc.androidlabs.Utils.NewsCachingUtil
import com.homa_inc.androidlabs.Utils.UserUtil
import java.text.MessageFormat


class HomeFragment : Fragment(), NewsReceiver {

    companion object {
        private const val RSS_API_KEY =
            "&api_key=jfsknxx6un3fzrasevu6vzyeva8j2zkse3p2u52p&count=30"
        private const val RSS_to_JSON_API =
            "https://api.rss2json.com/v1/api.json?rss_url="
    }

    private var newsRecyclerView: RecyclerView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private lateinit var thumbnailDownloader: ThumbnailDownloader<FeedViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        thumbnailDownloader = ThumbnailDownloader(Handler())
        setupThumbnailDownloader()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        newsRecyclerView = v.findViewById(R.id.newsRecyclerView)
        swipeRefresh = v.findViewById(R.id.swipeRefresh)
        swipeRefresh?.setOnRefreshListener { loadRSS() }
        newsRecyclerView?.layoutManager = getLayoutManager()
        if(UserUtil.instance.isFirstLogIn) {
            UserUtil.instance.isFirstLogIn = false
            showToast(MessageFormat.format(resources.getString(R.string.text_logged),
                UserUtil.instance.currentUser?.email))
        }
        //loadRSS()
        onNewsLoadPostExecuted(NewsCachingUtil.loadFromCache(), true)
        return v
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager? {
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            return LinearLayoutManager(activity?.baseContext, RecyclerView.VERTICAL, false)
        return GridLayoutManager(activity?.baseContext, 2)
    }

    private fun setupThumbnailDownloader(){
        thumbnailDownloader.setThumbnailDownloadListener(
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
        thumbnailDownloader.start()
        thumbnailDownloader.looper
    }

    private fun loadRSS(){
        if(UserUtil.instance.getRSSLink() == null){
            showToast(resources.getString(R.string.rss_link_required))
            return
        }
        if(HttpUtil.hasConnection(context as Context)) {
            thumbnailDownloader.clearQueue()
            val urlGetData = StringBuilder(RSS_to_JSON_API)
            urlGetData.append(UserUtil.instance.getRSSLink())
            urlGetData.append(RSS_API_KEY)
            val loadRSSAsync = NewsDownloadingTask(this@HomeFragment)
            loadRSSAsync.execute(urlGetData.toString())
            return
        }
        onNewsLoadPostExecuted(NewsCachingUtil.loadFromCache(), true)
        showToast(resources.getString(R.string.no_internet))
    }

    override fun onDestroy() {
        super.onDestroy()
        thumbnailDownloader.quit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        thumbnailDownloader.clearQueue()
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

    override fun onNewsLoadPreExecuted() {
        swipeRefresh?.isRefreshing = true
    }

    override fun onNewsLoadPostExecuted(rssObject: RSSObject?, cached: Boolean) {
        swipeRefresh?.isRefreshing = false
        if(rssObject == null) {
            showToast(resources.getString(R.string.text_connection_error))
            return
        }
        if(!cached){
            NewsCachingUtil.saveToCache(rssObject)
        }
        val adapter = FeedAdapter(thumbnailDownloader, rssObject, activity?.baseContext as Context)
        newsRecyclerView?.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}