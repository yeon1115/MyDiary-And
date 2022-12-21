package com.google.firebase.quickstart.database.kotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.canhub.cropper.CropImage
import com.google.firebase.quickstart.database.ActivityHelper
import com.google.firebase.quickstart.database.R
import com.google.firebase.quickstart.database.databinding.ActivityMainBinding
import com.google.firebase.quickstart.database.utils.CommonPermissionsUtil
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var activityHelper: ActivityHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        activityHelper = ActivityHelper()

        val fab = binding.fab
        val navController = findNavController(R.id.nav_host_fragment)
        navController.setGraph(R.navigation.nav_graph_kotlin)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.MainFragment) {
                fab.isVisible = true
                fab.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("ActivityHelper", activityHelper)
                    navController.navigate(R.id.action_MainFragment_to_NewPostFragment, bundle)
                }
            } else {
                fab.isGone = true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data!=null && resultCode == Activity.RESULT_OK) {
                val result = data.getParcelableExtra<CropImage.ActivityResult>(CropImage.CROP_IMAGE_EXTRA_RESULT)
                //val resultUri = result?.uri ?: return

                //val file = DirManager.getMyPhotoPath(applicationContext)
                try {
                    //copy image to the App Folder
                    if(result!=null && result.uriContent!=null){
                        //val bitmap = getBitmapByUri(this, result.uriContent!!)
                    }

                    //chosenPhoto = file.path
                    var chosenPhoto = result?.getUriFilePath(this)
                    Log.d(FireUtil.TAG, "chosenPhoto: "+chosenPhoto)
                    activityHelper.activityResultListener?.onActivityResult(chosenPhoto)

                } catch (e: IOException) {
                    Log.d(FireUtil.TAG, "e: "+e.message)
                    Toast.makeText(this, "e: "+e.message, Toast.LENGTH_SHORT).show()
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d(FireUtil.TAG, "could_not_get_this_image")
                Toast.makeText(this, "could_not_get_this_image", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode == CommonPermissionsUtil.PERMISSION_REQUEST_CODE) {
            if(!CommonPermissionsUtil.hasStoragePermissions(this))
                CommonPermissionsUtil.showPermissionNotice(this, CommonPermissionsUtil.permissions_read_storage)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!CommonPermissionsUtil.permissionsGranted(grantResults)) {
            CommonPermissionsUtil.showPermissionDenyAlertDialog(this, getString(R.string.Storage_permission_deny_message), CommonPermissionsUtil.PERMISSION_REQUEST_CODE)
        }
    }
}