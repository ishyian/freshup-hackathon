package com.melitopolcherry.hackathon.features.bottom

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.base.BaseBottomFragmentDialog
import com.melitopolcherry.hackathon.databinding.BottomSheetOrderLinksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderLinksBottomFragment(private val links: List<String>) : BaseBottomFragmentDialog(),
    AdapterView.OnItemClickListener {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    lateinit var binding: BottomSheetOrderLinksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetOrderLinksBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_update_password, null)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1, links
        )

        binding.rc.adapter = adapter
        binding.rc.onItemClickListener = this
    }

    companion object {
        const val TAG = "UpdatePasswordBottomFragment"
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(links[position])))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}