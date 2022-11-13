package com.google.firebase.quickstart.database.kotlin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.quickstart.database.R
import com.google.firebase.quickstart.database.databinding.FragmentPostDetailBinding
import com.google.firebase.quickstart.database.kotlin.models.Comment
import com.google.firebase.quickstart.database.kotlin.models.Post
import com.google.firebase.quickstart.database.kotlin.models.User
import com.google.firebase.quickstart.database.kotlin.viewholder.CommentViewHolder
import java.lang.IllegalArgumentException
import java.util.ArrayList

class PostDetailFragment : BaseFragment(), EventListener<DocumentSnapshot> {

    private lateinit var postKey: String
    private lateinit var firestore: FirebaseFirestore
    private lateinit var restaurantRef: DocumentReference
    private var restaurantRegistration: ListenerRegistration? = null

    private var adapter: CommentAdapter? = null

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get post key from arguments
        postKey = requireArguments().getString(EXTRA_POST_KEY)
                ?: throw IllegalArgumentException("Must pass EXTRA_POST_KEY")

        Log.d(TAG, "postKey: "+postKey)

        // Initialize Firestore
        firestore = Firebase.firestore

        // Get reference to the restaurant
        restaurantRef = firestore.collection(FireUtil.POSTS).document(postKey)

        // Initialize Views
        with(binding) {
            buttonPostComment.setOnClickListener { //postComment()
            }
            recyclerPostComments.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onStart() {
        super.onStart()
        restaurantRegistration = restaurantRef.addSnapshotListener(this)
//        restaurantRef.get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
//                    val post = document.data
//                    Log.d(TAG, "addOnSuccessListener => post: "+post)
//
//                } else {
//                    Log.d(TAG, "No such document")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "get failed with ", exception)
//            }

//        // Listen for comments
//        adapter = CommentAdapter(requireContext(), commentsReference)
//        binding.recyclerPostComments.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        restaurantRegistration?.remove()
        restaurantRegistration = null

//        // Clean up comments listener
//        adapter?.cleanupListener()
    }

//    private fun postComment() {
//        val uid = uid
//        Firebase.database.reference.child(FireUtil.USERS).child(uid)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        // Get user information
//                        val user = dataSnapshot.getValue<User>() ?: return
//
//                        val authorName = user.username
//
//                        // Create new comment object
//                        val commentText = binding.fieldCommentText.text.toString()
//                        val comment = Comment(uid, authorName, commentText)
//
//                        // Push the comment, it will appear in the list
//                        commentsReference.push().setValue(comment)
//
//                        // Clear the field
//                        binding.fieldCommentText.text = null
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {
//                    }
//                })
//    }

    private class CommentAdapter(
            private val context: Context,
            private val databaseReference: DatabaseReference
    ) : RecyclerView.Adapter<CommentViewHolder>() {

        private val childEventListener: ChildEventListener?

        private val commentIds = ArrayList<String>()
        private val comments = ArrayList<Comment>()

        init {

            // Create child event listener
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                    // A new comment has been added, add it to the displayed list
                    val comment = dataSnapshot.getValue<Comment>()

                    // Update RecyclerView
                    commentIds.add(dataSnapshot.key!!)
                    comments.add(comment!!)
                    notifyItemInserted(comments.size - 1)
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    val newComment = dataSnapshot.getValue<Comment>()
                    val commentKey = dataSnapshot.key

                    val commentIndex = commentIds.indexOf(commentKey)
                    if (commentIndex > -1 && newComment != null) {
                        // Replace with the new data
                        comments[commentIndex] = newComment

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex)
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child: $commentKey")
                    }
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    val commentKey = dataSnapshot.key

                    val commentIndex = commentIds.indexOf(commentKey)
                    if (commentIndex > -1) {
                        // Remove data from the list
                        commentIds.removeAt(commentIndex)
                        comments.removeAt(commentIndex)

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex)
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey!!)
                    }
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    val movedComment = dataSnapshot.getValue<Comment>()
                    val commentKey = dataSnapshot.key

                    // ...
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                    Toast.makeText(context, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show()
                }
            }
            databaseReference.addChildEventListener(childEventListener)

            // Store reference to listener so it can be removed on app stop
            this.childEventListener = childEventListener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_comment, parent, false)
            return CommentViewHolder(view)
        }

        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            holder.bind(comments[position])
        }

        override fun getItemCount(): Int = comments.size

        fun cleanupListener() {
            childEventListener?.let {
                databaseReference.removeEventListener(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "FireUtil"
        const val EXTRA_POST_KEY = "post_key"
    }

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {

        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e)
            return
        }

        snapshot?.let {
            val post = snapshot.toObject<Post>()
            Log.d(TAG, "onEvent => post: "+post)
            post?.let { it1 -> loadPostDetails(it1) }
        }
    }

    private fun loadPostDetails(post: Post){
        if (post != null) {
            binding.postAuthorLayout.postAuthor.text = post.author
            with(binding.postTextLayout) {
                Log.d(TAG, "onEvent => setText: ")
                postTitle.text = post.title
                postBody.text = post.body
            }
        }
    }
}