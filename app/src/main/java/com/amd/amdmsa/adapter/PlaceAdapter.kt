package com.amd.amdmsa.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.amd.amdmsa.R
import com.amd.amdmsa.databinding.ItemRowPlaceBinding
import com.amd.amdmsa.model.Place
import javax.inject.Inject


class PlaceAdapter  @Inject constructor (private val listener: PlaceItemListener) : RecyclerView.Adapter<PlaceAdapter.CategoryItemsViewHolder>() {
    private var placeList = ArrayList<Place>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceAdapter.CategoryItemsViewHolder {
        val binding = ItemRowPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryItemsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    override fun onBindViewHolder(holder: PlaceAdapter.CategoryItemsViewHolder, position: Int) {
        holder.updateData(placeList[position], position)
    }

    fun updateData(itemList: ArrayList<Place>) {
        placeList.clear()
        placeList.addAll(itemList)
        notifyDataSetChanged()
    }


    inner class CategoryItemsViewHolder(private val binding: ItemRowPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        private val itemName: AppCompatTextView = binding.tvPlaceName
        private val itemAddress: AppCompatTextView = binding.tvPlaceAddress
        private val itemDistance: AppCompatTextView = binding.tvPlaceDistance


        fun updateData(place: Place, position: Int) {
            itemName.text = itemName.context.getString(R.string.place_name, place.name)
            itemAddress.text = itemName.context.getString(R.string.place_address, place.location.formattedAddress)
            itemDistance.text = itemName.context.getString(R.string.place_distance, place.distance.toString())
        }
    }


    interface PlaceItemListener {

    }

}
