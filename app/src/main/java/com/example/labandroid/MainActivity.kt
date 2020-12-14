package com.example.labandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.work.*
import com.example.labandroid.auth.remote.AuthProxy
import com.example.labandroid.items.data.ItemWorker
import com.example.labandroid.utils.ConnectivityLiveData
import com.example.labandroid.utils.TAG

class MainActivity : AppCompatActivity() {

    private lateinit var connectivityLiveData: ConnectivityLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        connectivityLiveData = ConnectivityLiveData(getSystemService(android.net.ConnectivityManager::class.java))

        connectivityLiveData.observe(this, { connected ->
            if (connected) {
                Toast.makeText(this, "Connected to Network!", Toast.LENGTH_SHORT).show()
                startSyncJob()
            } else {
                Toast.makeText(this, "Unable to connect to Network!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startSyncJob() {

        Log.d(TAG, "Started background task")
//        WorkManager.getInstance(applicationContext).enqueue(OneTimeWorkRequest.from(ItemWorker::class.java))
//        val worker = ItemWorker(this, W)
//            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
//        val inputData = Data.Builder()
//            .putString("example_key", "example_value")
            .build()
//        val myWork = PeriodicWorkRequestBuilder<ExampleWorker>(1, TimeUnit.MINUTES)
        val myWork = OneTimeWorkRequest.Builder(ItemWorker::class.java)
            .setConstraints(constraints)
//            .setInputData(inputData)
            .build()
        val workId = myWork.id
        WorkManager.getInstance(this).apply {
            // enqueue Work
            enqueue(myWork)
            // observe work status
            getWorkInfoByIdLiveData(workId)
                .observe(this@MainActivity, { status ->
                    val isFinished = status?.state?.isFinished
                    Log.d(TAG, "Job $workId; finished: $isFinished")
                })
        }
        Toast.makeText(this, "Synchronizing data", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
//        menu?.findItem(R.id.logout_button)?.isEnabled = AuthProxy.isLoggedIn
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.logout_button -> {
                Log.d(TAG, "Logout Button pressed")
                AuthProxy.logout()
                val sharedPrefs = getSharedPreferences(getString(R.string.shared_prefs_file), 0)
                if (sharedPrefs != null) {
                    if (sharedPrefs.contains("token")) {
                        sharedPrefs.edit().remove("token").apply()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.LoginFragment)
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}