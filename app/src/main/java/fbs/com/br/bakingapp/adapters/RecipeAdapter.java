package fbs.com.br.bakingapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import fbs.com.br.bakingapp.R;
import fbs.com.br.bakingapp.model.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecyclerViewHolder> {

    List<Recipe> recipeList;
    Context context;
    final private ListItemClickListener listItemClickListener;

    public interface ListItemClickListener {
        void onListItemClick(Recipe clickedItemIndex);
    }

    public RecipeAdapter(ListItemClickListener listener) {
        listItemClickListener =listener;
    }


    public void setRecipeData(ArrayList<Recipe> recipesIn, Context context) {
        recipeList = recipesIn;
        this.context =context;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recipe_cardview_items;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup,  false);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
       holder.textRecyclerView.setText(recipeList.get(position).getName());
        String imageUrl= recipeList.get(position).getImage();

        if (!"".equals(imageUrl)) {
            Uri builtUri = Uri.parse(imageUrl).buildUpon().build();
            Picasso.with(context).load(builtUri).into(holder.imageRecyclerView);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0 ;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textRecyclerView;
        ImageView imageRecyclerView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            textRecyclerView = (TextView) itemView.findViewById(R.id.title);
            imageRecyclerView = (ImageView) itemView.findViewById(R.id.recipeImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listItemClickListener.onListItemClick(recipeList.get(clickedPosition));
        }

    }
}
