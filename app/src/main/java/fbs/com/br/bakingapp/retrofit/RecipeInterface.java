package fbs.com.br.bakingapp.retrofit;

import java.util.List;

import fbs.com.br.bakingapp.model.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeInterface {
    @GET("baking.json")
    Call<List<Recipe>> getRecipe();
}