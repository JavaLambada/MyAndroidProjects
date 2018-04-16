package com.lanchen.drawingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(new CustomDrawing(getApplicationContext()));
        setContentView(R.layout.activity_main);


        Button btnUndo = findViewById(R.id.btnUndo);
        Button btnRedo = findViewById(R.id.btnRedo);
        Button btnClear = findViewById(R.id.btnClear);
        SeekBar skbBrushSize = findViewById(R.id.skbBrushSize);
        final CustomDrawing customView = findViewById(R.id.cusViewDrawing);


        /**
         * undo button click
         */
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customView.undoClick();;
            }
        });

        /**
         * redo button click
         */
        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customView.redoCLick();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customView.clearClick();
            }
        });


        /**
         * change size bar move event
         */
        skbBrushSize.setMax(90);
        skbBrushSize.setProgress(10);
        skbBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                customView.setBrushSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }
}
