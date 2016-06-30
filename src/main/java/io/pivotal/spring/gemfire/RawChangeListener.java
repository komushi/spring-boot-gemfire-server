package io.pivotal.spring.gemfire;

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache.asyncqueue.AsyncEvent;
import com.gemstone.gemfire.cache.asyncqueue.AsyncEventListener;
import com.gemstone.gemfire.cache.query.Query;
import com.gemstone.gemfire.cache.query.QueryService;
import com.gemstone.gemfire.cache.query.SelectResults;
import com.gemstone.gemfire.pdx.JSONFormatter;
import com.gemstone.gemfire.pdx.PdxInstance;
import com.gemstone.org.json.JSONObject;

import java.util.*;

public class RawChangeListener implements AsyncEventListener, Declarable {

    private QueryService queryService;
    private Region regionCount;
    private Region<Integer, PdxInstance> regionTop;
    private Region regionTopTen;
    private PdxInstance raw;
    private final Integer TOP_SIZE = 10;


    private boolean isTopLoaded(){
        return getTopSize()>=TOP_SIZE;
    }

    private Integer getTopSize(){
        Set<Integer> keySet = regionTop.keySet();
        return keySet.size();
    }

    private Integer getSmallestRankInRegionTop()throws Exception{
        Set<Integer> keySet = regionTop.keySet();
        if(keySet.size()==0) return 0;
        List<Integer> keyList = new ArrayList<Integer>(keySet);

        Collections.sort(keyList);
        return keyList.get(0);



//		String queryString = "SELECT DISTINCT x.route_count FROM " + regionTop.getFullPath()
//				+ " x  ORDER BY x.route_count LIMIT 1";
//
//		Query query = queryService.newQuery(queryString);
//		SelectResults results = (SelectResults)query.execute();
//		for (Iterator iter = results.iterator(); iter.hasNext();) {
//
//
//			return ((Byte)iter.next()).intValue();
//		}
//
//		return 0;
    }


    private void removeRouteFromOldRank(Integer originalCount, String route) throws Exception{

        PdxInstance pdxObj = (PdxInstance)regionTop.get(originalCount);
        LinkedList routes = (LinkedList)pdxObj.getField("routes");
        System.out.println("RawChangeListener: removeRouteFromOldRank " + route + " " + originalCount + " originalRoutes: " + routes);
        routes.remove(route);
        System.out.println("updateRoutes: " + routes);

        if (routes.size() == 0)
        {

            regionTop.destroy(originalCount);

        }
        else
        {
            PdxInstance newPdxObj = generateRoutesJson(originalCount, routes);
            regionTop.replace(originalCount, newPdxObj);

        }
    }

    // replenish
    private void replenishTop(String excludedRoute, Integer smallest) throws Exception{

        // Get the next coordinate key from RegionCount
        String queryString1 = "SELECT DISTINCT * FROM " + regionCount.getFullPath() + " cnt1 WHERE cnt1.route_count IN ";
        String queryString2 = "(SELECT DISTINCT x.route_count FROM " + regionCount.getFullPath()
                + " x  WHERE x.route_count < " + smallest
                + " AND x.route <> '" + excludedRoute + "'"
                +  " ORDER BY x.route_count DESC LIMIT 1)";
        String queryString = queryString1 + queryString2;
        Query query = queryService.newQuery(queryString);
        SelectResults<PdxInstance> results = (SelectResults)query.execute();

        if (results.size() != 0)
        {
            LinkedList routes = new LinkedList();
            Integer biggest = 0;
            for (Iterator<PdxInstance> iter = results.iterator(); iter.hasNext();) {


                PdxInstance regionCountValue = iter.next();
                System.out.println(JSONFormatter.toJSON(regionCountValue));
                String route = (String)(regionCountValue).getField("route");
                biggest = ((Byte)(regionCountValue).getField("route_count")).intValue();
                routes.addLast(route);
            }

            PdxInstance newCountValue = generateRoutesJson(biggest, routes);

            regionTop.put(biggest, newCountValue);

        }

    }

    // removeRedunduncy
    private void removeRedunduncy() throws Exception{

        Integer smallest = getSmallestRankInRegionTop();

        regionTop.destroy(smallest);

    }


    private void addRouteToTop(Integer newCount, String route) throws Exception{
        System.out.println("RawChangeListener: addRouteToTopTen " + route + " " + newCount);

        LinkedList routes = null;
        Object pdxObj = regionTop.get(newCount);

        if (pdxObj != null)
        {
            routes = (LinkedList)((PdxInstance)regionTop.get(newCount)).getField("routes");
        }
        else
        {
            routes = new LinkedList();
        }

        routes.addLast(route);

        PdxInstance newCountValue = generateRoutesJson(newCount, routes);


        regionTop.put(newCount, newCountValue);
    }


