package iitr.collector.quest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    ScheduleFragment scheduleFragment = new ScheduleFragment();
    CompeteFragment competeFragment = new CompeteFragment();
    BuddiesFragment buddiesFragment = new BuddiesFragment();
    FragmentManager fragmentManager;
    private int currentFragment = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        fragmentManager.beginTransaction()
                .add(R.id.container, homeFragment, "home")
                .add(R.id.container, scheduleFragment, "schedule")
                .add(R.id.container, competeFragment, "compete")
                .add(R.id.container, buddiesFragment, "buddies")
                .hide(scheduleFragment)
                .hide(competeFragment)
                .hide(buddiesFragment)
                .commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (itemId == R.id.title_home) {
                    currentFragment = 0;
                    fragmentTransaction.show(homeFragment).hide(scheduleFragment).hide(competeFragment).hide(buddiesFragment);
                } else if (itemId == R.id.title_schedule) {
                    currentFragment = 1;
                    fragmentTransaction.show(scheduleFragment).hide(homeFragment).hide(competeFragment).hide(buddiesFragment);
                } else if (itemId == R.id.title_compete) {
                    currentFragment = 2;
                    fragmentTransaction.show(competeFragment).hide(homeFragment).hide(scheduleFragment).hide(buddiesFragment);
                } else if (itemId == R.id.title_buddies) {
                    currentFragment = 3;
                    fragmentTransaction.show(buddiesFragment).hide(homeFragment).hide(scheduleFragment).hide(competeFragment);
                }

                fragmentTransaction.commit();
                return true;
            }
        });


    }
}