package fbs.com.br.bakingapp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fbs.com.br.bakingapp.IdlingResource.SimpleIdlingResource;
import fbs.com.br.bakingapp.adapters.RecipeAdapter;
import fbs.com.br.bakingapp.model.Recipe;


public class MainActivity extends AppCompatActivity implements RecipeAdapter.ListItemClickListener {

    static String ALL_RECIPES="All_Recipes";
    static String SELECTED_RECIPES="Selected_Recipes";
    static String SELECTED_STEPS="Selected_Steps";
    static String SELECTED_INDEX="Selected_Index";

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Baking App");
    }

    @Override
    public void onListItemClick(Recipe selectedItemIndex) {
        Bundle selectedRecipeBundle = new Bundle();
        List<Recipe> selectedRecipe = new ArrayList<Recipe>();
        selectedRecipe.add(selectedItemIndex);
        selectedRecipeBundle.putParcelableArrayList(SELECTED_RECIPES, (ArrayList<? extends Parcelable>) selectedRecipe);

        final Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtras(selectedRecipeBundle);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
