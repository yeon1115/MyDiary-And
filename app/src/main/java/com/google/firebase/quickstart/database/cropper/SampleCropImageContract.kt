package com.google.firebase.quickstart.database.cropper

internal interface SampleCropImageContract {
    interface View {
        fun updateRotationCounter(counter: String)
        fun rotate(counter: Int)
    }

    interface Presenter {
        fun bindView(view: View)
        fun unbindView()
        fun onRotateClick()
    }
}
