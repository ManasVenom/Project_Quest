package iitr.collector.quest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentViewHolder> {

    Context context;
    List<Map<String, Object>> assignments;
    public AssignmentAdapter(Context context, List<Map<String, Object>> assignment) {
        this.context = context;
        this.assignments = assignment;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssignmentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        holder.nameTV.setText((CharSequence) assignments.get(position).get("name"));
        holder.dueTV.setText((CharSequence) assignments.get(position).get("due"));
        holder.courseTV.setText((CharSequence) assignments.get(position).get("course"));
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }
}
