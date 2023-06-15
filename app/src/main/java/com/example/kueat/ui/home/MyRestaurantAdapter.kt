package com.example.kueat.ui.home

import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kueat.databinding.LikerowBinding
import com.example.kueat.`object`.Restaurant
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyRestaurantAdapter(
    options: FirebaseRecyclerOptions<Restaurant>,
    private val currentLatLng: com.naver.maps.geometry.LatLng
) :
    FirebaseRecyclerAdapter<Restaurant, MyRestaurantAdapter.ViewHolder>(options) {

    private var locationManager: LocationManager? = null
    private val mainscope = CoroutineScope(Dispatchers.Main)
    lateinit var dbMenu: DatabaseReference
    private val TAG = "likeFragment"


    interface OnItemClickListener {
        fun OnItemClick(position: Int, model: Restaurant)
    }

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: LikerowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            Log.d("HI", "HI")

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
            LikerowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("HI", "HI")

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Restaurant) {
        Log.d("HI", "HI")

        holder.binding.apply {

            val restaurantLatlng = LatLng(
                model.location.Latitude.toDouble(),
                model.location.Longitude.toDouble()
            ) // 식당 위치 정보
            Log.d("RestaurantLatLng", restaurantLatlng.toString())
            Log.d("CurrentLatLng", currentLatLng.toString())

            if (currentLatLng != null) {
                val distance = getDistance(restaurantLatlng)
                restaurantDistance.text = "내 위치로부터 ${distance}m"
            }


//            restaurantImage.setImageResource(getUrl(model.photo))
            val imageUrl = model.photo
            Glide.with(restaurantImage)
                .load(imageUrl)
                .into(restaurantImage)



            textRestaurantName.text = model.name
            textTag.text = "#${model.tag_location} #${model.tag_type}"
            textRate.text = model.rating
            mainscope.launch {
                textSignature.text = "${getRepMenu(model.restaurant_id).await()}"
                textEnd.visibility = View.VISIBLE
            }
        }
    }

    fun getRepMenu(restaurant_id: Long): Deferred<String> {
        val restaurantId = restaurant_id
        dbMenu = Firebase.database.getReference("KueatDB/Menu")
        var signatureArr: ArrayList<String> = arrayListOf()
        var signatureStr = ""

        return mainscope.async {
            withContext(Dispatchers.IO) {
                dbMenu.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (data in it.result.children) {
                            Log.e(
                                TAG,
                                "ValueEventListener-onDataChange : ${data.value}",
                            )
                            val datas = data.value as Map<String, String>
                            var resId = datas["restaurant_id"]!!.toLong()
                            Log.e(
                                TAG,
                                "menu : ${datas["restaurant_id"]}&$restaurantId / ${datas["signature"]}",
                            )

                            if ((resId == restaurantId) && datas["signature"]!!.equals("1")) {
                                Log.e(
                                    TAG,
                                    "ValueEventListener-onDataChange : ${datas["restaurant_id"]}&$restaurantId",
                                )
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
            if (signatureArr.size == 0)
                signatureStr = "다"
            return@async signatureStr
        }
    }

    private fun getDistance(
        restaurantLocation: LatLng
    ): Int {
        val restaurantLocation = Location("restaurant").apply {
            latitude = restaurantLocation.latitude
            longitude = restaurantLocation.longitude
        }
        val currentLocation = Location("restaurant").apply {
            latitude = restaurantLocation.latitude
            longitude = restaurantLocation.longitude
        }

        Log.d("Distance", "${restaurantLocation}랑 ${currentLocation}의 거리 : ${currentLocation.distanceTo(restaurantLocation).toInt()} ")
        return currentLocation.distanceTo(restaurantLocation).toInt()
    }


}