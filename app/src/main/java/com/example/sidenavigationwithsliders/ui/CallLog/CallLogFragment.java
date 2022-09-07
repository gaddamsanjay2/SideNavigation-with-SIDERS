package com.example.sidenavigationwithsliders.ui.CallLog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sidenavigationwithsliders.R;
import com.example.sidenavigationwithsliders.ui.contacts.SliderAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CallLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CallLogFragment extends Fragment {
    ViewPager2 viewPager2;
    SliderAdapter sliderAdapter;
    int[] drawbles= new int[]{R.drawable.img_1,R.drawable.img_2,R.drawable.img_3};

    RecyclerView recyclerView_calllog;
    CallLogModel callLogModel;
    CallLogAdapter callLogAdapter;
    List<CallLogModel> callLogModelList=new ArrayList<>();

    //Content Providers
    Cursor cursor;
    ContentResolver contentResolver;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CallLogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CallLogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CallLogFragment newInstance(String param1, String param2) {
        CallLogFragment fragment = new CallLogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root= inflater.inflate(R.layout.fragment_call_log, container, false);
        viewPager2=root.findViewById(R.id.viewpage2);
        sliderAdapter=new SliderAdapter(getContext(),drawbles);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager2.setAdapter(sliderAdapter);

        recyclerView_calllog=root.findViewById(R.id.recyclerView);
        recyclerView_calllog.setHasFixedSize(true);
        recyclerView_calllog.setLayoutManager(new LinearLayoutManager(getContext()));

        callLogAdapter=new CallLogAdapter(getContext(),callLogModelList);
        recyclerView_calllog.setAdapter(callLogAdapter);



        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CALL_LOG},201);
        }
        else
        {
            loadallCallLogs();
        }




        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(8));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float v=1-Math.abs(position);
                page.setScaleY(0.8f + v*0.2f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);


    return root;

    }

    private void loadallCallLogs() {
        contentResolver=getContext().getContentResolver();

        Uri uri= CallLog.Calls.CONTENT_URI;
        String[] projections={CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE};
        String selection=null;
        String[] args=null;
        String order=""+ CallLog.Calls.DATE+" DESC";

        cursor=contentResolver.query(uri,projections,selection,args,order);

        if(cursor.getCount()>0&&cursor!=null)
        {
            while (cursor.moveToNext())
            {
                @SuppressLint("Range") String name=cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                @SuppressLint("Range") String number=cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                @SuppressLint("Range") String time=cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                @SuppressLint("Range") String duration=cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                @SuppressLint("Range") String type=cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));


                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy =   HH:mm");
                String dateString = formatter.format(new Date(Long.parseLong(time)));

                callLogModel=new CallLogModel(name,number,dateString,duration,type);
                callLogModelList.add(callLogModel);
                callLogAdapter.notifyDataSetChanged();
            }

        }
        else
        {
            Toast.makeText(getContext(), "No Call Logs Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 201:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    loadallCallLogs();
                }
                else
                {
                    Toast.makeText(getContext(), "Call Log Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }


    }
}