package com.example.navermap

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder;
import android.location.Location
import android.location.LocationManager
import android.os.Bundle;
import android.os.Looper
import android.util.Log
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager;
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var LOCATION_PERMISSION = 1004

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION ,android.Manifest.permission.ACCESS_COARSE_LOCATION )

    private val marker =Marker()
    private val infoWindow = InfoWindow()

    private var latLng :LatLng ?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val fragmentManager: FragmentManager = supportFragmentManager
        var mapFragment: MapFragment? = fragmentManager.findFragmentById(R.id.map) as MapFragment?
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            fragmentManager.beginTransaction().add(R.id.map, mapFragment).commit()
        }

        mapFragment!!.getMapAsync(this)

        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
            override fun getText(infoWindow: InfoWindow): CharSequence {

                return "정보 창 내용"
            }
        }


    }


    @UiThread
    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom =18.0
        naverMap.minZoom =5.0

        val uiSetting =naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true
        uiSetting.isCompassEnabled =true

        naverMap.locationSource = locationSource
        Log.i("NaverMap", "locationSource $locationSource")
        ActivityCompat.requestPermissions(this, PERMISSION, LOCATION_PERMISSION)


        if (latLng !=null){
            marker.position = (latLng!!)
            marker.map = naverMap
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.BLUE
        }

        naverMap.addOnCameraChangeListener { reason, animated ->
            Log.i("NaverMap", "카메라 변경 - reson: $reason, animated: $animated")
        }
        naverMap.addOnCameraIdleListener {
            Log.i("NaverMap", "카메라 finish")
        }

        infoWindow.open(marker)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when {
            requestCode != LOCATION_PERMISSION -> {
                return
            }
            else -> {
                when {
                    locationSource.onRequestPermissionsResult(requestCode,permissions,grantResults) -> {
                        if (!locationSource.isActivated){
                            naverMap.locationTrackingMode = LocationTrackingMode.None
                        }else{
                            Log.i("NaverMap", "onRequestPermissionsResult")
                            naverMap.locationTrackingMode = LocationTrackingMode.Follow

                        }
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

//    private fun startLocationUpdates() {
//        Log.d("NaverMap", "startLocationUpdates()")
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())
//    }
//
//
//    private val mLocationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            Log.d("NaverMap", "onLocationResult()")
//
//            locationResult.lastLocation
//            onLocationChanged(locationResult.lastLocation)
//        }
//    }
//    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
//    fun onLocationChanged(location: Location) {
//        Log.d("NaverMap", "onLocationChanged()")
//        lastLocation = location
//        latLng = LatLng(lastLocation.latitude,lastLocation.longitude)
//
//    }
//    // 위치 업데이터를 제거 하는 메서드
//    private fun stopLocationUpdates() {
//        Log.d("NaverMap", "stoplocationUpdates()")
//        // 지정된 위치 결과 리스너에 대한 모든 위치 업데이트를 제거
//        fusedLocationClient.removeLocationUpdates(mLocationCallback)
//    }



}