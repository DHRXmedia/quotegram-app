package com.nesib.yourbooknotes.ui.main.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nesib.yourbooknotes.R
import com.nesib.yourbooknotes.adapters.SearchBooksAdapter
import com.nesib.yourbooknotes.databinding.FragmentSearchBinding
import com.nesib.yourbooknotes.databinding.FragmentSearchBooksBinding
import com.nesib.yourbooknotes.ui.main.MainActivity
import com.nesib.yourbooknotes.ui.main.fragments.SearchFragmentDirections.actionSearchFragmentToBookProfileFragment
import com.nesib.yourbooknotes.ui.viewmodels.MainViewModel
import com.nesib.yourbooknotes.utils.DataState
import com.nesib.yourbooknotes.utils.IBooksNotifer
import com.nesib.yourbooknotes.utils.Utils

class SearchBooksFragment : Fragment(R.layout.fragment_search_books),IBooksNotifer {
    private lateinit var binding: FragmentSearchBooksBinding
    private val mainViewModel:MainViewModel by viewModels()
    private val adapter by lazy { SearchBooksAdapter() }
    private var searchViewTextChanged = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.booksNotifier = this
        binding = FragmentSearchBooksBinding.bind(view)
        subscribeObservers()
        setupRecyclerView()

        mainViewModel.getBooks()
    }

    private fun setupRecyclerView(){
        adapter.onBookClickListener = {book->
            val action = SearchFragmentDirections.actionSearchFragmentToBookProfileFragment(book.id)
            findNavController().navigate(action)
        }
        binding.searchBooksRecyclerView.itemAnimator = null
        binding.searchBooksRecyclerView.adapter = adapter
        binding.searchBooksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeObservers(){
        mainViewModel.books.observe(viewLifecycleOwner){
            when(it){
                is DataState.Success -> {
                    adapter.setData(it.data!!.books)
                    toggleProgressBar(false)
                }
                is DataState.Fail->{
                    toggleProgressBar(false)
                }
                is DataState.Loading -> {
                    if(!searchViewTextChanged){
                        toggleProgressBar(true)
                    }
                }
            }
        }
    }

    private fun toggleProgressBar(loading:Boolean){
        binding.progressBar.visibility = if(loading) View.VISIBLE else View.INVISIBLE
        binding.searchBooksRecyclerView.visibility = if(loading) View.INVISIBLE else View.VISIBLE
    }

    override fun onSearchViewTextChanged(text: String) {
        if(text.isNotEmpty()){
            searchViewTextChanged = true
        }
        Handler(Looper.getMainLooper())
            .postDelayed({
                mainViewModel.getBooks(text)

            },300)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Utils.booksNotifier = null
    }


}