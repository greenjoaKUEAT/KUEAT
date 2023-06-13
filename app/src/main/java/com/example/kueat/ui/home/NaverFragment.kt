package com.example.kueat.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kueat.databinding.FragmentNaverBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker


class NaverFragment : Fragment() , OnMapReadyCallback{
    var binding: FragmentNaverBinding ?= null
    lateinit var rdb : DatabaseReference
    var rest_id = 0
    var loc = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNaverBinding.inflate(inflater, container, false)
        rest_id = arguments?.getString("rest_id")!!.toInt()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.navermap.onStart()
        rdb = Firebase.database.getReference("KueatDB/Restaurant")
        rdb.get().addOnSuccessListener {
            val map = it.child(rest_id.toString()).getValue() as HashMap<String,Any>
            loc = map.get("location").toString()
        }

    }
    override fun onMapReady(naverMap: NaverMap) {
        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Satellite)
        val marker = Marker()
//        marker.position = LatLng(loc)
//        marker.map=naverMap

        //건물 표시
        naverMap.setLayerGroupEnabled(naverMap.enabledLayerGroups.toString(), true)

        //위치 및 각도 조정
        val cameraPosition = CameraPosition(
            LatLng(33.38, 126.55),  // 위치 지정
            9.0,  // 줌 레벨
            45.0,  // 기울임 각도
            45.0 // 방향
        )
        naverMap.setCameraPosition(cameraPosition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}