package uz.gita.puzzle15halloween;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.smb.glowbutton.GlowButton;


public class MainActivity extends AppCompatActivity {

    private GlowButton btnPlay ;
    GlowButton btnabout;
    GlowButton btnexit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btnPlay) ;
        btnabout=findViewById(R.id.about);
        btnexit=findViewById(R.id.exit_game);

        btnabout.setOnClickListener(v->{
            startActivity(new Intent(this,AboutActivity.class));
        });
        btnexit.setOnClickListener(v->{
            finish();
        });
        btnPlay.setOnClickListener(v -> startActivity(new Intent(this , PlayActivity.class)));
    }
}