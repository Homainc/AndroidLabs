package com.homa_inc.androidlabs.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.google.android.material.snackbar.Snackbar
import com.homa_inc.androidlabs.Utils.ThumbnailDownloader
import java.lang.StringBuilder
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.view.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.homa_inc.androidlabs.Extensions.NetReceiver
import com.homa_inc.androidlabs.Interfaces.NavigatorToWebView
import com.homa_inc.androidlabs.Interfaces.NetListener
import com.homa_inc.androidlabs.Interfaces.NewsReceiver
import com.homa_inc.androidlabs.Tasks.NewsDownloadingTask
import com.homa_inc.androidlabs.Utils.HttpUtil
import com.homa_inc.androidlabs.Utils.NewsCachingUtil
import com.homa_inc.androidlabs.Utils.UserUtil
import es.dmoral.toasty.Toasty
import java.text.MessageFormat


class HomeFragment : Fragment(), NewsReceiver, NavigatorToWebView, NetListener {

    companion object {
        private const val RSS_API_KEY =
            "&api_key=jfsknxx6un3fzrasevu6vzyeva8j2zkse3p2u52p&count=30"
        private const val RSS_to_JSON_API =
            "https://api.rss2json.com/v1/api.json?rss_url="
    }

    private var isConnected = false
    private var newsRecyclerView: RecyclerView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private lateinit var thumbnailDownloader: ThumbnailDownloader<FeedViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val size = Point()
        activity?.windowManager?.defaultDisplay?.getSize(size)
        thumbnailDownloader = ThumbnailDownloader(Handler(), size)
        setupThumbnailDownloader()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        isConnected = false
        newsRecyclerView = v.findViewById(R.id.newsRecyclerView)
        swipeRefresh = v.findViewById(R.id.swipeRefresh)
        swipeRefresh?.setOn-RefreshListener { loadRSS() }
        newsRecyclerView?.layoutManager = getLayoutManager()
        if(UserUtil.instance.isFirstLogIn) {
            UserUtil.instance.isFirstLogIn = false
            val msg = MessageFormat.format(resources.getString(R.string.text_logged),
                UserUtil.instance.currentUser?.email)
            Toasty.info(context as Context, msg, Toast.LENGTH_SHORT, true).show()
        }
        loadRSS()
        //onNewsLoadPostExecuted(NewsCachingUtil.loadFromCache(), true)
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
        if(UserUtil.instance.getRSSLink().isNullOrEmpty()){
            Toasty.warning(context as Context, R.string.rss_link_required, Toast.LENGTH_SHORT, true).show()
            findNavController().navigate(R.id.settingsFragment)
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
        Toasty.warning(context as Context, R.string.no_internet, Toast.LENGTH_SHORT, true).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        thumbnailDownloader.quit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        thumbnailDownloader.clearQueue()
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

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        filter.addAction("com.homa_inc.androidlabs.CONNECTIVITY_CHANGE")
        context?.registerReceiver(NetReceiver(this), filter)
        registerConnectivityNetworkMonitorForAPI21AndUp()
    }

    override fun onNewsLoadPreExecuted() {
        swipeRefresh?.isRefreshing = true
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun registerConnectivityNetworkMonitorForAPI21AndUp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(
            builder.build(),
            object: ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    context?.sendBroadcast(
                        getConnectivityIntent(false)
                    )
                }
                override fun onLost(network: Network) {
                    context?.sendBroadcast(
                        getConnectivityIntent(true)
                    )
                }
            }
        )
    }

    private fun getConnectivityIntent(noConnection : Boolean): Intent {
        val intent = Intent()
        intent.action = "com.homa_inc.androidlabs.CONNECTIVITY_CHANGE"
        intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, noConnection)
        return intent
    }

        override fun onNewsLoadPostExecuted(rssObject: RSSObject?, cached: Boolean) {
        swipeRefresh?.isRefreshing = false
        val currentContent = context
        currentContent?: return
        if(rssObject == null) {
            Toasty.error(currentContent, R.string.text_connection_error,
                Toast.LENGTH_SHORT, true).show()
            return
        }
        if(!cached){
            NewsCachingUtil.saveToCache(rssObject)
        }
        val adapter = FeedAdapter(thumbnailDownloader, rssObject,
            currentContent, this@HomeFragment)
        newsRecyclerView?.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun openWebView(link: String) {
        if(HttpUtil.hasConnection(activity!!)){
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWebViewActivity(link))
            return
        }
        Toasty.error(context!!, R.string.no_internet, Toast.LENGTH_SHORT, true).show()
    }

    override fun onConnectedNet() {
        if(!isConnected) {
            isConnected = true
        }
        else{
            Toasty.info(context as Context, "connected", Toast.LENGTH_SHORT, true).show()
            Snackbar.make(view as View, "Network connected", Snackbar.LENGTH_LONG)
                .setAction("reload news", { loadRSS() }).show()
        }
    }
}