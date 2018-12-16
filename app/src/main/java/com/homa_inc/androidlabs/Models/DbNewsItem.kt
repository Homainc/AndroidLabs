package com.homa_inc.androidlabs.Models

import com.orm.SugarRecord
import com.orm.dsl.Ignore

class DbNewsItem() : SugarRecord(){
    private var title: String? = null
    private var pubDate: String? = null
    private var description: String? = null
    private var link: String? = null
    private var thumbnailLink: String? = null

    @Ignore
    var newsItem: NewsItem
        get(){
                return NewsItem(
                    title?:"",
                    pubDate?:"",
                    link?:"",
                    "",
                    "",
                    "",
                    description?:"",
                    "",
                    Enclosure(thumbnailLink?:""),
                    List(0){""}
                )
            }
    set(item){
        title = item.title
        pubDate = item.pubDate
        description = item.description
        link = item.link
        thumbnailLink = item.enclosure.link
    }

}