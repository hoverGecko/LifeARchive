package hku.cs.lifearchive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase

class MapViewFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var mapper: MapView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps123, container, false)
    }
    override fun onResume() {
        super.onResume()
        mapper.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapper.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapper.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapper.onLowMemory()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapper =view.findViewById<MapView>(R.id.map2)
        mapper.onCreate(savedInstanceState)
        mapper.getMapAsync {
            this.googleMap = it
            val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
            val allentry = diaryEntryDao.getAll()
            for (entry in allentry){
                val longs = entry.location?.longitude
                val lats = entry.location?.latitude
                val positions =  LatLng(lats!!, longs!!)

                googleMap.addMarker(MarkerOptions().position(positions).title(entry.title))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(positions))
            }

        }


    }
}