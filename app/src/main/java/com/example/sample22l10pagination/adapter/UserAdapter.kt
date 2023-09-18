package com.example.sample22l10pagination.adapter

import com.example.sample22l10pagination.databinding.ItemLoadingBinding
import com.example.sample22l10pagination.databinding.ItemUserBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sample22l10pagination.model.PagingData
import com.example.sample22l10pagination.model.User


class UserAdapter(
    context: Context,
    private val onUserClicked: (User) -> Unit
) : ListAdapter<PagingData<User>, RecyclerView.ViewHolder>(DIFF_UTIL) {

    private val layoutInflater = LayoutInflater.from(context)

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PagingData.Item -> TYPE_USER
            is PagingData.Loading -> TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_USER -> {
                UserViewHolder(
                    binding = ItemUserBinding.inflate(layoutInflater, parent, false),
                    onUserClicked = onUserClicked
                )
            }
            TYPE_LOADING -> {
                LoadingViewHolder(
                    binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                )
            }
            else -> error("Unsupported view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PagingData.Item -> {
                checkNotNull(holder as UserViewHolder) { "Incorrect viewholder $item" }
                holder.bind(item.data)
            }
            is PagingData.Loading -> {
                //no op
            }
        }
    }

    companion object {

        private const val TYPE_USER = 0
        private const val TYPE_LOADING = 1

        private val DIFF_UTIL = object : DiffUtil.ItemCallback<PagingData<User>>() {
            override fun areItemsTheSame(
                oldItem: PagingData<User>,
                newItem: PagingData<User>
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PagingData<User>,
                newItem: PagingData<User>
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}