/* Copyright (c) 2006-2008 MetaCarta, Inc., published under the Clear BSD
 * license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */


/**
 * Namespace: Util
 */
OpenLayers.Util = {};

/** 
 * Function: getElement
 * This is the old $() from prototype
 */
OpenLayers.Util.getElement = function() {
    var elements = [];

    for (var i=0, len=arguments.length; i<len; i++) {
        var element = arguments[i];
        if (typeof element == 'string') {
            element = document.getElementById(element);
        }
        if (arguments.length == 1) {
            return element;
        }
        elements.push(element);
    }
    return elements;
};

/** 
 * Maintain $() from prototype
 */
if ($ == null) {
    var $ = OpenLayers.Util.getElement;
}

/**
 * APIFunction: extend
 * Copy all properties of a source object to a destination object.  Modifies
 *     the passed in destination object.  Any properties on the source object
 *     that are set to undefined will not be (re)set on the destination object.
 *
 * Parameters:
 * destination - {Object} The object that will be modified
 * source - {Object} The object with properties to be set on the destination
 *
 * Returns:
 * {Object} The destination object.
 */
OpenLayers.Util.extend = function(destination, source) {
    destination = destination || {};
    if(source) {
        for(var property in source) {
            var value = source[property];
            if(value !== undefined) {
                destination[property] = value;
            }
        }

        /**
         * IE doesn't include the toString property when iterating over an object's
         * properties with the for(property in object) syntax.  Explicitly check if
         * the source has its own toString property.
         */

        /*
         * FF/Windows < 2.0.0.13 reports "Illegal operation on WrappedNative
         * prototype object" when calling hawOwnProperty if the source object
         * is an instance of window.Event.
         */

        var sourceIsEvt = typeof window.Event == "function"
                          && source instanceof window.Event;

        if(!sourceIsEvt
           && source.hasOwnProperty && source.hasOwnProperty('toString')) {
            destination.toString = source.toString;
        }
    }
    return destination;
};


/** 
 * Function: removeItem
 * Remove an object from an array. Iterates through the array
 *     to find the item, then removes it.
 *
 * Parameters:
 * array - {Array}
 * item - {Object}
 * 
 * Return
 * {Array} A reference to the array
 */
OpenLayers.Util.removeItem = function(array, item) {
    for(var i = array.length - 1; i >= 0; i--) {
        if(array[i] == item) {
            array.splice(i,1);
            //break;more than once??
        }
    }
    return array;
};

/**
 * Function: clearArray
 * *Deprecated*. This function will disappear in 3.0.
 * Please use "array.length = 0" instead.
 * 
 * Parameters:
 * array - {Array}
 */
OpenLayers.Util.clearArray = function(array) {
    OpenLayers.Console.warn(
        OpenLayers.i18n(
            "methodDeprecated", {'newMethod': 'array = []'}
        )
    );
    array.length = 0;
};

/** 
 * Function: indexOf
 * Seems to exist already in FF, but not in MOZ.
 * 
 * Parameters:
 * array - {Array}
 * obj - {Object}
 * 
 * Returns:
 * {Integer} The index at, which the object was found in the array.
 *           If not found, returns -1.
 */
OpenLayers.Util.indexOf = function(array, obj) {

    for(var i=0, len=array.length; i<len; i++) {
        if (array[i] == obj) {
            return i;
        }
    }
    return -1;   
};



/**
 * Function: modifyDOMElement
 * 
 * Modifies many properties of a DOM element all at once.  Passing in 
 * null to an individual parameter will avoid setting the attribute.
 *
 * Parameters:
 * id - {String} The element id attribute to set.
 * px - {<OpenLayers.Pixel>} The left and top style position.
 * sz - {<OpenLayers.Size>}  The width and height style attributes.
 * position - {String}       The position attribute.  eg: absolute, 
 *                           relative, etc.
 * border - {String}         The style.border attribute.  eg:
 *                           solid black 2px
 * overflow - {String}       The style.overview attribute.  
 * opacity - {Float}         Fractional value (0.0 - 1.0)
 */
OpenLayers.Util.modifyDOMElement = function(element, id, px, sz, position, 
                                            border, overflow, opacity) {

    if (id) {
        element.id = id;
    }
    if (px) {
        element.style.left = px.x + "px";
        element.style.top = px.y + "px";
    }
    if (sz) {
        element.style.width = sz.w + "px";
        element.style.height = sz.h + "px";
    }
    if (position) {
        element.style.position = position;
    }
    if (border) {
        element.style.border = border;
    }
    if (overflow) {
        element.style.overflow = overflow;
    }
    if (parseFloat(opacity) >= 0.0 && parseFloat(opacity) < 1.0) {
        element.style.filter = 'alpha(opacity=' + (opacity * 100) + ')';
        element.style.opacity = opacity;
    } else if (parseFloat(opacity) == 1.0) {
        element.style.filter = '';
        element.style.opacity = '';
    }
};

/** 
 * Function: createDiv
 * Creates a new div and optionally set some standard attributes.
 * Null may be passed to each parameter if you do not wish to
 * set a particular attribute.
 * Note - zIndex is NOT set on the resulting div.
 * 
 * Parameters:
 * id - {String} An identifier for this element.  If no id is
 *               passed an identifier will be created 
 *               automatically.
 * px - {<OpenLayers.Pixel>} The element left and top position. 
 * sz - {<OpenLayers.Size>} The element width and height.
 * imgURL - {String} A url pointing to an image to use as a 
 *                   background image.
 * position - {String} The style.position value. eg: absolute,
 *                     relative etc.
 * border - {String} The the style.border value. 
 *                   eg: 2px solid black
 * overflow - {String} The style.overflow value. Eg. hidden
 * opacity - {Float} Fractional value (0.0 - 1.0)
 * 
 * Returns: 
 * {DOMElement} A DOM Div created with the specified attributes.
 */
