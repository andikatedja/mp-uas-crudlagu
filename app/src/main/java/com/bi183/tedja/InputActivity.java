package com.bi183.tedja;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bi183.tedja.model.ResponseData;
import com.bi183.tedja.services.ApiClient;
import com.bi183.tedja.services.ApiLagu;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputActivity extends AppCompatActivity {

    private EditText editJudulLagu, editAlbumLagu, editArtis, editTahun, editNegara, editPublisher;
    private ImageView iv_cover;
    private Button btnSave;
    private Bitmap selectedImage;
    private Spinner spGenre;
    private ProgressDialog progressDialog;
    private String genre, imgName;
    private int id = 0;
    private boolean update = false;

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
        btnSave = findViewById(R.id.btnSave);
        spGenre = findViewById(R.id.spGenre);
        progressDialog = new ProgressDialog(this);

        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(this, R.array.genre, android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenre.setAdapter(spAdapter);

        Intent receivedData = getIntent();
        Bundle data = receivedData.getExtras();
        if (data.getString("OPERATION").equals("insert")) {
            update = false;
        } else {
            update = true;
            id = data.getInt("ID");
            editJudulLagu.setText(data.getString("JUDUL_LAGU"));
            editAlbumLagu.setText(data.getString("ALBUM_LAGU"));
            editArtis.setText(data.getString("ARTIS"));
            editTahun.setText(data.getString("TAHUN"));
            editNegara.setText(data.getString("NEGARA"));
            editPublisher.setText(data.getString("PUBLISHER"));
            genre = data.getString("GENRE");
            int spinnerPosition = spAdapter.getPosition(genre);
            spGenre.setSelection(spinnerPosition);
            imgName = data.getString("COVER");
            if (!imgName.equals(null)) {
                Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
                builder.downloader(new OkHttp3Downloader(getApplicationContext()));
                builder.build().load(ApiClient.IMAGE_URL + imgName)
                        .placeholder(R.drawable.cover_default)
                        .error(R.drawable.cover_default)
                        .into(iv_cover);
            }
            iv_cover.setContentDescription(imgName);
        }

        iv_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void pickImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    Uri cover = result.getUri();
                    InputStream imageStream = getContentResolver().openInputStream(cover);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    iv_cover.setImageBitmap(selectedImage);
                } catch (FileNotFoundException er) {
                    er.printStackTrace();
                    Toast.makeText(this, "Ada kesalahan dalam pemilihan gambar", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Anda belum memilih gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveData() {
        progressDialog.setMessage("Menyimpan lagu...");
        progressDialog.show();

        MultipartBody.Part part;
        //Cek apakah ada gambar yang dipilih
        if (selectedImage != null) {
            File file = createTempFile(selectedImage);
            // Create a request body with file and image media type
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
            // Create MultipartBody.Part using file request-body,file name and part name
            part = MultipartBody.Part.createFormData("cover", file.getName(), fileReqBody);
        } else {
            // Create a request body with file and image media type
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("text/plain"), "");
            // Create MultipartBody.Part using file request-body,file name and part name
            part = MultipartBody.Part.createFormData("cover", "", fileReqBody);
        }

        //Create request body with text media type
        RequestBody judul_lagu = RequestBody.create(MediaType.parse("text/plain"), editJudulLagu.getText().toString());
        RequestBody album_lagu = RequestBody.create(MediaType.parse("text/plain"), editAlbumLagu.getText().toString());
        RequestBody artis = RequestBody.create(MediaType.parse("text/plain"), editArtis.getText().toString());
        RequestBody tahun = RequestBody.create(MediaType.parse("text/plain"), editTahun.getText().toString());
        RequestBody negara = RequestBody.create(MediaType.parse("text/plain"), editNegara.getText().toString());
        RequestBody publisher = RequestBody.create(MediaType.parse("text/plain"), editPublisher.getText().toString());
        RequestBody genre = RequestBody.create(MediaType.parse("text/plain"), spGenre.getSelectedItem().toString());

        ApiLagu api = ApiClient.getRetrofitInstance().create(ApiLagu.class);
        Call<ResponseData> call;
        call = api.addData(judul_lagu, album_lagu, artis, tahun, negara, publisher, genre, part);

        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                progressDialog.dismiss();
                String value = response.body().getValue();
                String message = response.body().getMessage();
                if(value.equals("1")) {
                    Toast.makeText(InputActivity.this, "SUKSES: " + message, Toast.LENGTH_LONG).show();
                    finish();
                } else{
                    Toast.makeText(InputActivity.this, "GAGAL: " + message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(InputActivity.this, "Gagal menghubungi server...", Toast.LENGTH_LONG).show();
                t.printStackTrace();
                Log.d("Input Data Error", t.toString());
            }
        });
    }

    private File createTempFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() +"_image.jpg");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,100, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
