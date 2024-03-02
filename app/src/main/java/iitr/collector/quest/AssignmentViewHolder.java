package iitr.collector.quest;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AssignmentViewHolder extends RecyclerView.ViewHolder {

    TextView courseTV,dueTV,nameTV;
    public AssignmentViewHolder(@NonNull View itemView) {
        super(itemView);
        courseTV = itemView.findViewById(R.id.courseTV);
        nameTV = itemView.findViewById(R.id.nameTV);
        dueTV = itemView.findViewById(R.id.dueTV);
    }
}
