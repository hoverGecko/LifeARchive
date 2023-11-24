package hku.cs.lifearchive

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

        fab.setOnClickListener {
            loadFragment(AddEntryFragment())
        }


    }
    fun AddandBack(v: View?){

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .commit()
    }
}