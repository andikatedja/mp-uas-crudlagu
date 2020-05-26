package com.bi183.tedja;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.bi183.tedja.adapter.LaguAdapter;
import com.bi183.tedja.model.Lagu;
import com.bi183.tedja.model.ResponseData;
import com.bi183.tedja.services.ApiClient;
import com.bi183.tedja.services.ApiLagu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private LaguAdapter laguAdapter;
    private RecyclerView rvShowLagu;
    private ProgressDialog progressDialog;
    private List<Lagu> dataLagu = new ArrayList<>();
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvShowLagu = findViewById(R.id.rv_tampil);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        fab = findViewById(R.id.fab_tambah);
        progressDialog = new ProgressDialog(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showLagu();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openInput = new Intent(getApplicationContext(), InputActivity.class);
                openInput.putExtra("OPERATION", "insert");
                startActivityForResult(openInput, 1);
            }
        });

        if (savedInstanceState == null) {
            progressDialog.setMessage("Memuat lagu...");
            showLagu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                laguAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                showLagu();
            }
        }
    }

    private void showLagu() {
        progressDialog.show();
        ApiLagu api = ApiClient.getRetrofitInstance().create(ApiLagu.class);
        Call<ResponseData> call = api.getData();

        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                String value = response.body().getValue();
                if (value.equals("1")) {
                    dataLagu = response.body().getResult();
                    laguAdapter = new LaguAdapter(dataLagu, getApplicationContext());
                    layoutManager = new LinearLayoutManager(MainActivity.this);
                    rvShowLagu.setLayoutManager(layoutManager);
                    rvShowLagu.setAdapter(laguAdapter);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }
}
