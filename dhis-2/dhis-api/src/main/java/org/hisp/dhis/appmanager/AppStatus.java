package org.hisp.dhis.appmanager;

/**
 * @author Stian Sandvold.
 */
public enum AppStatus
{
    OK, // Everything is OK
    DUPLICATE_APP, // App is already installed
    NAMESPACE_TAKEN, // Namespace is already protected
    INVALID_MANIFEST_FORMAT // Manifest is not parsable (json)
}
