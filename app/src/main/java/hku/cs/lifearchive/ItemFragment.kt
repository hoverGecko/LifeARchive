package hku.cs.lifearchive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase

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

        return view



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
        val datesorters = view.findViewById<Button>(R.id.datesorter)
        val titlesorters = view.findViewById<Button>(R.id.titlesorter)
        val allentry= diaryEntryDao.getAll().toMutableList()

        println("tester")
        println(allentry)
        // Set the adapter


        val lister = view.findViewById<RecyclerView>(R.id.list)
        if (lister is RecyclerView) {
            println("listerer found")
            with(lister) { layoutManager = when {columnCount <= 1 -> LinearLayoutManager(context) else -> GridLayoutManager(context, columnCount) }
                //adapter = MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS)
                adapter = MyItemRecyclerViewAdapter(allentry,activity)

                if (datesorters != null) {
                    datesorters.setOnClickListener{
                        allentry.sortBy {it.date}
                        println("timesorted")
                        println(allentry)
                        adapter = MyItemRecyclerViewAdapter(allentry,activity)
                    }
                }
                if (titlesorters != null) {
                    titlesorters.setOnClickListener{
                        allentry.sortBy {it.title}
                        println("titlesorted")
                        println(allentry)
                        adapter = MyItemRecyclerViewAdapter(allentry,activity)
                    }
                }
            }

        }

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