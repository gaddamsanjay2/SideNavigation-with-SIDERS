package com.example.sidenavigationwithsliders.ui.contacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import com.example.sidenavigationwithsliders.R;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {
    RecyclerView recycler;
    ContactModel contactModel;
    ContactAdapter contactAdapter;
    List<ContactModel> contactModelList = new ArrayList<>();
    List<ContactModel> filterlist = new ArrayList<>();
    Cursor cursor;
    ContentResolver contentResolver;
    SearchView searchView;
    TextView count;

    ViewPager2 viewPager2;
    SliderAdapter sliderAdapter;
    int[] drawbles = new int[]{R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4};



    public ContactFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_contact, container, false);
        viewPager2 = root.findViewById(R.id.viewpage2);
        sliderAdapter = new SliderAdapter(getContext(), drawbles);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager2.setAdapter(sliderAdapter);


        recycler = root.findViewById(R.id.recycler_contacts);
        searchView = root.findViewById(R.id.search_contacts);
        count = root.findViewById(R.id.count_contacts);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(8));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {

                float v = 1 - Math.abs(position);
                page.setScaleY(0.8f + v * 0.2f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 101);
        } else {
            readAllContacts();
            searchView.setQueryHint("Search among" + " " + contactModelList.size() + " " + "contacts(s)");

        }
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterlist.clear();

                if (newText.toString().isEmpty()) {
                    count.setVisibility(View.GONE);

                    recycler.setAdapter(new ContactAdapter(getContext(), contactModelList));
                    contactAdapter.notifyDataSetChanged();


                } else {

                    Filter(newText.toString());
//                    recycler.setAdapter(new ContactAdapter(getContext(), filterlist));
//                    contactAdapter.notifyDataSetChanged();
                }
                return true;
            }


        });
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        contactAdapter = new ContactAdapter(getContext(), contactModelList);
        recycler.setAdapter(contactAdapter);

        return root;
    }

    private void readAllContacts() {
        contactModelList.clear();

        contentResolver = getContext().getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; //Uniform Resource identifiers
        String[] projections = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Photo.PHOTO_URI};
        String selection = null; //Selection for row wise Search
        String[] args = null;
        String order = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " asc";

        cursor = contentResolver.query(uri, projections, selection, args, order);

        if (cursor.getCount() > 0 && cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                @SuppressLint("Range") String photo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));

                contactModel = new ContactModel(name, number, photo);
                contactModelList.add(contactModel);


            }
        } else {
            Toast.makeText(getContext(), "No contacts found in your phone ", Toast.LENGTH_SHORT).show();
        }



    }


    private void Filter(String text) {

        count.setVisibility(View.VISIBLE);


        for (ContactModel post : contactModelList) {
            if (post.getName().toLowerCase().contains(text.toLowerCase())) {
                filterlist.add(post);
            }
        }

        recycler.setAdapter(new ContactAdapter(getContext(), filterlist));
        contactAdapter.notifyDataSetChanged();
        count.setText(" " + filterlist.size() + " " + "CONTACTS FOUND");


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readAllContacts();
                } else {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}



