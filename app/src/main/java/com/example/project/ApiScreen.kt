package com.example.project

import android.R
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.retrofitClass.BooksService
import com.example.project.adapter.KachraAdapter
import com.example.project.databinding.ActivityApiScreenBinding
import com.example.project.retrofitClass.BooksItem
import com.example.project.retrofitClass.QueryResult
import com.example.project.retrofitClass.bookServiceListener

class ApiScreen : AppCompatActivity(), bookServiceListener, AdapterView.OnItemSelectedListener {

    private val binding by lazy {
        ActivityApiScreenBinding.inflate(layoutInflater)
    }

    private var adapter: KachraAdapter? = null
    val titles = arrayOf("Title", "Author")
    var currentTitle = "Title"
    private val bookService = BooksService()
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()

    }

    private fun init() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Loading...")
        with(binding) {
            rRecycle.hasFixedSize()
            rRecycle.layoutManager = LinearLayoutManager(this@ApiScreen)
        }
        setSpinner()
        bookService.setBookServiceListener(this)
        binding.ivSearch.setOnClickListener {
            val text = binding.editTextTextPersonName.text.toString()
            if (text.isNotEmpty()) {
                progressDialog!!.show()
                bookService.queryBooks(currentTitle, text)
            } else {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSpinner() {
        binding.spinner.onItemSelectedListener = this
        val aa: ArrayAdapter<*> = ArrayAdapter<Any?>(this, R.layout.simple_spinner_item, titles)
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = aa
    }

    override fun onResponse(queryResult: QueryResult?) {
        progressDialog!!.dismiss()
        val list = queryResult?.items ?: return
        setAdapter(list)
    }

    override fun onFailure(message: String?) {
        progressDialog!!.dismiss()
        Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentTitle = titles[position]

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun setAdapter(bookList: List<BooksItem>) {
        adapter = KachraAdapter(bookList)
        binding.rRecycle.adapter = adapter
    }
}