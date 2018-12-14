package com.homa_inc.androidlabs.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.homa_inc.androidlabs.Interfaces.NewsItemClickListener
import com.homa_inc.androidlabs.Models.RSSObject
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.ThumbnailDownloader
import org.jsoup.Jsoup

class FeedViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
    View.OnClickListener, View.OnLongClickListener{

    var textViewTitle: TextView
    var textViewPubDate: TextView
    var textViewContent: TextView
    var imageView: ImageView

    private var newsItemClickListener: NewsItemClickListener? = null

    init {
        textViewTitle = itemView.findViewById(R.id.cardTextTitle)
        textViewContent = itemView.findViewById(R.id.cardTextContent)
        textViewPubDate = itemView.findViewById(R.id.cardTextDate)
        imageView = itemView.findViewById(R.id.imageView)

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    fun setItemClickListener(itemClickListener: NewsItemClickListener){
        this.newsItemClickListener = itemClickListener
    }

    override fun onClick(v: View?) {
        newsItemClickListener!!.onClick(v as View, adapterPosition, false)
    }

    override fun onLongClick(v: View?): Boolean {
        newsItemClickListener!!.onClick(v as View, adapterPosition, false)
        return true
    }

    fun bindDrawable(drawable: Drawable){
        imageView.setImageDrawable(drawable)
    }
}

class FeedAdapter(
    private val thumbnailDownloader: ThumbnailDownloader<FeedViewHolder>,
    private val rssObject: RSSObject,
    private val mContext: Context
): RecyclerView.Adapter<FeedViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.textViewTitle.text = rssObject.items[position].title
        val description = rssObject.items[position].description
        holder.textViewContent.text = Jsoup.parse(description).text()
        holder.textViewPubDate.text = rssObject.items[position].pubDate
        thumbnailDownloader.queueThumbnail(holder, rssObject.items[position].enclosure.link)
        holder.setItemClickListener(object : NewsItemClickListener {
            override fun onClick(view: View, position: Int, isLongClick: Boolean) {
                if(!isLongClick){
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(rssObject.items[position].link))
                    browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    mContext.startActivity(browserIntent)
                }
            }
        })
    }

    override fun getItemCount(): Int = rssObject.items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val itemView = inflater.inflate(R.layout.news_row, parent, false)
        return FeedViewHolder(itemView)
    }
}