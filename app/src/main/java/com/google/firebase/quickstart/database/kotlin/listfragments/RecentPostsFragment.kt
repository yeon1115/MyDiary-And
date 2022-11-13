package com.google.firebase.quickstart.database.kotlin.listfragments

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.quickstart.database.kotlin.FireUtil

class RecentPostsFragment : PostListFragment() {

    override fun getQuery(databaseReference: DatabaseReference): Query {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys.
        return databaseReference.child(FireUtil.POSTS)
                .limitToFirst(100)
        // [END recent_posts_query]
    }
}
