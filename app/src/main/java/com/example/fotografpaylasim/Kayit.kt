package com.example.fotografpaylasim

import android.os.Bundle
import android.text.Layout.Directions
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.fotografpaylasim.databinding.FragmentKayitBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth


class Kayit : Fragment() {


    private  var _binding:FragmentKayitBinding?= null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =FragmentKayitBinding.inflate(inflater,container,false)
        val view= binding.root
        return view

    }
    fun kayit(view: View){

        val email=binding.mailEdittext.text.toString()
        val password=binding.sifreEdittext.text.toString()
        if(email.isNotEmpty()&& password.isNotEmpty()){
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val action=KayitDirections.actionKayitToFeed()
                    Navigation.findNavController(view).navigate(action)

                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }






    }
    fun giris(view: View){

        val email=binding.mailEdittext.text.toString()
        val password=binding.sifreEdittext.text.toString()

        if (email.isNotEmpty()&&password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener { task ->

                val action=KayitDirections.actionKayitToFeed()
                Navigation.findNavController(view).navigate(action)

            }.addOnFailureListener { exception->
                Toast.makeText(requireContext(), exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.kayitbtn.setOnClickListener{kayit(it)}
        binding.girisBtn.setOnClickListener{giris(it)}

        val guncelKullanici= auth.currentUser
        if (guncelKullanici !=null){
            val action=KayitDirections.actionKayitToFeed()
            Navigation.findNavController(view).navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


}