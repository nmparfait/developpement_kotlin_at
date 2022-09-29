package com.example.notesapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityAddNotesBinding
import com.example.notesapp.model.NotesModel
import com.example.notesapp.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class AddNotesActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAddNotesBinding.inflate(layoutInflater)
    }

    private var firebaseAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault())
    private var progressDialog: ProgressDialog? = null
    private var imgUri: Uri? = null

    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var title = ""
    private var description = ""

    private var mGetContent =
        registerForActivityResult<String, Uri>(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imgUri = uri
                Glide.with(this).load(uri).into(binding.ivProfile)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
        handleClicks()

    }

    private fun init() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setTitle("Please Wait")
        progressDialog?.setMessage("Loading...")

        binding.toolbar.menuBtn.setImageResource(R.drawable.ic_back_new)
        binding.toolbar.statusGroup.visibility = View.GONE
        binding.toolbar.mainTitleTxtView.text = getString(R.string.add_note)
        binding.etDescription.movementMethod = ScrollingMovementMethod()

        firebaseAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        firebaseStorage = FirebaseStorage.getInstance()
        mDatabase = Firebase.database.getReference("Notes")
    }

    private fun handleClicks() {
        binding.toolbar.menuBtn.setOnClickListener {
            onBackPressed()
        }

        binding.btnAdd.setOnClickListener {
            title = binding.etTitle.text.toString()
            description = binding.etDescription.text.toString()
            if (imgUri == null) {
                Toast.makeText(this, "Please select profile image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (title.isNotEmpty() && description.isNotEmpty()) {
                writeNotes(title, description)
            } else {
                showToast("Fill required fields")
            }
        }

        binding.ivProfile.setOnClickListener {
            mGetContent.launch("image/*")
        }
    }

    private fun writeNotes(title: String, description: String) {
        val currentDate = sdf.format(Date())
        progressDialog?.show()
        imgUri?.let { uri ->
            val uploadTask =
                storageReference?.child("profilePics/" + firebaseAuth?.currentUser?.uid + "/${System.currentTimeMillis()}")
                    ?.putFile(uri)
            uploadTask?.addOnCompleteListener { uploadTask1 ->
                if (uploadTask1.isSuccessful) {
                    uploadTask1.result.storage.downloadUrl.addOnCompleteListener { newTask ->
                        if (newTask.isSuccessful) {
                            val newUri = newTask.result
                            val userId = firebaseAuth?.uid ?: return@addOnCompleteListener
                            val notesModel = NotesModel(
                                notesTitle = title,
                                notesDetail = description,
                                notesDate = currentDate,
                                notesImagePath = newUri.toString()
                            )
                            mDatabase?.child(userId)?.child("NotesList")?.push()
                                ?.setValue(notesModel)?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        progressDialog?.dismiss()
                                        showToast("Note Added")
                                        val intent = Intent(this, MainActivity::class.java).apply {
                                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        }
                                        startActivity(intent)
                                    } else {
                                        progressDialog?.dismiss()
                                        Log.d(
                                            "fException",
                                            "createNewAccount: ${task.exception?.message}"
                                        )
                                        showToast("task.exception?.message")
                                    }

                                }?.addOnFailureListener {
                                    progressDialog?.dismiss()
                                    showToast(it.message.toString())
                                }
                        } else {
                            progressDialog?.dismiss()
                            showToast(newTask.exception?.message.toString())
                        }
                    }.addOnFailureListener {
                        progressDialog?.dismiss()
                        showToast(it.message.toString())
                    }
                } else {
                    progressDialog?.dismiss()
                    showToast(uploadTask1.exception?.message.toString())
                }
            }
        }
    }
}