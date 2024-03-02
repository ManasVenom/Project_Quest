package iitr.collector.quest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    private Context context;
    private DatabaseReference db;
    private SharedPreferences userdata;
    private String enroll;
    private String name;
    private String image;
    private String[] greetings;
    private String[] quotes;
    private ImageView profile_pic;
    private TextView greeting;
    private TextView quoteTV;
    private ImageView notification_iv;
    private TextView motivation_tv;
    private RecyclerView rv_day_glance;
    private RecyclerView rv_u_quests;
    private RecyclerView rv_assignments;
    private RecyclerView rv_events;
    private Random random;
    List<Map<String, Object>> assignmentList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize variables here
        context = getActivity();
        userdata = context.getSharedPreferences(getString(R.string.userdata_file), Context.MODE_PRIVATE);
        db = FirebaseDatabase.getInstance().getReference();
        enroll = userdata.getString("enroll", "23117110");
        name = userdata.getString("name", "Rakshit");
        image = userdata.getString("image", "https://channeli.in/media/kernel/display_pictures/2445eff0-d7e4-4780-9dac-63ab7f872134.jpg");
        greetings = new String[]{"Hello, ", "Hi, ", "Hey, ", "Greetings, ", "Welcome, ", "Yo, ", "Hi there, ", "Hey, ",
                "Salutations, ", "Howdy, ", "Hola, ", "Greetings, ", "Bonjour, ", "Namaste, ",
                "Ciao, ", "Sup, ", "Aloha, ", "How's it going? ", "What's up? ", "Good day, ",
                "Nice to see you, ", "Hiya, ", "Hey, friend, ", "Hey there, ", "Hello there, "};
        quotes = new String[]{
                "Believe you can and you're halfway there.",
                "The only way to do great work is to love what you do.",
                "Don't watch the clock; do what it does. Keep going.",
                "Success is not final, failure is not fatal:\nIt is the courage to continue that counts.",
                "Your time is limited, don't waste it living someone else's life.",
                "Believe in yourself and all that you are.\nKnow that there is something inside you that is greater than any obstacle.",
                "Success is stumbling from failure to failure with no loss of enthusiasm.",
                "The only limit to our realization of tomorrow will be our doubts of today.",
                "If you are not willing to risk the usual,\nyou will have to settle for the ordinary.",
                "It does not matter how slowly you go, as long as you do not stop.",
                "The only person you are destined to become is the person you decide to be.",
                "The future belongs to those who believe in the beauty of their dreams.",
                "Don't be pushed around by the fears in your mind.\nBe led by the dreams in your heart.",
                "Success usually comes to those who are too busy to be looking for it.",
                "The only place where success comes before work is in the dictionary.",
                "The only thing standing between you and your goal is the story you keep telling yourself\nas to why you can't achieve it.",
                "The journey of a thousand miles begins with one step.",
                "I find that the harder I work, the more luck I seem to have.",
                "The only limit to our realization of tomorrow will be our doubts of today.",
                "Don't be afraid to give up the good to go for the great.",
                "Success is not in what you have, but who you are.",
                "The only way to achieve the impossible is to believe it is possible.",
                "Don't let yesterday take up too much of today.",
                "Your life does not get better by chance, it gets better by change.",
                "I never dreamed about success, I worked for it.",
                "The only thing standing between you and your goal is the story you keep telling yourself\nas to why you can't achieve it.",
                "You are never too old to set another goal or to dream a new dream.",
                "Success is not final, failure is not fatal:\nIt is the courage to continue that counts.",
                "Don't be pushed around by the fears in your mind.\nBe led by the dreams in your heart.",
                "Your time is limited, don't waste it living someone else's life.",
                "Success is not in what you have, but who you are."
        };
        quoteTV = view.findViewById(R.id.tv_motivation);
        profile_pic = view.findViewById(R.id.profile_pic);
        greeting = view.findViewById(R.id.greeting_tv);
        notification_iv = view.findViewById(R.id.notification_button);
        motivation_tv = view.findViewById(R.id.tv_motivation);
        rv_day_glance = view.findViewById(R.id.rv_day_glance);
        rv_u_quests = view.findViewById(R.id.rv_u_quests);
        rv_assignments = view.findViewById(R.id.rv_assignments);
        rv_events = view.findViewById(R.id.rv_events);
        random = new Random();
        assignmentList = new ArrayList<>();
        // Perform any other necessary operations

        int randomValue = random.nextInt(greetings.length);
        greeting.setText(greetings[randomValue] + name);
        randomValue = random.nextInt(quotes.length);
        quoteTV.setText(quotes[randomValue]);
        Glide.with(this).load(image).circleCrop().into(profile_pic);

        db.child("assignments").child(enroll).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        Log.d("Firebase", "DataSnapshot exists. Children count: " + dataSnapshot.getChildrenCount());

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            Map<String, Object> assignmentMap = (Map<String, Object>) childSnapshot.getValue();
                            assignmentList.add(assignmentMap);
                        }

                        // Log the size of assignmentList
                        Log.d("Firebase", "Assignment list size: " + assignmentList.size());

                        // Notify the adapter that data has changed
                        if (rv_assignments.getAdapter() != null) {
                            rv_assignments.getAdapter().notifyDataSetChanged();
                        }
                    } else {
                        Log.d("Firebase", "DataSnapshot does not exist.");
                    }
                } else {
                    Log.e("Firebase", "Error getting data", task.getException());
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_assignments.setAdapter(new AssignmentAdapter(requireContext(), assignmentList));
    }

}
