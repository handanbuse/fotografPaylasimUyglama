package com.example.fotografpaylasim

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.fotografpaylasim.databinding.FragmentKayitBinding
import com.example.fotografpaylasim.databinding.FragmentYuklemeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID


class yukleme : Fragment() {
    private  var _binding: FragmentYuklemeBinding?= null
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    var secilengorsel :Uri? = null
    var secilenBitmap: Bitmap?=null

    private lateinit var auth: FirebaseAuth
    private lateinit var storage:FirebaseStorage

    private lateinit var db:FirebaseFirestore








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerLauncher()
        auth= Firebase.auth
        storage=Firebase.storage
        db=Firebase.firestore

        registerLauncher()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =FragmentYuklemeBinding.inflate(inflater,container,false)
        val view= binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageViewComment.setOnClickListener{gorselsec(it)}
        binding.commentBtn.setOnClickListener { yorumyap(it) }


    }

    fun gorselsec(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            // READ IMAGES
            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED){
                //izin yok
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                    // izin mantığını kullanıcıya göster
                    Snackbar.make(view,"Galeriye gitmek için izin gerekiyor ",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            //izin istememiz lazım
                        }).show()
                }else{
                    // izin istememiz lazım
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                }
            }else{
                //galeriye gitme kodu
                // izin verildi
                val intentToGallery =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }
        }
        else{

        }

    }
    fun yorumyap(view: View){

        val uuid=UUID.randomUUID()
        val gorselAdi="${uuid}.jpg" // farklı isimler ataması iiçin

       val reference= storage.reference
       val gorselreferansı= reference.child("images").child(gorselAdi)


        if (secilengorsel !=null){
            gorselreferansı.putFile(secilengorsel!!).addOnSuccessListener { uploadTask ->
                //uri alma işlemi
                gorselreferansı.downloadUrl.addOnSuccessListener { uri ->

                    if (auth.currentUser != null) {
                        val downloadUrl = uri.toString()
                        val postMap = hashMapOf<String, Any>()
                        postMap.put("downloadUrl", downloadUrl)
                        postMap.put("email", auth.currentUser!!.email.toString())
                        postMap.put("comment", binding.commentText.text.toString())
                        postMap.put("date", Timestamp.now()) // güncel tarih
                        db.collection("Post").add(postMap).addOnSuccessListener {
                            documentReference ->
                            // veri database yüklenmiş oluyor
                            val action=yuklemeDirections.actionYuklemeToFeed()
                            Navigation.findNavController(view).navigate(action)



                        }.addOnFailureListener { exception ->
                            Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
                        }


                    }
                }

            }.addOnFailureListener{
                exception ->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }



    }

    private fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if ( result.resultCode==RESULT_OK) {
                val intentFromResult=result.data
                if(intentFromResult !=null){
                    secilengorsel=intentFromResult.data
                    try{
                        if (Build.VERSION.SDK_INT>=28){
                            val source=ImageDecoder.createSource(requireActivity().contentResolver,secilengorsel!!)
                            secilenBitmap=ImageDecoder.decodeBitmap(source)
                            binding.imageViewComment.setImageBitmap(secilenBitmap)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

            }
        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            result ->
            if (result){
                // izin verildi
                val intentToGallery =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else{
                //izin verilmedi
                Toast.makeText(requireContext(),"izin verilmedi",Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}