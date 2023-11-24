package hku.cs.lifearchive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase

class AddEntryFragment : Fragment() {

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
        val title= view.findViewById<Button>(R.id.TitleInput)

        button?.setOnClickListener {
            title.editableText

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