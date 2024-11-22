package com.example.fotografpaylasim

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fotografpaylasim.databinding.FragmentFeedBinding
import com.example.fotografpaylasim.databinding.FragmentKayitBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase


class feed : Fragment(),PopupMenu.OnMenuItemClickListener{
    private  var _binding: FragmentFeedBinding?= null
    private val binding get() = _binding!!
    private lateinit var popup: PopupMenu

    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    val postList: ArrayList<Post> = ArrayList()
    private var adapter : PostAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      auth=Firebase.auth
        db=Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =FragmentFeedBinding.inflate(inflater,container,false)
        val view= binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingBtn.setOnClickListener{floatingBtn(it)}

        //MENÜ BAĞLAMA İŞLEMİ
         popup=PopupMenu(requireContext(),binding.floatingBtn) // BAĞLAMA İŞLEMİ
        val inflater=popup.menuInflater
        inflater.inflate(R.menu.my_popup_menu,popup.menu)
        popup.setOnMenuItemClickListener(this)
        firebaseVerileriAl()

        adapter= PostAdapter(postList)
        binding.recyclerviewFeed.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerviewFeed.adapter= adapter


    }

    private fun firebaseVerileriAl(){
        db.collection("Post").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error !=null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if (value !=null){
                    if (!value.isEmpty){
                        postList.clear()
                        //boş değilse
                        val documents=value.documents
                        for(document in documents){
                            val comment=document .get("comment") as String
                           val email= document.get("email") as String
                            val downloadUrl= document.get("downloadUrl") as String

                            val post=Post(email,comment,downloadUrl)
                            postList.add(post)

                    }
                        adapter?.notifyDataSetChanged() // yeni veriler geldi adapterı baştan oluştur demek
                    }
                }
            }
        }

    }

    fun floatingBtn(view: View){

        //MENÜYÜ GÖSTER
        popup.show()



    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        //tıklandığında menü nereye gitsin kontrolleri yap
        if (item?.itemId==R.id.yukleme_item){
            val action=feedDirections.actionFeedToYukleme()
            Navigation.findNavController(requireView()).navigate(action)
        } else if(item?.itemId==R.id.cikis){
            //çıkış yapınca tekrar kayıt sayfasına gelsin
            auth.signOut() // çıkış yapıldı

            val action=feedDirections.actionFeedToKayit()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return true

    }


}