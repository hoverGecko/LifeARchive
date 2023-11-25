package hku.cs.lifearchive

import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var bottomBar: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init bottom app bar
        loadFragment(ItemFragment())

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
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.add_entry_button_popup_menu, popup.menu)
            popup.setOnMenuItemClickListener {item ->
                when (item.itemId) {
                    R.id.add_normal_entry -> {
                        loadFragment(AddEntryFragment())
                        true
                    }
                    R.id.add_recording_entry -> {
                        Toast.makeText(this, "Add recording entry - To be implemented", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.add_ar_entry -> {
                        Toast.makeText(this, "Add AR entry - To be implemented", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }
            popup.show()
        }


    }



    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .commit()
    }
}