OpenLayers.Util.createDiv = function(id, px, sz, imgURL, position, 
                                     border, overflow, opacity) {

    var dom = document.createElement('div');

    if (imgURL) {
        dom.style.backgroundImage = 'url(' + imgURL + ')';
    }

    //set generic properties
    if (!id) {
        id = OpenLayers.Util.createUniqueID("OpenLayersDiv");
    }
    if (!position) {
        position = "absolute";
    }
    OpenLayers.Util.modifyDOMElement(dom, id, px, sz, position, 
                                     border, overflow, opacity);

    return dom;
};

/**
 * Function: createImage
 * Creates an img element with specific attribute values.
 *  
 * Parameters:
 * id - {String} The id field for the img.  If none assigned one will be
 *               automatically generated.
 * px - {<OpenLayers.Pixel>} The left and top positions.
 * sz - {<OpenLayers.Size>} The style.width and style.height values.
 * imgURL - {String} The url to use as the image source.
 * position - {String} The style.position value.
 * border - {String} The border to place around the image.
 * delayDisplay - {Boolean} If true waits until the image has been
 *                          loaded.
 * opacity - {Float} Fractional value (0.0 - 1.0)
 * 
 * Returns:
 * {DOMElement} A DOM Image created with the specified attributes.
 */
OpenLayers.Util.createImage = function(id, px, sz, imgURL, position, border,
                                       opacity, delayDisplay) {

    var image = document.createElement("img");

    //set generic properties
    if (!id) {
        id = OpenLayers.Util.createUniqueID("OpenLayersDiv");
    }
    if (!position) {
        position = "relative";
    }
    OpenLayers.Util.modifyDOMElement(image, id, px, sz, position, 
                                     border, null, opacity);

    if(delayDisplay) {
        image.style.display = "none";
        OpenLayers.Event.observe(image, "load", 
            OpenLayers.Function.bind(OpenLayers.Util.onImageLoad, image));
        OpenLayers.Event.observe(image, "error", 
            OpenLayers.Function.bind(OpenLayers.Util.onImageLoadError, image));
        
    }
    
    //set special properties
    image.style.alt = id;
    image.galleryImg = "no";
    if (imgURL) {
        image.src = imgURL;
    }


        
    return image;
};

/**
 * Function: setOpacity
 * *Deprecated*.  This function has been deprecated. Instead, please use 
 *     <OpenLayers.Util.modifyDOMElement> 
 *     or 
 *     <OpenLayers.Util.modifyAlphaImageDiv>
 * 
 * Set the opacity of a DOM Element
 *     Note that for this function to work in IE, elements must "have layout"
 *     according to:
 *     http://msdn.microsoft.com/workshop/author/dhtml/reference/properties/haslayout.asp
 *
 * Parameters:
 * element - {DOMElement} Set the opacity on this DOM element
 * opacity - {Float} Opacity value (0.0 - 1.0)
 */
OpenLayers.Util.setOpacity = function(element, opacity) {
    OpenLayers.Util.modifyDOMElement(element, null, null, null,
                                     null, null, null, opacity);
};

/**
 * Function: onImageLoad
 * Bound to image load events.  For all images created with <createImage> or
 *     <createAlphaImageDiv>, this function will be bound to the load event.
 */
OpenLayers.Util.onImageLoad = function() {
    // The complex check here is to solve issues described in #480.
    // Every time a map view changes, it increments the 'viewRequestID' 
    // property. As the requests for the images for the new map view are sent
    // out, they are tagged with this unique viewRequestID. 
    // 
    // If an image has no viewRequestID property set, we display it regardless, 
    // but if it does have a viewRequestID property, we check that it matches 
    // the viewRequestID set on the map.
    // 
    // If the viewRequestID on the map has changed, that means that the user
    // has changed the map view since this specific request was sent out, and
    // therefore this tile does not need to be displayed (so we do not execute
    // this code that turns its display on).
    //
    if (!this.viewRequestID ||
        (this.map && this.viewRequestID == this.map.viewRequestID)) { 
        this.style.backgroundColor = null;
        this.style.display = "";  
    }
};

/**
 * Property: onImageLoadErrorColor
 * {String} The color tiles with load errors will turn.
 *          Default is "pink"
 */
OpenLayers.Util.onImageLoadErrorColor = "pink";

/**
 * Property: IMAGE_RELOAD_ATTEMPTS
 * {Integer} How many times should we try to reload an image before giving up?
 *           Default is 0
 */
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 0;

/**
 * Function: onImageLoadError 
 */
OpenLayers.Util.onImageLoadError = function() {
    this._attempts = (this._attempts) ? (this._attempts + 1) : 1;
    if (this._attempts <= OpenLayers.IMAGE_RELOAD_ATTEMPTS) {
        var urls = this.urls;
        if (urls && urls instanceof Array && urls.length > 1){
            var src = this.src.toString();
            var current_url, k;
            for (k = 0; current_url = urls[k]; k++){
                if(src.indexOf(current_url) != -1){
                    break;
                }
            }
            var guess = Math.floor(urls.length * Math.random());
            var new_url = urls[guess];
            k = 0;
            while(new_url == current_url && k++ < 4){
                guess = Math.floor(urls.length * Math.random());
                new_url = urls[guess];
            }
            this.src = src.replace(current_url, new_url);
        } else {
            this.src = this.src;
        }
    } else {
        this.style.backgroundColor = OpenLayers.Util.onImageLoadErrorColor;
    }
    this.style.display = "";
};

