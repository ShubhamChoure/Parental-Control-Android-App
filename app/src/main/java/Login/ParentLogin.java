package Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.jspm.R;

public class ParentLogin extends AppCompatActivity {
    EditText mailEDtxt;
    Button continueBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parentlogin);
        init();
    }
    void init()
    {
      mailEDtxt = findViewById(R.id.LoginMailETxt);
      continueBtn = findViewById(R.id.continueBtn);
    }
}