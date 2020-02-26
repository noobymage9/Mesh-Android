package com.mesh.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;
import com.mesh.message.Message;
import com.mesh.message.SpeechBubbleAdaptor;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private EditText searchBar;
    private ArrayList<Message> messages;
    private RecyclerView recyclerView;
    private View root;
    private SearchAdapter searchAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);
        root = inflater.inflate(R.layout.fragment_search, container, false);
        initialiseSearchBar(root);
        searchViewModel.getMessages(searchBar.getText().toString()).observe(getViewLifecycleOwner(), this::initialiseRecyclerView);

        return root;
    }

    public void initialiseRecyclerView(ArrayList<Message> messageList){
        this.messages = messageList;
        recyclerView = root.findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);
        searchAdapter = new SearchAdapter(messages, getContext());
        recyclerView.setAdapter(searchAdapter);
        if (messageList != null)
            resetRecyclerView();
    }

    public void resetRecyclerView(){
        recyclerView.setPadding(0, 0, 0, 0);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void initialiseSearchBar(View root) {
        searchBar = root.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchViewModel.loadMessages(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}