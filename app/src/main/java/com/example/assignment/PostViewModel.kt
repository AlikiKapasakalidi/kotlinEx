package com.example.assignment

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PostViewModel:ViewModel(){
    var posts = mutableStateOf(mutableListOf<Post>())


    fun addPost(post: Post){
        var newPosts =  posts.value.toMutableList()
        newPosts.add(post)
        posts.value = newPosts
    }
    fun removePosts(post: Post){
        var removePosts =  posts.value.toMutableList()
        removePosts.remove(post)
        posts.value = removePosts
    }


}