package com.example.fotografpaylasim

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fotografpaylasim.databinding.RecyclerrowBinding
import com.squareup.picasso.Picasso

class PostAdapter(private val PostList: ArrayList<Post>):RecyclerView.Adapter<PostAdapter.PostHolder>(){

    class PostHolder(val binding:RecyclerrowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {

        val binding =RecyclerrowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
     return PostList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.binding.recycleremailText.text=PostList[position].email
        holder.binding.recyclercommandText.text=PostList[position].comment
        Picasso.get().load(PostList[position].downloadUrl).into(holder.binding.reycylerMage)




}
}