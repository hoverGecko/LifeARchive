package hku.cs.lifearchive

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomBar: BottomNavigationView
    private lateinit var fab: FloatingActionButton
    private lateinit var fabmic: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init bottom app bar
        loadFragment(ItemFragment())

        bottomBar = findViewById(R.id.BottomBar)

        fab = findViewById(R.id.fab)
        fabmic = findViewById(R.id.fabmic)

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

        fab.setOnClickListener {

        }
        fabmic.setOnClickListener {
            val intent = Intent(this, VoiceRecordingActivity::class.java)

            startActivity(intent)
        }


    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .commit()
    }
}