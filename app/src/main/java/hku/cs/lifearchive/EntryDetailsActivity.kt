package hku.cs.lifearchive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import hku.cs.lifearchive.diaryentrymodel.DiaryEntryDatabase

class EntryDetailsActivity : AppCompatActivity() {
    val dao = DiaryEntryDatabase.getDatabase(this@EntryDetailsActivity).dao()
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_text_entry)

//         toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = intent.getStringExtra("title") ?: "Entry details"

        val id = intent.getIntExtra("id", -1)
        if (id == -1) {
            throw Error("EntryDetailsActivity must be called with intent containing id.")
        }
        val bundle = Bundle()
        bundle.putInt("id", id)

        val Scroller = EntryDetailsFragment()
        Scroller.arguments=bundle

        loadFragment(Scroller)
    }
}