    private PdxInstance generateRoutesJson(Integer targetCount, LinkedList targetRoutes) throws Exception{


        JSONObject jsonObj = new JSONObject().put("routes", targetRoutes).put("route_count", targetCount);
        PdxInstance regionTopValue = JSONFormatter.fromJSON(jsonObj.toString());

        return regionTopValue;

    }

    private PdxInstance generateTopTenJson() throws Exception{

        JSONObject jsonObj = new JSONObject();
        long timstamp = (long)raw.getField("timestamp");
        long delay = (Calendar.getInstance().getTimeInMillis() - timstamp);

        jsonObj.put("pickupDatetime", raw.getField("pickupDatetime"))
                .put("dropoffDatetime", raw.getField("dropoffDatetime"));


        Set<Integer> keySet = regionTop.keySet();

        List<Integer> keyList = new ArrayList<Integer>(keySet);
        Collections.sort(keyList, Collections.reverseOrder());
        Integer count = 0;

        for (Iterator<Integer> iter = keyList.iterator(); iter.hasNext();) {

            Integer key = iter.next();
            PdxInstance regionTopValue = regionTop.get(key);
            LinkedList<String> routes = (LinkedList<String>)regionTopValue.getField("routes");
            ListIterator<String> listIterator = routes.listIterator();

            while (listIterator.hasNext()) {
                count++;
                String startCellKey = "start_cell_id_" + count;
                String endCellKey = "end_cell_id_" + count;
                String route = listIterator.next();
                String[] routeArray = route.split("_");
                String startCellValue = routeArray[0];
                String endCellValue = routeArray[1];
                jsonObj.put(startCellKey, startCellValue);
                jsonObj.put(endCellKey, endCellValue);

                if (count > 10)
                {
                    break;
                }
            }

            if (count > 10)
            {
                break;
            }
        }


        for (count++;count <= 10;count++)
        {
            String startCellKey = "start_cell_id_" + count;
            String endCellKey = "end_cell_id_" + count;
            jsonObj.put(startCellKey, "null");
            jsonObj.put(endCellKey, "null");
        }

        jsonObj.put("delay", delay);

        return JSONFormatter.fromJSON(jsonObj.toString());

    }

    // TODO
    private void refreshTopTen() throws Exception{


        PdxInstance newRegionTopTenValue = generateTopTenJson();
        PdxInstance crtRegionTopTenValue = (PdxInstance)regionTopTen.get(1);


        if (differTopTen(crtRegionTopTenValue, newRegionTopTenValue))
        {
            regionTopTen.put(1, newRegionTopTenValue);
        }

    }

