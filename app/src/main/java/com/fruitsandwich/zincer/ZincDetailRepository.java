package com.fruitsandwich.zincer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by nakac on 15/07/18.
 */
public class ZincDetailRepository {
    private static final String substanceBaseUrl = "http://zinc.docking.org/substance/";
    private static final String TAG = "ZincDetailRepository";

    private final Map<Long, ZincDetail> commonNameTable;

    public ZincDetailRepository() {
        commonNameTable = Maps.newHashMap();
    }

    public ZincDetail getZincDetail(Long zincId) throws IOException {
        boolean exists;
        exists = commonNameTable.containsKey(zincId);

        if (!exists) {
            ZincDetail detail = new ZincDetail();
            String url = substanceBaseUrl + zincId.toString();
            Document doc = Jsoup.connect(url).get();
            detail.setZincId(zincId);
            Element cn = doc.select("p.popular > a > span").first();
            detail.setCommonName(extractText(cn));
            Element w = doc.select("td.weight").first();
            detail.setMolecularWeight(extractText(w).trim());
            Element rb = doc.select("td.rbonds").first();
            detail.setRotatableBonds(extractText(rb).trim());
            Element img = doc.select("img.molecule").first();
            if (img != null)
                detail.setImageUrl(img.attr("src"));

            List<ChEMBLActivity> activities = Lists.newArrayList();
            Element chemblTmp = doc.select("#targets").first();
            if (chemblTmp != null) {
                Elements rows = chemblTmp.parent().select("tbody > tr");
                for (Element row : rows) {
                    ChEMBLActivity activity = new ChEMBLActivity();
                    Element uniprotElement = row.select("td.uniprot.id > a").first();
                    activity.setUniprotId(extractText(uniprotElement).trim());
                    Element swissprotElement = row.select("td.swissprot.id > a").first();
                    activity.setSwissprotId(extractText(swissprotElement).trim());
                    Element descElement = row.select("td.description").first();
                    activity.setDescription(extractText(descElement));
                    Element affinityElement = row.select("td.affinity").first();
                    activity.setAffinity(extractText(affinityElement));
                    Element leElement = row.select("td.le").first();
                    activity.setLigandEfficiency(extractText(leElement));
                    activities.add(activity);
                }
            }
            detail.setActivities(activities);

            commonNameTable.put(zincId, detail);
            return detail;
        } else {
            return commonNameTable.get(zincId);
        }
    }

    private String extractText(Element el) {
        return el == null ? "" : el.text();
    }
}
