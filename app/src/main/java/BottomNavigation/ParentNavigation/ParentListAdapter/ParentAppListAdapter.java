package BottomNavigation.ParentNavigation.ParentListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jspm.R;

import java.util.ArrayList;

import BottomNavigation.ChildeNavigation.AppListAdapter.AppListModel;

public class ParentAppListAdapter extends RecyclerView.Adapter<ParentAppListAdapter.ParentViewHolder> {

    Context context;
    ArrayList<ParentAppListModel> arrayList;

    public ParentAppListAdapter(Context context, ArrayList<ParentAppListModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_list_card_view,parent,false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        holder.appName.setText(arrayList.get(position).getAppName());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ParentViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iconImg,lockImg;
        TextView appName;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImg = itemView.findViewById(R.id.appIconIV);
            lockImg = itemView.findViewById(R.id.lockImgIV);
            appName = itemView.findViewById(R.id.appNameTV);

        }
    }
}
