package BottomNavigation.ChildeNavigation.AppListAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jspm.R;
import com.google.protobuf.DescriptorProtos;

import java.util.ArrayList;

import HomeActivity.ChildeHomeActivity.ChildHomeActivity;
import HomeActivity.ParentHomeActivity.HomeActivity;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.MyViewHolder> {
    Context Mycontext;

    ArrayList<AppListModel> appListModels;

    public AppListAdapter(Context mycontext, ArrayList<AppListModel> appListModels) {
        Mycontext = mycontext;
        this.appListModels = appListModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(Mycontext).inflate(R.layout.app_list_card_view,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.appName.setText(appListModels.get(position).appName);
        holder.iconImg.setImageDrawable(appListModels.get(position).AppIcon);
       if (ChildHomeActivity.lockSharedPreference.getBoolean(appListModels.get(position).getAppName(),false))
       {
           Log.e("tagStatusRV","app lock status is taken from sharedprefence");
           holder.lockImg.setImageResource(R.drawable.baseline_lock_24);
       }
       else {
           Log.e("tagStatusRV","app lock status is taken from default");
           holder.lockImg.setImageResource(R.drawable.baseline_lock_open_24);
       }

    }

    @Override
    public int getItemCount() {
        return appListModels.size();
    }

    public void setFilteredList(ArrayList<AppListModel> filteredList) {
        this.appListModels = filteredList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iconImg,lockImg;
        TextView appName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImg = itemView.findViewById(R.id.appIconIV);
            lockImg = itemView.findViewById(R.id.lockImgIV);
            appName = itemView.findViewById(R.id.appNameTV);

        }
    }
}
