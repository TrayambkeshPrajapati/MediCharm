package com.example.medicharm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicharm.database.Reminder;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    interface OnItemClickListener {
        void onEdit(Reminder reminder);
        void onDelete(Reminder reminder);
    }

    private List<Reminder> reminders;
    private Context context;
    private OnItemClickListener listener;

    public ReminderAdapter(Context context, List<Reminder> reminders, OnItemClickListener listener) {
        this.context = context;
        this.reminders = reminders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.tvName.setText(reminder.medicineName);
        holder.tvDosage.setText(reminder.dosage + " at " + reminder.time);

        holder.itemView.setOnClickListener(v -> listener.onEdit(reminder));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(reminder);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDosage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMedicineName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
        }
    }
}

