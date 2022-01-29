package com.colman.bar.admoni.a3rs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.colman.bar.admoni.a3rs.models.Post;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PostViewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_POST_ID = "postId";
    public static final String ARG_POST = "post";


    private String postId;
    private Post post;

    public PostViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
            post = (Post) getArguments().getSerializable(ARG_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_view, container, false);
        v.findViewById(R.id.goBackToFeedButton).setOnClickListener(this::handleGoBackToFeedClick);
        TextView textEdit = v.findViewById(R.id.postViewTextView);
        if (post != null) {
            textEdit.setText(post.getTitle() + " - " + post.getSubTitle());
        }
        return v;
    }

    private void handleGoBackToFeedClick(View r) {
        Navigation.findNavController(r).popBackStack();
    }
}