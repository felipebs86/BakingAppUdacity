package fbs.com.br.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import fbs.com.br.bakingapp.model.Recipe;
import fbs.com.br.bakingapp.model.Step;
import static fbs.com.br.bakingapp.RecipeDetailActivity.SELECTED_INDEX;
import static fbs.com.br.bakingapp.RecipeDetailActivity.SELECTED_STEPS;

public class RecipeStepDetailFragment extends Fragment {
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private SimpleExoPlayer mPlayer;
    private BandwidthMeter mBandwidthMeter;
    private ArrayList<Step> mSteps = new ArrayList<>();
    private int mSelectedIndex;
    private Handler mMainHandler;
    ArrayList<Recipe> mRecipe;
    String mRecipeName;
    private long mPlaybackPosition;
    private int mCurrentWindow;
    private boolean mPlayWhenReady;

    public RecipeStepDetailFragment() {

    }

   private ListItemClickListener itemClickListener;

    public interface ListItemClickListener {
        void onListItemClick(List<Step> allSteps, int Index, String recipeName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView;
        mMainHandler = new Handler();
        mBandwidthMeter = new DefaultBandwidthMeter();

        itemClickListener =(RecipeDetailActivity)getActivity();

        mRecipe = new ArrayList<>();

        if(savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(SELECTED_STEPS);
            mSelectedIndex = savedInstanceState.getInt(SELECTED_INDEX);
            mRecipeName = savedInstanceState.getString("Title");
            mPlaybackPosition = savedInstanceState.getLong("videoStep");
            mCurrentWindow = savedInstanceState.getInt("currentWindow");
            mPlayWhenReady = savedInstanceState.getBoolean("isReady");
        }
        else {
            mSteps = getArguments().getParcelableArrayList(SELECTED_STEPS);
            if (mSteps != null) {
                mSteps = getArguments().getParcelableArrayList(SELECTED_STEPS);
                mSelectedIndex = getArguments().getInt(SELECTED_INDEX);
                mRecipeName = getArguments().getString("Title");
            }
            else {
                mRecipe = getArguments().getParcelableArrayList(SELECTED_STEPS);
                mSteps = (ArrayList<Step>) (mRecipe != null ? mRecipe.get(0).getSteps() : null);
                mSelectedIndex =0;
            }

        }

        View rootView = inflater.inflate(R.layout.recipe_step_detail_fragment_body_part, container, false);
        textView = rootView.findViewById(R.id.recipe_step_detail_text);
        textView.setText(mSteps.get(mSelectedIndex).getDescription());
        textView.setVisibility(View.VISIBLE);

        mSimpleExoPlayerView = rootView.findViewById(R.id.playerView);
        mSimpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        String videoURL = mSteps.get(mSelectedIndex).getVideoURL();

        if (rootView.findViewWithTag("sw600dp-port-recipe_step_detail")!=null) {
           mRecipeName =((RecipeDetailActivity) getActivity()).recipeName;
           ((RecipeDetailActivity) getActivity()).getSupportActionBar().setTitle(mRecipeName);
        }

        String imageUrl= mSteps.get(mSelectedIndex).getThumbnailURL();
        if (!imageUrl.equals("")) {
            Uri builtUri = Uri.parse(imageUrl).buildUpon().build();
            ImageView thumbImage = rootView.findViewById(R.id.thumbImage);
            Picasso.with(getContext()).load(builtUri).into(thumbImage);
        }

        if (!videoURL.isEmpty()) {
            initializePlayer(Uri.parse(mSteps.get(mSelectedIndex).getVideoURL()));

           if (rootView.findViewWithTag("sw600dp-land-recipe_step_detail")!=null) {
                getActivity().findViewById(R.id.fragment_container2).setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
                mSimpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            }
            else if (isInLandscapeMode(getContext())){
                textView.setVisibility(View.GONE);
            }
        } else {
            mPlayer =null;
            mSimpleExoPlayerView.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility_off_white_36dp));
            mSimpleExoPlayerView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        }


        Button mPrevStep = rootView.findViewById(R.id.previousStep);
        Button mNextstep = rootView.findViewById(R.id.nextStep);

        mPrevStep.setOnClickListener(view -> {
        if (mSteps.get(mSelectedIndex).getId() > 0) {
            if (mPlayer !=null){
                mPlayer.stop();
            }
            itemClickListener.onListItemClick(mSteps, mSteps.get(mSelectedIndex).getId() - 1, mRecipeName);
        }
        else {
            Toast.makeText(getActivity(), R.string.WARNING_FIRST_STEP, Toast.LENGTH_SHORT).show();

        }
    });

        mNextstep.setOnClickListener(view -> {

            int lastIndex = mSteps.size()-1;
            if (mSteps.get(mSelectedIndex).getId() < mSteps.get(lastIndex).getId()) {
                if (mPlayer !=null){
                    mPlayer.stop();
                }
                itemClickListener.onListItemClick(mSteps, mSteps.get(mSelectedIndex).getId() + 1, mRecipeName);
            }
            else {
                Toast.makeText(getContext(), R.string.WARNING_LAST_STEP, Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
    }

    private void initializePlayer(Uri mediaUri) {
        if (mPlayer == null) {
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(mBandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(mMainHandler, videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            mPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mSimpleExoPlayerView.setPlayer(mPlayer);

            String userAgent = Util.getUserAgent(getContext(), "Baking App");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mPlayer.prepare(mediaSource);
            mPlayer.setPlayWhenReady(mPlayWhenReady);
            mPlayer.seekTo(mCurrentWindow, mPlaybackPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList(SELECTED_STEPS, mSteps);
        currentState.putInt(SELECTED_INDEX, mSelectedIndex);
        currentState.putString("Title", mRecipeName);
        currentState.putLong("videoStep", mPlaybackPosition);
        currentState.putInt("currentWindow", mCurrentWindow);
        currentState.putBoolean("isReady", mPlayWhenReady);
    }

    public boolean isInLandscapeMode( Context context ) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveStateFromPlayer();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveStateFromPlayer();
        if (Util.SDK_INT <= 23){
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            saveStateFromPlayer();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void saveStateFromPlayer() {
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getCurrentPosition();
            mCurrentWindow = mPlayer.getCurrentWindowIndex();
            mPlayWhenReady = mPlayer.getPlayWhenReady();
        }
    }

}
