package com.github.martoreto.aademo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class WifiListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<WifiListModel> wifiList = new ArrayList<>();
    private ClickInterface clickInterface;
    private int VIEW_TYPE_ITEM = 1;
    private int VIEW_TYPE_ADD = 2;

    public WifiListAdapter(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_wifi, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_add_wifi, parent, false);
            return new ViewHolderAddNetwork(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewholder, int position) {
        if (viewholder.getItemViewType() == VIEW_TYPE_ITEM) {
            ViewHolder holder = (ViewHolder) viewholder;
            WifiListModel wifiListModel = wifiList.get(position);
            holder.tvWifiName.setText(wifiListModel.getSSID());
            holder.imvStrength.setImageDrawable(context.getDrawable(wifiListModel.getSignalDrwable()));
            if (wifiListModel.isSecurityType()) {
                holder.imvLock.setVisibility(View.VISIBLE);
            } else {
                holder.imvLock.setVisibility(View.INVISIBLE);
            }

            if (wifiListModel.isConnected()) {
                holder.tvStatus.setVisibility(View.VISIBLE);
            } else {
                holder.tvStatus.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(view -> clickInterface.onClickConnectWifi(wifiListModel));

            holder.itemView.setOnLongClickListener(view -> {
                if (wifiListModel.isConnected()) {
                    clickInterface.forgotPassword();
                }
                return true;
            });
        } else {
            ViewHolderAddNetwork holder = (ViewHolderAddNetwork) viewholder;
            holder.llAddNetwork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickInterface.addWifiNetwork();
                }
            });
        }
    }

    public void clearList() {
        if (wifiList != null) {
            wifiList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return wifiList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position < wifiList.size() ? VIEW_TYPE_ITEM : VIEW_TYPE_ADD;
    }

    public void setData(ArrayList<WifiListModel> wifiList) {
        if (!Utilities.isNullOrEmpty(wifiList)) {
            Collections.sort(wifiList, (v1, v2) -> v2.getSignalStrength() - v1.getSignalStrength());
            this.wifiList = wifiList;
            notifyDataSetChanged();
        }
    }

    public interface ClickInterface {
        void forgotPassword();

        void onClickConnectWifi(WifiListModel wifiListModel);

        void addWifiNetwork();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imvStrength, imvLock;
        TextView tvWifiName, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWifiName = itemView.findViewById(R.id.tvWifiName);
            imvStrength = itemView.findViewById(R.id.imvStrength);
            imvLock = itemView.findViewById(R.id.imvLock);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    public static class ViewHolderAddNetwork extends RecyclerView.ViewHolder {

        LinearLayout llAddNetwork;

        public ViewHolderAddNetwork(@NonNull View itemView) {
            super(itemView);
            llAddNetwork = itemView.findViewById(R.id.llAddNetwork);
        }
    }
}

