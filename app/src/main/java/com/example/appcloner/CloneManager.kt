package com.example.appcloner
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import java.io.File
import java.util.UUID

class CloneManager(private val context: Context) {
    private val sharedPrefs = context.getSharedPreferences("cloner", Context.MODE_PRIVATE)
    
    fun getInstalledApps(): List<AppInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        return packages.mapNotNull { pkg ->
            if (pkg.packageName.startsWith("com.android") || 
                pkg.packageName.startsWith("android") ||
                pkg.packageName.startsWith("com.google.android") ||
                pkg.packageName == context.packageName) null
            else AppInfo(
                packageName = pkg.packageName,
                appName = pkg.applicationInfo.loadLabel(pm).toString(),
                icon = pkg.applicationInfo.loadIcon(pm),
                versionName = pkg.versionName ?: "Unknown",
                isSystemApp = (pkg.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            )
        }.filter { !it.isSystemApp }
    }
    
    fun createClone(appPackage: String, appName: String, cloneName: String): Clone {
        val cloneId = getNextCloneId()
        val profile = generateDeviceProfile()
        File(context.filesDir, "clones/$cloneId").mkdirs()
        val clone = Clone(
            id = cloneId,
            appPackage = appPackage,
            appName = appName,
            cloneName = cloneName,
            deviceProfile = profile,
            status = "INSTALLED",
            createdAt = System.currentTimeMillis()
        )
        saveClone(clone)
        return clone
    }
    
    fun launchClone(cloneId: Int): Boolean {
        val clone = getClone(cloneId) ?: return false
        clone.status = "RUNNING"
        saveClone(clone)
        return true
    }
    
    fun deleteClone(cloneId: Int): Boolean {
        val clones = getAllClones().toMutableList()
        clones.removeAll { it.id == cloneId }
        saveClones(clones)
        File(context.filesDir, "clones/$cloneId").deleteRecursively()
        return true
    }
    
    fun getAllClones(): List<Clone> {
        val json = sharedPrefs.getString("clones", "[]") ?: "[]"
        return try {
            com.google.gson.Gson().fromJson(json, Array<Clone>::class.java).toList()
        } catch (e: Exception) { emptyList() }
    }
    
    fun getClone(id: Int): Clone? = getAllClones().firstOrNull { it.id == id }
    private fun getNextCloneId(): Int = getAllClones().maxByOrNull { it.id }?.id?.plus(1) ?: 1
    private fun saveClone(clone: Clone) {
        val clones = getAllClones().toMutableList()
        val index = clones.indexOfFirst { it.id == clone.id }
        if (index >= 0) clones[index] = clone else clones.add(clone)
        saveClones(clones)
    }
    private fun saveClones(clones: List<Clone>) {
        sharedPrefs.edit().putString("clones", com.google.gson.Gson().toJson(clones)).apply()
    }
    
    fun generateDeviceProfile(): DeviceProfile {
        val devices = listOf(
            Pair("Samsung S23 Ultra", "Samsung"), Pair("Google Pixel 7 Pro", "Google"),
            Pair("OnePlus 11", "OnePlus"), Pair("Xiaomi 13", "Xiaomi"),
            Pair("iPhone 14 Pro", "Apple"), Pair("Samsung Galaxy Z Fold 4", "Samsung"),
            Pair("Google Pixel 6", "Google"), Pair("OnePlus 10 Pro", "OnePlus"),
            Pair("Xiaomi 12 Pro", "Xiaomi"), Pair("Samsung A53", "Samsung"),
            Pair("Nothing Phone 1", "Nothing"), Pair("Motorola Edge 40", "Motorola")
        )
        val device = devices.random()
        return DeviceProfile(
            imei = generateIMEI(),
            androidId = UUID.randomUUID().toString().replace("-", "").take(16),
            macAddress = generateMAC(),
            model = device.first,
            manufacturer = device.second,
            serialNumber = generateSerial(),
            androidVersion = (26..34).random()
        )
    }
    fun generateIMEI(): String = "35" + (1..13).map { (0..9).random() }.joinToString("")
    fun generateMAC(): String = (1..6).joinToString(":") { listOf("0123456789ABCDEF").random().toString() + listOf("0123456789ABCDEF").random() }
    fun generateSerial(): String = (1..12).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".random() }.joinToString("")
}

data class AppInfo(val packageName: String, val appName: String, val icon: Drawable?, val versionName: String, val isSystemApp: Boolean)
data class Clone(val id: Int, val appPackage: String, val appName: String, val cloneName: String, val deviceProfile: DeviceProfile, var status: String, val createdAt: Long)
data class DeviceProfile(val imei: String, val androidId: String, val macAddress: String, val model: String, val manufacturer: String, val serialNumber: String, val androidVersion: Int)
