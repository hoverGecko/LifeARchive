package hku.cs.lifearchive

import android.R.attr.defaultValue
import android.app.AlertDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase
import java.text.SimpleDateFormat
import java.util.Locale


class EntryDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entry_details, container, false)

        val titleGet : TextView = view.findViewById(R.id.Titledetail)
        val contentHint: TextView = view.findViewById(R.id.content)
        val contentGet: TextView = view.findViewById(R.id.Contentdetail)
        val videotitle: TextView= view.findViewById(R.id.textView11)
        val playbutton: Button = view.findViewById(R.id.Playvideo)
        //get value from bundle
        val bundle = this.arguments ?: return view

        val id = bundle.getInt("id", defaultValue)
        val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
        val target = diaryEntryDao.getById(id)
        titleGet.text = target.title
        contentGet.text = target.content
        if (target.content.isNullOrBlank()) {
            contentHint.visibility = View.GONE
            contentGet.visibility = View.GONE
        }

        // photo
        val photoView: ImageView = view.findViewById(R.id.photo_view)
        if (target.picturePaths.isNotEmpty()) {
            val photoUri = target.picturePaths[0]
            try {
                photoView.setImageURI(Uri.parse(photoUri))
                photoView.visibility = View.VISIBLE
            } catch(e: Exception) {
                println("EntryDetailsFragment setImageURI exception: $e")
            }
        }

        if(target.arVideoPath == null){
            videotitle.visibility = View.GONE
            playbutton.visibility = View.GONE
        }else{
            videotitle.visibility = View.VISIBLE
            playbutton.visibility = View.VISIBLE
            playbutton.setOnClickListener{
                val intent = Intent(activity, AddAREntryActivity::class.java)
                val bundle = Bundle()
                bundle.putString(
                    AddAREntryActivity.DESIRED_APP_STATE_KEY,
                    "PLAYBACK"
                )
                bundle.putString(
                    AddAREntryActivity.DESIRED_DATASET_PATH_KEY,
                    target.arVideoPath
                )
                bundle.putBoolean(
                    "fromAddEntry",
                    true
                )
                intent.putExtras(bundle)

                startActivity(intent)
                println("button Clicked")
            }
        }

        // date
        val dateView: TextView = view.findViewById(R.id.date_detail)
        dateView.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(target.date)

        // location
        val location = target.location
        println("item.location: $location")
        val locationTextView: TextView = view.findViewById(R.id.location_detail)
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        if (location != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            locationTextView.text = "(${location.latitude}, ${location.longitude})"
            geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                println("addresses: $addresses")
                if (addresses.size != 0) {
                    locationTextView.text = addresses[0].getAddressLine(0)
                }
            }
        } else if (location != null) {
            locationTextView.text = "(${location.latitude}, ${location.longitude})"
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses?.size == 1) {
                locationTextView.text = addresses[0].getAddressLine(0)
            }
        }

        // delete button
        val deleteButton: Button = view.findViewById(R.id.delete_btn)
        deleteButton.setOnClickListener {
            // create dialog
            AlertDialog.Builder(requireContext())
                .setTitle("Deleting entry")
                .setMessage("Are you sure you want to delete the diary entry?")
                .setPositiveButton("Yes") { dialog, _ ->
                    diaryEntryDao.deleteById(id)
                    dialog.dismiss()
                    activity?.finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        return view
    }
}