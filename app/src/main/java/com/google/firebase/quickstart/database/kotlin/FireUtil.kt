package com.google.firebase.quickstart.database.kotlin

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.quickstart.database.kotlin.models.Post

object FireUtil {
    private const val TAG = "FireUtil"
    private lateinit var firestore: FirebaseFirestore
    private lateinit var query: Query
    private lateinit var uid: String

    fun init(){
        firestore = Firebase.firestore
        uid = Firebase.auth.currentUser!!.uid
    }

    fun getPostsQuery(){
        query = firestore.collection("posts")
            .limit(50)
    }

    /*
    fun bind(
            snapshot: DocumentSnapshot,
            listener: OnRestaurantSelectedListener?
        ) {

            val restaurant = snapshot.toObject<Restaurant>()
            if (restaurant == null) {
                return
            }

            val resources = binding.root.resources

            // Load image
            Glide.with(binding.restaurantItemImage.context)
                    .load(restaurant.photo)
                    .into(binding.restaurantItemImage)

            val numRatings: Int = restaurant.numRatings

            binding.restaurantItemName.text = restaurant.name
            binding.restaurantItemRating.rating = restaurant.avgRating.toFloat()
            binding.restaurantItemCity.text = restaurant.city
            binding.restaurantItemCategory.text = restaurant.category
            binding.restaurantItemNumRatings.text = resources.getString(
                    R.string.fmt_num_ratings,
                    numRatings)
            binding.restaurantItemPrice.text = RestaurantUtil.getPriceString(restaurant)

            // Click listener
            binding.root.setOnClickListener {
                listener?.onRestaurantSelected(snapshot)
            }
        }
    */




    private fun writeNewPost(userId: String, username: String, title: String, body: String) {
        val post = Post(userId, username, title, body)
        val postValues = post.toMap()

        val batch = firestore.batch()
        val postsRef = firestore.collection("posts").document()
        batch.set(postsRef, postValues)
        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Write postsRef succeeded.")
                Log.d(TAG, "postsRef.id: "+postsRef.id)
                writeUserPost(postValues, postsRef.id)
            } else {
                Log.w(TAG, "write postsRef failed.", task.exception)
            }
        }
    }

    private fun writeUserPost(postValues :Map<String, Any?>, postKey: String){
        val batch = firestore.batch()
        val users = firestore.collection("users").document(uid)
        val user_posts = users.collection("posts").document(postKey)

        batch.set(user_posts, postValues)
        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Write user_posts succeeded.")
            } else {
                Log.w(TAG, "write user_posts failed.", task.exception)
            }
        }
    }
}