package com.appcrud.comunicacion.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.appcrud.comunicacion.R
import com.bumptech.glide.Glide

class ImageDialogFragment(private val imageUrl: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_image_fullscreen, null)

        val imageView = view.findViewById<ImageView>(R.id.fullImageView)
        Glide.with(requireContext())
            .load(imageUrl)
            .into(imageView)

        builder.setView(view)
        return builder.create()
    }
}
