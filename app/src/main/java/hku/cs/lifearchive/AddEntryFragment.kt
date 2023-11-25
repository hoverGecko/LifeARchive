package hku.cs.lifearchive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.textfield.TextInputLayout
import hku.cs.lifearchive.diaryentrymodel.DiaryEntry
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase
import java.util.Date

class AddEntryFragment : Fragment() {



    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        fun newInstance() = AddEntryFragment()
    }




    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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





        button?.setOnClickListener {
            println(title.editText?.text)

            diaryEntryDao.insertEntry(
                DiaryEntry(title=title.editText?.text.toString(), content = content.editText?.text.toString(),
                picturePaths = arrayListOf("1,2,","testpath"), voiceRecording = null,
                arVideoPath = null, longitude = null, latitude = null, date = dates
            )
            )
            println("button Clicked")
            loadFragment(ItemFragment())
        }

    }

    private fun loadFragment(fragment: Fragment){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(this.id, fragment)
        transaction?.commit()
    }
}