package hku.cs.lifearchive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hku.cs.lifearchive.diaryentrymodel.DiaryEntry
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase
import hku.cs.lifearchive.diaryentrymodel.Location
import java.util.Date

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get database DAO
        val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
        //test entry
        diaryEntryDao.add(DiaryEntry(1,title="Check", content = "Content Test",
            picturePaths = arrayListOf("1,2,","testpath"), voiceRecording = null,
            arVideoPath = null, location = Location(), date = Date()
        ))
        val allentry= diaryEntryDao.getAll()

        println("tester")
        println(allentry)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                //adapter = MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS)

                adapter = MyItemRecyclerViewAdapter(allentry)


            }

        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}