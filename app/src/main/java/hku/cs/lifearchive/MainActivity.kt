package hku.cs.lifearchive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomBar: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init bottom app bar
        loadFragment(ItemFragment())
        bottomBar = findViewById(R.id.BottomBar)
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


    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .commit()
    }
}