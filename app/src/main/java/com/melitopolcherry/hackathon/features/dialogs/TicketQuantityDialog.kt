package com.melitopolcherry.hackathon.features.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.adapters.CheckoutQuantityAdapter
import com.melitopolcherry.hackathon.adapters.callbacks.TicketCountCallback
import com.melitopolcherry.hackathon.databinding.DialogTicketQuantityBinding

class TicketQuantityDialog(
    private val listOfCount: List<Int>?,
    private val ticketCountCallback: TicketCountCallback.Callback
) : DialogFragment(), CheckoutQuantityAdapter.QuantitySelectedListener {

    private var adapter: CheckoutQuantityAdapter? = null
    private var selectedQuaint: Int? = null
    lateinit var binding: DialogTicketQuantityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogTicketQuantityBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CheckoutQuantityAdapter(this)

        listOfCount?.let {
            adapter?.setData(it, selectedQuaint)
        }
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.checkoutCountRecycler.adapter = adapter
    }

    override fun onQuantitySelected(count: Int) {
        if (selectedQuaint != count) {
            selectedQuaint = count
        }

        if (selectedQuaint != null) {
            ticketCountCallback.onClick(selectedQuaint!!)
            dismiss()
        } else {
            Toast.makeText(requireContext(), "You should select quantity", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroyView() {
        binding.checkoutCountRecycler.adapter = null
        super.onDestroyView()
    }

     companion object {
         var TAG = "TicketQuantityDialog"
     }
}