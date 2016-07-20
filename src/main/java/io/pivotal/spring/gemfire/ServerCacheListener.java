package io.pivotal.spring.gemfire;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.Operation;
import com.gemstone.gemfire.cache.RegionEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import java.util.Properties;

public class ServerCacheListener<K,V> extends CacheListenerAdapter<K,V> implements Declarable {

    public void afterCreate(EntryEvent<K,V> e) {
        System.out.println("    Received afterCreate event for entry: " +
                e.getKey() + ", " + e.getNewValue());
    }

    public void afterUpdate(EntryEvent<K,V> e) {
        System.out.println("    Received afterUpdate event for entry: " +
                e.getKey() + ", " + e.getNewValue());
    }

    public void afterDestroy(EntryEvent<K,V> e) {
        System.out.println("    Received afterDestroy event for entry: " +
                e.getKey());



        if (e.getOperation().equals(Operation.EXPIRE_DESTROY)) {


        }
    }

    public void afterInvalidate(EntryEvent<K,V> e) {
        System.out.println("    Received afterInvalidate event for entry: " +
                e.getKey());
    }

    public void afterRegionLive(RegionEvent e) {
        System.out.println("    Received afterRegionLive event, sent to durable clients after \nthe server has finished replaying stored events.  ");
    }

    public void init(Properties props) {
        // do nothing
    }
}
