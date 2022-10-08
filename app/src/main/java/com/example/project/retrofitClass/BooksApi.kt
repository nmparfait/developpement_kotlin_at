package com.example.project.retrofitClass

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApi {

    @GET("volumes")
    fun queryBooks(@Query("q") query: String?) : Call<QueryResult?>?
}