/* Copyright (c) 2006-2008 MetaCarta, Inc., published under the Clear BSD
 * license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */


/**
 * @requires OpenLayers/Layer/Grid.js
 * @requires OpenLayers/Layer/KaMap.js
 */

/**
 * Class: OpenLayers.Layer.KaMapCache
 * 
 * This class is designed to talk directly to a web-accessible ka-Map
 * cache generated by the precache2.php script.
 * 
 * To create a a new KaMapCache layer, you must indicate also the "i" parameter
 * (that will be used to calculate the file extension), and another special
 * parameter, object names "metaTileSize", with "h" (height) and "w" (width)
 * properties.
 * 
 *     // Create a new kaMapCache layer. 
 *     var kamap_base = new OpenLayers.Layer.KaMapCache(
 *         "Satellite",
 *         "http://www.example.org/web/acessible/cache",
 *         {g: "satellite", map: "world", i: 'png24', metaTileSize: {w: 5, h: 5} }
 *       );
 *    
 *     // Create an kaMapCache overlay layer (using "isBaseLayer: false"). 
 *     // Forces the output to be a "gif", using the "i" parameter.
 *     var kamap_overlay = new OpenLayers.Layer.KaMapCache(
 *         "Streets",
 *         "http://www.example.org/web/acessible/cache",
 *         {g: "streets", map: "world", i: "gif", metaTileSize: {w: 5, h: 5} },
 *         {isBaseLayer: false}
 *       );
 *
 * The cache URLs must look like: 
 *   var/cache/World/50000/Group_Name/def/t-440320/l20480
 * 
 * This means that the cache generated via tile.php will *not* work with
 *     this class, and should instead use the KaMap layer.
 *
 * More information is available in Ticket #1518.
 * 
 * Inherits from:
 *  - <OpenLayers.Layer.KaMap>
 *  - <OpenLayers.Layer.Grid>
 */
OpenLayers.Layer.KaMapCache = OpenLayers.Class(OpenLayers.Layer.KaMap, {

    /**
     * Constant: IMAGE_EXTENSIONS
     * {Object} Simple hash map to convert format to extension.
     */
    IMAGE_EXTENSIONS: {
        'jpeg': 'jpg',
        'gif' : 'gif',
        'png' : 'png',
        'png8' : 'png',
        'png24' : 'png',
        'dithered' : 'png'
    },
    
    /**
     * Constant: DEFAULT_FORMAT
     * {Object} Simple hash map to convert format to extension.
     */
    DEFAULT_FORMAT: 'jpeg',
    
    /**
     * Constructor: OpenLayers.Layer.KaMapCache
     * 
     * Parameters:
     * name - {String}
     * url - {String}
     * params - {Object} Parameters to be sent to the HTTP server in the
     *    query string for the tile. The format can be set via the 'i'
     *    parameter (defaults to jpg) , and the map should be set via 
     *    the 'map' parameter. It has been reported that ka-Map may behave
     *    inconsistently if your format parameter does not match the format
     *    parameter configured in your config.php. (See ticket #327 for more
     *    information.)
     * options - {Object} Additional options for the layer. Any of the 
     *     APIProperties listed on this layer, and any layer types it
     *     extends, can be overridden through the options parameter. 
     */
    initialize: function(name, url, params, options) {
        OpenLayers.Layer.KaMap.prototype.initialize.apply(this, arguments);
        this.extension = this.IMAGE_EXTENSIONS[this.params.i.toLowerCase() || DEFAULT_FORMAT];
    },

    /**
     * Method: getURL
     * 
     * Parameters:
     * bounds - {<OpenLayers.Bounds>} 
     * 
     * Returns:
     * {String} A string with the layer's url and parameters and also the 
     *          passed-in bounds and appropriate tile size specified as 
     *          parameters
     */
    getURL: function (bounds) {
        bounds = this.adjustBounds(bounds);
        var mapRes = this.map.getResolution();
        var scale = Math.round((this.map.getScale() * 10000)) / 10000;
        var pX = Math.round(bounds.left / mapRes);
        var pY = -Math.round(bounds.top / mapRes);

        var metaX = Math.floor(pX / this.tileSize.w / this.params.metaTileSize.w) * this.tileSize.w * this.params.metaTileSize.w;
        var metaY = Math.floor(pY / this.tileSize.h / this.params.metaTileSize.h) * this.tileSize.h * this.params.metaTileSize.h;

        // if url is not a string, it should be an array of strings,
        // in which case we will deterministically select one of them in
        // order to evenly distribute requests to different urls.
        //
        var url = this.url;
        if (url instanceof Array) {
            url = this.selectUrl(paramsString, url);
        }  
    
        var components = [
            url,
            "/",
            this.params.map,
            "/",
            scale,
            "/",
            this.params.g.replace(/\s/g, '_'),
            "/def/t", 
            metaY,
            "/l",
            metaX,
            "/t",
            pY,
            "l",
            pX,
            ".",
            this.extension
          ];
          
        return components.join("");
    },

    CLASS_NAME: "OpenLayers.Layer.KaMapCache"
});
