package com.melitopolcherry.hackathon.features.dialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import coil.imageLoader
import coil.request.ImageRequest
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.databinding.DialogPicturePreviewBinding

class PicturePreviewDialog(private val url: String) : DialogFragment() {

    lateinit var binding: DialogPicturePreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPicturePreviewBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogMain.setOnClickListener { dismiss() }
        binding.closeButton.setOnClickListener { dismiss() }
        binding.collapsingImageView.setOnClickListener {}

        binding.loadingView.visibility = View.VISIBLE
        val request = ImageRequest.Builder(requireContext())
            .data(url)
            .target(
                onSuccess = { result ->
                    binding.loadingView.visibility = View.GONE
                    binding.collapsingImageView.setImageDrawable(result)
                    binding.closeButton.visibility = View.VISIBLE
                },
                onError = {
                    binding.loadingView.visibility = View.GONE
                    binding.seatEmptyState.visibility = View.VISIBLE
                    binding.closeButton.visibility = View.VISIBLE
                    binding.collapsingImageView.setBackgroundColor(Color.LTGRAY)
                }
            )
            .build()
        requireContext().imageLoader.enqueue(request)
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }
}
