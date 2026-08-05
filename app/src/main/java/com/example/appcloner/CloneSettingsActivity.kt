package com.example.appcloner
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CloneSettingsActivity : AppCompatActivity() {
    private lateinit var cloneManager: CloneManager
    private lateinit var clone: Clone
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clone_settings)
        val cloneId = intent.getIntExtra("clone_id", -1)
        cloneManager = CloneManager(this)
        clone = cloneManager.getClone(cloneId) ?: run { finish(); return }
        findViewById<TextView>(R.id.tvCloneName).text = clone.cloneName
        findViewById<TextView>(R.id.tvAppName).text = "App: ${clone.appName}"
        findViewById<TextView>(R.id.tvImei).text = "IMEI: ${clone.deviceProfile.imei}"
        findViewById<TextView>(R.id.tvAndroidId).text = "Android ID: ${clone.deviceProfile.androidId}"
        findViewById<TextView>(R.id.tvMac).text = "MAC: ${clone.deviceProfile.macAddress}"
        findViewById<TextView>(R.id.tvModel).text = "Model: ${clone.deviceProfile.model}"
        findViewById<TextView>(R.id.tvManufacturer).text = "Manufacturer: ${clone.deviceProfile.manufacturer}"
        findViewById<TextView>(R.id.tvSerial).text = "Serial: ${clone.deviceProfile.serialNumber}"
        findViewById<TextView>(R.id.tvStatus).text = "Status: ${clone.status}"
        findViewById<Button>(R.id.btnRegenerate).setOnClickListener {
            val newProfile = DeviceProfile(
                imei = cloneManager.generateIMEI(),
                androidId = java.util.UUID.randomUUID().toString().replace("-", "").take(16),
                macAddress = cloneManager.generateMAC(),
                model = clone.deviceProfile.model,
                manufacturer = clone.deviceProfile.manufacturer,
                serialNumber = cloneManager.generateSerial(),
                androidVersion = clone.deviceProfile.androidVersion
            )
            Toast.makeText(this, "Device ID regenerated!", Toast.LENGTH_SHORT).show()
            finish()
        }
        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Delete Clone")
                .setMessage("Delete ${clone.cloneName}?")
                .setPositiveButton("Delete") { _, _ ->
                    cloneManager.deleteClone(clone.id)
                    Toast.makeText(this, "Clone deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}
