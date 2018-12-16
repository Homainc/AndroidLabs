package com.homa_inc.androidlabs.Utils

import com.homa_inc.androidlabs.Models.DbNewsItem
import com.homa_inc.androidlabs.Models.Feed
import com.homa_inc.androidlabs.Models.RSSObject
import com.orm.SugarRecord

class NewsCachingUtil {
    companion object {
        fun saveToCache(rssObject: RSSObject){
            SugarRecord.deleteAll(DbNewsItem::class.java)
            for(i in 0..10) {
                val current = rssObject.items.getOrNull(i)
                current?: break
                val dbItem = DbNewsItem()
                dbItem.newsItem = current
                dbItem.save()
            }
        }
        fun loadFromCache(): RSSObject?{
            val count = SugarRecord.count<DbNewsItem>(DbNewsItem::class.java).toInt()
            if(count == 0)
                return null
            val dbItems = SugarRecord.findAll(DbNewsItem::class.java)
            val items = List(count){ dbItems.next().newsItem }
            return RSSObject("", Feed(), items)
        }
        fun clearCache(){
            SugarRecord.deleteAll(DbNewsItem::class.java)
        }
    }
}