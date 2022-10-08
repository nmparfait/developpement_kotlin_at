package com.example.project.retrofitClass

interface bookServiceListener {

    fun onResponse(queryResult: QueryResult?)

    fun onFailure(message: String?)
}
