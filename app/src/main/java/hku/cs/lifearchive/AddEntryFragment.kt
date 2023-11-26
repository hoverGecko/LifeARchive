package hku.cs.lifearchive

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import hku.cs.lifearchive.diaryentrymodel.DiaryEntry
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase
import java.security.Permissions
import java.util.Date

class AddEntryFragment : Fragment() {

    val rc = 1

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        fun newInstance() = AddEntryFragment()
    }




    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return inflater.inflate(R.layout.fragment_add_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
        val button= view.findViewById<Button>(R.id.Add)
        val title= view.findViewById<TextInputLayout>(R.id.TitleInput)
        val nowdate = view.findViewById<TextView>(R.id.Dateview)
        val nowlong = view.findViewById<TextView>(R.id.LongView)
        val nowlata = view.findViewById<TextView>(R.id.LatView)
        val content = view.findViewById<TextInputLayout>(R.id.ContentInput)
        val dates = Date()
        var nowlocation  = hku.cs.lifearchive.diaryentrymodel.Location()
        nowdate.text = dates.toString()


        //TODO: find ways to get current longtitude and latitude
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
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
            ActivityCompat.requestPermissions(requireActivity(),arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), rc)
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->

                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    println("success")
                    nowlong.text = location.latitude.toString()
                    nowlata.text = location.longitude.toString()
                    nowlocation.latitude = location.latitude
                    nowlocation.longitude = location.longitude
                    println(location.latitude)
                    println(location.longitude)

                }else
                {
                    println("null location")
                }
            }


        button?.setOnClickListener {
            println(title.editText?.text)

            diaryEntryDao.add(
                DiaryEntry(title=title.editText?.text.toString(), content = content.editText?.text.toString(),
                picturePaths = arrayListOf("1,2,","testpath"), voiceRecording = null,
                arVideoPath = null, location = nowlocation, date = dates
            )
            )
            println("button Clicked")
            loadFragment(ItemFragment())
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == rc)  {
            if (grantResults.size > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "Location permissions granted", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun loadFragment(fragment: Fragment){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(this.id, fragment)
        transaction?.commit()
    }
}