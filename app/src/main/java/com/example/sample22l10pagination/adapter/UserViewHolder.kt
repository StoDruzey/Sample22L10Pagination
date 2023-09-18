package com.example.sample22l10pagination.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.sample22l10pagination.model.User
import com.example.sample22l10pagination.databinding.ItemUserBinding

class UserViewHolder(
    private val binding: ItemUserBinding,
    private val onUserClicked: (User) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: User) {
        with(binding) {
            root.setOnClickListener { onUserClicked(item) }
            imageAvatar.load(item.avatarUrl)
            userName.text =item.login
        }
    }
}