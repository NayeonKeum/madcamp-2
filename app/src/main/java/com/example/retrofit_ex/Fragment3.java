package com.example.retrofit_ex;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Fragment3 extends Fragment{
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://172.10.18.137:80";
    String name;
    TextView allpost;
    Button showBtn;
    private RecyclerView postRV;
    private PostVAdapter postRVAdapter;
    ArrayList<PostInfo> Postlist;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent= getActivity().getIntent();
        name=intent.getStringExtra("name");
        Log.d("내 이름이 모라구?", name);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_3, container, false);
        return view;
    }
    public void onResume() {
        super.onResume();
        Postlist = new ArrayList<>();
    }


    @Override
    public void onStart() {
        super.onStart();

        allpost = getView().findViewById(R.id.allpost);
        showBtn = getView().findViewById(R.id.showBtn);

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<ResponseBody> call = retrofitInterface.getPost();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //List<JSONObject> result= response.body();
                        //showPostResult
                        if (response.code() == 500) {
                            Toast.makeText(getActivity(),
                                    "database has failed", Toast.LENGTH_LONG).show();
                        }
                        else if (response.code() == 200){
                            Toast.makeText(getActivity(),
                                    "Uploaded successfully", Toast.LENGTH_LONG).show();

                            Gson gson = new Gson();
                            try {
                                String jsonString= response.body().string();
                                List<PostInfo> list = gson.fromJson(jsonString, new TypeToken<List<PostInfo>>(){}.getType());
                                //allpost.setText(list.toString());
                                Postlist=new ArrayList<PostInfo>();
                                for (int i=0;i<list.size();i++){
                                    Postlist.add(new PostInfo(list.get(i).getName(),list.get(i).getTitle(),list.get(i).getContent()));
                                }
                                String total="";
                                for (int i=0;i<Postlist.size();i++){
                                    total+="name : "+Postlist.get(i).getName()+"\n"
                                            +"title : "+Postlist.get(i).getTitle()+"\n"
                                            +"content : "+Postlist.get(i).getContent()+"\n\n";
                                }
                                //allpost.setText(total);

                                ////////////////////////
                                postRV = getView().findViewById(R.id.idRVContacts);
                                //on below line we are setting layout mnager.
                                postRV.setLayoutManager(new LinearLayoutManager(getContext()));
                                postRV.addItemDecoration(new DividerItemDecoration(view.getContext(), 1));

                                postRVAdapter = new PostVAdapter(Postlist, name);
                                //on below line we are setting adapter to our recycler view.
                                postRV.setAdapter(postRVAdapter);




                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });



            }
        });
    }

}



