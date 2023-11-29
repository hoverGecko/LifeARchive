package hku.cs.lifearchive

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import hku.cs.lifearchive.diaryentrymodel.DiaryEntry
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// https://developer.android.com/develop/ui/views/components/pickers#DatePicker
class TimePickerFragment(
    private val calendar: Calendar,
    private val inputText: TextInputEditText,
    private val format: SimpleDateFormat
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker.
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it.
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time the user picks.
        println("hourOfDay: $hourOfDay")
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        inputText.setText(format.format(calendar.time))
    }
}

class DatePickerFragment(
    private val calendar: Calendar,
    private val inputText: TextInputEditText,
    private val format: SimpleDateFormat
) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker.
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it.
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date the user picks.
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        inputText.setText(format.format(calendar.time))
    }
}

class AddTextEntryFragment : Fragment() {

    val rc = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        fun newInstance() = AddTextEntryFragment()

        fun newInstance(arVideoPath: String?=null,title:String?=null): AddTextEntryFragment {
            val f = AddTextEntryFragment ()
            val args = Bundle()
            args.putString("arVideoPath", arVideoPath)
            args.putString("title", title)
            f.setArguments(args)
            return f
        }
    }




    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return inflater.inflate(R.layout.fragment_add_text_entry, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
        val doneButton= view.findViewById<FloatingActionButton>(R.id.Add)
        val ARbutton= view.findViewById<Button>(R.id.viewARbtn)
        val title= view.findViewById<TextInputLayout>(R.id.TitleInput)
        title.requestFocus()
//        val nowdate = view.findViewById<TextView>(R.id.Dateview)
//        val nowlong = view.findViewById<TextView>(R.id.LongView)
//        val nowlata = view.findViewById<TextView>(R.id.LatView)
        val ARPath = view.findViewById<TextView>(R.id.ARpath)
        val content = view.findViewById<TextInputLayout>(R.id.ContentInput)
//        val dates = Date()
        var nowlocation  = hku.cs.lifearchive.diaryentrymodel.Location()
//        nowdate.text = dates.toString()

        // handling date and time input
        val calendar = Calendar.getInstance()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        val dateInputText = view.findViewById<TextInputEditText>(R.id.date_input_text)
        dateInputText.setText(dateFormat.format(calendar.time))
        val timeInputText = view.findViewById<TextInputEditText>(R.id.time_input_text)
        timeInputText.setText(timeFormat.format(calendar.time))

        dateInputText.setOnClickListener {
            DatePickerFragment(calendar, dateInputText, dateFormat)
                .show(requireActivity().supportFragmentManager, "datePicker")
        }
        timeInputText.setOnClickListener {
            TimePickerFragment(calendar, timeInputText, timeFormat)
                .show(requireActivity().supportFragmentManager, "timePicker")
        }

        // handling photo
        val addPhotoButton = view.findViewById<Button>(R.id.add_photo_btn)
        val removePhotoButton = view.findViewById<Button>(R.id.remove_photo_btn)
        val photoView = view.findViewById<ImageView>(R.id.photo_view)
        var imageUri: Uri? = null
        // initialize photo picker
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                photoView.setImageURI(uri)
                imageUri = uri
                photoView.visibility = View.VISIBLE
                addPhotoButton.visibility = View.GONE
                removePhotoButton.visibility = View.VISIBLE
            }
        }
        addPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        removePhotoButton.setOnClickListener {
            imageUri = null
            photoView.visibility = View.GONE
            addPhotoButton.visibility = View.VISIBLE
            removePhotoButton.visibility = View.GONE
        }

        //getting permission
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // calling for permission
            //TODO: find ways to call for permission at start of main activty, or close app if unable
            ActivityCompat.requestPermissions(requireActivity(),arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), rc)
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->

                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    println("success")
//                    nowlong.text = location.latitude.toString()
//                    nowlata.text = location.longitude.toString()
                    nowlocation.latitude = location.latitude
                    nowlocation.longitude = location.longitude
                    println(location.latitude)
                    println(location.longitude)

                }else
                {
                    println("null location")
                }
            }

        // handling location input to output actual location name

        val locationInputText = view.findViewById<TextInputEditText>(R.id.location_input_text)
        locationInputText.setText("(${nowlocation.latitude}, ${nowlocation.longitude})")
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(nowlocation.latitude, nowlocation.longitude, 1
            ) { addresses ->
                println("addresses: $addresses")
                if (addresses.size != 0) {
                    locationInputText.setText(addresses[0].getAddressLine(0).trim().dropLast(1))
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(nowlocation.latitude, nowlocation.longitude, 1)
            if (addresses?.size == 1) {
                locationInputText.setText(addresses[0].getAddressLine(0).trim().dropLast(1))
            }
        }

        // nullable
        var arVideoPath = arguments?.getString("arVideoPath");
        var voiceRecordedTitle = arguments?.getString("title");


        doneButton?.setOnClickListener {
            println(title.editText?.text)
            var titleText = title.editText?.text.toString()
            if (imageUri != null) {
                requireContext().contentResolver.takePersistableUriPermission(imageUri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (titleText.isBlank()) {
                titleText = "Untitled entry"
            }

            diaryEntryDao.add(
                DiaryEntry(title=titleText, content = content.editText?.text.toString(),
                    picturePaths = arrayListOf(imageUri?.toString()).filterNotNull(), voiceRecording = null,
                arVideoPath = arVideoPath, location = nowlocation, date = calendar.time
                )
            )
            println("button Clicked")

            activity?.finish()
        }

        // from ar screen: video is recorded, show replay btn

        if(arVideoPath!=null){
            ARPath.text = arVideoPath
            ARbutton.visibility =View.VISIBLE

            ARbutton?.setOnClickListener {
                val intent = Intent(activity, AddAREntryActivity::class.java)
                val bundle = Bundle()
                bundle.putString(
                    AddAREntryActivity.DESIRED_APP_STATE_KEY,
                    "PLAYBACK"
                )
                bundle.putString(
                    AddAREntryActivity.DESIRED_DATASET_PATH_KEY,
                    requireArguments().getString("arVideoPath")
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
        // set title from voice recording screen
        if(voiceRecordedTitle!=null){
            title.editText?.setText(voiceRecordedTitle)
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
//    private fun loadFragment(fragment: Fragment){
//        val transaction = activity?.supportFragmentManager?.beginTransaction()
//        transaction?.replace(this.id, fragment)
//        transaction?.commit()
//    }
}