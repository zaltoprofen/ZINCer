package com.fruitsandwich.zincer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.fruitsandwich.zincer.opengl.Mol2Renderer;
import com.fruitsandwich.zincer.parser.Mol2;
import com.fruitsandwich.zincer.parser.Mol2Parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class Structure3DActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private GLSurfaceView glView;
    private Mol2Renderer renderer;
    private static final String TAG = "Structure3DActivity";
    private Structure3DActivity thisActivity = this;
    private SeekBar seekTheta;
    private SeekBar seekPhi;
    private SeekBar seekDistance;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_structure_3d);
        glView = (GLSurfaceView) findViewById(R.id.gl_view);
        seekTheta = (SeekBar) findViewById(R.id.seek_theta);
        seekPhi = (SeekBar) findViewById(R.id.seek_phi);
        seekDistance = (SeekBar) findViewById(R.id.seek_distance);
        //ZincDetail detail = (ZincDetail) getIntent().getSerializableExtra("detail");
        Long zincId = getIntent().getLongExtra("zinc_id", 0);
        //Long zincId = detail.getZincId();
        renderer = new Mol2Renderer();
        glView.setRenderer(renderer);
        subscription = Observable.create(new Mol2Loader(zincId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Mol2>() {
                            @Override
                            public void call(Mol2 mol2) {
                                renderer.setMol2(mol2);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                Log.e(TAG, "Occurred error while loading MOL2", e);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(thisActivity);
                                dialog.setTitle("Error");
                                dialog.setMessage("Occurred error while loading MOL2");
                                dialog.setPositiveButton("OK", new DialogEventHandler());
                                dialog.create().show();
                            }
                        }
                );
        seekTheta.setOnSeekBarChangeListener(this);
        seekPhi.setOnSeekBarChangeListener(this);
        seekDistance.setOnSeekBarChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_structure3_d, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == seekTheta)
            renderer.setTheta((double)i);
        if (seekBar == seekPhi)
            renderer.setPhi((double)i);
        if (seekBar == seekDistance)
            renderer.setDistance((double)i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class Mol2Loader implements Observable.OnSubscribe<Mol2> {
        String url;

        public Mol2Loader(Long zincId) {
            this.url = String.format(
                    "http://zinc.docking.org/results?zinc.id=%d&page.format=mol2", zincId);
        }

        @Override
        public void call(Subscriber<? super Mol2> subscriber) {
            try {
                InputStream is = new URL(url).openStream();
                subscriber.onNext(new Mol2Parser().parse(is));
                subscriber.onCompleted();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class DialogEventHandler implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            finish();
        }
    }
}
