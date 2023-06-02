package com.example.kueat.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kueat.databinding.FragmentNaverBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback


class NaverFragment : Fragment() , OnMapReadyCallback{

    lateinit var binding: FragmentNaverBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNaverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navermap.onStart()
    }
    override fun onMapReady(naverMap: NaverMap) {
        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Satellite)

        //건물 표시
        //naverMap.setLayerGroupEnabled(naverMap.enabledLayerGroups.toString(), true)

        //위치 및 각도 조정
        val cameraPosition = CameraPosition(
            LatLng(33.38, 126.55),  // 위치 지정
            9.0,  // 줌 레벨
            45.0,  // 기울임 각도
            45.0 // 방향
        )
        naverMap.setCameraPosition(cameraPosition)
    }


}