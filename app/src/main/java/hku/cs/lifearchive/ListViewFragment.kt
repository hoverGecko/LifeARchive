package hku.cs.lifearchive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase

/**
 * A fragment representing a list of Items.
 */
class ListViewFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_list_view, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.list_view_recycler_view)
        val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
        //test entry
//        diaryEntryDao.add(DiaryEntry(1,title="Check", content = "Content Test",
//            picturePaths = arrayListOf("1,2,","testpath"), voiceRecording = null,
//            arVideoPath = null, location = Location(), date = Date()
//        ))
        val dateSortBtn = view.findViewById<Button>(R.id.date_sort_btn)
        val titleSortBtn = view.findViewById<Button>(R.id.title_sort_btn)
        val allEntries= diaryEntryDao.getAll().toMutableList()


        println("tester")
        println(allEntries)

        // Set the adapter
        with(recyclerView) {
            layoutManager = when { columnCount <= 1 -> LinearLayoutManager(context) else -> GridLayoutManager(context, columnCount) }
            adapter = MyItemRecyclerViewAdapter(allEntries,activity)
            dateSortBtn?.setOnClickListener{
                allEntries.sortBy {it.date}
                println("timesorted")
                println(allEntries)
                adapter = MyItemRecyclerViewAdapter(allEntries,activity)
            }
            titleSortBtn?.setOnClickListener{
                allEntries.sortBy {it.title}
                println("titlesorted")
                println(allEntries)
                adapter = MyItemRecyclerViewAdapter(allEntries,activity)
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
            ListViewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}