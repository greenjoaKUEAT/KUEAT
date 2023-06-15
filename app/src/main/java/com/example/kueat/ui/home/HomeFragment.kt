package com.example.kueat.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.R
import com.example.kueat.databinding.FragmentHomeBinding
import com.example.kueat.`object`.Restaurant
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class HomeFragment() : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentHomeBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: MyRestaurantAdapter
    lateinit var rdb: DatabaseReference
    var Loc = ""
    var Menu = ""
    private val mainscope = CoroutineScope(Dispatchers.Main)



    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var fusedLocationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    var lat:Double?=0.0
    var lon:Double?=0.0




    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val grantedPermissions = permissions.entries.filter { it.value }.map { it.key }
            val deniedPermissions = permissions.entries.filter { !it.value }.map { it.key }

            // 권한 처리 코드 작성
            if (grantedPermissions.contains(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 위치 권한이 허용된 경우 처리
                // ...
            }

            if (deniedPermissions.contains(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 위치 권한이 거부된 경우 처리
                // ...
            }
        }

    // 위치 업데이트를 위한 설정
    private val locationRequest = LocationRequest.create().apply {
        interval = 10000 // 위치 업데이트 간격 (밀리초 단위)
        fastestInterval = 5000 // 가장 빠른 위치 업데이트 간격 (밀리초 단위)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 위치 요청의 우선순위
    }


    // 위치 업데이트 리스너
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0?.let { result ->
                val location = result.lastLocation
                location?.let { location ->
                    // 위치 업데이트를 수신할 때 수행할 작업을 여기에 작성합니다.
                    lat = location.latitude
                    lon = location.longitude
                    // 위치 정보를 활용하여 필요한 작업을 수행합니다.
                    Log.d("LOCATION", "Latitude: $lat, Longitude: $lon")
                }
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            Loc = it.getString("Loc").toString().replace("\"", "")
            Menu = it.getString("Menu")!!.replace("\"", "")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentHomeMenu.text = Menu
        binding.fragmentHomeLocation.text = Loc
//        showProgressBar()

        init()

        // 위치 업데이트 요청
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun init() {
        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // FusedLocationSource 초기화
        fusedLocationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)



        layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rdb = Firebase.database.getReference("KueatDB/Restaurant")
        Log.d("homeFragment", Loc)

        var query = rdb.orderByChild("tag").equalTo(Loc + Menu)
        if (Menu == "다 좋아") {
            query = rdb.orderByChild("tag_location").equalTo(Loc)
        }
        val option = FirebaseRecyclerOptions.Builder<Restaurant>()
            .setQuery(query, Restaurant::class.java)
            .build()
        adapter = MyRestaurantAdapter(option,LatLng(lat!!, lon!!))
        Log.d("CurrentLatLngFromFragment", "${lat}이랑 ${lon}을 보내주고 있어요!")
        adapter.itemClickListener = object : MyRestaurantAdapter.OnItemClickListener {
            override fun OnItemClick(position: Int, model: Restaurant) {
                val bundle = Bundle()
                bundle.putLong("restaurant", model.restaurant_id)
                val restaurantFragment = RestaurantFragment()
                restaurantFragment.arguments = bundle
//                (activity as MainActivity).changeBottomIconHome()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(com.example.kueat.R.id.main_frm, restaurantFragment).commit()
                /* 송현이형 프래그먼트 이름 알아낸 후 전환해주기
                val bundle = Bundle() // 번들을 통해 값 전달
                bundle.putInt("RestaurantID", model.restaurant_id) //번들에 넘길 값 저장
                val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                val fragment2 = RestaurantDetailFragment() //프래그먼트2 선언
                fragment2.setArguments(bundle) //번들을 프래그먼트2로 보낼 준비
                transaction.replace(com.example.kueat.R.id.init_frm, fragment2)
                transaction.commit()
                 */
            }
        }

        binding.fragmentHomeRecyclerView.layoutManager = layoutManager
        binding.fragmentHomeRecyclerView.adapter = adapter
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
        Log.d("homeFragment", "start")
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        Log.d("homeFragment", "stop")
    }





    override fun onMapReady(naverMap: NaverMap) {
        // 위치 소스 설정
        naverMap.locationSource = fusedLocationSource

        // 현재 위치 가져오기
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        lat = it.latitude
                        lon = it.longitude

                        // 현재 위치 정보를 활용하여 원하는 작업 수행
                        Log.d("LATLON","$lat $lon")
                    }
                }
        } else {
            requestLocationPermission()
            Log.d("LATLON","퍼미션 실패!")

        }
    }

    private fun requestLocationPermission() {
        // 위치 권한 요청
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 허용된 경우 처리
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.navermap) as MapFragment?
                mapFragment?.getMapAsync(this)
            } else {
                // 위치 권한이 거부된 경우 처리
                // ...
            }
        }
    }



}













