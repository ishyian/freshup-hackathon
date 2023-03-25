package com.melitopolcherry.hackathon.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.melitopolcherry.hackathon.databinding.ItemPlaceRecyclerBinding
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ChangeAddressAutocompleteAdapter(private val context: Context) :
    RecyclerView.Adapter<ChangeAddressAutocompleteAdapter.PredictionHolder>(), Filterable {

    private var resultList = ArrayList<PlaceAutocomplete>()
    private val placesClient: PlacesClient = Places.createClient(context)
    private var clickListener: ClickListener? = null

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null) {
                    resultList = getPredictions(constraint)
                    results.values = resultList
                    results.count = resultList.size
                }
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun getPredictions(constraint: CharSequence): ArrayList<PlaceAutocomplete> {
        val resultList = ArrayList<PlaceAutocomplete>()
        val token = AutocompleteSessionToken.newInstance()

        val request = FindAutocompletePredictionsRequest.builder()
            //.setLocationBias(bounds)
            //.setCountry("BD")
            .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(token).setQuery(constraint.toString()).build()

        val autocompletePredictions = placesClient.findAutocompletePredictions(request)

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }

        return if (autocompletePredictions.isSuccessful) {
            val findAutocompletePredictionsResponse = autocompletePredictions.result
            if (findAutocompletePredictionsResponse != null) for (prediction in findAutocompletePredictionsResponse.autocompletePredictions) {
                resultList.add(
                    PlaceAutocomplete(
                        prediction.placeId,
                        prediction.getPrimaryText(STYLE_NORMAL).toString(),
                        prediction.getFullText(STYLE_BOLD).toString()
                    )
                )
            }
            resultList
        } else {
            resultList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): PredictionHolder {
        val binding =
            ItemPlaceRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PredictionHolder(binding)
    }

    override fun onBindViewHolder(mPredictionHolder: PredictionHolder, i: Int) =
        mPredictionHolder.bind(resultList[mPredictionHolder.bindingAdapterPosition])

    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class PredictionHolder(val binding: ItemPlaceRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(placeAutocomplete: PlaceAutocomplete) {
            binding.placeAddress.text = placeAutocomplete.address
            binding.placeArea.text = placeAutocomplete.area
        }

        override fun onClick(v: View) {
            if (bindingAdapterPosition < resultList.size) {
                val item = resultList[bindingAdapterPosition]
                val placeId = item.placeId.toString()

                val placeFields =
                    listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS,
                        Place.Field.ADDRESS_COMPONENTS
                    )
                val request = FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient.fetchPlace(request).addOnSuccessListener { response ->
                    val place = response.place
                    clickListener?.onPlaceSelected(place)
                }.addOnFailureListener { exception ->
                    if (exception is ApiException) {
                        Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    class PlaceAutocomplete(
        var placeId: CharSequence, var area: CharSequence,
        var address: CharSequence
    ) {
        override fun toString(): String {
            return area.toString()
        }
    }

    interface ClickListener {
        fun onPlaceSelected(place: Place)
    }

    companion object {
        private val STYLE_BOLD = StyleSpan(Typeface.BOLD)
        private val STYLE_NORMAL = StyleSpan(Typeface.NORMAL)
    }
}