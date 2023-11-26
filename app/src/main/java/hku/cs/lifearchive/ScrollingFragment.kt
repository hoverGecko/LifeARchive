package hku.cs.lifearchive

import android.R.attr.defaultValue
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase


class ScrollingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
        val view = inflater.inflate(R.layout.fragment_scrolling, container, false)
        var id = 0
        val idGet : TextView = view.findViewById(R.id.idview)
        //get value from bundle
        val bundle = this.arguments
        if (bundle != null) {
            id = bundle.getInt("id", defaultValue)
        }
        return view
    }
}