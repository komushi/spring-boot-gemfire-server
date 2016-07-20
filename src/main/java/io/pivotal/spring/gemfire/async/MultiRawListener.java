package io.pivotal.spring.gemfire.async;

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache.asyncqueue.AsyncEvent;
import com.gemstone.gemfire.cache.asyncqueue.AsyncEventListener;
import com.gemstone.gemfire.cache.query.Query;
import com.gemstone.gemfire.cache.query.QueryService;
import com.gemstone.gemfire.cache.query.SelectResults;
import com.gemstone.gemfire.pdx.JSONFormatter;
import com.gemstone.gemfire.pdx.PdxInstance;
import com.gemstone.org.json.JSONException;
import com.gemstone.org.json.JSONObject;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.GemFireCache;
import com.gemstone.gemfire.cache.Region;

import java.util.*;
/**
 * Created by lei_xu on 7/17/16.
 */
public class MultiRawListener implements AsyncEventListener, Declarable {

    private GemFireCache gemFireCache;
    private Region regionCount;
//    private Region regionRaw = gemFireCache.getRegion("RegionRaw");
    private Region<Integer, PdxInstance> regionTop;
    private Region regionTopTen;

    @Override
    public boolean processEvents(List<AsyncEvent> events) {

        List<String> routes = new ArrayList<>();
        List<Integer> countDiffs = new ArrayList<>();

        gemFireCache = CacheFactory.getAnyInstance();
        regionCount = gemFireCache.getRegion("RegionCount");
        regionTop = gemFireCache.getRegion("RegionTop");
        regionTopTen = gemFireCache.getRegion("RegionTopTen");

        Integer smallestToptenCount = 0;


        PdxInstance topTenValue = (PdxInstance)regionTopTen.get(1);
        if (topTenValue != null) {
            LinkedList toptenList = (LinkedList)topTenValue.getField("toptenlist");
            smallestToptenCount = ((Byte)((PdxInstance)toptenList.getLast()).getField("count")).intValue();
        }

//        try {
//            String queryString = "SELECT DISTINCT count FROM " + regionTopTen.getFullPath() +  " ORDER BY rank DESC LIMIT 1";
//
//            Query query = gemFireCache.getQueryService().newQuery(queryString);
//            SelectResults<PdxInstance> results = (SelectResults) query.execute();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        System.out.println("new events, events.size:" + events.size());
        try {
            for (AsyncEvent event : events) {


                Operation operation = event.getOperation();
                Integer countDiff = 0;

                if (operation.equals(Operation.PUTALL_CREATE)) {
                    countDiff = 1;
                }
                else if (operation.equals(Operation.EXPIRE_DESTROY)) {
                    countDiff = -1;
                }
                else {
                    System.out.println("unknown ooperation " + event.getOperation());
                    continue;
                }

                PdxInstance raw = (PdxInstance) event.getDeserializedValue();

                // get route from the key in JSON format
                String route = (String)raw.getField("route");
                Long newTimestamp = (Long)raw.getField("timestamp");

                Integer originalCount = 0 ;
                Integer newCount = 0;
                Long originalTimestamp = 0L;

                PdxInstance originCountValue = (PdxInstance)regionCount.get(route);

                if(originCountValue==null){

                    newCount = 1;
                }
                else
                {
                    originalCount = ((Byte)originCountValue.getField("route_count")).intValue();
                    originalTimestamp = (Long)originCountValue.getField("timestamp");
                    newCount = (originalCount + countDiff);

                }

                processRegionCount(route, originalCount, originalTimestamp, newCount, newTimestamp);

                processRegionTop(route, originalCount, originalTimestamp, newCount, newTimestamp);

            }

            processRegionTopTen();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void processRegionCount(String route, Integer originalCount, Long originalTimestamp, Integer newCount, Long newTimestamp) throws Exception{
        // process RegionCount

        JSONObject jsonObj = new JSONObject().put("route", route).put("route_count", newCount).put("timestamp", newTimestamp);
        PdxInstance entryValue = JSONFormatter.fromJSON(jsonObj.toString());

        if(originalCount == 0){
            regionCount.putIfAbsent(route, entryValue);
        }
        else
        {
            regionCount.put(route, entryValue);
        }

    }



/*
    private void processRegionCount(String route, Integer countDiff, Long timestamp) throws Exception{
//        CacheTransactionManager tm = ((Cache)event.getRegion().getRegionService()).getCacheTransactionManager();
//        tm.begin();

//        GemFireCache gemFireCache = CacheFactory.getAnyInstance();
//        Region regionCount = gemFireCache.getRegion("RegionCount");
//        Region regionRaw = gemFireCache.getRegion("RegionRaw");


        // process RegionCount
        Integer originalCount = 0 ;
        Integer newCount = 0;
        Long originalTimestamp = 0L;

        PdxInstance originCountValue = (PdxInstance)regionCount.get(route);

        if(originCountValue==null){
            JSONObject jsonObj = new JSONObject().put("route", route).put("route_count", 1).put("timestamp", timestamp);


            PdxInstance firstValue = JSONFormatter.fromJSON(jsonObj.toString());

            if(regionCount.putIfAbsent(route, firstValue)==null){
                newCount = 1;
            } else System.out.println("-----------------------------------------putIfAbsent rerun");
        }
        else
        {
            originalCount = ((Byte)originCountValue.getField("route_count")).intValue();
            originalTimestamp = (Long)originCountValue.getField("timestamp");
            newCount = (originalCount + countDiff);

            JSONObject jsonObj = new JSONObject().put("route", route).put("route_count", newCount).put("timestamp", timestamp);

            PdxInstance newCountValue = JSONFormatter.fromJSON(jsonObj.toString());

            if (regionCount.replace(route, originCountValue, newCountValue))
            {
                System.out.println("ori: " + originalCount + " new: " + newCount);
            }
            else
            {
                System.out.println("---------------------------------------------replace rerun");
            }

        }

        // process RegionTop
        processRegionTop(route, originalCount, originalTimestamp, newCount, timestamp);

    }
*/

    private void processRegionTop(String route, Integer originalCount, Long originalTimestamp, Integer newCount, Long newTimestamp) throws Exception{


        if (newCount > originalCount) {
            if (originalCount == 0) {
                // add to new entry
                addRouteToTop(regionTop, route, newCount, newTimestamp);

            } else {
                // add to new entry
                addRouteToTop(regionTop, route, newCount, newTimestamp);

                // remove from old entry
                removeRouteFromOldTop(regionTop, route, originalCount, originalTimestamp);
            }

//            if (newCount >= smallestToptenCount) {
//
//            }

        }
        else if (newCount < originalCount) {
            // TODO waiting for expiration destroy opertaion to be published in async event
            // https://issues.apache.org/jira/browse/GEODE-1209

        }

    }

    private PdxInstance generateRoutesJson(Integer targetCount, LinkedList<String> targetRoutes, LinkedList<Long> targetTimestamps) throws Exception{


        JSONObject jsonObj = new JSONObject().put("route_count", targetCount).put("routes", targetRoutes).put("timestamps", targetTimestamps);

        PdxInstance regionTopValue = JSONFormatter.fromJSON(jsonObj.toString());

        return regionTopValue;

    }

    private void addRouteToTop(Region<Integer, PdxInstance> regionTop, String route, Integer newCount, Long newTimestamp) throws Exception{
        System.out.println("RawChangeListener: addRouteToTop " + route + " " + newCount);

        LinkedList<String> routes = null;
        LinkedList<Long> timestamps = null;
        Object pdxObj = regionTop.get(newCount);

        if (pdxObj != null)
        {
            routes = (LinkedList)((PdxInstance)pdxObj).getField("routes");
            timestamps = (LinkedList)((PdxInstance)pdxObj).getField("timestamps");
        }
        else
        {
            routes = new LinkedList<String>();
            timestamps = new LinkedList<Long>();
        }


        routes.addFirst(route);
        timestamps.addFirst(newTimestamp);

        PdxInstance newCountValue = generateRoutesJson(newCount, routes, timestamps);

        regionTop.put(newCount, newCountValue);
    }

    private void removeRouteFromOldTop(Region<Integer, PdxInstance> regionTop, String route, Integer originalCount, Long originalTimestamp) throws Exception{

        PdxInstance pdxObj = (PdxInstance)regionTop.get(originalCount);
        LinkedList<String> routes = (LinkedList<String>)pdxObj.getField("routes");
        LinkedList<Long> timestamps = (LinkedList<Long>)pdxObj.getField("timestamps");
        System.out.println("RawChangeListener: removeRouteFromOldTop route:" + route + " originalCount: " + originalCount);


        Integer originalIndex = routes.indexOf(route);
        routes.remove(route);
        timestamps.remove(originalTimestamp);

        System.out.println("removeRouteFromOldTop indexOf: " + originalIndex);

        if (routes.size() == 0)
        {
            regionTop.destroy(originalCount);

        }
        else
        {
            PdxInstance newPdxObj = generateRoutesJson(originalCount, routes, timestamps);
            regionTop.replace(originalCount, newPdxObj);

        }
    }

    private void processRegionTopTen() throws Exception{


        PdxInstance newRegionTopTenValue = generateTopTenJson();
        PdxInstance crtRegionTopTenValue = (PdxInstance)regionTopTen.get(1);


        if (differTopTen(crtRegionTopTenValue, newRegionTopTenValue))
        {
            regionTopTen.put(1, newRegionTopTenValue);
        }

    }

    private PdxInstance generateTopTenJson() throws Exception{

        JSONObject toptenJson = new JSONObject();
        long crtTimestamp = 100L;
        String crtUuid = "468244D1361B8A3EB8D206CC394BC9E9";
        String crtRoute = "test_test";
        long delay = (Calendar.getInstance().getTimeInMillis() - crtTimestamp);


        Set<Integer> keySet = regionTop.keySet();

        List<Integer> keyList = new ArrayList<Integer>(keySet);
        Collections.sort(keyList, Collections.reverseOrder());

        // top ten list for gui table
        LinkedList<JSONObject> topTenList = new LinkedList();


        // top ten matrix for d3 matrix
        JSONObject matrix = new JSONObject();
        LinkedList<JSONObject> nodes = new LinkedList();
        LinkedList<JSONObject> links = new LinkedList();

        Integer rank = 0;

        for (Iterator<Integer> iter = keyList.iterator(); iter.hasNext();) {

//            rank++;

            Integer key = iter.next();
            PdxInstance regionTopValue = regionTop.get(key);
            LinkedList<String> routes = (LinkedList<String>)regionTopValue.getField("routes");
            ListIterator<String> listIterator = routes.listIterator();

            while (listIterator.hasNext()) {

                rank++;

                String route = listIterator.next();
                String[] routeArray = route.split("_");
                String fromCellValue = routeArray[0];
                String toCellValue = routeArray[1];

                // top ten list element
                JSONObject topTenElement = new JSONObject();
//                topTenElement.put("rank", topTenList.size() + 1);
                topTenElement.put("rank", rank);
                topTenElement.put("count", key);
                topTenElement.put("from", fromCellValue);
                topTenElement.put("to", toCellValue);

                topTenList.addLast(topTenElement);

                // top ten matrix element
                JSONObject fromNodeElement = new JSONObject();
                JSONObject toNodeElement = new JSONObject();
                JSONObject linkElement = new JSONObject();

                fromNodeElement.put("name", fromCellValue);
                // fromNodeElement.put("rank", rank);
                // fromNodeElement.put("group", 1);

                toNodeElement.put("name", toCellValue);
                // toNodeElement.put("group", 0);

                Integer fromPosition = addNodeToNodes(nodes, fromNodeElement);
                Integer toPosition = addNodeToNodes(nodes, toNodeElement);

                linkElement.put("source", fromPosition);
                linkElement.put("target", toPosition);
                linkElement.put("value", key);
                linkElement.put("rank", rank);

                links.addLast(linkElement);

                if (topTenList.size() >= 10)
                {
                    break;
                }
            }

            if (topTenList.size() >= 10)
            {
                break;
            }
        }

        // get current raw entry value and set to region top ten
        PdxInstance regionCountValue = (PdxInstance)regionCount.get(crtRoute);
//        Integer routeCount = ((Byte)regionCountValue.getField("route_count")).intValue();


        String[] crtRouteArray = crtRoute.split("_");
        String crtFromCellValue = crtRouteArray[0];
        String crtToCellValue = crtRouteArray[1];

        toptenJson.put("from", crtFromCellValue);
        toptenJson.put("to", crtToCellValue);
        toptenJson.put("count", 100);
        toptenJson.put("uuid", crtUuid);
        toptenJson.put("delay", delay);
        toptenJson.put("timestamp", crtTimestamp);

        // set toptenlist
        toptenJson.put("toptenlist", topTenList);

        // set matrix
        matrix.put("nodes", nodes);
        matrix.put("links", links);
        toptenJson.put("matrix", matrix);

        return JSONFormatter.fromJSON(toptenJson.toString());

    }

    private Integer addNodeToNodes(LinkedList<JSONObject> nodes, JSONObject nodeElement) throws Exception{

        for(int num=0; num < nodes.size(); num++) {
            JSONObject crtNode = nodes.get(num);

            if (crtNode.getString("name").equals(nodeElement.getString("name"))) {
                return num;
            }
        }

        nodes.addLast(nodeElement);

        return nodes.size() - 1;
    }

    private boolean differTopTen(PdxInstance crtRegionTopTenValue, PdxInstance newRegionTopTenValue){

        if (crtRegionTopTenValue == null && newRegionTopTenValue!= null)
        {
            return true;
        }

        if (crtRegionTopTenValue != null && newRegionTopTenValue== null)
        {
            return true;
        }


        LinkedList<PdxInstance> crtTopTenList = (LinkedList)crtRegionTopTenValue.getField("toptenlist");
        LinkedList<PdxInstance> newTopTenList = (LinkedList)newRegionTopTenValue.getField("toptenlist");

        if (crtTopTenList.size() != newTopTenList.size()) {
            return true;
        }

        for(int num=0; num < crtTopTenList.size(); num++)
        {
            try {
                JSONObject crtTopTenElement = new JSONObject(JSONFormatter.toJSON(crtTopTenList.get(num)));
                JSONObject newTopTenElement = new JSONObject(JSONFormatter.toJSON(newTopTenList.get(num)));

                if (crtTopTenElement.getInt("rank") != newTopTenElement.getInt("rank")) {
                    return true;
                }

                if (crtTopTenElement.getInt("count") != newTopTenElement.getInt("count")) {
                    return true;
                }

                if (!crtTopTenElement.getString("from").equals(newTopTenElement.getString("from"))) {
                    return true;
                }

                if (!crtTopTenElement.getString("to").equals(newTopTenElement.getString("to"))) {
                    return true;
                }
            }
            catch (JSONException e) {
                return true;
            }

        }

        return false;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(Properties arg0) {
        // TODO Auto-generated method stub

    }
}