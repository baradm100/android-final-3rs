package com.colman.bar.admoni.a3rs;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.colman.bar.admoni.a3rs.models.Post;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PostViewFragment extends Fragment {
    public static final String ARG_POST_ID = "postId";
    public static final String ARG_POST = "post";

    private String postId;
    private Post post;
    private boolean showEdit;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private ActivityResultLauncher<Intent> mStartForResult;
    MapView mMapView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
            post = (Post) getArguments().getSerializable(ARG_POST);
            showEdit = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(post.getUserUid());
        }

        mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Post update
                        Log.d(Consts.TAG, "Post Updated!");
                        Post updatedPost = (Post) result.getData().getExtras().getSerializable(ARG_POST);
                        post = updatedPost;
                        loadPostData();
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        // Post delete
                        Log.d(Consts.TAG, "Post deleted!");
                        Navigation.findNavController(getView()).popBackStack();
                    }
                });
    }

    private void loadPostData() {
        View v = getView();
        loadPostData(v);
    }

    private void loadPostData(View v) {
        TextView postViewTitleTextView = v.findViewById(R.id.postViewTitleTextView);
        TextView postViewSubtitleTextView = v.findViewById(R.id.postViewSubtitleTextView);
        TextView postViewDescriptionTextView = v.findViewById(R.id.postViewDescriptionTextView);
        TextView postViewUserEditText = v.findViewById(R.id.postViewUserTextView);
        ImageView postViewImageView = v.findViewById(R.id.postViewImageView);

        postViewTitleTextView.setText(post.getTitle());
        postViewSubtitleTextView.setText(post.getSubTitle());
        postViewDescriptionTextView.setText(post.getDescription());
        postViewUserEditText.setText(post.getUserName());

        StorageReference storageRef = storage.getReference();
        StorageReference productImageRef = storageRef.child("images/" + postId + ".jpg");

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(v.getContext());
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setCenterRadius(30);
        circularProgressDrawable.start();

        Glide.with(this)
                .load(productImageRef)
                .placeholder(circularProgressDrawable)
                .into(postViewImageView);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_view, container, false);

        if (showEdit) {
            v.findViewById(R.id.postViewEditPostFloatingActionButton).setVisibility(View.VISIBLE);
        }

        v.findViewById(R.id.postViewGoBackButton).setOnClickListener(this::handleGoBackToFeedClick);
        v.findViewById(R.id.postViewImageButton).setOnClickListener(this::handleCallClick);
        v.findViewById(R.id.postViewEditPostFloatingActionButton).setOnClickListener(this::handleEditClick);

        loadPostData(v);

        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(map -> {
            Log.d(Consts.TAG, "Map was loaded!");
            map.addMarker(new MarkerOptions()
                    .position(post.getGeoPoint().to())
                    .title(post.getAddressName()));
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(post.getGeoPoint().to(), 11.0f);
            map.animateCamera(yourLocation);
        });


        return v;
    }

    private void handleEditClick(View r) {
        if (!showEdit) {
            return;
        }

        Intent i = new Intent(r.getContext(), NewPostActivity.class);
        i.putExtra(ARG_POST, post);
        i.putExtra(ARG_POST_ID, postId);

        mStartForResult.launch(i);
    }

    private void handleGoBackToFeedClick(View r) {
        Navigation.findNavController(r).popBackStack();
    }

    private void handleCallClick(View r) {
        try {
            String phone = post.getUserPhone();
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phone));
            startActivity(callIntent);
        } catch (Exception e) {
            Log.w(Consts.TAG, "Failed to call", e);
            Toast.makeText(this.getContext(), "Failed to call.", Toast.LENGTH_SHORT).show();
        }

    }
}