package com.mjavacam.tmdb;

import java.io.IOException;
import java.util.ArrayList;

import org.mjavacam.wrapper.tmdb.TMDb;
import org.mjavacam.wrapper.tmdb.collections.MovieList;
import org.mjavacam.wrapper.tmdb.collections.Results;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MovieActivity extends Activity {
	Context context;
	ArrayAdapter<?> adapter;
	ListView lvMovies;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie);
		context = this; 
		lvMovies = (ListView) findViewById(R.id.listView1);
		GetMovieList data = new GetMovieList();
		data.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.movie, menu);
		return true;
	}
	
	private class GetMovieList extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			TMDb tmdb = new TMDb();
			String data = "";
			Intent intent = getIntent();
			String genreId = intent.getExtras().getString("genreId");
			try {
				data = tmdb.GetAllMoviesByGenreId(genreId);
				Log.d("Movies",data);
				return data;
			} catch (IOException e) {
				Log.d("ERROR",e.getMessage());
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			
			super.onPostExecute(result);
			ObjectMapper mapper = new ObjectMapper();
			MovieList moviesByGenre = null;
			try {
				if(!result.isEmpty())
					moviesByGenre = mapper.readValue(result, MovieList.class);
			} catch (JsonParseException e) {
				Log.d("ERROR",e.getMessage());
				e.printStackTrace();
			} catch (JsonMappingException e) {
				Log.d("ERROR",e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("ERROR",e.getMessage());
				e.printStackTrace();
			}
			ArrayList<String> movies = new ArrayList<String>();
			for(int i =0;i < moviesByGenre.getResults().size(); i++){
				movies.add(moviesByGenre.getResults().get(i).getTitle().toUpperCase().toString());
			}
			adapter = new ArrayAdapter<String>(context,R.layout.list_item,R.id.tvText,movies);
			adapter.notifyDataSetChanged();
			lvMovies.setAdapter(adapter);
		}
		
	}

}