/**
 * Function: alphaHack
 * Checks whether it's necessary (and possible) to use the png alpha
 * hack which allows alpha transparency for png images under Internet
 * Explorer.
 * 
 * Returns:
 * {Boolean} true if alpha has is necessary and possible, false otherwise.
 */
OpenLayers.Util.alphaHack = function() {
    var arVersion = navigator.appVersion.split("MSIE");
    var version = parseFloat(arVersion[1]);
    var filter = false;
    
    // IEs4Lin dies when trying to access document.body.filters, because 
    // the property is there, but requires a DLL that can't be provided. This
    // means that we need to wrap this in a try/catch so that this can
    // continue.
    
    try { 
        filter = !!(document.body.filters);
    } catch (e) {
    }    
    
    return ( filter &&
                      (version >= 5.5) && (version < 7) );
};

/** 
 * Function: modifyAlphaImageDiv
 * 
 * div - {DOMElement} Div containing Alpha-adjusted Image
 * id - {String}
 * px - {<OpenLayers.Pixel>}
 * sz - {<OpenLayers.Size>}
 * imgURL - {String}
 * position - {String}
 * border - {String}
 * sizing {String} 'crop', 'scale', or 'image'. Default is "scale"
 * opacity - {Float} Fractional value (0.0 - 1.0)
 */ 
OpenLayers.Util.modifyAlphaImageDiv = function(div, id, px, sz, imgURL, 
                                               position, border, sizing, 
                                               opacity) {

    OpenLayers.Util.modifyDOMElement(div, id, px, sz, position,
                                     null, null, opacity);

    var img = div.childNodes[0];

    if (imgURL) {
        img.src = imgURL;
    }
    OpenLayers.Util.modifyDOMElement(img, div.id + "_innerImage", null, sz, 
                                     "relative", border);
    
    if (OpenLayers.Util.alphaHack()) {
        if(div.style.display != "none") {
            div.style.display = "inline-block";
        }
        if (sizing == null) {
            sizing = "scale";
        }
        
        div.style.filter = "progid:DXImageTransform.Microsoft" +
                           ".AlphaImageLoader(src='" + img.src + "', " +
                           "sizingMethod='" + sizing + "')";
        if (parseFloat(div.style.opacity) >= 0.0 && 
            parseFloat(div.style.opacity) < 1.0) {
            div.style.filter += " alpha(opacity=" + div.style.opacity * 100 + ")";
        }

        img.style.filter = "alpha(opacity=0)";
    }
};

/** 
 * Function: createAlphaImageDiv
 * 
 * id - {String}
 * px - {<OpenLayers.Pixel>}
 * sz - {<OpenLayers.Size>}
 * imgURL - {String}
 * position - {String}
 * border - {String}
 * sizing {String} 'crop', 'scale', or 'image'. Default is "scale"
 * delayDisplay{Boolean}
 * 
 * Returns:
 * {DOMElement} A DOM Div created with a DOM Image inside it. If the hack is 
 *              needed for transparency in IE, it is added.
 */ 
OpenLayers.Util.createAlphaImageDiv = function(id, px, sz, imgURL, 
                                               position, border, sizing, 
                                               opacity, delayDisplay) {
    
    var div = OpenLayers.Util.createDiv();
    var img = OpenLayers.Util.createImage(null, null, null, null, null, null, 
                                          null, false);
    div.appendChild(img);

    if (delayDisplay) {
        img.style.display = "none";
        OpenLayers.Event.observe(img, "load",
            OpenLayers.Function.bind(OpenLayers.Util.onImageLoad, div));
        OpenLayers.Event.observe(img, "error",
            OpenLayers.Function.bind(OpenLayers.Util.onImageLoadError, div));
    }

    OpenLayers.Util.modifyAlphaImageDiv(div, id, px, sz, imgURL, position, 
                                        border, sizing, opacity);
    
    return div;
};


/** 
 * Function: upperCaseObject
 * Creates a new hashtable and copies over all the keys from the 
 *     passed-in object, but storing them under an uppercased
 *     version of the key at which they were stored.
 * 
 * Parameters: 
 * object - {Object}
 * 
 * Returns: 
 * {Object} A new Object with all the same keys but uppercased
 */
OpenLayers.Util.upperCaseObject = function (object) {
    var uObject = {};
    for (var key in object) {
        uObject[key.toUpperCase()] = object[key];
    }
    return uObject;
};

/** 
 * Function: applyDefaults
 * Takes an object and copies any properties that don't exist from
 *     another properties, by analogy with OpenLayers.Util.extend() from
 *     Prototype.js.
 * 
 * Parameters:
 * to - {Object} The destination object.
 * from - {Object} The source object.  Any properties of this object that
 *     are undefined in the to object will be set on the to object.
 *
 * Returns:
 * {Object} A reference to the to object.  Note that the to argument is modified
 *     in place and returned by this function.
 */
OpenLayers.Util.applyDefaults = function (to, from) {
    to = to || {};
    /*
     * FF/Windows < 2.0.0.13 reports "Illegal operation on WrappedNative
     * prototype object" when calling hawOwnProperty if the source object is an
     * instance of window.Event.
     */
    var fromIsEvt = typeof window.Event == "function"
                    && from instanceof window.Event;

    for (var key in from) {
        if (to[key] === undefined ||
            (!fromIsEvt && from.hasOwnProperty
             && from.hasOwnProperty(key) && !to.hasOwnProperty(key))) {
            to[key] = from[key];
        }
    }
    /**
     * IE doesn't include the toString property when iterating over an object's
     * properties with the for(property in object) syntax.  Explicitly check if
     * the source has its own toString property.
     */
    if(!fromIsEvt && from && from.hasOwnProperty
       && from.hasOwnProperty('toString') && !to.hasOwnProperty('toString')) {
        to.toString = from.toString;
    }
    
    return to;
};

