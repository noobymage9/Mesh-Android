package com.mesh.ui.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;
import com.mesh.message.UserCollection;

import java.util.ArrayList;

public class SavedFragment extends Fragment {

    private SavedViewModel savedViewModel;
    private RecyclerView recyclerView;
    private UserGroupAdapter userGroupAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        savedViewModel =
                new ViewModelProvider(this).get(SavedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_saved, container, false);
        savedViewModel.getUserCollectons().observe(getViewLifecycleOwner(), userCollections -> initialiseRecyclerView(root, userCollections));
        return root;
    }

    private void initialiseRecyclerView(View root, ArrayList<UserCollection> userCollections) {
        recyclerView = root.findViewById(R.id.userCollectionList);
        recyclerView.setHasFixedSize(true);
        userGroupAdapter = new UserGroupAdapter(userCollections, this.getContext());
        recyclerView.setAdapter(userGroupAdapter);

    }
}