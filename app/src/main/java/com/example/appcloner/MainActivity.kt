package com.example.appcloner
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CloneAdapter
    private lateinit var btnCreate: Button
    private lateinit var tvCount: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var cloneManager: CloneManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        btnCreate = findViewById(R.id.btnCreate)
        tvCount = findViewById(R.id.tvCount)
        tvEmpty = findViewById(R.id.tvEmpty)
        cloneManager = CloneManager(this)
        adapter = CloneAdapter { clone, action ->
            when (action) {
                CloneAdapter.Action.LAUNCH -> {
                    cloneManager.launchClone(clone.id)
                    Toast.makeText(this, "Launching ${clone.cloneName}", Toast.LENGTH_SHORT).show()
                    refreshList()
                }
                CloneAdapter.Action.DELETE -> {
                    cloneManager.deleteClone(clone.id)
                    refreshList()
                }
                CloneAdapter.Action.SETTINGS -> {
                    val intent = Intent(this, CloneSettingsActivity::class.java)
                    intent.putExtra("clone_id", clone.id)
                    startActivity(intent)
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        btnCreate.setOnClickListener {
            startActivity(Intent(this, AppSelectionActivity::class.java))
        }
        refreshList()
    }
    override fun onResume() { super.onResume(); refreshList() }
    private fun refreshList() {
        val clones = cloneManager.getAllClones()
        adapter.submitList(clones)
        tvCount.text = "Total Clones: ${clones.size}"
        tvEmpty.visibility = if (clones.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }
}