/**
 * Function: getParameterString
 * 
 * Parameters:
 * params - {Object}
 * 
 * Returns:
 * {String} A concatenation of the properties of an object in 
 *          http parameter notation. 
 *          (ex. <i>"key1=value1&key2=value2&key3=value3"</i>)
 *          If a parameter is actually a list, that parameter will then
 *          be set to a comma-seperated list of values (foo,bar) instead
 *          of being URL escaped (foo%3Abar). 
 */
OpenLayers.Util.getParameterString = function(params) {
    var paramsArray = [];
    
    for (var key in params) {
      var value = params[key];
      if ((value != null) && (typeof value != 'function')) {
        var encodedValue;
        if (typeof value == 'object' && value.constructor == Array) {
          /* value is an array; encode items and separate with "," */
          var encodedItemArray = [];
          for (var itemIndex=0, len=value.length; itemIndex<len; itemIndex++) {
            encodedItemArray.push(encodeURIComponent(value[itemIndex]));
          }
          encodedValue = encodedItemArray.join(",");
        }
        else {
          /* value is a string; simply encode */
          encodedValue = encodeURIComponent(value);
        }
        paramsArray.push(encodeURIComponent(key) + "=" + encodedValue);
      }
    }
    
    return paramsArray.join("&");
};

/**
 * Property: ImgPath
 * {String} Default is ''.
 */
OpenLayers.ImgPath = '';

/** 
 * Function: getImagesLocation
 * 
 * Returns:
 * {String} The fully formatted image location string
 */
OpenLayers.Util.getImagesLocation = function() {
    return OpenLayers.ImgPath || (OpenLayers._getScriptLocation() + "img/");
};


/** 
 * Function: Try
 * Execute functions until one of them doesn't throw an error. 
 *     Capitalized because "try" is a reserved word in JavaScript.
 *     Taken directly from OpenLayers.Util.Try()
 * 
 * Parameters:
 * [*] - {Function} Any number of parameters may be passed to Try()
 *    It will attempt to execute each of them until one of them 
 *    successfully executes. 
 *    If none executes successfully, returns null.
 * 
 * Returns:
 * {*} The value returned by the first successfully executed function.
 */
OpenLayers.Util.Try = function() {
    var returnValue = null;

    for (var i=0, len=arguments.length; i<len; i++) {
      var lambda = arguments[i];
      try {
        returnValue = lambda();
        break;
      } catch (e) {}
    }

    return returnValue;
};


/** 
 * Function: getNodes
 * 
 * These could/should be made namespace aware?
 * 
 * Parameters:
 * p - {}
 * tagName - {String}
 * 
 * Returns:
 * {Array}
 */
OpenLayers.Util.getNodes=function(p, tagName) {
    var nodes = OpenLayers.Util.Try(
        function () {
            return OpenLayers.Util._getNodes(p.documentElement.childNodes,
                                            tagName);
        },
        function () {
            return OpenLayers.Util._getNodes(p.childNodes, tagName);
        }
    );
    return nodes;
};

/**
 * Function: _getNodes
 * 
 * Parameters:
 * nodes - {Array}
 * tagName - {String}
 * 
 * Returns:
 * {Array}
 */
OpenLayers.Util._getNodes=function(nodes, tagName) {
    var retArray = [];
    for (var i=0, len=nodes.length; i<len; i++) {
        if (nodes[i].nodeName==tagName) {
            retArray.push(nodes[i]);
        }
    }

    return retArray;
};



/**
 * Function: getTagText
 * 
 * Parameters:
 * parent - {}
 * item - {String}
 * index - {Integer}
 * 
 * Returns:
 * {String}
 */
OpenLayers.Util.getTagText = function (parent, item, index) {
    var result = OpenLayers.Util.getNodes(parent, item);
    if (result && (result.length > 0))
    {
        if (!index) {
            index=0;
        }
        if (result[index].childNodes.length > 1) {
            return result.childNodes[1].nodeValue; 
        }
        else if (result[index].childNodes.length == 1) {
            return result[index].firstChild.nodeValue; 
        }
    } else { 
        return ""; 
    }
};

/**
 * Function: getXmlNodeValue
 * 
 * Parameters:
 * node - {XMLNode}
 * 
 * Returns:
 * {String} The text value of the given node, without breaking in firefox or IE
 */
OpenLayers.Util.getXmlNodeValue = function(node) {
    var val = null;
    OpenLayers.Util.Try( 
        function() {
            val = node.text;
            if (!val) {
                val = node.textContent;
            }
            if (!val) {
                val = node.firstChild.nodeValue;
            }
        }, 
        function() {
            val = node.textContent;
        }); 
    return val;
};

/** 
 * Function: mouseLeft
 * 
 * Parameters:
 * evt - {Event}
 * div - {HTMLDivElement}
 * 
 * Returns:
 * {Boolean}
 */
OpenLayers.Util.mouseLeft = function (evt, div) {
    // start with the element to which the mouse has moved
    var target = (evt.relatedTarget) ? evt.relatedTarget : evt.toElement;
    // walk up the DOM tree.
    while (target != div && target != null) {
        target = target.parentNode;
    }
    // if the target we stop at isn't the div, then we've left the div.
    return (target != div);
};