    private boolean differTopTen(PdxInstance crtRegionTopTenValue, PdxInstance newRegionTopTenValue)
    {
        if (crtRegionTopTenValue == null && newRegionTopTenValue!= null)
        {
            return true;
        }

        if (crtRegionTopTenValue != null && newRegionTopTenValue== null)
        {
            return true;
        }

        if (!crtRegionTopTenValue.getField("start_cell_id_1").equals(newRegionTopTenValue.getField("start_cell_id_1")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("end_cell_id_1").equals(newRegionTopTenValue.getField("end_cell_id_1")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("start_cell_id_2").equals(newRegionTopTenValue.getField("start_cell_id_2")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("end_cell_id_2").equals(newRegionTopTenValue.getField("end_cell_id_2")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("start_cell_id_3").equals(newRegionTopTenValue.getField("start_cell_id_3")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("end_cell_id_3").equals(newRegionTopTenValue.getField("end_cell_id_3")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("start_cell_id_4").equals(newRegionTopTenValue.getField("start_cell_id_4")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("end_cell_id_4").equals(newRegionTopTenValue.getField("end_cell_id_4")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("start_cell_id_5").equals(newRegionTopTenValue.getField("start_cell_id_5")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("end_cell_id_5").equals(newRegionTopTenValue.getField("end_cell_id_5")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("start_cell_id_6").equals(newRegionTopTenValue.getField("start_cell_id_6")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("end_cell_id_6").equals(newRegionTopTenValue.getField("end_cell_id_6")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("start_cell_id_7").equals(newRegionTopTenValue.getField("start_cell_id_7")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("end_cell_id_7").equals(newRegionTopTenValue.getField("end_cell_id_7")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("start_cell_id_8").equals(newRegionTopTenValue.getField("start_cell_id_8")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("end_cell_id_8").equals(newRegionTopTenValue.getField("end_cell_id_8")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("start_cell_id_9").equals(newRegionTopTenValue.getField("start_cell_id_9")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("end_cell_id_9").equals(newRegionTopTenValue.getField("end_cell_id_9")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("start_cell_id_10").equals(newRegionTopTenValue.getField("start_cell_id_10")))
        {
            return true;
        }
        else if (!crtRegionTopTenValue.getField("end_cell_id_10").equals(newRegionTopTenValue.getField("end_cell_id_10")))
        {
            return true;
        }

        return false;
    }

    private void processRanking(String route, Integer originalCount, Integer newCount) throws Exception{
        System.out.println("RawChangeListener: processRanking " + route + " originalCount: " + originalCount + " newCount: " + newCount);

        Integer smallest = getSmallestRankInRegionTop();
        Boolean topLoaded = isTopLoaded();
        Boolean topChanged = false;


        if(topLoaded)
        {
            if(newCount >= smallest){
                addRouteToTop(newCount, route);
                topChanged = Boolean.logicalOr(topChanged, Boolean.TRUE);
            }

            if (originalCount >= smallest && originalCount != 0) {
                removeRouteFromOldRank(originalCount, route);
                topChanged = Boolean.logicalOr(topChanged, Boolean.TRUE);
            }


            Integer crtSize = getTopSize();
            System.out.println("RegionTop loaded: " + crtSize);

            if (crtSize > TOP_SIZE){
                removeRedunduncy();
                topChanged = Boolean.logicalOr(topChanged, Boolean.TRUE);

            }
            else if (crtSize < TOP_SIZE){
                replenishTop(route, smallest);
                topChanged = Boolean.logicalOr(topChanged, Boolean.TRUE);
            }

        }
        else {
            addRouteToTop(newCount, route);

            if (originalCount >= smallest && originalCount != 0) {
                removeRouteFromOldRank(originalCount, route);
            }

            topChanged = Boolean.logicalOr(topChanged, Boolean.TRUE);

        }

        if (topChanged)
        {
            refreshTopTen();
        }

    }

    private void transactionProcessing(AsyncEvent event, String route, Integer countDiff) throws Exception{
        CacheTransactionManager tm = ((Cache)event.getRegion().getRegionService()).getCacheTransactionManager();
        tm.begin();
        Integer originalCount = 0 ;
        Integer newCount = 0;



        PdxInstance originCountValue = (PdxInstance)regionCount.get(route);

        if(originCountValue==null){
            JSONObject jsonObj = new JSONObject().put("route", route).put("route_count", 1);


            PdxInstance firstValue = JSONFormatter.fromJSON(jsonObj.toString());

            if(regionCount.putIfAbsent(route, firstValue)==null){
                newCount = 1;
            } else System.out.println("-----------------------------------------putIfAbsent rerun");
        }
        else
        {
            originalCount = ((Byte)originCountValue.getField("route_count")).intValue();
            newCount = (originalCount + countDiff);

            JSONObject jsonObj = new JSONObject().put("route", route).put("route_count", newCount);

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

        try {
            processRanking(route, originalCount, newCount);
            tm.commit();
        }catch(CommitConflictException e){
            e.printStackTrace();
            tm.rollback();
            System.out.println("-----------------------------CommitConflictException");
            throw new Exception(e);
        }
    }

    @Override
    public boolean processEvents(List<AsyncEvent> events) {
        System.out.println("new event!!!");
        try{
            for(AsyncEvent event: events) {

                Operation operation =  event.getOperation();
                Integer countDiff = 0;

                if(operation.equals(Operation.PUTALL_CREATE)){
                    countDiff = 1;
                }else if(operation.equals(Operation.DESTROY)){
                    countDiff = -1;
                }else{
                    System.out.println("unknown ooperation " + event.getOperation());
                    continue;
                }

                queryService = event.getRegion().getRegionService().getQueryService();

                regionCount = event.getRegion().getRegionService().getRegion("RegionCount");
                regionTop = event.getRegion().getRegionService().getRegion("RegionTop");
                regionTopTen = event.getRegion().getRegionService().getRegion("RegionTopTen");

                raw = (PdxInstance)event.getDeserializedValue();


                // get route from the key in JSON format
                String route = (String)raw.getField("route");


                // TODO remove while loop for single event implementation

                try{
                    transactionProcessing(event, route, countDiff);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
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