package com.nesib.yourbooknotes.ui.main.fragments.search

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nesib.yourbooknotes.R
import com.nesib.yourbooknotes.adapters.HomeAdapter
import com.nesib.yourbooknotes.databinding.FragmentSearchQuotesBinding
import com.nesib.yourbooknotes.models.Quote
import com.nesib.yourbooknotes.ui.main.MainActivity
import com.nesib.yourbooknotes.ui.viewmodels.AuthViewModel
import com.nesib.yourbooknotes.ui.viewmodels.QuoteViewModel
import com.nesib.yourbooknotes.ui.viewmodels.SharedViewModel
import com.nesib.yourbooknotes.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SearchQuotesFragment : Fragment(R.layout.fragment_search_quotes) {
    private lateinit var binding: FragmentSearchQuotesBinding
    private val homeAdapter by lazy { HomeAdapter((activity as MainActivity).dialog) }
    private val quoteViewModel: QuoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels({ requireActivity() })
    private val args by navArgs<SearchQuotesFragmentArgs>()

    private var currentPage = 1
    private var paginatingFinished = false
    private var paginationLoading = false
    private var currentQuotes: List<Quote> = emptyList()
    private var followingGenres: String = ""

    private var genres: MutableList<String> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchQuotesBinding.bind(view)
        setHasOptionsMenu(true)
        setupUi()
        setupRecyclerView()
        subscribeObservers()
        quoteViewModel.getQuotesByGenre(args.genre)
    }

    private fun setupUi() {
//        binding.genreText.text = "#${args.genre}"
//        if(authViewModel.currentUser?.followingGenres!!.contains(args.genre.toLowerCase())){
//            toggleFollowButtonStyle(true)
//        }else{
//            toggleFollowButtonStyle(false)
//        }
    }

//    private fun toggleFollowButtonStyle(following: Boolean) {
//        binding.apply {
//            followGenreButton.setBackgroundResource(if (following) R.drawable.add_quote_from_this_book_bg else R.drawable.follow_button_bg)
//            followButtonTextView.text = if (following) "Following" else "Follow"
//            followButtonTextView.setTextColor(
//                if (following) ContextCompat.getColor(
//                    requireContext(),
//                    R.color.blue
//                ) else ContextCompat.getColor(requireContext(), R.color.white)
//            )
//
//        }
//    }

    private fun subscribeObservers() {
        quoteViewModel.quotes.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    if (paginationLoading) {
                        binding.paginationProgressBar.visibility = View.INVISIBLE
                        paginationLoading = false
                        if (currentQuotes.size == it.data!!.quotes.size) {
                            paginatingFinished = true
                        }
                    }
                    currentQuotes = it.data!!.quotes
                    binding.shimmerLayout.visibility = View.INVISIBLE
                    homeAdapter.setData(currentQuotes)
                }
                is DataState.Fail -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {
                    if (paginationLoading) {
                        Log.d("mytag", "show pagination progress bar")
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    } else {
                        binding.shimmerLayout.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val mLayoutManager = LinearLayoutManager(requireContext())
        binding.searchQuotesRecyclerView.apply {
            adapter = homeAdapter
            layoutManager = mLayoutManager
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        if (!paginatingFinished && mLayoutManager.findLastCompletelyVisibleItemPosition() == (currentQuotes.size - 1) && !paginationLoading) {
                            currentPage++
                            paginationLoading = true
                            quoteViewModel.getQuotesByGenre(args.genre, currentPage)
                        }
                    }
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.discover_quotes_menu, menu)
        followingGenres = authViewModel.getFollowingGenres()
        genres = followingGenres.split(",").toMutableList()
        if (followingGenres.contains(args.genre.toLowerCase(Locale.ROOT))) {
            menu.findItem(R.id.follow_genre_menu_item).title = "Following"
        } else {
            menu.findItem(R.id.follow_genre_menu_item).title = "Follow"
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var genresChanged = true
        if (item.itemId == R.id.follow_genre_menu_item) {
            if (!genres.contains(args.genre)) {
                genres.add(args.genre)
                item.title = "Following"
            } else {
                if (genres.size > 3) {
                    genres.remove(args.genre)
                    item.title = "Follow"
                } else {
                    genresChanged = false
                    Toast.makeText(
                        requireContext(),
                        "You should at least select 3 genres",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            if(genresChanged){
                val genresText = genres.toCustomizedString()
                authViewModel.saveFollowingGenres(genresText)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun MutableList<String>.toCustomizedString(): String {
        var text = ""
        this.forEachIndexed { index, genre ->
            text += if (index != this.size - 1) {
                "$genre,"
            } else {
                genre
            }
        }
        return text
    }
}