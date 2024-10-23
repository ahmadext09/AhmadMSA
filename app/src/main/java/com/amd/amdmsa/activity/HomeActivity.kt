package com.amd.amdmsa.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amd.amdmsa.adapter.PlaceAdapter
import com.amd.amdmsa.databinding.ActivityHomeBinding
import com.amd.amdmsa.model.FsqNearbyQueryParam
import com.amd.amdmsa.model.Place
import com.amd.amdmsa.utility.AppUtility
import com.amd.amdmsa.utility.Resource
import com.amd.amdmsa.viewmodel.HomeViewModel
import com.amd.amdmsa.viewmodel.HomeViewModelProviderFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity(), PlaceAdapter.PlaceItemListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var ll: String? = null
    private lateinit var manager: LinearLayoutManager

    @Inject
    lateinit var viewModelFactory: HomeViewModelProviderFactory
    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var adapter: PlaceAdapter

    @Inject
    lateinit var appUtility: AppUtility
    private val pizzaPlaces: ArrayList<Place> = ArrayList()
    private val juicePlaces: ArrayList<Place> = ArrayList()
    private val pizzaJuicePlaces: ArrayList<Place> = ArrayList()
    private val combinedLiveData = MediatorLiveData<Pair<List<Place>?, List<Place>?>>()


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
        initViews()
    }

    private fun init() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initViews() {
        checkUserPermission()
        observeResponse()
        setUpRVPlace()
        handleDisplayPizzaClick()
        handleDisplayJuiceClick()
    }

    private fun setUpRVPlace() {
        manager = LinearLayoutManager(this)
        binding.rvPlaces.layoutManager = manager
        binding.rvPlaces.adapter = adapter
    }


    private fun checkUserPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getLocation()
        }
    }


    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    ll = "$latitude,$longitude"
                    ll?.let {
                        val pizzaQuery = FsqNearbyQueryParam(ll.toString(), 1500, "pizza", 20)
                        val juiceQuery = FsqNearbyQueryParam(ll.toString(), 1500, "juice", 20)
                        appUtility.showProgressDialog(this)
                        lifecycleScope.launch {
                            makeParallelApiCalls(pizzaQuery, juiceQuery)
                        }
                    }
                    Toast.makeText(this, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private suspend fun makeParallelApiCalls(pizzaQuery: FsqNearbyQueryParam, juiceQuery: FsqNearbyQueryParam) {
        try {
            coroutineScope {
                async { viewModel.getNearByLocations(pizzaQuery, viewModel.nearByPizzaLocationLiveData) }.await()
                async { viewModel.getNearByLocations(juiceQuery, viewModel.nearByJuiceLocationLiveData) }.await()

                withContext(Dispatchers.Main) {
                    appUtility.hideProgressDialog()
                }
            }
        } catch (e: Exception) {
            appUtility.hideProgressDialog()
            Toast.makeText(this@HomeActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun observeResponse() {
        combinedLiveData.addSource(viewModel.nearByPizzaLocationLiveData) {
            when (it) {
                is Resource.Success -> {
                    it.data.let { fourSquareNearbyResponse ->
                        fourSquareNearbyResponse?.results.let { pizzaList ->
                            if (!pizzaList.isNullOrEmpty()) {
                                pizzaPlaces.clear()
                                pizzaPlaces.addAll(pizzaList)
                                updateCombinedLiveData()
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                }
            }
        }


        combinedLiveData.addSource(viewModel.nearByJuiceLocationLiveData) {
            when (it) {
                is Resource.Success -> {
                    it.data.let { fourSquareNearbyResponse ->
                        fourSquareNearbyResponse?.results.let { juiceList ->
                            if (!juiceList.isNullOrEmpty()) {
                                juicePlaces.clear()
                                juicePlaces.addAll(juiceList)
                                updateCombinedLiveData()
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    appUtility.hideProgressDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                }
            }
        }

        combinedLiveData.observe(this) { pair ->
            if (juicePlaces.isNotEmpty() && pizzaPlaces.isNotEmpty()) {
                performMatchForCommonPlaces()
            }
        }

    }


    private fun updateCombinedLiveData() {
        combinedLiveData.value = Pair(pizzaPlaces, juicePlaces)
    }

    private fun performMatchForCommonPlaces() {
        pizzaJuicePlaces.clear()
        if (pizzaPlaces.isNotEmpty() && juicePlaces.isNotEmpty()) {
            val pizzaPlaceIds = pizzaPlaces.map { it.fsqId }.toHashSet()
            pizzaJuicePlaces.addAll(juicePlaces.filter { pizzaPlaceIds.contains(it.fsqId) })
            if (pizzaJuicePlaces.isNotEmpty()) {
                adapter.updateData(pizzaJuicePlaces)
            } else {
                binding.clOptions.visibility = View.VISIBLE
            }
            Toast.makeText(this, "Places offering both pizza and juice: ${pizzaJuicePlaces.size}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleDisplayPizzaClick() {
        binding.btnDisplayPizzaPlaces.setOnClickListener {
            if (pizzaPlaces.isNotEmpty())
                adapter.updateData(pizzaPlaces)
            else
                Toast.makeText(this, "No search result for pizza", Toast.LENGTH_SHORT).show()

        }
    }

    private fun handleDisplayJuiceClick() {
        binding.btnDisplayJuicePlaces.setOnClickListener {
            if (juicePlaces.isNotEmpty())
                adapter.updateData(juicePlaces)
            else
                Toast.makeText(this, "No search result for juice", Toast.LENGTH_SHORT).show()
        }
    }


}
