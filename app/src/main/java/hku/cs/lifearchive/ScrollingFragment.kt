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
        val view = inflater.inflate(R.layout.fragment_scrolling, container, false)

        val titleGet : TextView = view.findViewById(R.id.Titledetail)
        val contentGet: TextView = view.findViewById(R.id.Contentdetail)
        //get value from bundle
        val bundle = this.arguments
        if (bundle != null) {
            val id = bundle.getInt("id", defaultValue)
            val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
            val target = diaryEntryDao.getById(id)
            titleGet.text = target.title
            contentGet.text = target.content
        }
        return view
    }
}