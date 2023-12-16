package com.adel.moviesapp.presentation.homeScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.databinding.MovieItemBinding
import com.squareup.picasso.Picasso

class AllMoviesAdapter(var moviesList: List<MovieModel>, var listener: (MovieModel) -> Unit) :
    RecyclerView.Adapter<AllMoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: MovieItemBinding =
            MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieItem: MovieModel = moviesList.get(position)
        with(holder.binding) {
            Picasso.get().load(movieItem.image).into(ivFilm)
            tvMovieName.text = movieItem.name
            tvMovieType.text = movieItem.type
            tvMovieRate.text = movieItem.rate.toString()
        }
        holder.itemView.setOnClickListener {
            with(holder.binding) {
                listener(movieItem)
            }
        }
    }

    override fun getItemCount(): Int = moviesList.size

    inner class ViewHolder(val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root)

}