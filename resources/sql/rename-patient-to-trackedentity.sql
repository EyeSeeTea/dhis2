

alter table patient rename to trackedentityinstance;
ALTER TABLE trackedentityinstance RENAME COLUMN patientid TO trackedentityinstanceid;
ALTER TABLE trackedentityinstance RENAME CONSTRAINT patient_pkey TO trackedentityinstance_pkey;
ALTER TABLE trackedentityinstance RENAME CONSTRAINT fk_patient_organisationunitid TO fk_trackedentityinstance_organisationunitid;
ALTER TABLE trackedentityinstance RENAME CONSTRAINT fk_user_patientid TO fk_user_trackedentityinstance;


alter table patientattribute rename to trackedentityattribute;
ALTER TABLE trackedentityattribute RENAME COLUMN patientattributeid TO trackedentityattributeid;
ALTER TABLE trackedentityattribute RENAME COLUMN patientattributegroupid TO trackedentityattributegroupid;
ALTER TABLE trackedentityattribute RENAME CONSTRAINT patientattribute_pkey TO trackedentityattribute_pkey;
ALTER TABLE trackedentityattribute RENAME CONSTRAINT patientattribute_code_key TO trackedentityattribute_code_key;
ALTER TABLE trackedentityattribute RENAME CONSTRAINT patientattribute_name_key TO trackedentityattribute_name_key;


alter table patientattributegroup rename to trackedentityattributegroup;
ALTER TABLE trackedentityattributegroup RENAME COLUMN patientattributegroupid TO trackedentityattributegroupid;
ALTER TABLE trackedentityattributegroup RENAME CONSTRAINT patientattributegroup_pkey TO trackedentityattributegroup_pkey;
ALTER TABLE trackedentityattributegroup RENAME CONSTRAINT patientattributegroup_code_key TO trackedentityattributegroup_code_key;
ALTER TABLE trackedentityattributegroup RENAME CONSTRAINT patientattributegroup_name_key TO trackedentityattributegroup_name_key;


alter table patientattributeoption rename to trackedentityattributeoption;
ALTER TABLE trackedentityattributeoption RENAME COLUMN patientattributeoptionid TO trackedentityattributeoptionid;
ALTER TABLE trackedentityattributeoption RENAME COLUMN patientattributeid TO trackedentityattributeid;
ALTER TABLE trackedentityattributeoption RENAME CONSTRAINT patientattributeoption_pkey TO trackedentityattributeoption_pkey;
ALTER TABLE trackedentityattributeoption RENAME CONSTRAINT fk_patientattributeoption_patientattributeid TO fk_attributeoption_attributeid;


alter table patientaudit rename to trackedentityaudit;
ALTER TABLE trackedentityaudit RENAME COLUMN patientauditid TO trackedentityauditid;
ALTER TABLE trackedentityaudit RENAME CONSTRAINT patientaudit_pkey TO trackedentityaudit_pkey;
ALTER TABLE trackedentityaudit RENAME CONSTRAINT fk_patientauditid_patientid TO fk_trackedentityauditid_trackedentityinstanceid;


alter table patientregistrationform rename to trackedentityform;
ALTER TABLE trackedentityform RENAME COLUMN patientregistrationformid TO trackedentityformid;
ALTER TABLE trackedentityform RENAME CONSTRAINT patientregistrationform_pkey TO trackedentityform_pkey;
ALTER TABLE trackedentityform RENAME CONSTRAINT fk_patientregistrationform_programid TO fk_trackedentityform_programid;
ALTER TABLE trackedentityform RENAME CONSTRAINT fk_patientregistrationform_dataentryformid TO fk_trackedentityform_dataentryformid;


alter table patientreminder rename to trackedentityinstancereminder;
ALTER TABLE trackedentityinstancereminder RENAME COLUMN patientreminderid TO trackedentityinstancereminderid;
ALTER TABLE trackedentityinstancereminder RENAME CONSTRAINT patientreminder_pkey TO trackedentityinstancereminder_pkey;
ALTER TABLE trackedentityinstancereminder RENAME CONSTRAINT fk_patientreminder_usergroup TO fk_trackedentityinstancereminder_programid;


alter table patientattributevalue rename to trackedentityattributevalue;
ALTER TABLE trackedentityattributevalue RENAME COLUMN patientid TO trackedentityinstanceid;
ALTER TABLE trackedentityattributevalue RENAME COLUMN patientattributeid TO trackedentityattributeid;
ALTER TABLE trackedentityattributevalue RENAME COLUMN patientattributeoptionid TO trackedentityattributeoptionid;
ALTER TABLE trackedentityattributevalue RENAME CONSTRAINT patientattributevalue_pkey TO trackedentityattributevalue_pkey;
ALTER TABLE trackedentityattributevalue RENAME CONSTRAINT fk_patientattributevalue_patientattributeid TO fk_attributevalue_attributeid;
ALTER TABLE trackedentityattributevalue RENAME CONSTRAINT fk_patientattributevalue_patientattributeoption TO fk_attributeValue_attributeoptionid;
ALTER TABLE trackedentityattributevalue RENAME CONSTRAINT fk_patientattributevalue_patientid TO fk_attributevalue_trackedentityinstanceid;

