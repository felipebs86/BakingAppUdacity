package fbs.com.br.bakingapp;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import fbs.com.br.bakingapp.adapters.RecipeDetailAdapter;
import fbs.com.br.bakingapp.model.Ingredient;
import fbs.com.br.bakingapp.model.Recipe;
import fbs.com.br.bakingapp.widget.UpdateBakingService;

import static fbs.com.br.bakingapp.MainActivity.SELECTED_RECIPES;

public class RecipeDetailFragment extends Fragment {

    List<Recipe> recipe;
    String recipeName;

    public RecipeDetailFragment() {

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView;
        TextView textView;

        recipe = new ArrayList<>();

        if(savedInstanceState != null) {
            recipe = savedInstanceState.getParcelableArrayList(SELECTED_RECIPES);
        }
        else {
            recipe =getArguments().getParcelableArrayList(SELECTED_RECIPES);
        }

        List<Ingredient> ingredients = recipe.get(0).getIngredients();
        recipeName=recipe.get(0).getName();

        View rootView = inflater.inflate(R.layout.recipe_detail_fragment_body_part, container, false);
        textView = rootView.findViewById(R.id.recipe_detail_text);

        ArrayList<String> recipeIngredientsForWidgets = new ArrayList<>();

        ingredients.forEach((a) ->
            {
                textView.append("\u2022 "+ a.getIngredient()+"\n");
                textView.append("\t\t\t Quantity: "+a.getQuantity().toString()+"\n");
                textView.append("\t\t\t Measure: "+a.getMeasure()+"\n\n");

                recipeIngredientsForWidgets.add(a.getIngredient()+"\n"+
                        "Quantity: "+a.getQuantity().toString()+"\n"+
                        "Measure: "+a.getMeasure()+"\n");
            });

        recyclerView = rootView.findViewById(R.id.recipe_detail_recycler);
        LinearLayoutManager mLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        RecipeDetailAdapter mRecipeDetailAdapter =new RecipeDetailAdapter((RecipeDetailActivity)getActivity());
        recyclerView.setAdapter(mRecipeDetailAdapter);
        mRecipeDetailAdapter.setMasterRecipeData(recipe);

        UpdateBakingService.startBakingService(getContext(),recipeIngredientsForWidgets);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList(SELECTED_RECIPES, (ArrayList<? extends Parcelable>) recipe);
        currentState.putString("Title",recipeName);
    }


}


