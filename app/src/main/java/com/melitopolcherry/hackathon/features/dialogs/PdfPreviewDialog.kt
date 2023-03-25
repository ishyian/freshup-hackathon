package com.melitopolcherry.hackathon.features.dialogs

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.databinding.DialogPdfPreviewBinding

class PdfPreviewDialog(private var pdfString: String) : DialogFragment() {

    lateinit var binding: DialogPdfPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPdfPreviewBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogMain.setOnClickListener { dismiss() }
        binding.closeButton.setOnClickListener { dismiss() }
        val clearData = pdfString.replace("\\n", "")
        val bytes = clearData.toByteArray(charset("UTF-8"))
        val decoded = Base64.decode(bytes, Base64.DEFAULT)
        binding.pdfView.fromBytes(decoded)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .load()
    }
}