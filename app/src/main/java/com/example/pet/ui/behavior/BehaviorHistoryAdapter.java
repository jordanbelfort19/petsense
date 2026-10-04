package com.example.pet.ui.behavior;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pet.R;
import java.util.ArrayList;
import java.util.List;

public class BehaviorHistoryAdapter extends RecyclerView.Adapter<BehaviorHistoryAdapter.ViewHolder> {
    
    private List<BehaviorAnalysisItem> items = new ArrayList<>();

    public void setItems(List<BehaviorAnalysisItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_behavior_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BehaviorAnalysisItem item = items.get(position);
        holder.tvDate.setText(item.getDate());
        holder.tvEmoji.setText(item.getEmoji());
        holder.tvBehavior.setText(item.getBehavior());
        holder.tvConfidence.setText(item.getConfidence());
        holder.tvTime.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvEmoji, tvBehavior, tvConfidence, tvTime;
        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvEmoji = itemView.findViewById(R.id.tvHistoryEmoji);
            tvBehavior = itemView.findViewById(R.id.tvHistoryBehavior);
            tvConfidence = itemView.findViewById(R.id.tvHistoryConfidence);
            tvTime = itemView.findViewById(R.id.tvHistoryTime);
        }
    }
}
