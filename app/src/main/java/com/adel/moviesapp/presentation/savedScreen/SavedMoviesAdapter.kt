package com.adel.moviesapp.presentation.savedScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.databinding.SavedMovieItemBinding
import com.squareup.picasso.Picasso

class SavedMoviesAdapter(var moviesList: List<MovieModel>, var listener: (MovieModel) ->Unit) :
        RecyclerView.Adapter<SavedMoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: SavedMovieItemBinding =
                SavedMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieItem: MovieModel = moviesList.get(position)
        Picasso.get().load(movieItem.image).into(holder.binding.ivMovie)
        holder.itemView.setOnClickListener {
            listener(movieItem)
        }
    }

    override fun getItemCount(): Int = moviesList.size

    inner class ViewHolder(val binding: SavedMovieItemBinding) : RecyclerView.ViewHolder(binding.root)

}