alter table patientcomment rename to trackedentitycomment;
ALTER TABLE trackedentitycomment RENAME COLUMN patientcommentid TO trackedentitycommentid;
ALTER TABLE trackedentitycomment RENAME CONSTRAINT patientcomment_pkey TO trackedentitycomment_pkey;


alter table patientdatavalue rename to trackedentitydatavalue;
ALTER TABLE trackedentitydatavalue RENAME CONSTRAINT patientdatavalue_pkey TO trackedentitydatavalue_pkey;
ALTER TABLE trackedentitydatavalue RENAME CONSTRAINT fk_patientdatavalue_dataelementid TO fk_entityinstancedatavalue_dataelementid;
ALTER TABLE trackedentitydatavalue RENAME CONSTRAINT fk_patientdatavalue_programstageinstanceid TO fk_entityinstancedatavalue_programstageinstanceid;


ALTER TABLE relationship RENAME COLUMN patientaid TO trackedentityinstanceaid;
ALTER TABLE relationship RENAME COLUMN patientbid TO trackedentityinstancebid;
ALTER TABLE relationship RENAME CONSTRAINT fk_relationship_patientida TO fk_relationship_trackedentityinstanceida;
ALTER TABLE relationship RENAME CONSTRAINT fk_relationship_patientidb TO fk_relationship_trackedentityinstanceidb;


ALTER TABLE patientmobilesetting rename to trackedentitymobilesetting;
ALTER TABLE trackedentitymobilesetting RENAME COLUMN patientmobilesettingid TO trackedentitymobilesettingid;
ALTER TABLE trackedentitymobilesetting RENAME CONSTRAINT patientmobilesetting_pkey TO trackedentitymobilesetting_pkey;


ALTER TABLE program_attributes RENAME COLUMN programattributeid TO programtrackedentityattributeid;
ALTER TABLE program_attributes RENAME COLUMN attributeid TO trackedentityattributeid; 


ALTER TABLE programinstance RENAME COLUMN patientcommentid TO trackedentitycommentid;
ALTER TABLE programinstance RENAME COLUMN patientid TO trackedentityinstanceid;


ALTER TABLE programstage_dataelements RENAME COLUMN patienttabularreportid TO trackedentitytabularreportid;

ALTER TABLE programstageinstance RENAME COLUMN patientcommentid TO trackedentitycommentid;
ALTER TABLE trackedentityattribute RENAME COLUMN patientmobilesettingid TO trackedentitymobilesettingid;
ALTER TABLE trackedentityattribute RENAME COLUMN sort_order_patientattributename TO sort_order_trackedentityattributename;

ALTER TABLE patientaggregatereportmembers RENAME TO trackedentityaggregatereportmembers;
ALTER TABLE trackedentityaggregatereportmembers RENAME COLUMN patientaggregatereportid TO trackedentityaggregatereportid;
ALTER TABLE trackedentityaudit RENAME COLUMN patientid TO trackedentityinstanceid;
ALTER TABLE patienttabularreportmembers RENAME TO trackedentitytabularreportmembers;
ALTER TABLE trackedentitytabularreportmembers RENAME COLUMN patienttabularreportid TO trackedentitytabularreportid;


ALTER TABLE programstageinstance_patients RENAME TO programstageinstance_trackedentityinstances;
ALTER TABLE programstageinstance_trackedentityinstances RENAME COLUMN patientid TO trackedentityinstanceid;


DROP TABLE patientaggregatereport_dimension;
DROP TABLE patientaggregatereport_filters;
DROP TABLE patientaggregatereportmembers;
DROP TABLE patientaggregatereportusergroupaccesses;
DROP TABLE patientaggregatereport;

DROP TABLE patienttabularreport_dimensions;
DROP TABLE patienttabularreport_filters;
DROP TABLE patienttabularreportmembers;
DROP TABLE patienttabularreportusergroupaccesses;
DROP TABLE patienttabularreport;

DROP TABLE patientmobilesetting;
DROP TABLE dashboarditem_patienttabularreports;

DROP TABLE patientregistrationform_attributes;
DROP TABLE patientregistrationform_fixedattributes;
DROP TABLE patientregistrationform_identifiertypes;
DROP TABLE patientregistrationform_attributes;

DROP TABLE patientidentifiertype;

