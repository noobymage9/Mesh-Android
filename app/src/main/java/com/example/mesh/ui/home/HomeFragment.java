package com.example.mesh.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mesh.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private List<ContactInfo> contactList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Recycler View
        RecyclerView recList = root.findViewById(R.id.contactList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        for (int i = 0; i < 15; i++) { // Generating sample contacts
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.default_icon);
            if (i == 0)
                contactList.add(new ContactInfo("SIM Han Wei", bm, "(Text Here)"));
            else
                contactList.add(new ContactInfo("Contact " + i, bm, "(Text Here)"));
        }

        ContactAdapter contactAdapter = new ContactAdapter(contactList);
        recList.setAdapter(contactAdapter);
        return root;
    }
}