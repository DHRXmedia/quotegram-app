package com.nesib.quotegram.ui.on_boarding.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.nesib.quotegram.R
import com.nesib.quotegram.base.BaseFragment
import com.nesib.quotegram.databinding.FragmentAuthorizationBinding
import com.nesib.quotegram.ui.main.MainActivity
import com.nesib.quotegram.ui.on_boarding.StartActivity
import com.nesib.quotegram.ui.viewmodels.AuthViewModel
import com.nesib.quotegram.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentAuthorizationBinding>(R.layout.fragment_authorization) {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var googleSignInActivityLauncher: ActivityResultLauncher<Intent>

    private val authViewModel: AuthViewModel by viewModels({ requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerActivityResult()
        subscribeObserver()
        setupClickListeners()
        (requireActivity() as StartActivity).showToolbar()
    }

    private fun registerActivityResult() {
        googleSignInActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    if (task.isSuccessful) {
                        val account = task.getResult(ApiException::class.java)
                        signInWithGoogle(account)
                    }
                } catch (e: Exception) {
                    signInWithGoogleFailed()
                }
            }
    }

    private fun signInWithGoogle(account: GoogleSignInAccount?) {
        val username = account?.email?.split("@")?.get(0)
        val fullName = account?.displayName
        val email = account?.email
        val profileImage = account?.photoUrl?.toString()
        if (email != null && username != null) {
            googleSignInClient.signOut()
            authViewModel.signupWithGoogle(
                email,
                fullName ?: "",
                username,
                profileImage ?: ""
            )
        } else {
            signInWithGoogleFailed()
        }
    }

    private fun signInWithGoogleFailed() {
        showSnackBar(getString(R.string.smthng_went_wrong))
    }

    private fun setupClickListeners() = with(binding) {
        btnSignGoogle.setOnClickListener {
            val googleSignInIntent = googleSignInClient.signInIntent
            googleSignInActivityLauncher.launch(googleSignInIntent)
        }

        btnSkip.setOnClickListener {
            val action =
                LoginFragmentDirections.actionLoginFragmentToSelectCategoriesFragment(null, null)
            findNavController().navigate(action)
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun subscribeObserver() = with(binding) {
        authViewModel.auth.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    authViewModel.saveUser()
                    startMainActivity()
                }
                is DataState.Fail -> {
                    btnSignGoogle.hideLoading()
                    showSnackBar(it.message)
                }
                is DataState.Loading -> {
                    btnSignGoogle.showLoading()
                }
            }
        }
    }

    override fun createBinding(view: View) = FragmentAuthorizationBinding.bind(view)
}