package com.example.sample22l10pagination.retrofit

import com.example.sample22l10pagination.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubInterface {

    @GET("users")
    fun getUsers(@Query("since") since: Int, @Query("per_page") perPage: Int): Call<List<User>>
}