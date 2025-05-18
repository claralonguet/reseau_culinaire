package com.example.culinar.models.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class FriendViewModel : ViewModel() {

    val friends = mutableStateListOf<String>()

    fun addFriend(name: String) {
        if (!friends.contains(name)) {
            friends.add(name)
        }
    }
}