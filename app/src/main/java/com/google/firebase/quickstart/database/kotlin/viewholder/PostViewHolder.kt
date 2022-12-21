package com.google.firebase.quickstart.database.kotlin.viewholder

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.quickstart.database.R
import com.google.firebase.quickstart.database.kotlin.models.Post

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val postTitle: TextView = itemView.findViewById(R.id.postTitle)
    private val postAuthor: TextView = itemView.findViewById(R.id.postAuthor)
    private val postNumStars: TextView = itemView.findViewById(R.id.postNumStars)
    private val postImage: ImageView = itemView.findViewById(R.id.postImage)
    private val postBody: TextView = itemView.findViewById(R.id.postBody)
    private val star: ImageView = itemView.findViewById(R.id.star)

    fun bindToPost(context: Context, post: Post, starClickListener: View.OnClickListener) {
        postTitle.text = post.title
        postAuthor.text = post.author
        postNumStars.text = post.starCount.toString()
        postBody.text = post.body

        if(!post.photoUrl.equals("") && context!=null){
            Glide.with(context).load(post.photoUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false;
                    }

                }).into(postImage)
        }

        star.setOnClickListener(starClickListener)
    }

    fun setLikedState(liked: Boolean) {
        if (liked) {
            star.setImageResource(R.drawable.ic_toggle_star_24)
        } else {
            star.setImageResource(R.drawable.ic_toggle_star_outline_24)
        }
    }
}
