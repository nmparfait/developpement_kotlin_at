package com.example.project.retrofitClass

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BooksService {

    private  var api: BooksApi
    private lateinit var listener: bookServiceListener

    init{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(BooksApi::class.java)
    }

    fun setBookServiceListener(listener: bookServiceListener){
        this.listener = listener
    }

    fun queryBooks(type:String,query: String){

        if(type.isNotEmpty() && query.isNotEmpty()){
            var queryWithType = ""

            when(type){
                "Titulo" -> queryWithType = "intitle:"
                "Autor" -> queryWithType = "inauthor:"
            }
            queryWithType += query

            val call = api.queryBooks(queryWithType)

            call!!.enqueue(object : Callback<QueryResult?> {
                override fun onResponse(
                    call: Call<QueryResult?>,
                    response: Response<QueryResult?>
                ) {
                    Log.i("DR3", "onResponse  ${response.code()}")
                    if(response.isSuccessful){
                        listener.onResponse(response.body())
                    }

                }

                override fun onFailure(call: Call<QueryResult?>, t: Throwable) {
                    Log.i("DR3", "onFailure ERRO ${t.message}")
                    listener.onFailure(t.message)
                }


            })
        }
    }
}