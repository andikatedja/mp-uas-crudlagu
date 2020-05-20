package com.bi183.tedja;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bi183.tedja.model.ResponseData;
import com.bi183.tedja.services.ApiClient;
import com.bi183.tedja.services.ApiLagu;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputActivity extends AppCompatActivity {

    private EditText editJudulLagu, editAlbumLagu, editArtis, editTahun, editNegara, editPublisher, editGenre;
    private ImageView iv_cover;
    private Uri cover;
    private Button btnSave;
    private String imgDecodableString;

    private static final int GALLERY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        iv_cover = findViewById(R.id.iv_cover);
        editJudulLagu = findViewById(R.id.editJudulLagu);
        editAlbumLagu = findViewById(R.id.editAlbumLagu);
        editArtis = findViewById(R.id.editArtis);
        editTahun = findViewById(R.id.editTahun);
        editNegara = findViewById(R.id.editNegara);
        editPublisher = findViewById(R.id.editPublisher);
        editJudulLagu = findViewById(R.id.editJudulLagu);
        editGenre = findViewById(R.id.editGenre);
        btnSave = findViewById(R.id.btnSave);

        iv_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE){
            //data.getData returns the content URI for the selected Image
            cover = data.getData();
//            iv_cover.setImageURI(cover);

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // Get the cursor
            Cursor cursor = getContentResolver().query(cover, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            //Get the column index of MediaStore.Images.Media.DATA
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //Gets the String value in the column
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            // Set the Image in ImageView after decoding the String
            iv_cover.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
        }
    }

    private void saveData() {
        String sjudul_lagu = editJudulLagu.getText().toString();
        String salbum_lagu = editAlbumLagu.getText().toString();
        String sartis = editArtis.getText().toString();
        String stahun = editTahun.getText().toString();
        String snegara = editNegara.getText().toString();
        String spublisher = editPublisher.getText().toString();
        String sgenre = editGenre.getText().toString();

        String path = getPath(cover);
        File file = new File(path);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("cover", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody judul_lagu = RequestBody.create(MediaType.parse("text/plain"), sjudul_lagu);
        RequestBody album_lagu = RequestBody.create(MediaType.parse("text/plain"), salbum_lagu);
        RequestBody artis = RequestBody.create(MediaType.parse("text/plain"), sartis);
        RequestBody tahun = RequestBody.create(MediaType.parse("text/plain"), stahun);
        RequestBody negara = RequestBody.create(MediaType.parse("text/plain"), snegara);
        RequestBody publisher = RequestBody.create(MediaType.parse("text/plain"), spublisher);
        RequestBody genre = RequestBody.create(MediaType.parse("text/plain"), sgenre);

        ApiLagu api = ApiClient.getRetrofitInstance().create(ApiLagu.class);
        Call<ResponseData> call;
        call = api.addData(judul_lagu, album_lagu, artis, tahun, negara, publisher, genre, part);

        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                if(value.equals("1")) {
                    Toast.makeText(InputActivity.this, "SUKSES: " + message, Toast.LENGTH_LONG).show();
//                    finish();
                } else{
                    Toast.makeText(InputActivity.this, "GAGAL: " + message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Toast.makeText(InputActivity.this, "Gagal menghubungi server...", Toast.LENGTH_LONG).show();
                t.printStackTrace();
                Log.d("Input Data Error", t.toString());
            }
        });
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
