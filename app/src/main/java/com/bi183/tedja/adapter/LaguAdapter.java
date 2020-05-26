package com.bi183.tedja.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bi183.tedja.InputActivity;
import com.bi183.tedja.R;
import com.bi183.tedja.TampilActivity;
import com.bi183.tedja.model.Lagu;
import com.bi183.tedja.services.ApiClient;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LaguAdapter extends RecyclerView.Adapter<LaguAdapter.LaguViewHolder> implements Filterable {

    private List<Lagu> dataLagu;
    private List<Lagu> dataLaguFull;
    private Context context;

    public LaguAdapter(List<Lagu> dataLagu, Context context) {
        this.dataLagu = dataLagu;
        this.context = context;
        this.dataLaguFull = new ArrayList<>(dataLagu);
    }

    @NonNull
    @Override
    public LaguViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_lagu, parent, false);
        return new LaguViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaguViewHolder holder, int position) {
        Lagu tempLagu = dataLagu.get(position);
        holder.id = tempLagu.getId();
        holder.tvJudulLagu.setText(tempLagu.getJudul_lagu());
        holder.tvArtis.setText(tempLagu.getArtis());
        holder.albumLagu = tempLagu.getAlbum_lagu();
        holder.tahun = tempLagu.getTahun();
        holder.negara = tempLagu.getNegara();
        holder.publisher = tempLagu.getPublisher();
        holder.genre = tempLagu.getGenre();
        String imgName = tempLagu.getCover();
        if (!imgName.equals(null)) {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.downloader(new OkHttp3Downloader(context));
            builder.build().load(ApiClient.IMAGE_URL + imgName)
                    .placeholder(R.drawable.cover_default)
                    .error(R.drawable.cover_default)
                    .into(holder.iv_cover);
        }
        holder.iv_cover.setContentDescription(tempLagu.getCover());
    }

    @Override
    public int getItemCount() {
        return dataLagu.size();
    }

    @Override
    public Filter getFilter() {
        return laguFilter;
    }

    private Filter laguFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Lagu> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataLaguFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Lagu lagu : dataLaguFull) {
                    if (lagu.getJudul_lagu().toLowerCase().contains(filterPattern) || lagu.getArtis().toLowerCase().contains(filterPattern)) {
                        filteredList.add(lagu);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataLagu.clear();
            dataLagu.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class LaguViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvJudulLagu, tvArtis;
        private ImageView iv_cover;
        private int id;
        private String albumLagu, tahun, negara, publisher, genre;

        public LaguViewHolder(@NonNull View itemView) {
            super(itemView);

            tvJudulLagu = itemView.findViewById(R.id.tvJudulLagu);
            tvArtis = itemView.findViewById(R.id.tvArtis);
            iv_cover = itemView.findViewById(R.id.iv_cover);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent openLagu = new Intent(context, TampilActivity.class);
            openLagu.putExtra("JUDUL_LAGU", tvJudulLagu.getText().toString());
            openLagu.putExtra("ALBUM_LAGU", albumLagu);
            openLagu.putExtra("ARTIS", tvArtis.getText().toString());
            openLagu.putExtra("TAHUN", tahun);
            openLagu.putExtra("NEGARA", negara);
            openLagu.putExtra("PUBLISHER", publisher);
            openLagu.putExtra("GENRE", genre);
            openLagu.putExtra("COVER", iv_cover.getContentDescription());
            itemView.getContext().startActivity(openLagu);
        }

        @Override
        public boolean onLongClick(View v) {
            Intent openLagu = new Intent(context, InputActivity.class);
            openLagu.putExtra("OPERATION", "update");
            openLagu.putExtra("ID", id);
            openLagu.putExtra("JUDUL_LAGU", tvJudulLagu.getText().toString());
            openLagu.putExtra("ALBUM_LAGU", albumLagu);
            openLagu.putExtra("ARTIS", tvArtis.getText().toString());
            openLagu.putExtra("TAHUN", tahun);
            openLagu.putExtra("NEGARA", negara);
            openLagu.putExtra("PUBLISHER", publisher);
            openLagu.putExtra("GENRE", genre);
            openLagu.putExtra("COVER", iv_cover.getContentDescription());
            ((Activity) context).startActivityForResult(openLagu, 1);
            return false;
        }
    }
}
