package fbs.com.br.bakingapp;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import fbs.com.br.bakingapp.IdlingResource.SimpleIdlingResource;
import fbs.com.br.bakingapp.adapters.RecipeAdapter;
import fbs.com.br.bakingapp.model.Recipe;
import fbs.com.br.bakingapp.retrofit.RecipeInterface;
import fbs.com.br.bakingapp.retrofit.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static fbs.com.br.bakingapp.MainActivity.ALL_RECIPES;


public class RecipeFragment extends Fragment  {

    public RecipeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView;

        View rootView = inflater.inflate(R.layout.recipe_fragment_body_part, container, false);

        recyclerView = rootView.findViewById(R.id.recipe_recycler);
        RecipeAdapter recipesAdapter = new RecipeAdapter((MainActivity)getActivity());
        recyclerView.setAdapter(recipesAdapter);

        if (rootView.getTag()!=null && rootView.getTag().equals("phone-land")){
            GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),4);
            recyclerView.setLayoutManager(mLayoutManager);
        }
        else {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
        }

        RecipeInterface recipeInterface = RetrofitBuilder.Retrieve();
        Call<List<Recipe>> recipe = recipeInterface.getRecipe();

        SimpleIdlingResource idlingResource = (SimpleIdlingResource)((MainActivity)getActivity()).getIdlingResource();

        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        recipe.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                Integer statusCode = response.code();
                Log.v("status code: ", statusCode.toString());

                List<Recipe> recipes = response.body();

                Bundle recipesBundle = new Bundle();
                recipesBundle.putParcelableArrayList(ALL_RECIPES, (ArrayList<? extends Parcelable>) recipes);

                recipesAdapter.setRecipeData((ArrayList<Recipe>) recipes, getContext());
                idlingResource.setIdleState(true);
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e("Falha na comunicação: ", t.getMessage());
            }
        });

        return rootView;
    }
}
