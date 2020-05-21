package com.bi183.tedja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bi183.tedja.services.ApiClient;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class TampilActivity extends AppCompatActivity {

    private ImageView ivCover;
    private TextView tvJudulLagu, tvAlbumLagu, tvArtis, tvTahun, tvNegara, tvPublisher, tvGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampil);

        ivCover = findViewById(R.id.iv_cover);
        tvJudulLagu = findViewById(R.id.tvJudulLagu);
        tvAlbumLagu = findViewById(R.id.tvAlbumLagu);
        tvArtis = findViewById(R.id.tvArtis);
        tvTahun = findViewById(R.id.tvTahun);
        tvNegara = findViewById(R.id.tvNegara);
        tvPublisher = findViewById(R.id.tvPublisher);
        tvGenre = findViewById(R.id.tvGenre);

        Intent receivedData = getIntent();
        Bundle data = receivedData.getExtras();
        tvJudulLagu.setText(data.getString("JUDUL_LAGU"));
        tvAlbumLagu.setText(data.getString("ALBUM_LAGU"));
        tvArtis.setText(data.getString("ARTIS"));
        tvTahun.setText(data.getString("TAHUN"));
        tvNegara.setText(data.getString("NEGARA"));
        tvPublisher.setText(data.getString("PUBLISHER"));
        tvGenre.setText(data.getString("GENRE"));
        String imgName = data.getString("COVER");
        if (!imgName.equals(null)) {
            Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
            builder.downloader(new OkHttp3Downloader(getApplicationContext()));
            builder.build().load(ApiClient.IMAGE_URL + imgName)
                    .placeholder(R.drawable.cover_default)
                    .error(R.drawable.cover_default)
                    .into(ivCover);
        }
        ivCover.setContentDescription(imgName);
    }
}
