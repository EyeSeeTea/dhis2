var WP = {
	groupId: null,
	indicatorId: null,
	idGroupMap: {},
	idIndicatorMap: {},
	showFrontPage: null,
	showIndicators: null,
	showReport: null
};

Ext.onReady( function() {
	var support = {},
		levelName = 'Province',
		createViewport,
		contextPath,
        systemInfo,
        crossDomain,
        resourcePath,
        version,
		groups;

	// support
	support.sort = function(array, direction, key) {
		// accepts [number], [string], [{key: number}], [{key: string}]

		if (!Ext.isArray(array)) {
			return array;
		}

		key = key || 'name';

		array.sort( function(a, b) {

			// if object, get the property values
			if (Ext.isObject(a) && Ext.isObject(b) && key) {
				a = a[key];
				b = b[key];
			}

			// string
			if (Ext.isString(a) && Ext.isString(b)) {
				a = a.toLowerCase();
				b = b.toLowerCase();

				if (direction === 'DESC') {
					return a < b ? 1 : (a > b ? -1 : 0);
				}
				else {
					return a < b ? -1 : (a > b ? 1 : 0);
				}
			}

			// number
			else if (Ext.isNumber(a) && Ext.isNumber(b)) {
				return direction === 'DESC' ? b - a : a - b;
			}

			return 0;
		});

		return array;
	};
	
	createViewport = function() {
		var frontPage,
			setBackHandler,
			showFrontPage,
			showIndicators,
			showReport,
			northRegion,
			westRegion,
			centerRegion,
			chartCt,
			tableCt,
			mapCt,
			viewport;
			
		// front page
		(function() {
			frontPage = '<h2>Data by topic</h2>';
			frontPage += '<ul>';

			for (var i = 0; i < groups.length; i++) {
				frontPage += '<li id="' + groups[i].id + '" onclick="WP.groupId = this.id; WP.showIndicators();">' + groups[i].name + '</li>';

				WP.idGroupMap[groups[i].id] = {
					name: groups[i].name,
					url: 'indicatorGroups',
					indicators: 'indicators'
				};
			}

			frontPage += '</ul><div style="clear:both">';
		}());			

		showFrontPage = function() {
			centerRegion.update(frontPage);
		};

		showIndicators = function() {
			var id = WP.groupId,
				group = WP.idGroupMap[id];
			
			Ext.Ajax.request({
				url: contextPath + '/api/' + group.url + '/' + id + '.json',
				method: 'GET',
				success: function(r) {
					var items = support.sort(Ext.decode(r.responseText)[group.indicators]),
						html = '';

					if (!(Ext.isArray(items) && items.length)) {
						alert('No ' + (group.dimension === 'in' ? 'indicators' : 'data elements') + ' in this group');
						return;
					}

					// center region

					html += '<nav onclick="WP.showFrontPage();">Back</nav>';
					html += '<h3>' + group.name + '</h3>';
					html += '<ul>';

					for (var i = 0; i < items.length; i++) {
						html += '<li id="' + items[i].id + '" class="indicator" onclick="WP.indicatorId = this.id; WP.showReport();">' + items[i].name + '</li>';

						WP.idIndicatorMap[items[i].id] = items[i];
					}

					html += '</ul><div style="clear:both"></div>';
					centerRegion.update(html);
					centerRegion.indicatorsId = id;
				}
			});
		};

		showReport = function() {
			var id = WP.indicatorId,
				indicator = WP.idIndicatorMap[id],
				vpWidth = centerRegion.getWidth(),
				html = '';

			html += '<nav onclick="WP.showIndicators();">Back</nav>';
			html += '<h3>' + indicator.name + ' This Year By ' + levelName + '</h3><div id="chartDiv1"></div>';
			html += '<h3>' + indicator.name + ' Last 12 Months</h3><div id="chartDiv2"></div>';
			html += '<h3>' + indicator.name + ' This Year By ' + levelName + '</h3><div id="mapDiv"></div>';
			html += '<h3>' + indicator.name + ' Last 12 Months By ' + levelName + '</h3><div id="tableDiv"></div>';

			centerRegion.update(html);

			// plugins
			
			DHIS.getChart({
				url: contextPath,
				el: 'chartDiv1',
                dashboard: true,
				width: centerRegion.getWidth() - 120,
				height: 400,
				columns: [{
					dimension: 'in',
					items: [{id: indicator.id}]
				}],
				rows: [{
					dimension: 'ou',
					items: [{id: 'LEVEL-2'}]
				}],
				filters: [{
					dimension: 'pe',
					items: [{id: 'THIS_YEAR'}]
				}],
				hideTitle: true,
				hideLegend: true
			});
			
			DHIS.getChart({
				url: contextPath,
				el: 'chartDiv2',
                dashboard: true,
				width: centerRegion.getWidth() - 120,
				height: 400,
				type: 'line',
				columns: [{
					dimension: 'in',
					items: [{id: indicator.id}]
				}],
				rows: [{
					dimension: 'pe',
					items: [{id: 'LAST_12_MONTHS'}]
				}],
				filters: [{
					dimension: 'ou',
					items: [{id: 'USER_ORGUNIT'}]
				}],
				hideTitle: true,
				hideLegend: true
			});
			
			DHIS.getMap({
				url: contextPath,
				el: 'mapDiv',
                dashboard: true,
				width: centerRegion.getWidth() - 120,
				mapViews: [{
					columns: [{dimension: 'in', items: [{id: indicator.id}]}],
					rows: [{dimension: 'ou', items: [{id: 'LEVEL-2'}]}],
					filters: [{dimension: 'pe', items: [{id: 'THIS_YEAR'}]}],
					classes: 7
				}]
			});
									
			DHIS.getTable({
				url: contextPath,
				el: 'tableDiv',
                dashboard: true,
				columns: [{
					dimension: 'in',
					items: [{id: indicator.id}]
				}, {
					dimension: 'pe',
					items: [{id: 'LAST_12_MONTHS'}]
				}],
				rows: [{
					dimension: 'ou',
					items: [{id: 'LEVEL-2'}]
				}],
				displayDensity: vpWidth > 1100 ? 'comfortable' : (vpWidth > 850 ? 'normal' : 'compact')
			});
		};			

		northRegion = Ext.create('Ext.panel.Panel', {
			region: 'north',
			bodyCls: 'wp-north',
			html: '<h1 onclick="WP.showFrontPage()">DHIS 2 WEB PORTAL</h1>'
		});
		
		centerRegion = Ext.create('Ext.panel.Panel', {
			region: 'center',
			bodyCls: 'wp-center',
			autoScroll: true,
			html: frontPage
		});

		viewport = Ext.create('Ext.container.Viewport', {
			layout: 'border',
			items: [
				northRegion,
				centerRegion
			]
		});

		WP.showFrontPage = showFrontPage;
		WP.showIndicators = showIndicators;
		WP.showReport = showReport;
	};

    // manifest
	Ext.Ajax.request({
		url: 'manifest.webapp',
		success: function(r) {
			contextPath = Ext.decode(r.responseText).activities.dhis.href;
            contextPath = contextPath === '*' ? '//apps.dhis2.org/demo' : contextPath;
            crossDomain = contextPath.indexOf('localhost') === -1 ? true : false;

            // version
            Ext.Ajax.request({
				url: contextPath + '/api/system/info.json',
                success: function(r) {
                    systemInfo = Ext.decode(r.responseText);
                    version = ('v' + parseFloat(systemInfo.version)).split('.').join('');
                    resourcePath = crossDomain ? '//dhis2-cdn.org/' + version : contextPath + '/dhis-web-commons/javascripts';
                    
                    // levels
                    Ext.Ajax.request({
                        url: contextPath + '/api/organisationUnitLevels.json?fields=id,name,level',
                        method: 'GET',
                        success: function(r) {
                            var json = Ext.decode(r.responseText);

                            if (json && Ext.isArray(json.organisationUnitLevels)) {
                                for (var i = 0; i < json.organisationUnitLevels.length; i++) {
                                    if (json.organisationUnitLevels[i].level === 2) {
                                        levelName = json.organisationUnitLevels[i].name;
                                        break;
                                    }
                                }
                            }

                            // indicator groups
                            Ext.Ajax.request({
                                url: contextPath + '/api/indicatorGroups.json?fields=id,name&paging=false',
                                method: 'GET',
                                success: function(r) {
                                    groups = support.sort(Ext.decode(r.responseText).indicatorGroups);

                                    if (!(Ext.isArray(groups) && groups.length)) {
                                        alert('No topics found');
                                        return;
                                    }

                                    // plugins
                                    Ext.Loader.injectScriptElement(resourcePath + '/openlayers/OpenLayers.js', function() {
                                        Ext.Loader.injectScriptElement(resourcePath + '/plugin/table.js', function() {
                                            Ext.Loader.injectScriptElement(resourcePath + '/plugin/chart.js', function() {
                                                Ext.Loader.injectScriptElement(resourcePath + '/plugin/map.js', function() {
                                                    createViewport();
                                                });
                                            });
                                        });
                                    });
                                }
                            });
                        }
                    });
                }
            });
		}
	});
});