/**
 * Function: rad
 * 
 * Parameters:
 * x - {Float}
 * 
 * Returns:
 * {Float}
 */
OpenLayers.Util.rad = function(x) {return x*Math.PI/180;};

/**
 * Function: distVincenty
 * Given two objects representing points with geographic coordinates, this
 *     calculates the distance between those points on the surface of an
 *     ellipsoid.
 * 
 * Parameters:
 * p1 - {<OpenLayers.LonLat>} (or any object with both .lat, .lon properties)
 * p2 - {<OpenLayers.LonLat>} (or any object with both .lat, .lon properties)
 * 
 * Returns:
 * {Float} The distance (in km) between the two input points as measured on an
 *     ellipsoid.  Note that the input point objects must be in geographic
 *     coordinates (decimal degrees) and the return distance is in kilometers.
 */
OpenLayers.Util.distVincenty=function(p1, p2) {
    var a = 6378137, b = 6356752.3142,  f = 1/298.257223563;
    var L = OpenLayers.Util.rad(p2.lon - p1.lon);
    var U1 = Math.atan((1-f) * Math.tan(OpenLayers.Util.rad(p1.lat)));
    var U2 = Math.atan((1-f) * Math.tan(OpenLayers.Util.rad(p2.lat)));
    var sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
    var sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
    var lambda = L, lambdaP = 2*Math.PI;
    var iterLimit = 20;
    while (Math.abs(lambda-lambdaP) > 1e-12 && --iterLimit>0) {
        var sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
        var sinSigma = Math.sqrt((cosU2*sinLambda) * (cosU2*sinLambda) +
        (cosU1*sinU2-sinU1*cosU2*cosLambda) * (cosU1*sinU2-sinU1*cosU2*cosLambda));
        if (sinSigma==0) {
            return 0;  // co-incident points
        }
        var cosSigma = sinU1*sinU2 + cosU1*cosU2*cosLambda;
        var sigma = Math.atan2(sinSigma, cosSigma);
        var alpha = Math.asin(cosU1 * cosU2 * sinLambda / sinSigma);
        var cosSqAlpha = Math.cos(alpha) * Math.cos(alpha);
        var cos2SigmaM = cosSigma - 2*sinU1*sinU2/cosSqAlpha;
        var C = f/16*cosSqAlpha*(4+f*(4-3*cosSqAlpha));
        lambdaP = lambda;
        lambda = L + (1-C) * f * Math.sin(alpha) *
        (sigma + C*sinSigma*(cos2SigmaM+C*cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)));
    }
    if (iterLimit==0) {
        return NaN;  // formula failed to converge
    }
    var uSq = cosSqAlpha * (a*a - b*b) / (b*b);
    var A = 1 + uSq/16384*(4096+uSq*(-768+uSq*(320-175*uSq)));
    var B = uSq/1024 * (256+uSq*(-128+uSq*(74-47*uSq)));
    var deltaSigma = B*sinSigma*(cos2SigmaM+B/4*(cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)-
        B/6*cos2SigmaM*(-3+4*sinSigma*sinSigma)*(-3+4*cos2SigmaM*cos2SigmaM)));
    var s = b*A*(sigma-deltaSigma);
    var d = s.toFixed(3)/1000; // round to 1mm precision
    return d;
};

/**
 * Function: getParameters
 * Parse the parameters from a URL or from the current page itself into a 
 *     JavaScript Object. Note that parameter values with commas are separated
 *     out into an Array.
 * 
 * Parameters:
 * url - {String} Optional url used to extract the query string.
 *                If null, query string is taken from page location.
 * 
 * Returns:
 * {Object} An object of key/value pairs from the query string.
 */
OpenLayers.Util.getParameters = function(url) {
    // if no url specified, take it from the location bar
    url = url || window.location.href;

    //parse out parameters portion of url string
    var paramsString = "";
    if (OpenLayers.String.contains(url, '?')) {
        var start = url.indexOf('?') + 1;
        var end = OpenLayers.String.contains(url, "#") ?
                    url.indexOf('#') : url.length;
        paramsString = url.substring(start, end);
    }
        
    var parameters = {};
    var pairs = paramsString.split(/[&;]/);
    for(var i=0, len=pairs.length; i<len; ++i) {
        var keyValue = pairs[i].split('=');
        if (keyValue[0]) {
            var key = decodeURIComponent(keyValue[0]);
            var value = keyValue[1] || ''; //empty string if no value

            //decode individual values
            value = value.split(",");
            for(var j=0, jlen=value.length; j<jlen; j++) {
                value[j] = decodeURIComponent(value[j]);
            }

            //if there's only one value, do not return as array                    
            if (value.length == 1) {
                value = value[0];
            }                
            
            parameters[key] = value;
         }
     }
    return parameters;
};

/**
 * Function: getArgs
 * *Deprecated*.  Will be removed in 3.0.  Please use instead
 *     <OpenLayers.Util.getParameters>
 * 
 * Parameters:
 * url - {String} Optional url used to extract the query string.
 *                If null, query string is taken from page location.
 * 
 * Returns:
 * {Object} An object of key/value pairs from the query string.
 */
OpenLayers.Util.getArgs = function(url) {
    OpenLayers.Console.warn(
        OpenLayers.i18n(
            "methodDeprecated", {'newMethod': 'OpenLayers.Util.getParameters'}
        )
    );
    return OpenLayers.Util.getParameters(url);
};

/**
 * Property: lastSeqID
 * {Integer} The ever-incrementing count variable.
 *           Used for generating unique ids.
 */
OpenLayers.Util.lastSeqID = 0;

