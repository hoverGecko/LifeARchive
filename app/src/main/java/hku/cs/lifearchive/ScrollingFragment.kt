package hku.cs.lifearchive

import android.R.attr.defaultValue
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val videotitle: TextView= view.findViewById(R.id.textView11)
        val playbutton: Button = view.findViewById(R.id.Playvideo)
        //get value from bundle
        val bundle = this.arguments
        if (bundle != null) {
            val id = bundle.getInt("id", defaultValue)
            val diaryEntryDao = DiaryEntryDatabase.getDatabase(requireContext()).dao()
            val target = diaryEntryDao.getById(id)
            titleGet.text = target.title
            contentGet.text = target.content

            if(target.arVideoPath == null){
                videotitle.visibility = View.GONE
                playbutton.visibility = View.GONE
            }else{
                videotitle.visibility = View.VISIBLE
                playbutton.visibility = View.VISIBLE
                playbutton.setOnClickListener{
                    val intent = Intent(activity, HelloRecordingPlaybackActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString(
                        HelloRecordingPlaybackActivity.DESIRED_APP_STATE_KEY,
                        "PLAYBACK"
                    )
                    bundle.putString(
                        HelloRecordingPlaybackActivity.DESIRED_DATASET_PATH_KEY,
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

        }
        return view
    }
}