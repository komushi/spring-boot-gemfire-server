package io.pivotal.spring.gemfire.async;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gemstone.gemfire.pdx.PdxSerializable;
import com.gemstone.gemfire.pdx.PdxWriter;
import com.gemstone.gemfire.pdx.PdxReader;

import org.springframework.util.ObjectUtils;

@Region("RegionRaw")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class RawRecord implements PdxSerializable {

	protected static final RawPdxSerializer pdxSerializer = new RawPdxSerializer();

	public RawRecord(){
		super();
	}
	
	@Id
	private String uuid;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uniqueKey) {
		this.uuid = uuid;
	}


	private String pickupLongitude;
	private String pickupLatitude;
	private String dropoffLongitude;
	private String dropoffLatitude;
	private String pickupDatetime;
	private String dropoffDatetime;
	private String route;

	public String getPickupLongitude() {
		return pickupLongitude;
	}
	public void setPickupLongitude(String pickupLongitude) {
		this.pickupLongitude = pickupLongitude;
	}
	public String getPickupLatitude() {
		return pickupLatitude;
	}
	public void setPickupLatitude(String pickupLatitude) {
		this.pickupLatitude = pickupLatitude;
	}
	public String getDropoffLongitude() {
		return dropoffLongitude;
	}
	public void setDropoffLongitude(String dropoffLongitude) {
		this.dropoffLongitude = dropoffLongitude;
	}
	public String getDropoffLatitude() {
		return dropoffLatitude;
	}
	public void setDropoffLatitude(String dropoffLatitude) {
		this.dropoffLatitude = dropoffLatitude;
	}
	public String getPickupDatetime() {
		return pickupDatetime;
	}
	public void setPickupDatetime(String pickupDatetime) {
		this.pickupDatetime = pickupDatetime;
	}
	public String getDropoffDatetime() {
		return dropoffDatetime;
	}
	public void setDropoffDatetime(String dropoffDatetime) {
		this.dropoffDatetime = dropoffDatetime;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}


	@Override
	public void fromData(PdxReader reader) {
		RawRecord rawRecord = (RawRecord) pdxSerializer.fromData(RawRecord.class, reader);

		if (rawRecord != null) {
			this.uuid = rawRecord.getUuid();
			this.route = rawRecord.getRoute();
		}
	}

	@Override
	public void toData(PdxWriter writer) {
		pdxSerializer.toData(this, writer);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof RawRecord)) {
			return false;
		}

		RawRecord that = (RawRecord) obj;

		return ObjectUtils.nullSafeEquals(this.getUuid(), that.getUuid());
	}

	@Override
	public int hashCode() {
		int hashValue = 17;
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getUuid());
		return hashValue;
	}

	@Override
	public String toString() {
		return String.format("{ @type = %1$s, id = %2$d, route = %3$s }",
				getClass().getName(), getUuid(), getRoute());
	}
}