/**
 * Function: createUniqueID
 * Create a unique identifier for this session.  Each time this function
 *     is called, a counter is incremented.  The return will be the optional
 *     prefix (defaults to "id_") appended with the counter value.
 * 
 * Parameters:
 * prefix {String} Optionsal string to prefix unique id. Default is "id_".
 * 
 * Returns:
 * {String} A unique id string, built on the passed in prefix.
 */
OpenLayers.Util.createUniqueID = function(prefix) {
    if (prefix == null) {
        prefix = "id_";
    }
    OpenLayers.Util.lastSeqID += 1; 
    return prefix + OpenLayers.Util.lastSeqID;        
};

/**
 * Constant: INCHES_PER_UNIT
 * {Object} Constant inches per unit -- borrowed from MapServer mapscale.c
 * derivation of nautical miles from http://en.wikipedia.org/wiki/Nautical_mile
 */
OpenLayers.INCHES_PER_UNIT = { 
    'inches': 1.0,
    'ft': 12.0,
    'mi': 63360.0,
    'm': 39.3701,
    'km': 39370.1,
    'dd': 4374754,
    'yd': 36
};
OpenLayers.INCHES_PER_UNIT["in"]= OpenLayers.INCHES_PER_UNIT.inches;
OpenLayers.INCHES_PER_UNIT["degrees"] = OpenLayers.INCHES_PER_UNIT.dd;
OpenLayers.INCHES_PER_UNIT["nmi"] = 1852 * OpenLayers.INCHES_PER_UNIT.m;

/** 
 * Constant: DOTS_PER_INCH
 * {Integer} 72 (A sensible default)
 */
OpenLayers.DOTS_PER_INCH = 72;

/**
 * Function: normalzeScale
 * 
 * Parameters:
 * scale - {float}
 * 
 * Returns:
 * {Float} A normalized scale value, in 1 / X format. 
 *         This means that if a value less than one ( already 1/x) is passed
 *         in, it just returns scale directly. Otherwise, it returns 
 *         1 / scale
 */
OpenLayers.Util.normalizeScale = function (scale) {
    var normScale = (scale > 1.0) ? (1.0 / scale) 
                                  : scale;
    return normScale;
};

/**
 * Function: getResolutionFromScale
 * 
 * Parameters:
 * scale - {Float}
 * units - {String} Index into OpenLayers.INCHES_PER_UNIT hashtable.
 *                  Default is degrees
 * 
 * Returns:
 * {Float} The corresponding resolution given passed-in scale and unit 
 *         parameters.
 */
OpenLayers.Util.getResolutionFromScale = function (scale, units) {

    if (units == null) {
        units = "degrees";
    }

    var normScale = OpenLayers.Util.normalizeScale(scale);

    var resolution = 1 / (normScale * OpenLayers.INCHES_PER_UNIT[units]
                                    * OpenLayers.DOTS_PER_INCH);
    return resolution;
};

/**
 * Function: getScaleFromResolution
 * 
 * Parameters:
 * resolution - {Float}
 * units - {String} Index into OpenLayers.INCHES_PER_UNIT hashtable.
 *                  Default is degrees
 * 
 * Returns:
 * {Float} The corresponding scale given passed-in resolution and unit 
 *         parameters.
 */
OpenLayers.Util.getScaleFromResolution = function (resolution, units) {

    if (units == null) {
        units = "degrees";
    }

    var scale = resolution * OpenLayers.INCHES_PER_UNIT[units] *
                    OpenLayers.DOTS_PER_INCH;
    return scale;
};

/**
 * Function: safeStopPropagation
 * *Deprecated*. This function has been deprecated. Please use directly 
 *     <OpenLayers.Event.stop> passing 'true' as the 2nd 
 *     argument (preventDefault)
 * 
 * Safely stop the propagation of an event *without* preventing
 *   the default browser action from occurring.
 * 
 * Parameter:
 * evt - {Event}
 */
OpenLayers.Util.safeStopPropagation = function(evt) {
    OpenLayers.Event.stop(evt, true);
};

/**
 * Function: pagePositon
 * Calculates the position of an element on the page. 
 *
 * Parameters:
 * forElement - {DOMElement}
 * 
 * Returns:
 * {Array} two item array, L value then T value.
 */
OpenLayers.Util.pagePosition = function(forElement) {
    var valueT = 0, valueL = 0;

    var element = forElement;
    var child = forElement;
    while(element) {

        if(element == document.body) {
            if(OpenLayers.Element.getStyle(child, 'position') == 'absolute') {
                break;
            }
        }
        
        valueT += element.offsetTop  || 0;
        valueL += element.offsetLeft || 0;

        child = element;
        try {
            // wrapping this in a try/catch because IE chokes on the offsetParent
            element = element.offsetParent;
        } catch(e) {
            OpenLayers.Console.error(OpenLayers.i18n(
                                  "pagePositionFailed",{'elemId':element.id}));
            break;
        }
    }

    element = forElement;
    while(element) {
        valueT -= element.scrollTop  || 0;
        valueL -= element.scrollLeft || 0;
        element = element.parentNode;
    }
    
    return [valueL, valueT];
};


/** 
 * Function: isEquivalentUrl
 * Test two URLs for equivalence. 
 * 
 * Setting 'ignoreCase' allows for case-independent comparison.
 * 
 * Comparison is based on: 
 *  - Protocol
 *  - Host (evaluated without the port)
 *  - Port (set 'ignorePort80' to ignore "80" values)
 *  - Hash ( set 'ignoreHash' to disable)
 *  - Pathname (for relative <-> absolute comparison) 
 *  - Arguments (so they can be out of order)
 *  
 * Parameters:
 * url1 - {String}
 * url2 - {String}
 * options - {Object} Allows for customization of comparison:
 *                    'ignoreCase' - Default is True
 *                    'ignorePort80' - Default is True
 *                    'ignoreHash' - Default is True
 *
 * Returns:
 * {Boolean} Whether or not the two URLs are equivalent
 */
