package client_android.m_vote.feature.pilih_calon;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import client_android.m_vote.R;
import client_android.m_vote.model.CalonModel;

/**
 * Created by wahyuade on 16/07/17.
 */

public class ListCalonGridAdapter extends BaseAdapter{
    ArrayList<CalonModel> data_calon;
    Activity activity;
    String local;

    int[] presiden = {R.drawable.jokowi, R.drawable.prabowo};

    public ListCalonGridAdapter(ArrayList<CalonModel> data_calon, Activity activity, String local) {
        this.data_calon = data_calon;
        this.activity = activity;
        this.local = local;
    }

    @Override
    public int getCount() {
        return data_calon.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = new View(activity);
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(activity.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            itemView = inflater.inflate(R.layout.list_calon, null);
            TextView nama_calon = (TextView) itemView.findViewById(R.id.nama_calon);
            TextView nomor_nomor = (TextView) itemView.findViewById(R.id.nomor_urut);
            ImageView foto_calon = (ImageView)itemView.findViewById(R.id.foto_calon);

            nama_calon.setText(data_calon.get(i).getNama());
            nomor_nomor.setText(data_calon.get(i).getId());
            foto_calon.setImageResource(presiden[i]);
        } else {
            itemView = (View) view;
        }

        return itemView;
    }
}
