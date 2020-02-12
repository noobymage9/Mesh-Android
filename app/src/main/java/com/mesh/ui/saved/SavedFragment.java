package com.mesh.ui.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.mesh.R;
import com.mesh.message.UserCollection;

import java.util.ArrayList;

public class SavedFragment extends Fragment {

    private SavedViewModel favouriteViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favouriteViewModel =
                ViewModelProviders.of(this).get(SavedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favourite, container, false);
        favouriteViewModel.getUserCollectons().observe(this, this::initialiseRecyclerView);
        return root;
    }

    private void initialiseRecyclerView(ArrayList<UserCollection> s) {

    }
}