package com.example.project2

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project2.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val wishlistItems = mutableListOf<WishlistItem>()
    private lateinit var adapter: WishlistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupListeners()
        updateUiState()
    }

    private fun setupRecyclerView() {
        adapter = WishlistAdapter(
            items = wishlistItems,
            onItemClick = { item ->
                openUrlInBrowser(item.url)
            },
            onItemLongClick = { item, position ->
                showDeleteConfirmationDialog(item, position)
            }
        )

        binding.rvWishlist.layoutManager = LinearLayoutManager(this)
        binding.rvWishlist.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnAddItem.setOnClickListener {
            addNewWishlistItem()
        }
    }

    private fun addNewWishlistItem() {
        val name = binding.etItemName.text?.toString()?.trim().orEmpty()
        val priceStr = binding.etItemPrice.text?.toString()?.trim().orEmpty()
        val url = binding.etItemUrl.text?.toString()?.trim().orEmpty()

        // Input validation
        var isValid = true

        if (name.isEmpty()) {
            binding.tilItemName.error = getString(R.string.err_empty_name)
            isValid = false
        } else {
            binding.tilItemName.error = null
        }

        if (priceStr.isEmpty()) {
            binding.tilItemPrice.error = getString(R.string.err_empty_price)
            isValid = false
        } else {
            val numericPrice = priceStr.replace("$", "").toDoubleOrNull()
            if (numericPrice == null || numericPrice < 0) {
                binding.tilItemPrice.error = getString(R.string.err_invalid_price)
                isValid = false
            } else {
                binding.tilItemPrice.error = null
            }
        }

        if (!isValid) return

        val newItem = WishlistItem(
            name = name,
            price = priceStr,
            url = url
        )

        adapter.addItem(newItem)

        // Reset input fields
        binding.etItemName.text?.clear()
        binding.etItemPrice.text?.clear()
        binding.etItemUrl.text?.clear()

        // Clear focus and hide soft keyboard
        binding.etItemName.clearFocus()
        binding.etItemPrice.clearFocus()
        binding.etItemUrl.clearFocus()
        hideKeyboard()

        updateUiState()

        Snackbar.make(binding.root, getString(R.string.msg_item_added, name), Snackbar.LENGTH_SHORT).show()
    }

    private fun openUrlInBrowser(rawUrl: String) {
        if (rawUrl.isBlank()) {
            Toast.makeText(this, R.string.msg_invalid_url, Toast.LENGTH_SHORT).show()
            return
        }

        // Format URL: Ensure http:// or https:// protocol is present
        val formattedUrl = if (!rawUrl.startsWith("http://", ignoreCase = true) &&
            !rawUrl.startsWith("https://", ignoreCase = true)
        ) {
            "https://$rawUrl"
        } else {
            rawUrl
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.msg_invalid_url, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(this, R.string.msg_invalid_url, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog(item: WishlistItem, position: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(getString(R.string.dialog_delete_message, item.name))
            .setPositiveButton(getString(R.string.dialog_delete_confirm)) { _, _ ->
                val removedItem = adapter.removeItem(position)
                updateUiState()

                if (removedItem != null) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.msg_item_deleted, removedItem.name),
                        Snackbar.LENGTH_LONG
                    ).setAction(getString(R.string.msg_undo)) {
                        adapter.insertItem(position, removedItem)
                        updateUiState()
                    }.show()
                }
            }
            .setNegativeButton(getString(R.string.dialog_cancel), null)
            .show()
    }

    private fun updateUiState() {
        val count = wishlistItems.size
        binding.llEmptyState.visibility = if (count == 0) View.VISIBLE else View.GONE
        binding.rvWishlist.visibility = if (count == 0) View.GONE else View.VISIBLE

        binding.tvTotalItems.text = getString(R.string.total_items, count)

        var totalCost = 0.0
        for (item in wishlistItems) {
            val priceNum = item.price.replace("$", "").toDoubleOrNull() ?: 0.0
            totalCost += priceNum
        }
        binding.tvTotalCost.text = getString(R.string.total_cost, totalCost)
    }

    private fun hideKeyboard() {
        val currentFocusedView = currentFocus ?: binding.root
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
    }
}
