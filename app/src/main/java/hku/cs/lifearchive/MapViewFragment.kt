package hku.cs.lifearchive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hku.cs.lifearchive.diaryentrymodel.DiaryEntry
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
        var mapsearchview = view.findViewById<SearchView>(R.id.searchView)

        mapper.getMapAsync {
            this.googleMap = it
            val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
            val allentry = diaryEntryDao.getAll()
            mapsearchview.queryHint = "Search by Title"
            mapsearchview.setOnQueryTextListener(object : OnQueryTextListener,
                SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    println("text changed")
                    return false
                }
                override fun onQueryTextSubmit(query: String): Boolean {
                    // task HERE
                    googleMap.clear()

                    println("text submitted")
                    if (query == null){
                        markerplacer(allentry,googleMap)
                    }else{
                        var result = allentry.filter { item -> item.title.contains(query) }
                        markerplacer(result,googleMap)
                    }
                    return false
                }
            })
            markerplacer(allentry,googleMap)


        }




    }
    private fun markerplacer(allentry: List<DiaryEntry>, googleMap: GoogleMap) {
        for (entry in allentry){
            val longs = entry.location?.longitude
            val lats = entry.location?.latitude
            val positions =  LatLng(lats!!, longs!!)

            googleMap.addMarker(MarkerOptions().position(positions).title(entry.title))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positions,10.0f))
        }
    }

}