OpenLayers.Util.isEquivalentUrl = function(url1, url2, options) {
    options = options || {};

    OpenLayers.Util.applyDefaults(options, {
        ignoreCase: true,
        ignorePort80: true,
        ignoreHash: true
    });

    var urlObj1 = OpenLayers.Util.createUrlObject(url1, options);
    var urlObj2 = OpenLayers.Util.createUrlObject(url2, options);

    //compare all keys (host, port, etc)
    for(var key in urlObj1) {
        if (options.test) {
            OpenLayers.Console.userError(key + "\n1:" + urlObj1[key] + "\n2:" + urlObj2[key]);
        }
        var val1 = urlObj1[key];
        var val2 = urlObj2[key];
        
        switch(key) {
            case "args":
                //do nothing, they'll be treated below
                break;
            case "host":
            case "port":
            case "protocol":
                if ((val1 == "") || (val2 == "")) {
                    //these will be blank for relative urls, so no need to 
                    // compare them here -- call break. 
                    // 
                    break;
                } 
                // otherwise continue with default compare
                //
            default: 
                if ( (key != "args") && (urlObj1[key] != urlObj2[key]) ) {
                    return false;
                }
                break;
        }
        
    }

    // compare search args - irrespective of order
    for(var key in urlObj1.args) {
        if(urlObj1.args[key] != urlObj2.args[key]) {
            return false;
        }
        delete urlObj2.args[key];
    }
    // urlObj2 shouldn't have any args left
    for(var key in urlObj2.args) {
        return false;
    }
    
    return true;
};

/**
 * Function: createUrlObject
 * 
 * Parameters:
 * url - {String}
 * options - {Object} A hash of options.  Can be one of:
 *            ignoreCase: lowercase url,
 *            ignorePort80: don't include explicit port if port is 80,
 *            ignoreHash: Don't include part of url after the hash (#).
 * 
 * Returns:
 * {Object} An object with separate url, a, port, host, and args parsed out 
 *          and ready for comparison
 */
OpenLayers.Util.createUrlObject = function(url, options) {
    options = options || {};

    var urlObject = {};
  
    if (options.ignoreCase) {
        url = url.toLowerCase(); 
    }

    var a = document.createElement('a');
    a.href = url;
    
  //host (without port)
    urlObject.host = a.host;
    var port = a.port;
    if (port.length <= 0) {
        var newHostLength = urlObject.host.length - (port.length);
        urlObject.host = urlObject.host.substring(0, newHostLength); 
    }

  //protocol
    urlObject.protocol = a.protocol;  

  //port
    urlObject.port = ((port == "80") && (options.ignorePort80)) ? "" : port;
                                                                     
  //hash
    urlObject.hash = (options.ignoreHash) ? "" : a.hash;  
    
  //args
    var queryString = a.search;
    if (!queryString) {
        var qMark = url.indexOf("?");
        queryString = (qMark != -1) ? url.substr(qMark) : "";
    }
    urlObject.args = OpenLayers.Util.getParameters(queryString);


  //pathname (this part allows for relative <-> absolute comparison)
    if ( ((urlObject.protocol == "file:") && (url.indexOf("file:") != -1)) || 
         ((urlObject.protocol != "file:") && (urlObject.host != "")) ) {

        urlObject.pathname = a.pathname;  

        //Test to see if the pathname includes the arguments (Opera)
        var qIndex = urlObject.pathname.indexOf("?");
        if (qIndex != -1) {
            urlObject.pathname = urlObject.pathname.substring(0, qIndex);
        }

    } else {
        var relStr = OpenLayers.Util.removeTail(url);

        var backs = 0;
        do {
            var index = relStr.indexOf("../");

            if (index == 0) {
                backs++;
                relStr = relStr.substr(3);
            } else if (index >= 0) {
                var prevChunk = relStr.substr(0,index - 1);
                
                var slash = prevChunk.indexOf("/");
                prevChunk = (slash != -1) ? prevChunk.substr(0, slash +1)
                                          : "";
                
                var postChunk = relStr.substr(index + 3);                
                relStr = prevChunk + postChunk;
            }
        } while(index != -1);

        var windowAnchor = document.createElement("a");
        var windowUrl = window.location.href;
        if (options.ignoreCase) {
            windowUrl = windowUrl.toLowerCase();
        }
        windowAnchor.href = windowUrl;

      //set protocol of window
        urlObject.protocol = windowAnchor.protocol;

        var splitter = (windowAnchor.pathname.indexOf("/") != -1) ? "/" : "\\";
        var dirs = windowAnchor.pathname.split(splitter);
        dirs.pop(); //remove filename
        while ((backs > 0) && (dirs.length > 0)) {
            dirs.pop();
            backs--;
        }
        relStr = dirs.join("/") + "/"+ relStr;
        urlObject.pathname = relStr;
    }
    
    if ((urlObject.protocol == "file:") || (urlObject.protocol == "")) {
        urlObject.host = "localhost";
    }

    return urlObject; 
};
 
/**
 * Function: removeTail
 * Takes a url and removes everything after the ? and #
 * 
 * Parameters:
 * url - {String} The url to process
 * 
 * Returns:
 * {String} The string with all queryString and Hash removed
 */
