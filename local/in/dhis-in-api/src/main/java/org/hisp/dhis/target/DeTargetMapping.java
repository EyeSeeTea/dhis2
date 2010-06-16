package org.hisp.dhis.target;

/*
 * Copyright (c) 2004-2009, University of Oslo
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

import java.io.Serializable;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;

@SuppressWarnings("serial")
public class DeTargetMapping implements Serializable
{
	 /**
     * The unique identifier for this Target.
     */
   // private int id;
    /**
     * All DataElements.
     */
    private DataElement dataelement;
    /**
     * Option Combos for DataElement.
     */
    private DataElementCategoryOptionCombo dataelementoptioncombo;
    /**
     * All DataElement associated with this Target.
     */
    private DataElement targetDataelement;
    /**
     * Option Combos for Targets.
     */
    private DataElementCategoryOptionCombo targetoptioncombo;
    
 // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DeTargetMapping()
    {
    }
    
    public DeTargetMapping(DataElement dataelement,DataElementCategoryOptionCombo dataelementoptioncombo,DataElement targetDataelement,DataElementCategoryOptionCombo targetoptioncombo)
    {
    	this.dataelement = dataelement;
    	this.dataelementoptioncombo = dataelementoptioncombo;
    	this.targetDataelement = targetDataelement;
    	this.targetoptioncombo = targetoptioncombo;
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

	/*public int getId() 
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
*/
	public DataElement getDataelement()
	{
		return dataelement;
	}

	public void setDataelement(DataElement dataelement)
	{
		this.dataelement = dataelement;
	}

	public DataElementCategoryOptionCombo getDataelementoptioncombo() {
		return dataelementoptioncombo;
	}

	public void setDataelementoptioncombo(
			DataElementCategoryOptionCombo dataelementoptioncombo) {
		this.dataelementoptioncombo = dataelementoptioncombo;
	}

	public DataElementCategoryOptionCombo getTargetoptioncombo() {
		return targetoptioncombo;
	}

	public void setTargetoptioncombo(
			DataElementCategoryOptionCombo targetoptioncombo) {
		this.targetoptioncombo = targetoptioncombo;
	}

	public DataElement getTargetDataelement()
	{
		return targetDataelement;
	}

	public void setTargetDataelement(DataElement targetDataelement)
	{
		this.targetDataelement = targetDataelement;
	}

	
}
