package com.example.android.politicalpreparedness.representative.adapter

import android.view.View
import android.widget.*
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R

import com.example.android.politicalpreparedness.network.GoogleApiStatus


@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
                .load(uri)
                .circleCrop()
                .apply (RequestOptions()
                        .placeholder(R.drawable.loading_img)
                        .error(R.drawable.ic_broken_image)
                        )
                .into(view)
    }

}

@BindingAdapter("loadingImageState")
fun bindStatus(statusImageView: ImageView,status: GoogleApiStatus?){

    when(status){
        GoogleApiStatus.LOADING ->{
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource((R.drawable.loading_animation))
        }
        GoogleApiStatus.ERROR ->{
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        GoogleApiStatus.DONE->{
            statusImageView.visibility = View.GONE
           // statusImageView.alpha =0f
        }
    }
}

@BindingAdapter("buttonVisibility")
fun buttonStateVisibility(btn: View, status: GoogleApiStatus?){
    when(status){
        GoogleApiStatus.DONE-> btn.visibility = View.VISIBLE
        else ->  btn.visibility = View.GONE
    }
}



@BindingAdapter("buttonText")
fun buttonState(view:Button, state:Boolean){
    when(state){
        true-> view.setText(R.string.unfollow)
        else->view.setText(R.string.follow)
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}



inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T>{
    return adapter as ArrayAdapter<T>
}


