package br.edu.ufabc.listacontatosresponsiva

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import br.edu.ufabc.listacontatosresponsiva.databinding.ActivityMainBinding
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import com.google.android.material.snackbar.Snackbar
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {
    companion object {
        private var contactsFile = "contacts.json"
    }
    private lateinit var contacts: List<Contact>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadData()
        doLayout()
    }

    private fun loadData() {
        try {
            resources.assets.open(contactsFile).use {
                contacts = Klaxon().parseArray(it) ?: emptyList()
            }
        } catch (e: FileNotFoundException) {
            Log.e("APP", "Failed to open dataset file", e)
            Snackbar.make(binding.root.rootView,
                "Failed to access contacts data", Snackbar.LENGTH_INDEFINITE).show()
        } catch (e: KlaxonException) {
            Log.e("APP", "Failed to parse dataset file", e)
            Snackbar.make(binding.root.rootView,
                "Failed to read contacts data", Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    private fun isTablet() = resources.configuration.smallestScreenWidthDp >= 600

    private fun doLayout() {
        try {
            if (isTablet()) doTwoPaneLayout() else doOnePaneLayout()
        } catch (e: IllegalStateException) {
            Log.e("APP", e.message, e)
            Snackbar.make(binding.root.rootView, "Failed to build app layout",
                Snackbar.LENGTH_INDEFINITE).show()
        }

    }

    private fun doOnePaneLayout() {
        val mainContainer = binding.mainFragmentContainer
            ?: throw IllegalStateException("The main fragment container is null")

        supportFragmentManager.commit {
            replace(mainContainer.id, ContactListFragment().also { it.contacts = contacts })
        }
        supportFragmentManager.setFragmentResultListener(ContactListFragment.itemClickedKey,
            this) { _, bundle ->
            val position = bundle.getInt(ContactListFragment.itemClickedPosition)

            supportFragmentManager.commit {
                replace(mainContainer.id, ContactItemFragment().also { it.contactPosition = position })
                addToBackStack(null)
            }
        }
    }

    private fun doTwoPaneLayout() {
        val listContainer = binding.contactListFragmentContainer
            ?: throw IllegalStateException("The list fragment container is null")
        val itemContainer = binding.contactItemFragmentContainer
            ?: throw IllegalStateException("The item fragment container is null")

        supportFragmentManager.commit {
            replace(listContainer.id, ContactListFragment().also { it.contacts = contacts })
            replace(itemContainer.id, ContactItemFragment().also { it.contactPosition = 0 })
        }
        supportFragmentManager.setFragmentResultListener(ContactListFragment.itemClickedKey,
            this) { _, bundle ->
            val position = bundle.getInt(ContactListFragment.itemClickedPosition)

            supportFragmentManager.commit {
                replace(itemContainer.id, ContactItemFragment().also { it.contactPosition = position })
            }
        }
    }

    fun getContactByPosition(position: Int): Contact = try {
        contacts[position]
    } catch (e: IndexOutOfBoundsException) {
        Log.e("APP", "No contact at position $position")
        Contact("", "", "", "")
    }

}