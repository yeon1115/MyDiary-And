package com.google.firebase.quickstart.database.cropper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.core.app.ActivityCompat
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageOptions
import com.google.firebase.quickstart.database.R
import com.google.firebase.quickstart.database.databinding.SampleCropimageActivityBinding

class SampleCropImageActivity : CustomCropImageActivity(), SampleCropImageContract.View {

    companion object {
        fun start(activity: Activity) {
            ActivityCompat.startActivityForResult(
                    activity,
                    Intent(activity, SampleCropImageActivity::class.java),
                    CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE,
                    null
            )
        }
    }

    private lateinit var binding: SampleCropimageActivityBinding
    private val presenter: SampleCropImageContract.Presenter = SampleCropImagePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = SampleCropimageActivityBinding.inflate(layoutInflater)

        val bundle = Bundle()
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)
        var opt = CropImageOptions()
        opt.imageSourceIncludeGallery = true
        opt.imageSourceIncludeCamera = false
        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, opt)

        super.onCreate(savedInstanceState)
        presenter.bindView(this)

        binding.saveBtn.setOnClickListener {
            cropImage() // CropImageActivity.cropImage()
        }
        binding.backBtn.setOnClickListener {
            onBackPressed() // CropImageActivity.onBackPressed()
        }
        binding.rotateText.setOnClickListener {
            presenter.onRotateClick()
        }

        setCropImageView(binding.cropImageView)
        binding.cropImageView.setAspectRatio(1, 1)
        //binding.cropImageView.setFixedAspectRatio()
    }

    override fun showImageSourceDialog(openSource: (Source) -> Unit) {
        // Override this if you wanna a custom dialog layout
        super.showImageSourceDialog(openSource)
    }

    override fun setContentView(view: View) {
        // Override this to use your custom layout
        super.setContentView(binding.root)
    }

    override fun onDestroy() {
        presenter.unbindView()
        super.onDestroy()
    }

    override fun rotate(counter: Int) {
        binding.cropImageView.rotateImage(counter)
    }

    override fun updateRotationCounter(counter: String) {
        binding.rotateText.text = getString(R.string.rotation_value, counter)
    }

    override fun onPickImageResult(resultUri: Uri?) {
        super.onPickImageResult(resultUri)

        if (resultUri != null) {
            binding.cropImageView.setImageUriAsync(resultUri)
        }
    }

    // Override this to add more information into the intent
    override fun getResultIntent(uri: Uri?, error: java.lang.Exception?, sampleSize: Int): Intent {
        val result = super.getResultIntent(uri, error, sampleSize)
        return result.putExtra("EXTRA_KEY", "Extra data")
    }

    override fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
        val result = CropImage.ActivityResult(
            binding.cropImageView.imageUri,
            uri,
            error,
            binding.cropImageView.cropPoints,
            binding.cropImageView.cropRect,
            binding.cropImageView.rotatedDegrees,
            binding.cropImageView.wholeImageRect,
            sampleSize
        )

        super.setResult(uri, error, sampleSize)

        binding.cropImageView.setImageUriAsync(result.uriContent)
    }

    override fun setResultCancel() {
        super.setResultCancel()
    }

    override fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
        super.updateMenuItemIconColor(menu, itemId, color)
    }
}
