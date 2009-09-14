package org.hisp.dhis.vn.report;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */

import java.util.List;

import org.hisp.dhis.dataelement.DataElement;

public class DataElementOrderInGroup {
	
	private int id;

	private int dataElementGroupId;

	private String dataElementGroupName;

	/*
	 * This is data element of this group
	 */
	private List<DataElement> dataElements;

	public DataElementOrderInGroup() {
	}

	public int getDataElementGroupId() {
		return dataElementGroupId;
	}

	public void setDataElementGroupId(int dataElementGroupId) {
		this.dataElementGroupId = dataElementGroupId;
	}

	public List<DataElement> getDataElements() {
		return dataElements;
	}

	public void setDataElements(List<DataElement> dataElements) {
		this.dataElements = dataElements;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDataElementGroupName() {
		return dataElementGroupName;
	}

	public void setDataElementGroupName(String dataElementGroupName) {
		this.dataElementGroupName = dataElementGroupName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataElementOrderInGroup other = (DataElementOrderInGroup) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
