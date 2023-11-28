package hku.cs.lifearchive

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuInflater
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomBar: BottomNavigationView
    private lateinit var fab: FloatingActionButton





    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(ItemFragment())
        //init bottom app bar
        bottomBar = findViewById(R.id.BottomBar)

        fab = findViewById(R.id.fab)


        bottomBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_list -> {
                    loadFragment(ItemFragment())
                    true
                }

                R.id.menu_map -> {
                    loadFragment(MapsFragment())
                    true
                }

                else -> false
            }
        }

        val layout = findViewById<ConstraintLayout>(R.id.activity_main)
        registerForContextMenu(layout)

        // add entry menu button
        registerForContextMenu(fab)

        fab.setOnClickListener {
            val popup = PopupMenu(this, it)
            popup.setForceShowIcon(true)
            popup.gravity = Gravity.END
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.add_entry_button_popup_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.add_normal_entry -> {
                        loadFragment(AddEntryFragment())
                        true
                    }

                    R.id.add_recording_entry -> {
                        val intent = Intent(this, VoiceRecordingActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    R.id.add_ar_entry -> {
                        val intent = Intent(this, HelloRecordingPlaybackActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    else -> super.onOptionsItemSelected(item)
                }
            }
            popup.show()
        }

        // by Henry: to receive the AR video path just recorded and send to fragment
        if(intent.hasExtra("arVideoPath")){
            var bundle :Bundle ?=intent.extras
            var arVideoPath = bundle!!.getString("arVideoPath","") // 1
            Log.i("SS",arVideoPath!!);
            loadFragment(  AddEntryFragment.newInstance(arVideoPath))

        }else if(intent.hasExtra("title")){
            var bundle :Bundle ?=intent.extras
            var title = bundle!!.getString("title","") // 1
            loadFragment(  AddEntryFragment.newInstance("",title))
        }

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .commit()
    }
}