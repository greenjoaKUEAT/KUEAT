package com.example.kueat.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.EdgeEffectCompat.getDistance
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kueat.databinding.FragmentHomeRowBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import java.net.URL

class MyRestaurantAdapter(options: FirebaseRecyclerOptions<Restaurant>) :
    FirebaseRecyclerAdapter<Restaurant, MyRestaurantAdapter.ViewHolder>(options) {

    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null



    interface OnItemClickListener {
        fun OnItemClick(position: Int, model: Restaurant)
    }

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: FragmentHomeRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            Log.d("HI","HI")

            binding.root.setOnClickListener {
                itemClickListener!!.OnItemClick(
                    bindingAdapterPosition,
                    getItem(bindingAdapterPosition)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            FragmentHomeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("HI","HI")

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Restaurant) {
        Log.d("HI","HI")

        holder.binding.apply {

            val restaurantLocation = LatLng(model.location.latitude, model.location.longitude) // 식당 위치 정보
            if (currentLocation != null) {
                val distance = getDistance(currentLocation!!, restaurantLocation)
                restaurantDistance.text = "내 위치로부터 ${distance}m"
            }


//            restaurantImage.setImageResource(getUrl(model.photo))
            val imageUrl = model.photo
            Glide.with(restaurantImage)
                .load(imageUrl)
                .into(restaurantImage)



            restaurantName.text = model.name
            restaurantLocationTag.text = "#" + model.tag_location
            restaurantMenuTag.text = "#" + model.tag_type
            restaurantRepMenu.text = "대표 메뉴 : " + getRepMenu(model.restaurant_id)


        }
    }

    private fun getRepMenu(restaurant_id: Int): String? {
        val restaurantId = restaurant_id

        val menuRef = FirebaseDatabase.getInstance().getReference("KueatDB/Menu")
        val query = menuRef.orderByChild("signature").equalTo("1")


        return "아직 모르겠다"

    }

    private fun getDistance(
        currentLocation: Location,
        restaurantLocation: LatLng
    ): Int {
        val restaurantLatLng = Location("restaurant").apply {
            latitude = restaurantLocation.latitude
            longitude = restaurantLocation.longitude
        }

        return currentLocation.distanceTo(restaurantLatLng).toInt()
    }


    // 위치 권한을 확인하고 위치 서비스를 시작합니다.
    private fun startLocationUpdates(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // 위치 권한이 허용되었는지 확인합니다.
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener
            )

            // 최신 위치 정보를 가져옵니다.
            val lastKnownLocation =
                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                currentLocation = lastKnownLocation
            }
        }
    }

    // 위치 서비스를 중지합니다.
    private fun stopLocationUpdates() {
        locationManager?.removeUpdates(locationListener)
    }

    companion object {
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 1000 // 최소 업데이트 간격 (1초)
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10.0f // 최소 업데이트 거리 (10미터)
    }

    // 위치 정보를 업데이트하는 LocationListener
    private
    val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
        }

    }


}