package com.fruitsandwich.zincer;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.google.common.collect.Lists;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by nakac on 15/07/21.
 */
public class ZincSearch implements Observable.OnSubscribe<ZincSearchResult> {
    private static final String synonymBaseUrl = "http://zinc.docking.org/synonym/";
    private static final String TAG = "ZincSearch";

    private String searchQuery;

    public ZincSearch(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    public void call(Subscriber<? super ZincSearchResult> subscriber) {
        try {
            String url = synonymBaseUrl + URLEncoder.encode(searchQuery, "utf-8");
            Document doc = Jsoup.connect(url).get();
            Elements xs = doc.select("li.zinc.summary-item");
            for (Element x : xs) {
                Element sl = x.select("a.sub-link").first();
                Long zincId = sl == null ? 0 : Long.parseLong(sl.text());
                String subLink = sl == null ? "" : sl.attr("href");
                Element iu = x.select("img.molecule").first();
                String imageUrl = iu == null ? null : iu.attr("src");
                ZincSearchResult result = new ZincSearchResult(zincId, imageUrl, subLink);
                Log.d(TAG, "parsed: " + result.toString());
                subscriber.onNext(result);
            }
            subscriber.onCompleted();
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 404)
                Log.i(TAG, "search result is empty", e);
            else
                Log.e(TAG, "http status is not 200", e);
            subscriber.onError(e);
        } catch (InterruptedIOException e){
            Log.i(TAG, "interrupted requests due to other requests");
            subscriber.onError(e);
        } catch (IOException e) {
            Log.e(TAG, "onRequest error", e);
            subscriber.onError(e);
        }
    }
}
