package com.fruitsandwich.zincer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fruitsandwich.zincer.util.HttpStatuses;
import com.google.common.base.Strings;

import org.jsoup.HttpStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class SearchActivity extends Activity implements TextView.OnEditorActionListener {

    EditText editText;
    public static final String TAG = "SearchActivity";
    public SearchResultsAdaptor adaptor;
    public ZincDetailRepository zinc = new ZincDetailRepository();
    public ImageRepository imageRepository = new ImageRepository();
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private SearchActivity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        editText = (EditText)this.findViewById(R.id.editText);
        editText.setOnEditorActionListener(this);

        final ListView listView = (ListView)this.findViewById(R.id.searchResults);
        adaptor = new SearchResultsAdaptor(SearchActivity.this);
        adaptor.setResults(new ArrayList<ZincSearchResult>());
        listView.setAdapter(adaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ZincSearchResult a = (ZincSearchResult) adapterView.getItemAtPosition(i);
                ZincDetail detail = a.getDetail();
                if (detail != null){
                    Intent detailIntent = new Intent(thisActivity, DetailActivity.class);
                    detailIntent.putExtra("detail", detail);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    a.getImage().compress(Bitmap.CompressFormat.PNG, 100, baos);
                    detailIntent.putExtra("image", baos.toByteArray());
                    startActivity(detailIntent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String str = textView.getText().toString();
        Log.d(TAG, "textView.text: " + str);
        adaptor.refresh();
        subscriptions.unsubscribe();
        subscriptions = new CompositeSubscription();

        Observable<ZincSearchResult> searchObservable = Observable.create(new ZincSearch(str));

        subscriptions.add(searchObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SearchSubscriber()));
        return true;
    }

    private class DetailFetcher implements Observable.OnSubscribe<ZincDetail> {

        private Long zincId;

        public DetailFetcher(Long zincId) {
            this.zincId = zincId;
        }

        @Override
        public void call(Subscriber<? super ZincDetail> subscriber) {
            try {
                ZincDetail detail = zinc.getZincDetail(zincId);
                subscriber.onNext(detail);
            } catch (IOException e) {
                Log.e(TAG, "occurred error fetching detail", e);
                throw new RuntimeException(e);
            }
        }
    }

    private class SearchSubscriber extends Subscriber<ZincSearchResult> {

        @Override
        public void onCompleted() {
            adaptor.notifyDataSetChanged();
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof HttpStatusException) {
                int statusCode = ((HttpStatusException) e).getStatusCode();
                Toast.makeText(thisActivity, HttpStatuses.valueOf(statusCode), Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(thisActivity, "Occurred error when search", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNext(final ZincSearchResult zincSearchResult) {
            adaptor.addResult(zincSearchResult);
            Subscription sub = Observable.create(new DetailFetcher(zincSearchResult.getZincId()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ZincDetail>() {
                        @Override
                        public void call(ZincDetail zincDetail) {
                            zincSearchResult.setDetail(zincDetail);
                            adaptor.notifyDataSetChanged();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e(TAG, "Occurred error while fetching details", throwable);
                            Toast.makeText(thisActivity, "Occurred error while fetching details", Toast.LENGTH_LONG).show();
                        }
                    });
            subscriptions.add(sub);

            Subscription sub2 = Observable.from(imageRepository.getImageFuture("http:" + zincSearchResult.getImageUrl()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            zincSearchResult.setImage(bitmap);
                            adaptor.notifyDataSetChanged();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e(TAG, "Occurred error while fetching image", throwable);
                            Toast.makeText(thisActivity, "Occurred error while fetching image", Toast.LENGTH_SHORT).show();
                        }
                    });
            subscriptions.add(sub2);
        }

    }
}
