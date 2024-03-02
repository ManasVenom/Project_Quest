package iitr.collector.quest;

import android.app.Application;
import com.google.android.material.color.DynamicColors;
import com.google.firebase.database.FirebaseDatabase;

public class DynamicTheme extends Application {

    @Override
    public void onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate();
    }
}
