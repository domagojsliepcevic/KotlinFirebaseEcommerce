package hr.algebra.sverccommercefinal.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.data.User
import hr.algebra.sverccommercefinal.databinding.FragmentRegisterBinding
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.RegisterViewModel

private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.fragment_register) {
    private lateinit var binding:FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                val user = User(
                    etFirstNameRegister.text.toString().trim(),
                    etLastNameRegister.text.toString().trim(),
                    etEmailRegister.text.toString().trim()
                )
                val password = etPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user,password)

            }
        }

        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.buttonRegisterRegister.startAnimation()

                    }
                    is Resource.Success ->{
                        Log.d("test",it.data.toString())
                        binding.buttonRegisterRegister.revertAnimation()

                    }
                    is Resource.Error ->{
                        Log.e(TAG,it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    else ->Unit
                }
            }
        }
    }
}