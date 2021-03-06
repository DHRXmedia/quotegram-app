package com.nesib.yourbooknotes.ui.main.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nesib.yourbooknotes.R
import com.nesib.yourbooknotes.adapters.SearchPagerAdapter
import com.nesib.yourbooknotes.databinding.FragmentSearchBinding
import com.nesib.yourbooknotes.ui.main.MainActivity
import com.nesib.yourbooknotes.utils.Utils

class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private val pagerAdapter by lazy { SearchPagerAdapter(this) }
    private var selectedTabItem = "Books"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        setupTabLayout()
        binding.searchInput.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(binding.viewPager.currentItem == 1){
                    Utils.usersNotifier?.notify(s.toString())
                }else{
                    Utils.booksNotifier?.notify(s.toString())
                }
            }

        })
    }

    private fun setupTabLayout(){
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(
            binding.searchTabLayout,
            binding.viewPager
        ) { tab: TabLayout.Tab, i: Int ->
            tab.text = if(i==0) "Books" else "Users"
        }.attach()
    }

}