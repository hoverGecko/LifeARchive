package hku.cs.lifearchive

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuInflater
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomBar: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(ListViewFragment())
        //init bottom app bar
        //request permission


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