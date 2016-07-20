package io.pivotal.spring.gemfire.async;

import com.gemstone.gemfire.pdx.PdxSerializer;
import com.gemstone.gemfire.pdx.PdxWriter;
import com.gemstone.gemfire.pdx.PdxReader;

/**
 * Created by lei_xu on 7/17/16.
 */
public class RawPdxSerializer implements PdxSerializer {
    public boolean toData(Object obj, PdxWriter writer) {
        if (obj instanceof RawRecord) {
            RawRecord rawRecord = (RawRecord)obj;
            writer.writeString("pickupLatitude", rawRecord.getPickupLatitude())
                    .writeString("pickupLongitude", rawRecord.getPickupLongitude())
                    .writeString("dropoffLatitude", rawRecord.getDropoffLatitude())
                    .writeString("dropoffLongitude", rawRecord.getDropoffLongitude())
                    .writeString("pickupDatetime", rawRecord.getPickupDatetime())
                    .writeString("dropoffDatetime", rawRecord.getDropoffDatetime())
                    .writeString("route", rawRecord.getRoute())
                    .writeString("uuid", rawRecord.getUuid());

            return true;
        } else {
            return false;
        }
    }

    public Object fromData(Class clazz, PdxReader reader) {
        if (RawRecord.class.isAssignableFrom(clazz)) {
            RawRecord rawRecord = new RawRecord();
            rawRecord.setPickupLatitude(reader.readString("pickupLatitude"));
            rawRecord.setPickupLongitude(reader.readString("pickupLongitude"));
            rawRecord.setDropoffLatitude(reader.readString("dropoffLatitude"));
            rawRecord.setDropoffLongitude(reader.readString("dropoffLongitude"));
            rawRecord.setPickupDatetime(reader.readString("pickupDatetime"));
            rawRecord.setDropoffDatetime(reader.readString("dropoffDatetime"));
            rawRecord.setRoute(reader.readString("route"));
            rawRecord.setUuid(reader.readString("uuid"));

            return rawRecord;
        } else {
            return null;
        }
    }
}
