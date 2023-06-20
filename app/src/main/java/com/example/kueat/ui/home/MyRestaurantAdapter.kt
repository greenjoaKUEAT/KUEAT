package com.example.kueat.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kueat.databinding.FragmentHomeRowBinding
import com.example.kueat.databinding.LikerowBinding
import com.example.kueat.`object`.Restaurant
import com.example.kueat.`object`.location
import com.example.kueat.viewmodel.MyUserModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

class MyRestaurantAdapter(options: FirebaseRecyclerOptions<Restaurant>,val activity: Activity) :
    FirebaseRecyclerAdapter<Restaurant, MyRestaurantAdapter.ViewHolder>(options) {

    private var locationManager: LocationManager? = null
    private val mainscope = CoroutineScope(Dispatchers.Main)
    lateinit var dbMenu: DatabaseReference
    private val TAG="restaurantAdapter"
    lateinit var fusedLocationClient: FusedLocationProviderClient
    internal lateinit var mLocationRequest: LocationRequest
    lateinit var mLastLocation: Location
    var userLocation = location()
    private var currentLocation: Location? = null
    interface OnItemClickListener {
        fun OnItemClick(position: Int, model: Restaurant)
    }

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: LikerowBinding) :
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

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LikerowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("HI","HI")

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Restaurant) {
        Log.d("HI","HI")

        holder.binding.apply {
//            restaurantImage.setImageResource(getUrl(model.photo))
            var firebaseStorage = FirebaseStorage.getInstance()
            Log.d("qwerty123", firebaseStorage.toString())
            var firebaseStorageRef = firebaseStorage.getReference(model.photo)
            Log.d("qwerty123", model.photo.toString())
            var url = firebaseStorageRef.downloadUrl.addOnSuccessListener {
                Glide.with(activity.applicationContext).load(it).dontAnimate().into(restaurantImage)
                Log.d("qwerty123", it.toString())
            }
            textRestaurantName.text = model.name
            textTag.text = "#${model.tag_location} #${model.tag_type}"
            textRate.text = model.rating
            textSignature.text = model.signature
            textEnd.visibility = View.VISIBLE
            mainscope.launch {
                val restaurantLocation = LatLng(model.latitude.toDouble(), model.longitude.toDouble()) // 식당 위치 정보
                restaurantDistance.text = "내 위치로부터 ${getDistance(restaurantLocation).await()}m"

            }
        }
    }

    fun getRepMenu(restaurant_id: Long): Deferred<String> {
        val restaurantId = restaurant_id
        dbMenu = Firebase.database.getReference("KueatDB/Menu")
        var signatureArr:ArrayList<String> = arrayListOf()
        var signatureStr = ""

        return mainscope.async{
            withContext(Dispatchers.IO){
                dbMenu.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (data in it.result.children) {
                            val datas = data.value as Map<String, String>
                            var resId = datas["restaurant_id"]!!.toLong()

                            if ((resId==restaurantId)&& datas["signature"]!!.equals("1")) {
                                datas["name"]?.let { it1 -> signatureArr.add(it1) }
                            }
                        }
                    }
                }.await()
            }

            for (i in signatureArr) {
                signatureStr += i
                if (i != signatureArr[signatureArr.size - 1])
                    signatureStr += ", "
            }
            if(signatureArr.size==0)
                signatureStr = "다"
            return@async signatureStr
        }
    }

    private fun getDistance(
        restaurantLocation: LatLng
    ): Deferred<Int> {
        val restaurantLatLng = Location("restaurant").apply {
            latitude = restaurantLocation.latitude
            longitude = restaurantLocation.longitude
        }
        return mainscope.async {
            currentLocation = Location("restaurnat").apply {
                    getUserLocation().await()
                    latitude = userLocation.Latitude.toDouble()
                    longitude = userLocation.Longitude.toDouble()
                }
//            Log.d(TAG,"current : ${currentLocation!!.latitude}/${currentLocation!!.longitude}, current : ${restaurantLatLng!!.latitude}/${restaurantLatLng!!.longitude}")
//            Log.d(TAG, "${restaurantLocation}랑 ${currentLocation}의 거리 : ${currentLocation!!.distanceTo(restaurantLatLng).toInt()} ")
            return@async currentLocation!!.distanceTo(restaurantLatLng).toInt()
        }
    }
    val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getUserLocation(): Deferred<location> {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
            fastestInterval = 800
        }
        var loc: location = location()
        return mainscope.async {
            if (permissions.all {
                    ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
                }) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        fusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())

                    }.await()
            }else{
            }
            return@async loc
        }

    }
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        userLocation = location(mLastLocation.latitude.toString(),mLastLocation.longitude.toString())
        Log.d(TAG,"userLoc : ${userLocation.Latitude}")
        notifyDataSetChanged()
    }

    fun stopLocationUpdate(){
        fusedLocationClient.removeLocationUpdates(mLocationCallback)
    }
}