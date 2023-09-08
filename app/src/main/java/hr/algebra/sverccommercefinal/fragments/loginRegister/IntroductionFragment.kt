package hr.algebra.sverccommercefinal.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.databinding.FragmentIntroductionBinding

class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding // View binding for this fragment.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflates the layout for this fragment and initializes view binding.
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up a click listener for the "Start" button.
        binding.buttonStart.setOnClickListener {
            // Navigate to the account options fragment when the button is clicked.
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
    }
}
