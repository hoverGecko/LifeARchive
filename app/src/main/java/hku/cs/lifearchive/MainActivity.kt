package hku.cs.lifearchive

import android.content.Intent
import android.os.Bundle
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
        loadFragment(ListViewFragment())
        //init bottom app bar
        bottomBar = findViewById(R.id.BottomBar)

        fab = findViewById(R.id.fab)


        bottomBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_list -> {
                    loadFragment(ListViewFragment())
                    true
                }

                R.id.menu_map -> {
                    loadFragment(MapViewFragment())
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
                        val intent = Intent(this, AddTextEntryActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    R.id.add_recording_entry -> {
                        val intent = Intent(this, AddVoiceRecordingEntryActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    R.id.add_ar_entry -> {
                        val intent = Intent(this, AddAREntryActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    else -> super.onOptionsItemSelected(item)
                }
            }
            popup.show()
        }
    }

    // refresh
    override fun onResume() {
        super.onResume()
        bottomBar = findViewById(R.id.BottomBar)
        when (bottomBar.selectedItemId) {
            R.id.menu_list -> {
                loadFragment(ListViewFragment())
            }
            R.id.menu_map -> {
                loadFragment(MapViewFragment())
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .commit()
    }
}