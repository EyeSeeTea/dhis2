// Make the first letter lowercase
function lowercaseFirstLetter( string )
{
    return string.charAt( 0 ).toLowerCase() + string.slice( 1 );
}

// Remove all white spaces from a String
function removeWhiteSpace( string )
{
    return string.replace( / /g, '' )
}

// Remove empty strings from an array
function removeEmptyStringFromArray( array )
{
    for ( var i = 0; i < array.length; i++ )
    {
        if ( array[i] === "" )
        {
            array.splice( i, 1 );
        }
    }
    return array;
}

// Remove the last white space + comma from the metaDataUids list
function removeLastComma( string )
{
    var length = string.length;
    return string.substring( 0, length - 2 );
}

// Replace id with uid
function replaceIdWithUid( object )
{
    object.uid = object.id;
    delete object.id;
    return object;
}
