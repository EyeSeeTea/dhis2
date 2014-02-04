package org.hisp.dhis.importexport.dhis14.object;

import java.util.Date;

public class Dhis14CalculatedDataElement {
	

	private int dataElementId;
	private String dataElementName;
	private String dataElementShort;
	private String dataElementDescription;
	private String type;
	private String aggregationOperator;
	private Integer sortOrder;
	private Date lastUpdated;
	private String uid;
    private Integer calculated;
    
	public int getDataElementId() {
		return dataElementId;
	}
	public void setDataElementId(int dataElementId) {
		this.dataElementId = dataElementId;
	}
	public String getDataElementName() {
		return dataElementName;
	}
	public void setDataElementName(String dataElementName) {
		this.dataElementName = dataElementName;
	}
	public String getDataElementShort() {
		return dataElementShort;
	}
	public void setDataElementShort(String dataElementShort) {
		this.dataElementShort = dataElementShort;
	}
	public String getDataElementDescription() {
		return dataElementDescription;
	}
	public void setDataElementDescription(String dataElementDescription) {
		this.dataElementDescription = dataElementDescription;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAggregationOperator() {
		return aggregationOperator;
	}
	public void setAggregationOperator(String aggregationOperator) {
		this.aggregationOperator = aggregationOperator;
	}
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Integer getCalculated() {
		return calculated;
	}
	public void setCalculated(Integer calculated) {
		this.calculated = calculated;
	}
	
    
    
}
