package org.hisp.dhis.cbhis.db;

import org.hisp.dhis.cbhis.model.AbstractModel;

public class OrgUnitRecordFilter extends AbstractModelRecordFilter{
	
	public OrgUnitRecordFilter(AbstractModel model) {
		super(model);
	}
	
	public boolean matches(byte[] suspect) {
		return super.matches(suspect);
	}

}
