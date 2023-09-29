package HomeActivity.ChildeHomeActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.jspm.R;
import com.google.firebase.auth.FirebaseAuth;

public class ChildHomeActivity extends AppCompatActivity {
    Button logoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_home);
        init();

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }
    void init()
    {
        logoutbtn = findViewById(R.id.logoutBtn);
    }
}