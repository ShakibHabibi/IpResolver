package com.shkbhbb.ping

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shkbhbb.ping.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException


class MainActivity : AppCompatActivity() {
    private val urls = mutableListOf(
        "google.com",
        "facebook.com",
        "youtube.com",
        "this-website-does-not-exist.com"
    )
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, urls)
        binding.urlsLv.adapter = adapter

        binding.urlsLv.setOnItemClickListener { _, _, position, _ -> onItemClicked(position) }
    }

    private fun onItemClicked(position: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            val selectedItem = urls[position]
            try {
                val inetAddress: InetAddress
                withContext(Dispatchers.IO) { inetAddress = InetAddress.getByName(selectedItem) }
                showIpDialog(inetAddress)
            } catch (e: UnknownHostException) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.host_is_unknown), Toast.LENGTH_LONG
                ).show()
            }
            moveToTop(position)
        }
    }

    private fun showIpDialog(inetAddress: InetAddress) {
        val message = inetAddress.hostAddress?.let {
            """
            ${getString(R.string.domain)}: ${inetAddress.hostName}
            ${getString(R.string.ip)}: $it
        """.trimIndent()
        } ?: getString(R.string.ip_not_found)

        AlertDialog.Builder(this@MainActivity)
            .setTitle(R.string.domain_ip)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun moveToTop(position: Int) {
        urls.add(0, urls[position])
        urls.removeAt(position + 1)
        adapter.notifyDataSetChanged()
    }
}