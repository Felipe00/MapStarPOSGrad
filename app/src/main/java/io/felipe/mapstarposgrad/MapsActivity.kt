package io.felipe.mapstarposgrad

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mLocationPermissionGranted: Boolean = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Checa se o usuário permitiu o acesso à localização
        getLocationPermission()

        // Se a permissão foi garantida, atualiza o mapa
        if (mLocationPermissionGranted) {
            updateUi()
        }

        // Instancia o fragmento do MAPA
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Checa se as permissões foram garantidas, senão solicita a permissão em tempo de execução
     */
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                    updateUi()
                }
            }
        }
    }

    /**
     * Se o mapa estiver pronto, passa o mapa pra variável global
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
    }

    /**
     * Atualiza o mapa com a localização
     */
    private fun updateUi() {
        // Checa se há algum Provider habilitado para buscar a localização
        if (SmartLocation.with(this@MapsActivity).location().state().isNetworkAvailable) {
            Toast.makeText(this, "Aguarde, recebendo localização", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MapsActivity, "Ative o GPS", Toast.LENGTH_LONG).show();
        }
        SmartLocation.with(this@MapsActivity)
                .location()
                .config(LocationParams.BEST_EFFORT)
                .start { location ->
                    if (location != null) {
                        var cp: CameraPosition = CameraPosition(LatLng(location.latitude, location.longitude), 15F, 1F, 1F)
                        val sydney = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(sydney).title("Você está aqui"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp))
                    }
                }
    }
}
