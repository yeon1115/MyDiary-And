package com.google.firebase.quickstart.database.kotlin

import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.quickstart.database.kotlin.models.Post
import com.google.firebase.quickstart.database.kotlin.models.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.*
import java.io.File
import java.util.*

object FireUtil {
    const val TAG = "FireUtil"
    const val USERS = "users"
    const val POSTS = "posts"
    private lateinit var firestore: FirebaseFirestore
    private lateinit var query: Query
    private lateinit var uid: String
    private lateinit var firestorage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var posts_imageRef: StorageReference

    fun init(){
        firestore = Firebase.firestore
        uid = Firebase.auth.currentUser?.uid.toString()
        firestorage = Firebase.storage
        storageRef = firestorage.reference
        posts_imageRef = storageRef.child("posts_image")
    }

    fun getPostsQuery(): Query{
        query = firestore.collection(FireUtil.POSTS)
            .limit(50)
        return query
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

    fun writeNewUser(userId: String, name: String, email: String?) {
        val user = User(name, email)

        val batch = firestore.batch()
        val users = firestore.collection(FireUtil.USERS).document(userId)
        batch.set(users, user)
        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "writeNewUser succeeded.")
            } else {
                Log.w(TAG, "writeNewUser failed.", task.exception)
            }
        }
    }


    fun writeNewPost(post: Post) {
        //val post = Post(uid, username, title, body)

        val batch = firestore.batch()
        val postsRef = firestore.collection(FireUtil.POSTS).document()

        post.id = postsRef.id
        Log.d(TAG, "Write Before postsRef.id: "+postsRef.id)
        val postValues = post.toMap()
        batch.set(postsRef, postValues)
        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Write postsRef succeeded.")
                Log.d(TAG, "Write After postsRef.id: "+postsRef.id)
                writeUserPost(postValues, postsRef.id)
            } else {
                Log.w(TAG, "write postsRef failed.", task.exception)
            }
        }
    }

    private fun writeUserPost(postValues :Map<String, Any?>, postKey: String){
        val batch = firestore.batch()
        val users = firestore.collection(FireUtil.USERS).document(uid)
        val user_posts = users.collection(FireUtil.POSTS).document(postKey)
        val user_username = users.collection("username").document(postKey)

        batch.set(user_posts, postValues)
        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Write user_posts succeeded.")
            } else {
                Log.w(TAG, "write user_posts failed.", task.exception)
            }
        }
    }

    fun uploadImageAtStorage(imagePath: String): Maybe<Triple<String, String, String>> {
        //generate new name for the file when uploading to firebase storage
        val fileName = UUID.randomUUID().toString() + getFileExtensionFromPath(imagePath)
        //upload image
        val ref = posts_imageRef.child(fileName)

        val file = Uri.fromFile(File(imagePath))

        return RxFirebaseStorage.putFile(ref, file).flatMapMaybe {
            return@flatMapMaybe RxFirebaseStorage.getDownloadUrl(ref)

        }.flatMap {
            val downloadUrl = it.toString()
            val updateMap: MutableMap<String, Any> = HashMap()

            //generate circle bitmap
            val circleBitmap = BitmapUtils.getCircleBitmap(BitmapUtils.convertFileImageToBitmap(imagePath))
            //decode the image as base64 string
            val decodedImage = BitmapUtils.decodeImageAsPng(circleBitmap)

            //add the photo to the map
            updateMap["photo"] = downloadUrl
            //add the thumb circle image to the map
            updateMap["thumbImg"] = decodedImage

            // no need to save them in firebase database
            // because CL-925
            Maybe.just(Triple(decodedImage, fileName, downloadUrl))
        }
    }

    fun getFileExtensionFromPath(string: String): String? {
        val index = string.lastIndexOf(".")
        return string.substring(index + 1)
    }



















    fun writeNewPostWithImage(post: Post, imagePath: String){
        var pair: Pair<MutableLiveData<Boolean>, LiveData<Boolean>> = startTimeOut(120*1000)
        var isCompleteTask: MutableLiveData<Boolean> = pair.first
        var timeOut: LiveData<Boolean> = pair.second


        var downloadUrl = ""
        uploadImageAtStorage(imagePath).toObservable().subscribe({

            val thumb = it.first
            val fileName = it.second
            downloadUrl = it.third

        }, { throwable ->
            isCompleteTask.value = true
            if(timeOut.value!!.equals(false)){
                throwable.message?.let {
                    Log.d(TAG, "uploadImageAtStorage failed.")
                    Log.d(TAG, "throwable.message: "+throwable.message)

                }
            }
        }, {
            //onComplete
            isCompleteTask.value = true
            if(timeOut.value!!.equals(false)){
                Log.d(TAG, "uploadImageAtStorage succeeded.")
                post.photoUrl = downloadUrl
                writeNewPost(post)
            }
        })

        //.addTo(disposables)
    }

    fun startTimeOut(delayMillis: Long): Pair<MutableLiveData<Boolean>, LiveData<Boolean>>{
        //var onCompleteJob: Job = onCompleteFirebaseFunc()
        val isCompleteTask = MutableLiveData<Boolean>(false)
        val _timeOut = MutableLiveData<Boolean>(false)

        runTimeout(isCompleteTask, _timeOut, delayMillis)

        return Pair(isCompleteTask, _timeOut)
    }

    val DEFAULT_TIMEOUT_TIME: Long = 20000
    lateinit var onShowErrorTimeOut: (exception: Throwable) -> Unit
    fun runTimeout(isCompleteTask: MutableLiveData<Boolean>, timeOut: MutableLiveData<Boolean>, delayMillis: Long){
        Handler().postDelayed({
            try{
                if(isCompleteTask.value!!.equals(false)){
                    timeOut.value = true
                    // CL2-215
                    onShowErrorTimeOut.invoke(Throwable("TIME_OUT"))
                }
            }catch (t: Throwable){
            }
        }, delayMillis)
    }
}