package Locks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.jspm.MainActivity;
import com.example.jspm.R;
import com.itsxtt.patternlock.PatternLockView;

import java.util.ArrayList;
import java.util.Arrays;

public class PopUpPatternLock extends AppCompatActivity {


    public static SharedPreferences childlockSharedPreference;
    public static final String PREF_LOCK = "ChildLockStatus";
    PatternLockView patternLockView;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_pattern_lock);
        init();

        patternLockView.setOnPatternListener(new PatternLockView.OnPatternListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(@NonNull ArrayList<Integer> arrayList) {

            }

            @Override
            public boolean onComplete(@NonNull ArrayList<Integer> arrayList) {
                String patternStr =  childlockSharedPreference.getString("Pattern Lock Key","0");
                String[] arr = patternStr.split(",");
                Log.e("tagPatternArray",patternStr);
                //Log.e("tagPatternArray",arr[0]+arr[1]+arr[2]+arr[3]+arr[4]+arr[5]+arr[6]+arr[7]+arr[8]);

              ArrayList<Integer> patternArray =  new ArrayList<>();
                for(String i:arr){
                    patternArray.add(Integer.parseInt(i));
                }
                Log.e("tagPatternArray",patternArray.toString());
                if(patternArray.equals(arrayList)){
                    finish();
                }else{
                    builder.setTitle("Oops Wrong Pattern");
                    builder.setMessage("Try Again");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
                }

                return true;
            }
        });
    }
    void init(){
        childlockSharedPreference = getSharedPreferences(PREF_LOCK,MODE_PRIVATE);
        patternLockView = findViewById(R.id.childPatternView);
        builder = new AlertDialog.Builder(this);
    }
}