OpenLayers.Util.removeTail = function(url) {
    var head = null;
    
    var qMark = url.indexOf("?");
    var hashMark = url.indexOf("#");

    if (qMark == -1) {
        head = (hashMark != -1) ? url.substr(0,hashMark) : url;
    } else {
        head = (hashMark != -1) ? url.substr(0,Math.min(qMark, hashMark)) 
                                  : url.substr(0, qMark);
    }
    return head;
};


/**
 * Function: getBrowserName
 * 
 * Returns:
 * {String} A string which specifies which is the current 
 *          browser in which we are running. 
 * 
 *          Currently-supported browser detection and codes:
 *           * 'opera' -- Opera
 *           * 'msie'  -- Internet Explorer
 *           * 'safari' -- Safari
 *           * 'firefox' -- FireFox
 *           * 'mozilla' -- Mozilla
 * 
 *          If we are unable to property identify the browser, we 
 *           return an empty string.
 */
OpenLayers.Util.getBrowserName = function() {
    var browserName = "";
    
    var ua = navigator.userAgent.toLowerCase();
    if ( ua.indexOf( "opera" ) != -1 ) {
        browserName = "opera";
    } else if ( ua.indexOf( "msie" ) != -1 ) {
        browserName = "msie";
    } else if ( ua.indexOf( "safari" ) != -1 ) {
        browserName = "safari";
    } else if ( ua.indexOf( "mozilla" ) != -1 ) {
        if ( ua.indexOf( "firefox" ) != -1 ) {
            browserName = "firefox";
        } else {
            browserName = "mozilla";
        }
    }
    
    return browserName;
};



    
/**
 * Method: getRenderedDimensions
 * Renders the contentHTML offscreen to determine actual dimensions for
 *     popup sizing. As we need layout to determine dimensions the content
 *     is rendered -9999px to the left and absolute to ensure the 
 *     scrollbars do not flicker
 *     
 * Parameters:
 * contentHTML
 * size - {<OpenLayers.Size>} If either the 'w' or 'h' properties is 
 *     specified, we fix that dimension of the div to be measured. This is 
 *     useful in the case where we have a limit in one dimension and must 
 *     therefore meaure the flow in the other dimension.
 * options - {Object}
 *     displayClass - {String} Optional parameter.  A CSS class name(s) string
 *         to provide the CSS context of the rendered content.
 * 
 * Returns:
 * {OpenLayers.Size}
 */
OpenLayers.Util.getRenderedDimensions = function(contentHTML, size, options) {
    
    var w, h;
    
    // create temp container div with restricted size
    var container = document.createElement("div");
    container.style.overflow= "";
    container.style.position = "absolute";
    container.style.left = "-9999px";
        
    //fix a dimension, if specified.
    if (size) {
        if (size.w) {
            w = size.w;
            container.style.width = w + "px";
        } else if (size.h) {
            h = size.h;
            container.style.height = h + "px";
        }
    }

    //add css classes, if specified
    if (options && options.displayClass) {
        container.className = options.displayClass;
    }
    
    // create temp content div and assign content
    var content = document.createElement("div");
    content.innerHTML = contentHTML;
    
    // add content to restricted container 
    container.appendChild(content);
    
    // append container to body for rendering
    document.body.appendChild(container);
    
    // calculate scroll width of content and add corners and shadow width
    if (!w) {
        w = parseInt(content.scrollWidth);
    
        // update container width to allow height to adjust
        container.style.width = w + "px";
    }        
    // capture height and add shadow and corner image widths
    if (!h) {
        h = parseInt(content.scrollHeight);
    }

    // remove elements
    container.removeChild(content);
    document.body.removeChild(container);
    
    return new OpenLayers.Size(w, h);
};

/**
 * APIFunction: getScrollbarWidth
 * This function has been modified by the OpenLayers from the original version,
 *     written by Matthew Eernisse and released under the Apache 2 
 *     license here:
 * 
 *     http://www.fleegix.org/articles/2006/05/30/getting-the-scrollbar-width-in-pixels
 * 
 *     It has been modified simply to cache its value, since it is physically 
 *     impossible that this code could ever run in more than one browser at 
 *     once. 
 * 
 * Returns:
 * {Integer}
 */
OpenLayers.Util.getScrollbarWidth = function() {
    
    var scrollbarWidth = OpenLayers.Util._scrollbarWidth;
    
    if (scrollbarWidth == null) {
        var scr = null;
        var inn = null;
        var wNoScroll = 0;
        var wScroll = 0;
    
        // Outer scrolling div
        scr = document.createElement('div');
        scr.style.position = 'absolute';
        scr.style.top = '-1000px';
        scr.style.left = '-1000px';
        scr.style.width = '100px';
        scr.style.height = '50px';
        // Start with no scrollbar
        scr.style.overflow = 'hidden';
    
        // Inner content div
        inn = document.createElement('div');
        inn.style.width = '100%';
        inn.style.height = '200px';
    
        // Put the inner div in the scrolling div
        scr.appendChild(inn);
        // Append the scrolling div to the doc
        document.body.appendChild(scr);
    
        // Width of the inner div sans scrollbar
        wNoScroll = inn.offsetWidth;
    
        // Add the scrollbar
        scr.style.overflow = 'scroll';
        // Width of the inner div width scrollbar
        wScroll = inn.offsetWidth;
    
        // Remove the scrolling div from the doc
        document.body.removeChild(document.body.lastChild);
    
        // Pixel width of the scroller
        OpenLayers.Util._scrollbarWidth = (wNoScroll - wScroll);
        scrollbarWidth = OpenLayers.Util._scrollbarWidth;
    }

    return scrollbarWidth;
};
