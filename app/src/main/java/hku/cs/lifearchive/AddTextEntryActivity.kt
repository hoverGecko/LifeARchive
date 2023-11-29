package hku.cs.lifearchive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar

class AddTextEntryActivity : AppCompatActivity() {
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_text_entry)

        // toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // by Henry: to receive the AR video path just recorded and send to fragment
        if(intent.hasExtra("arVideoPath")){
            var bundle :Bundle ?= intent.extras
            var arVideoPath = bundle!!.getString("arVideoPath","") // 1
            Log.i("SS",arVideoPath!!);
            loadFragment(AddTextEntryFragment.newInstance(arVideoPath))
        } else if(intent.hasExtra("title")){
            var bundle :Bundle ?= intent.extras
            var title = bundle!!.getString("title","") // 1
            loadFragment(AddTextEntryFragment.newInstance("",title))
        }
    }
}