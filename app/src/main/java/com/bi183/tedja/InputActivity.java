package com.bi183.tedja;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bi183.tedja.model.ResponseData;
import com.bi183.tedja.services.ApiClient;
import com.bi183.tedja.services.ApiLagu;
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

    private EditText editJudulLagu, editAlbumLagu, editArtis, editTahun, editNegara, editPublisher, editGenre;
    private ImageView iv_cover;
    private Button btnSave;
    private Bitmap selectedImage;

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
                pickImage();
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
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
        String sjudul_lagu = editJudulLagu.getText().toString();
        String salbum_lagu = editAlbumLagu.getText().toString();
        String sartis = editArtis.getText().toString();
        String stahun = editTahun.getText().toString();
        String snegara = editNegara.getText().toString();
        String spublisher = editPublisher.getText().toString();
        String sgenre = editGenre.getText().toString();

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
                    finish();
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
