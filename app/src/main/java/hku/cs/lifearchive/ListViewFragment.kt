package hku.cs.lifearchive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.list_view_recycler_view)
        val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
        //test entry
//        diaryEntryDao.add(DiaryEntry(1,title="Check", content = "Content Test",
//            picturePaths = arrayListOf("1,2,","testpath"), voiceRecording = null,
//            arVideoPath = null, location = Location(), date = Date()
//        ))
        val dateSortBtn = view.findViewById<Button>(R.id.date_sort_btn)
        val titleSortBtn = view.findViewById<Button>(R.id.title_sort_btn)
        val listsearchview = view.findViewById<SearchView>(R.id.list_view_search_view)
        val allEntries= diaryEntryDao.getAll().toMutableList()
        var searchentries = allEntries
        var datebtnpushed = false
        var titlebtnpushed = false
        println("tester")
        println(allEntries)
        recyclerView.adapter = MyItemRecyclerViewAdapter(searchentries,activity)

        listsearchview.queryHint = "Search by Title"
        listsearchview.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                println("text changed")
                if (newText == null){
                    searchentries = allEntries

                }else{
                    searchentries = allEntries.filter { item -> item.title.contains(newText) }.toMutableList()
                }
                recyclerView.adapter = MyItemRecyclerViewAdapter(searchentries,activity)
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                return false
            }
        })

        with(recyclerView) {
            layoutManager = when { columnCount <= 1 -> LinearLayoutManager(context) else -> GridLayoutManager(context, columnCount) }
            adapter = MyItemRecyclerViewAdapter(allEntries,activity)
            dateSortBtn?.setOnClickListener{

                if(datebtnpushed == false){
                    datebtnpushed = true
                    searchentries.sortBy {it.date}
                    println("timesorted")
                    println(searchentries)
                    adapter = MyItemRecyclerViewAdapter(searchentries,activity)

                }else{
                    datebtnpushed = false
                    searchentries.sortByDescending {it.date}
                    println("timesorted by Descending")
                    println(searchentries)
                    adapter = MyItemRecyclerViewAdapter(searchentries,activity)
                }

            }
            titleSortBtn?.setOnClickListener{
                if(titlebtnpushed == false){
                    titlebtnpushed = true
                    searchentries.sortBy {it.title}
                    println("titlesorted")
                    println(searchentries)
                    adapter = MyItemRecyclerViewAdapter(searchentries,activity)
                }else{
                    titlebtnpushed = false
                    searchentries.sortByDescending {it.title}
                    println("titlesorted by Descending")
                    println(searchentries)
                    adapter = MyItemRecyclerViewAdapter(searchentries,activity)
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
            ListViewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}