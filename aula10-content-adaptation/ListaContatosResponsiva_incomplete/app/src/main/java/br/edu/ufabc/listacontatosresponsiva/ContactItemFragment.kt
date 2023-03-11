package br.edu.ufabc.listacontatosresponsiva

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.edu.ufabc.listacontatosresponsiva.databinding.FragmentContactItemBinding


class ContactItemFragment : Fragment() {
    private lateinit var binding: FragmentContactItemBinding
    var contactPosition = -1

    companion object {
        const val contactPositionKey = "contactPosition"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactItemBinding.inflate(inflater, container, false)
        contactPosition = if (contactPosition < 0)
                savedInstanceState?.getInt(contactPositionKey) ?: 0 else contactPosition
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).getContactByPosition(contactPosition).let { contact: Contact ->
            binding.contactListItemFullname.text = contact.name
            binding.contactListItemPhoneValue.text = contact.phone
            binding.contactListItemEmailValue.text = contact.email
            binding.contactListItemAddressValue.text = contact.address
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(contactPositionKey, contactPosition)
    }
}
