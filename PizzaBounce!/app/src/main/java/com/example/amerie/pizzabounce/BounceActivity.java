package com.example.amerie.pizzabounce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BounceActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bounce);
    }

    public void clear_sprites(View view) {
        CanvasView cv = (CanvasView) findViewById(R.id.bounceCanvas);
        cv.Sprites.clear();
    }

    public void launch_help(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
}
