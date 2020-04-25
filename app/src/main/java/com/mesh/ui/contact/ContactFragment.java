package com.mesh.ui.contact;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.mesh.R;
import com.mesh.ui.home.Contact;

import java.util.ArrayList;

public class ContactFragment extends Fragment {
    public static final String CONTACT_PARCEL = "Contact Parcel";


    private ContactViewModel contactViewModel;
    private EditText searchBar;
    private ArrayList<Contact> contacts;
    private RecyclerView recyclerView;
    private View root;
    private ContactAdapter contactAdapter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contactViewModel =
                new ViewModelProvider(this).get(ContactViewModel.class);
        root = inflater.inflate(R.layout.fragment_search, container, false);
        initialiseSearchBar(root);
        contactViewModel.getContacts(searchBar.getText().toString()).observe(getViewLifecycleOwner(), this::initialiseRecyclerView);

        return root;
    }
    public void initialiseRecyclerView(ArrayList<Contact> contactList){
        this.contacts = contactList;
        recyclerView = root.findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);
        contactAdapter = new ContactAdapter(contactList, this);
        recyclerView.setAdapter(contactAdapter);
        if (contactList != null)
            resetRecyclerView();
    }

    public void resetRecyclerView(){
        recyclerView.setPadding(0, 0, 0, 0);
    }


    private void initialiseSearchBar(View root) {
        searchBar = root.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactViewModel.loadContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    hideKeyboard();
                }
            }
        });
    }
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
    }

}