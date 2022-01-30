package com.colman.bar.admoni.a3rs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.colman.bar.admoni.a3rs.models.Post;
import com.google.firebase.auth.FirebaseAuth;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
            post = (Post) getArguments().getSerializable(ARG_POST);
            showEdit = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(post.getUserUid());
        }
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

        TextView postViewTitleTextView = v.findViewById(R.id.postViewTitleTextView);
        TextView postViewSubtitleTextView = v.findViewById(R.id.postViewSubtitleTextView);
        TextView postViewDescriptionTextView = v.findViewById(R.id.postViewDescriptionTextView);
        TextView postViewUserEditText = v.findViewById(R.id.postViewUserTextView);

        postViewTitleTextView.setText(post.getTitle());
        postViewSubtitleTextView.setText(post.getSubTitle());
        postViewDescriptionTextView.setText(post.getDescription());
        postViewUserEditText.setText(post.getUserName());

        return v;
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