package com.adel.moviesapp.presentation.homeScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.databinding.ViewPagerMovieItemBinding
import com.squareup.picasso.Picasso

class PopularMoviesSliderAdapter(var moviesList: List<MovieModel>, var listener: (MovieModel) -> Unit) :
    RecyclerView.Adapter<PopularMoviesSliderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewPagerMovieItemBinding =
            ViewPagerMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieItem: MovieModel = moviesList[position]
        with(holder.binding) {
            Picasso.get().load(movieItem.image).into(ivFilm)
            tvFilmName.text = movieItem.name
            tvFilmType.text = movieItem.type
        }
        holder.itemView.setOnClickListener {
            listener(movieItem)
        }
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    inner class ViewHolder(val binding: ViewPagerMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}
