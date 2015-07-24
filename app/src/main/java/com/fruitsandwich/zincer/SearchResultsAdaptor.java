package com.fruitsandwich.zincer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by nakac on 15/07/19.
 */
public class SearchResultsAdaptor extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<ZincSearchResult> results;
    ImageRepository imageRepository;
    private static final String TAG = "SearchResultsAdaptor";

    public SearchResultsAdaptor(Context ctx) {
        this.context = ctx;
        this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageRepository = new ImageRepository();
    }

    public List<ZincSearchResult> getResults() {
        return results;
    }

    public void setResults(List<ZincSearchResult> results) {
        this.results = results;
    }

    public void addResult(ZincSearchResult result) {
        this.results.add(result);
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int i) {
        return results.get(i);
    }

    @Override
    public long getItemId(int i) {
        return results.get(i).getZincId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.search_result_view_item, viewGroup, false);

        ZincSearchResult result = results.get(i);
        ZincDetail detail = result.getDetail();
        ((TextView) view.findViewById(R.id.zincId)).setText(result.getZincId().toString());
        if (detail != null) {
            ((TextView) view.findViewById(R.id.commonName)).setText(detail.getCommonName());
        }
        if (result.getImage() != null) {
            ((ImageView) view.findViewById(R.id.small_structure)).setImageBitmap(result.getImage());
        }
        return view;
    }

    public void refresh() {
        this.setResults(Lists.<ZincSearchResult>newArrayList());
        this.notifyDataSetChanged();
    }

    private class ImageLoader implements Observable.OnSubscribe<Bitmap> {
        private static final String TAG = "ImageLoader";

        private final String url;

        public ImageLoader(String url) {
            this.url = url;
        }

        @Override
        public void call(Subscriber<? super Bitmap> subscriber) {
            try {
                InputStream is = new URL(url).openStream();
                subscriber.onNext(BitmapFactory.decodeStream(is));
                subscriber.onCompleted();
            } catch (IOException e) {
                Log.e(TAG, "occurred error loading image", e);
                subscriber.onError(e);
            }
        }
    }
}
