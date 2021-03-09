package com.nesib.yourbooknotes.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nesib.yourbooknotes.R
import com.nesib.yourbooknotes.databinding.QuoteFromBookLayoutBinding
import com.nesib.yourbooknotes.models.Quote

class BookQuotesAdapter : RecyclerView.Adapter<BookQuotesAdapter.ViewHolder>() {
    var OnUsernameTextViewClickListener: (() -> Unit)? = null
    var OnUserImageViewClickListener: (() -> Unit)? = null
    var onReachBottom: (()->Unit)? = null


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var binding:QuoteFromBookLayoutBinding = QuoteFromBookLayoutBinding.bind(itemView)

        init {
            binding.userImage.setOnClickListener(this)
            binding.userImage.setOnClickListener(this)
        }
        fun bindData(){
            val quote = differ.currentList[adapterPosition]
            binding.apply {
                username.text = quote.creator!!.username
                userImage.load(R.drawable.user){
                    crossfade(600)
                }
                bookQuoteTextView.text = quote.quote
                quoteLikesCount.text = quote.likes?.size.toString()
            }
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.username -> {
                    OnUsernameTextViewClickListener!!()
                }
                R.id.userImage -> {
                    OnUserImageViewClickListener!!()
                }
            }
        }
    }

    private val diffCallback = object: DiffUtil.ItemCallback<Quote>(){
        override fun areItemsTheSame(oldItem: Quote, newItem: Quote): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Quote, newItem: Quote): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookQuotesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quote_from_book_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookQuotesAdapter.ViewHolder, position: Int) {
        holder.bindData()
    }

    override fun getItemCount() = differ.currentList.size

    fun setData(newQuoteList:List<Quote>){
        differ.submitList(newQuoteList)
    }
}