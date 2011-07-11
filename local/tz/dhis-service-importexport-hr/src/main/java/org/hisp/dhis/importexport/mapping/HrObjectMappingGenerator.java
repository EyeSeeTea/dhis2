package org.hisp.dhis.importexport.mapping;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.Map;

/**
 * This component is responsible for generating a mapping between the identifier
 * of an object prior to import and the identifier being auto-generated by the 
 * persistence medium during import. Setting the preview argument to true implies 
 * that no mapping takes place; simply returning the identifier prior to import.
 * 
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id: ObjectMappingGenerator.java 6425 2008-11-22 00:08:57Z larshelg $
 */
public interface HrObjectMappingGenerator
{
    String ID = HrObjectMappingGenerator.class.getName();
    
    Map<Object, Integer> getAttributeMapping(boolean skipMapping );
    
    Map<Object, Integer> getAttributeOptionsMapping( boolean skipMapping );
    
    Map<Object, Integer> getAttributeGroupMapping( boolean skipMapping );
    
    Map<Object, Integer> getAttributeOptionGroupMapping( boolean skipMapping );
    
    Map<Object, Integer> getHrDataSetMapping( boolean skipMapping );
    
    Map<Object, Integer> getDataValuesMapping( boolean skipMapping );
    
    Map<Object, Integer> getHistoryMapping( boolean skipMapping );
    
    Map<Object, Integer> getTrainingMapping( boolean skipMapping );
    
    Map<Object, Integer> getPersonMapping( boolean skipMapping );
    
    Map<Object, Integer> getTargetIndicator( boolean skipMapping );
    
    Map<Object, Integer> getInputTypeMapping( boolean skipMapping );
    
    Map<Object, Integer> getDataTypeMapping( boolean skipMapping );
    
    Map<Object, Integer> getHrOrganisationUnitMapping( boolean skipMapping );
}
