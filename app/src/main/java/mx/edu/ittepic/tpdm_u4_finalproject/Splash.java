package mx.edu.ittepic.tpdm_u4_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class Splash extends AppCompatActivity {
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pb=(ProgressBar)findViewById(R.id.progressBar);
        new Thread(new Runnable() {
            public void run() {
                doWork();
                Splash.this.finish();
                ventanaPrincipal();
            }
        }).start();
    }
    private void doWork() {
        for (int progress = 0; progress < 100; progress += 25) {
            try {
                Thread.sleep(1000);
                pb.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ventanaPrincipal(){
        Intent v= new Intent(this, MainActivity.class);
        startActivity(v);
    }
}
