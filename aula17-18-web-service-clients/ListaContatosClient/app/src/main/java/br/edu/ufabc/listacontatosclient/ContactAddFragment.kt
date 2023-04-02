package br.edu.ufabc.listacontatosclient

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import br.edu.ufabc.listacontatosclient.databinding.FragmentContactAddBinding
import br.edu.ufabc.listacontatosclient.model.Contact
import com.google.android.material.snackbar.Snackbar

class ContactAddFragment : Fragment() {
    private lateinit var binding: FragmentContactAddBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_add, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_save -> {
                        add()
                    }
                }
                return true
            }

        }
        activity?.addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun add() {
        val contact = Contact(
            id = 0,
            name = binding.edittextName.text?.trim().toString(),
            email = binding.edittextEmail.text?.trim().toString(),
            address = binding.edittextAddress.text?.trim().toString(),
            phone = binding.edittextPhone.text?.trim().toString(),
        )
        viewModel.isLoading.value = true
        viewModel.add(contact).observe(viewLifecycleOwner) { result ->
            when (result.status) {
                is MainViewModel.Status.Success -> {
                    ContactAddFragmentDirections.onAddSuccess(result.result ?: 0).let {
                        findNavController().navigate(it, navOptions {
                            popUpTo(R.id.destination_contact_list)
                        })
                    }
                }
                is MainViewModel.Status.Error -> {
                    Log.e("FRAGMENT", "Failed to add item", result.status.e)
                    Snackbar.make(binding.root, "Failed to add item", Snackbar.LENGTH_LONG).show()
                }
            }
        }
        viewModel.isLoading.value = false
    }
}