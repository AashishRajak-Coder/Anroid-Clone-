package com.example.appcloner
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AppSelectionActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: Button
    private lateinit var tvTitle: TextView
    private lateinit var cloneManager: CloneManager
    private lateinit var adapter: AppSelectionAdapter
    private lateinit var searchView: EditText
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_selection)
        recyclerView = findViewById(R.id.recyclerView)
        btnBack = findViewById(R.id.btnBack)
        tvTitle = findViewById(R.id.tvTitle)
        searchView = findViewById(R.id.searchView)
        cloneManager = CloneManager(this)
        val apps = cloneManager.getInstalledApps()
        adapter = AppSelectionAdapter { app -> showCreateCloneDialog(app) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.submitList(apps.filter { it.appName.contains(s.toString(), ignoreCase = true) })
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        adapter.submitList(apps)
        btnBack.setOnClickListener { finish() }
        tvTitle.text = "Select App to Clone"
    }
    
    private fun showCreateCloneDialog(app: AppInfo) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_clone, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.cloneNameInput)
        nameInput.setText("${app.appName} Clone")
        AlertDialog.Builder(this)
            .setTitle("Create Clone")
            .setMessage("Create clone of ${app.appName}?")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val cloneName = nameInput.text.toString().ifEmpty { "${app.appName} Clone" }
                cloneManager.createClone(app.packageName, app.appName, cloneName)
                Toast.makeText(this, "Clone created!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

class AppSelectionAdapter(private val onAppClick: (AppInfo) -> Unit) : RecyclerView.Adapter<AppSelectionAdapter.ViewHolder>() {
    private var apps = listOf<AppInfo>()
    fun submitList(list: List<AppInfo>) { apps = list; notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.bind(app)
        holder.itemView.setOnClickListener { onAppClick(app) }
    }
    override fun getItemCount() = apps.size
    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvAppName)
        val tvPackage: TextView = view.findViewById(R.id.tvPackageName)
        val ivIcon: android.widget.ImageView = view.findViewById(R.id.ivIcon)
        fun bind(app: AppInfo) {
            tvName.text = app.appName
            tvPackage.text = app.packageName
            ivIcon.setImageDrawable(app.icon)
        }
    }
}
