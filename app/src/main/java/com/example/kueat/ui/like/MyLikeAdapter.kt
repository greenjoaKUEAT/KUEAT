package com.example.kueat.ui.like

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.databinding.LikerowBinding
import com.example.kueat.`object`.Restaurant
import com.example.kueat.`object`.location
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyLikeAdapter(val items:ArrayList<Restaurant>,val activity: Activity)
    : RecyclerView.Adapter<MyLikeAdapter.ViewHolder>(){
    private val mainscope = CoroutineScope(Dispatchers.Main)
    lateinit var dbMenu: DatabaseReference
    private val TAG="likeFragment"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    internal lateinit var mLocationRequest: LocationRequest
    lateinit var mLastLocation: Location
    var userLocation = location()
    private var currentLocation: Location? = null

    interface OnItemClickListener{
        fun onItemClick(holder: ViewHolder,position:Int)
    }
    var itemClickListener:OnItemClickListener?=null
    inner class ViewHolder(val binding:LikerowBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener{
                itemClickListener?.onItemClick(this,bindingAdapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyLikeAdapter.ViewHolder {
        val view = LikerowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getRepMenu(restaurant_id: Long):Deferred<String>{
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

    override fun onBindViewHolder(
        holder: MyLikeAdapter.ViewHolder,
        position: Int
    ) {
        holder.binding.apply{
            textRestaurantName.text = items[position].name
            textTag.text = "#${items[position].tag_location} #${items[position].tag_type}"
            textRate.text = items[position].rating
            mainscope.launch {
                val restaurantLocation = LatLng(items[position].latitude.toDouble(), items[position].longitude.toDouble()) // 식당 위치 정보
                restaurantDistance.text = "내 위치로부터 ${getDistance(restaurantLocation).await()}m"
                textSignature.text = "${getRepMenu(items[position].restaurant_id).await()}"
                textEnd.visibility = View.VISIBLE
            }
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