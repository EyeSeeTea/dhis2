DHIS = {};
DHIS.conf = {
    finals: {
        ajax: {
            data_get: 'api/chartValues.jsonp',
            favorite_get: 'api/charts/'
        },        
        dimension: {
            data: {
                value: 'data',
                rawvalue: 'Data'
            },
            indicator: {
                value: 'indicator',
                rawvalue: 'Indicator'
            },
            dataelement: {
                value: 'dataelement',
                rawvalue: 'Data element'
            },
            period: {
                value: 'period',
                rawvalue: 'Period'
            },
            organisationunit: {
                value: 'organisationunit',
                rawvalue: 'Organisation unit'
            }
        },
        chart: {
            x: 'x',
            series: 'series',
            category: 'category',
            filter: 'filter',
            column: 'column',
            stackedcolumn: 'stackedcolumn',
            bar: 'bar',
            stackedbar: 'stackedbar',
            line: 'line',
            area: 'area',
            pie: 'pie',
            orgUnitIsParent: 'orgUnitIsParent'
        },
        data: {
			domain: 'domain_',
			targetline: 'targetline_',
			baseline: 'baseline_',
			trendline: 'trendline_'
		},
    },
    chart: {
        style: {
            inset: 30,
            font: 'arial,sans-serif,ubuntu,consolas'
        },
        theme: {
            dv1: ['#94ae0a', '#0b3b68', '#a61120', '#ff8809', '#7c7474', '#a61187', '#ffd13e', '#24ad9a', '#a66111', '#414141', '#4500c4', '#1d5700']
        }
    }
};

Ext.onReady( function() {
	
    DHIS.initialize = function() {
        DHIS.store.column = DHIS.store.defaultChartStore;
        DHIS.store.stackedcolumn = DHIS.store.defaultChartStore;
        DHIS.store.stackedbar = DHIS.store.bar;
        DHIS.store.line = DHIS.store.defaultChartStore;
        DHIS.store.area = DHIS.store.defaultChartStore;
        DHIS.store.pie = DHIS.store.defaultChartStore;
        
        DHIS.getChart = DHIS.exe.addToQueue;
        DHIS.destroyChart = DHIS.exe.destroy;
    };
    
    DHIS.projects = {};
    
    DHIS.plugin = {
		SimpleRegression: function SimpleRegression() {
			var sumX = 0; // Sum of x values
			var sumY = 0; // Sum of y values
			var sumXX = 0; // Total variation in x
			var sumXY = 0; // Sum of products
			var n = 0; // Number of observations
			var xbar = 0; // Mean of accumulated x values, used in updating formulas
			var ybar = 0; // Mean of accumulated y values, used in updating formulas
			
			this.addData = function(x, y) {
				if ( n == 0 ) {
					xbar = x;
					ybar = y;
				}
				else {
					var dx = x - xbar;
					var dy = y - ybar;
					sumXX += dx * dx * n / ( n + 1 );
					sumXY += dx * dy * n / ( n + 1 );
					xbar += dx / ( n + 1 );
					ybar += dy / ( n + 1 );
				}
				
				sumX += x;
				sumY += y;
				n++;
			};
			
			this.predict = function( x ) {
				var b1 = this.getSlope();				
				return this.getIntercept( b1 ) + b1 * x;
			};
			
			this.getSlope = function() {
				if ( n < 2 ) {
					return Number.NaN;
				}				
				return sumXY / sumXX;
			};
			
			this.getIntercept = function( slope ) {
				return ( sumY - slope * sumX ) / n;
			};
		}
	};
    
    DHIS.util = {
        dimension: {
            indicator: {
                getIdsFromObjects: function(indicators) {
                    var a = []
                    for (var i = 0; i < indicators.length; i++) {
                        a.push(indicators[i].id);
                    }
                    return a;
                }
            },
            dataelement: {
                getIdsFromObjects: function(dataelements) {
                    var a = []
                    for (var i = 0; i < dataelements.length; i++) {
                        a.push(dataelements[i].id);
                    }
                    return a;
                }
            },
            data: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.state.conf.indicators, function(r) {
                        a.push('indicatorIds=' + r);
                    });
                    Ext.Array.each(DHIS.state.state.conf.dataelements, function(r) {
                        a.push('dataElementIds=' + r);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                }
            },
            period: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.state.conf.periods, function(r) {
						a.push(r + '=true')
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getRelativesFromObject: function(obj) {
                    var a = [];
                    for (var k in obj) {
                        if (obj[k]) {
                            a.push(k);
                        }
                    }
                    return a;
                }
            },
            organisationunit: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.state.conf.organisationunits, function(r) {
						a.push('organisationUnitIds=' + r)
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getIdsFromObjects: function(organisationunits) {
                    var a = []
                    for (var i = 0; i < organisationunits.length; i++) {
                        a.push(organisationunits[i].id);
                    }
                    return a;
                }
            }
        },
        chart: {
			def: {
				getChart: function(project, axes, series, elWidth, elHeight) {
					return Ext.create('Ext.chart.Chart', {
						renderTo: project.state.conf.el,
						animate: true,
						store: project.store,
						insetPadding: DHIS.conf.chart.style.inset,						
						items: project.state.conf.hideSubtitle ? false : DHIS.util.chart.def.getTitle(project),
						legend: project.state.conf.hideLegend ? false : DHIS.util.chart.def.getLegend(project.store.range.length),
						width: project.state.conf.width || elWidth,
						height: project.state.conf.height || elHeight,
						axes: axes,
						series: series,
						theme: project.state.conf.el
					});
				},
				getLegend: function(len) {
					len = len ? len : 1;
					return {
						position: len > 5 ? 'right' : 'top',
						labelFont: '11px ' + DHIS.conf.chart.style.font,
						boxStroke: '#ffffff',
						boxStrokeWidth: 0,
						padding: 0
					};
				},
				getTitle: function(project) {
					return {
						type: 'text',
						text: project.state.filter.names[0],
						font: 'bold 13px ' + DHIS.conf.chart.style.font,
						fill: '#222',
						width: 300,
						height: 20,
						x: 28,
						y: 16
					};
				},
				getGrid: function() {
					return {
						opacity: 1,
						fill: '#f1f1f1',
						stroke: '#aaa',
						'stroke-width': 0.2
					};
				},
				setMask: function(str) {
					if (DHIS.mask) {
						DHIS.mask.hide();
					}
					DHIS.mask = new Ext.LoadMask(DHIS.chart.chart, {msg: str});
					DHIS.mask.show();
				},
				label: {
					getCategory: function() {
						return {
							font: '11px ' + DHIS.conf.chart.style.font,
							rotate: {
								degrees: 320
							}
						};
					},
					getNumeric: function(values) {
						return {
							font: '11px ' + DHIS.conf.chart.style.font,
							renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer(values))
						};
					}
				},
				axis: {
					getNumeric: function(project, isStacked) {
						return {
							type: 'Numeric',
							position: 'left',
							title: project.state.conf.rangeAxisLabel || false,
							labelTitle: {
								font: '17px ' + DHIS.conf.chart.style.font
							},
							minimum: 0,
							fields: isStacked ? project.state.series.names : project.store.range,
							label: DHIS.util.chart.def.label.getNumeric(project.values),
							grid: {
								odd: {
									opacity: 1,
									fill: '#fefefe',
									stroke: '#aaa',
									'stroke-width': 0.1
								},									
								even: {
									opacity: 1,
									fill: '#f1f1f1',
									stroke: '#aaa',
									'stroke-width': 0.1
								}
							}
						};
					},
					getCategory: function(project) {
						return {
							type: 'Category',
							position: 'bottom',
							title: project.state.conf.domainAxisLabel || false,
							labelTitle: {
								font: '17px ' + DHIS.conf.chart.style.font
							},
							fields: DHIS.conf.finals.data.domain,
							label: DHIS.util.chart.def.label.getCategory()
						};
					}
				},
				series: {
					getTips: function() {
						return {
							trackMouse: true,
							cls: 'dv-chart-tips',
							renderer: function(r, item) {
								this.update('' + item.value[1]);
							}
						};
					},
					getTargetLine: function(project) {
						var title = project.state.conf.targetLineLabel || 'Target';
						title += ' (' + project.state.conf.targetLineValue + ')';
						return {
							type: 'line',
							axis: 'left',
							xField: DHIS.conf.finals.data.domain,
							yField: DHIS.conf.finals.data.targetline,
							style: {
								opacity: 1,
								lineWidth: 3,
								stroke: '#041423'
							},
							markerConfig: {
								type: 'circle',
								radius: 0
							},
							title: title
						};
					},
					getBaseLine: function(project) {
						var title = project.state.conf.baseLineLabel || 'Base';
						title += ' (' + project.state.conf.baseLineValue + ')';
						return {
							type: 'line',
							axis: 'left',
							xField: DHIS.conf.finals.data.domain,
							yField: DHIS.conf.finals.data.baseline,
							style: {
								opacity: 1,
								lineWidth: 3,
								stroke: '#041423'
							},
							markerConfig: {
								type: 'circle',
								radius: 0
							},
							title: title
						};
					},
					getTrendLineArray: function(project) {						
						var a = [];
						for (var i = 0; i < project.trendline.length; i++) {
							a.push({
								type: 'line',
								axis: 'left',
								xField: DHIS.conf.finals.data.domain,
								yField: project.trendline[i].key,
								style: {
									opacity: 0.8,
									lineWidth: 3
								},
								markerConfig: {
									type: 'circle',
									radius: 4
								},
								tips: DHIS.util.chart.def.series.getTips(),
								title: project.trendline[i].name
							});
						}
						return a;
					},
					setTheme: function(project) {
						var colors = DHIS.conf.chart.theme.dv1.slice(0, project.state.series.names.length);
						if (project.state.conf.targetLineValue || project.state.conf.baseLineValue) {
							colors.push('#051a2e');
						}					
						if (project.state.conf.targetLineValue) {
							colors.push('#051a2e');
						}					
						if (project.state.conf.baseLineValue) {
							colors.push('#051a2e');
						}
						Ext.chart.theme[project.state.conf.el] = Ext.extend(Ext.chart.theme.Base, {
							constructor: function(config) {
								Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
									seriesThemes: colors,
									colors: colors
								}, config));
							}
						});
					}
				}
			},
            bar: {
				label: {
					getCategory: function() {
						return {
							font: '11px ' + DHIS.conf.chart.style.font
						};
					}
				},
				axis: {
					getNumeric: function(project) {
						var num = DHIS.util.chart.def.axis.getNumeric(project);
						num.position = 'bottom';
						return num;
					},
					getCategory: function(project) {
						var cat = DHIS.util.chart.def.axis.getCategory(project);
						cat.position = 'left';
						cat.label = DHIS.util.chart.bar.label.getCategory();
						return cat;
					}
				},
                series: {
					getTips: function() {
						return {
							trackMouse: true,
							cls: 'dv-chart-tips',
							renderer: function(si, item) {
								this.update('' + item.value[0]);
							}
						};
					},
					getTargetLine: function(project) {
						var line = DHIS.util.chart.def.series.getTargetLine(project);
						line.axis = 'bottom';
						line.xField = DHIS.conf.finals.data.targetline;
						line.yField = DHIS.conf.finals.data.domain;
						return line;
					},
					getBaseLine: function(project) {
						var line = DHIS.util.chart.def.series.getBaseLine(project);
						line.axis = 'bottom';
						line.xField = DHIS.conf.finals.data.baseline;
						line.yField = DHIS.conf.finals.data.domain;
						return line;
					},
					getTrendLineArray: function(project) {
						var a = [];
						for (var i = 0; i < project.trendline.length; i++) {
							a.push({
								type: 'line',
								axis: 'bottom',
								xField: project.trendline[i].key,
								yField: DHIS.conf.finals.data.domain,
								style: {
									opacity: 0.8,
									lineWidth: 3
								},
								markerConfig: {
									type: 'circle',
									radius: 4
								},
								tips: DHIS.util.chart.bar.series.getTips(),
								title: project.trendline[i].name
							});
						}
						return a;
					}
				}
            },
            line: {
				series: {
					getArray: function(project) {
						var a = [];
						for (var i = 0; i < project.state.series.names.length; i++) {
							a.push({
								type: 'line',
								axis: 'left',
								xField: DHIS.conf.finals.data.domain,
								yField: project.state.series.names[i],
								style: {
									opacity: 0.8,
									lineWidth: 3
								},
								markerConfig: {
									type: 'circle',
									radius: 4
								},
								tips: DHIS.util.chart.def.series.getTips()
							});
						}
						return a;
					},
					setTheme: function(project) {
						var colors = DHIS.conf.chart.theme.dv1.slice(0, project.state.series.names.length);
						if (project.state.conf.trendLine) {
							colors = colors.concat(colors);
						}
						if (project.state.conf.targetLineValue) {
							colors.push('#051a2e');
						}
						if (project.state.conf.baseLineValue) {
							colors.push('#051a2e');
						}
						Ext.chart.theme[project.state.conf.el] = Ext.extend(Ext.chart.theme.Base, {
							constructor: function(config) {
								Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
									seriesThemes: colors,
									colors: colors
								}, config));
							}
						});
					}
                }
            },
            pie: {
                getTitle: function(title, subtitle) {
                    return [
                        {
                            type: 'text',
                            text: title,
                            font: 'bold 13px arial',
                            fill: '#222',
                            width: 300,
                            height: 20,
                            x: 28,
                            y: 16
                        },
                        {
                            type: 'text',
                            text: subtitle,
                            font: 'bold 11px arial',
                            fill: '#777',
                            width: 300,
                            height: 20,
                            x: 28,
                            y: 36
                        }
                    ];                        
                },
                series: {
					getTips: function(project) {
						return {
							trackMouse: true,
							cls: 'dv-chart-tips-pie',
							renderer: function(item) {
								this.update(item.data[DHIS.conf.finals.data.domain] + '<br/><b>' + item.data[project.state.series.names[0]] + '</b>');
							}
						};
					},
					setTheme: function(project) {
						var colors = DV.conf.chart.theme.dv1.slice(0, project.state.category.names.length);
						Ext.chart.theme[project.state.conf.el] = Ext.extend(Ext.chart.theme.Base, {
							constructor: function(config) {
								Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
									seriesThemes: colors,
									colors: colors
								}, config));
							}
						});
					}
				}
            }
        },
        number: {
            isInteger: function(n) {
                var str = new String(n);
                if (str.indexOf('.') > -1) {
                    var d = str.substr(str.indexOf('.') + 1);
                    return (d.length === 1 && d == '0');
                }
                return false;
            },
            allValuesAreIntegers: function(values) {
                for (var i = 0; i < values.length; i++) {
                    if (!this.isInteger(values[i].v)) {
                        return false;
                    }
                }
                return true;
            },
            getChartAxisFormatRenderer: function(values) {
                return this.allValuesAreIntegers(values) ? '0' : '0.0';
            }
        },
        string: {
            getEncodedString: function(text) {
                return text.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'');
            },
            extendUrl: function(url) {
                if (url.charAt(url.length-1) !== '/') {
                    url += '/';
                }
                return url;
            },
            appendUrlIfTrue: function(url, param, expression) {
            	if (expression && expression == true) {
            		url = Ext.String.urlAppend(url, param + '=true');
            	}
            	return url;            	
            }
        },
        value: {
            jsonfy: function(r) {                
                var object = {
                    values: [],
                    periods: r.p,
                    datanames: [],
                    organisationunitnames: []
                };
                for (var i = 0; i < r.v.length; i++) {
                    var obj = {};
                    obj.v = r.v[i][0];
                    obj[DHIS.conf.finals.dimension.data.value] = r.v[i][1];
                    obj[DHIS.conf.finals.dimension.period.value] = r.v[i][2];
                    obj[DHIS.conf.finals.dimension.organisationunit.value] = r.v[i][3];
                    object.values.push(obj);
                }
                for (var j = 0; j < r.d.length; j++) {
					object.datanames.push(DHIS.util.string.getEncodedString(r.d[j]));
				}
                for (var k = 0; k < r.o.length; k++) {
					object.organisationunitnames.push(DHIS.util.string.getEncodedString(r.o[k]));
				}
                return object;
            }
        }
    };
    
    DHIS.store = {
        getChartStore: function(project) {
            var keys = [];            
            Ext.Array.each(project.data, function(item) {
                keys = Ext.Array.merge(keys, Ext.Object.getKeys(item));
            });            
            project.store = Ext.create('Ext.data.Store', {
                fields: keys,
                data: project.data
            });
            project.store.range = keys.slice(0);
            for (var i = 0; i < project.store.range.length; i++) {
                if (project.store.range[i] === DHIS.conf.finals.data.domain) {
                    project.store.range.splice(i, 1);
                }
            }
            
			DHIS.chart.getChart(project);
        }
    };
    
    DHIS.state = {
        state: null,
        getState: function(conf) {
            var project = {
                state: {
                    conf: null,
                    type: null,
                    series: {
                        dimension: null,
                        names: []
                    },
                    category: {
                        dimension: null,
                        names: []
                    },
                    filter: {
                        dimension: null,
                        names: []
                    }
                }
            };
            
            var defaultConf = {
                type: 'column',
                stacked: false,
                indicators: [],
                periods: ['last12Months'],
                organisationunits: [],
                series: 'data',
                category: 'period',
                filter: 'organisationunit',
                el: '',
                legendPosition: false,
                orgUnitIsParent: false,
                showData: false,
                trendLine: false,
                hideLegend: false,
                hideSubtitle: false,
                targetLineValue: null,
                targetLineLabel: null,
                baseLineValue: null,
                baseLineLabel: null,                
                url: ''
            };
            
            project.state.conf = Ext.applyIf(conf, defaultConf);
            project.state.conf.type = project.state.conf.type.toLowerCase();
            project.state.conf.series = project.state.conf.series.toLowerCase();
            project.state.conf.category = project.state.conf.category.toLowerCase();
            project.state.conf.filter = project.state.conf.filter.toLowerCase();
            
            project.state.conf[project.state.conf.series] = DHIS.conf.finals.chart.series;
            project.state.conf[project.state.conf.category] = DHIS.conf.finals.chart.category;
            project.state.conf[project.state.conf.filter] = DHIS.conf.finals.chart.filter;
            
            project.state.type = project.state.conf.type;
            project.state.series.dimension = project.state.conf.series;
            project.state.category.dimension = project.state.conf.category;
            project.state.filter.dimension = project.state.conf.filter;
            
            DHIS.state.state = project.state;
            
			DHIS.value.getValues(project);
        },
        setState: function(conf) {
            if (conf.uid) {
                Ext.data.JsonP.request({
                    url: conf.url + DHIS.conf.finals.ajax.favorite_get + conf.uid + '.jsonp',
                    scope: this,
                    success: function(r) {
                        if (!r) {
                            alert('Invalid uid');
                            return;
                        }
                        
                        conf.type = r.type.toLowerCase();
                        conf.periods = DHIS.util.dimension.period.getRelativesFromObject(r.relativePeriods);
                        conf.organisationunits = DHIS.util.dimension.organisationunit.getIdsFromObjects(r.organisationUnits);
                        conf.series = r.series.toLowerCase();
                        conf.category = r.category.toLowerCase();
                        conf.filter = r.filter.toLowerCase();
                        conf.legendPosition = conf.legendPosition || false;
                        
                        if (r.indicators) {
                            conf.indicators = DHIS.util.dimension.indicator.getIdsFromObjects(r.indicators);
                        }
                        if (r.dataElements) {
                            conf.dataelements = DHIS.util.dimension.dataelement.getIdsFromObjects(r.dataElements);
                        }
                        
                        this.getState(conf);                        
                    }
                });
            }
        }
    };
    
    DHIS.value = {
        getValues: function(project) {
            var params = [];                
            params = params.concat(DHIS.util.dimension[project.state.series.dimension].getUrl());
            params = params.concat(DHIS.util.dimension[project.state.category.dimension].getUrl());
            params = params.concat(DHIS.util.dimension[project.state.filter.dimension].getUrl(true));
                        
            var baseUrl = DHIS.util.string.extendUrl(project.state.conf.url) + DHIS.conf.finals.ajax.data_get;
            baseUrl = DHIS.util.string.appendUrlIfTrue(baseUrl, DHIS.conf.finals.chart.orgUnitIsParent, project.state.orgUnitIsParent);
            
            Ext.Array.each(params, function(item) {
                baseUrl = Ext.String.urlAppend(baseUrl, item);
            });
            
            Ext.data.JsonP.request({
                url: baseUrl,
                disableCaching: false,
                success: function(r) {
                    var json = DHIS.util.value.jsonfy(r);
                    project.values = json.values;
                    
                    if (!project.values.length) {
                        alert('No data values');
                        return;
                    }
                    
                    for (var i = 0; i < project.values.length; i++) {
                        project.values[i][DHIS.conf.finals.dimension.data.value] = DHIS.util.string.getEncodedString(project.values[i][DHIS.conf.finals.dimension.data.value]);
                        project.values[i][DHIS.conf.finals.dimension.period.value] = DHIS.util.string.getEncodedString(project.values[i][DHIS.conf.finals.dimension.period.value]);
                        project.values[i][DHIS.conf.finals.dimension.organisationunit.value] = DHIS.util.string.getEncodedString(project.values[i][DHIS.conf.finals.dimension.organisationunit.value]);
                    }
                    
                    project.state[project.state.conf.data].names = json.datanames;
                    project.state[project.state.conf.organisationunit].names = json.organisationunitnames;
                    Ext.Array.each(project.values, function(item) {						
                        Ext.Array.include(project.state[project.state.conf.period].names, DHIS.util.string.getEncodedString(item[project.state[project.state.conf.period].dimension]));
                        item.v = parseFloat(item.v);
                    });
                    
                    for (var k in project.state.conf) {
                        if (project.state.conf[k] == 'period') {
                            project.state[k].names = json.periods;
                        }
                    }
                    
                    DHIS.state.state = project.state;
					DHIS.chart.getData(project);
                }
            });
        }
    };
    
    DHIS.chart = {
        getData: function(project) {
            project.data = [];
			
            Ext.Array.each(project.state.category.names, function(item) {
                var obj = {};
                obj[DHIS.conf.finals.data.domain] = item;
                project.data.push(obj);
            });
            
            Ext.Array.each(project.data, function(item) {
                for (var i = 0; i < project.state.series.names.length; i++) {
                    item[project.state.series.names[i]] = 0;
                }
            });
            
            Ext.Array.each(project.data, function(item) {
                for (var i = 0; i < project.state.series.names.length; i++) {
                    for (var j = 0; j < project.values.length; j++) {
                        if (project.values[j][project.state.category.dimension] === item[DHIS.conf.finals.data.domain] && project.values[j][project.state.series.dimension] === project.state.series.names[i]) {
                            item[project.values[j][project.state.series.dimension]] = project.values[j].v;
                            break;
                        }
                    }
                }
            });
            
			if (project.state.conf.trendLine) {
				project.trendline = [];
				for (var i = 0; i < project.state.series.names.length; i++) {
					var s = project.state.series.names[i],
						reg = new DHIS.plugin.SimpleRegression();
					for (var j = 0; j < project.data.length; j++) {
						reg.addData(j, project.data[j][s]);
					}
					var key = DHIS.conf.finals.data.trendline + s;
					for (var j = 0; j < project.data.length; j++) {
						var n = reg.predict(j);
						project.data[j][key] = parseFloat(reg.predict(j).toFixed(1));
					}
					project.trendline.push({
						key: key,
						name: 'Trend (' + s + ')'
					});
				}
			}

			if (project.state.conf.targetLineValue) {
				Ext.Array.each(project.data, function(item) {
					item[DHIS.conf.finals.data.targetline] = project.state.conf.targetLineValue;
				});
			}

			if (project.state.conf.baseLineValue) {
				Ext.Array.each(project.data, function(item) {
					item[DHIS.conf.finals.data.baseline] = project.state.conf.baseLineValue;
				});
			}
                
			DHIS.store.getChartStore(project);
        },
        el: null,
        getChart: function(project) {
            this.el = Ext.get(project.state.conf.el);
            this[project.state.type](project);
            DHIS.exe.execute();
        },
        column: function(project, isStacked) {
			var series = [];
			if (project.state.conf.trendLine) {
				var a = DHIS.util.chart.def.series.getTrendLineArray(project);
				for (var i = 0; i < a.length; i++) {
					series.push(a[i]);
				}
			}
			var main = {
				type: 'column',
				axis: 'left',
				xField: DHIS.conf.finals.data.domain,
				yField: project.state.series.names,
				stacked: isStacked,
				style: {
					opacity: 0.8,
					stroke: '#333'
				},
				tips: DHIS.util.chart.def.series.getTips()
			};
			if (project.state.conf.showData) {
				main.label = {display: 'outside', field: project.state.series.names};
			}
			series.push(main);
			if (project.state.conf.targetLineValue) {
				series.push(DHIS.util.chart.def.series.getTargetLine(project));
			}
			if (project.state.conf.baseLineValue) {
				series.push(DHIS.util.chart.def.series.getBaseLine(project));
			}
			
			var axes = [];
			var numeric = DHIS.util.chart.def.axis.getNumeric(project, isStacked);
			axes.push(numeric);
			axes.push(DHIS.util.chart.def.axis.getCategory(project));
			
			DHIS.util.chart.def.series.setTheme(project);
			project.chart = DHIS.util.chart.def.getChart(project, axes, series, this.el.getWidth(), this.el.getHeight());
			
            DHIS.projects[project.state.conf.el] = project;
        },
        stackedcolumn: function(project) {
            this.column(project, true);
        },
        bar: function(project, isStacked) {
			var series = [];
			if (project.state.conf.trendLine) {
				var a = DHIS.util.chart.bar.series.getTrendLineArray(project);
				for (var i = 0; i < a.length; i++) {
					series.push(a[i]);
				}
			}
			var main = {
				type: 'bar',
				axis: 'bottom',
				xField: DHIS.conf.finals.data.domain,
				yField: project.state.series.names,
				stacked: isStacked,
				style: {
					opacity: 0.8,
					stroke: '#333'
				},
				tips: DHIS.util.chart.def.series.getTips()
			};
			if (project.state.conf.showData) {
				main.label = {display: 'outside', field: project.state.series.names};
			}
			series.push(main);
			if (project.state.conf.targetLineValue) {
				series.push(DHIS.util.chart.bar.series.getTargetLine(project));
			}
			if (project.state.conf.baseLineValue) {
				series.push(DHIS.util.chart.bar.series.getBaseLine(project));
			}
			
			var axes = [];
			var numeric = DHIS.util.chart.bar.axis.getNumeric(project, isStacked);
			axes.push(numeric);
			axes.push(DHIS.util.chart.bar.axis.getCategory(project));
			
			DHIS.util.chart.def.series.setTheme(project);
			project.chart = DHIS.util.chart.def.getChart(project, axes, series, this.el.getWidth(), this.el.getHeight());
			
            DHIS.projects[project.state.conf.el] = project;
        },
        stackedbar: function(project) {
            this.bar(project, true);
        },
        line: function(project) {
			var series = [];
			if (project.state.conf.trendLine) {
				var a = DHIS.util.chart.def.series.getTrendLineArray(project);
				for (var i = 0; i < a.length; i++) {
					series.push(a[i]);
				}
			}	
			series = series.concat(DHIS.util.chart.line.series.getArray(project));
			
			if (project.state.conf.targetLineValue) {
				series.push(DHIS.util.chart.def.series.getTargetLine(project));
			}
			if (project.state.conf.baseLineValue) {
				series.push(DHIS.util.chart.def.series.getBaseLine(project));
			}
			
			var axes = [];
			var numeric = DHIS.util.chart.def.axis.getNumeric(project);
			axes.push(numeric);
			axes.push(DHIS.util.chart.def.axis.getCategory(project));
			
			DHIS.util.chart.line.series.setTheme(project);
			this.chart = DHIS.util.chart.def.getChart(project, axes, series, this.el.getWidth(), this.el.getHeight());
            
            DHIS.projects[project.state.conf.el] = project;
        },
        area: function(project) {
			var series = [];
			series.push({
				type: 'area',
				axis: 'left',
				xField: DHIS.conf.finals.data.domain,
				yField: project.state.series.names,
				style: {
					opacity: 0.65,
					stroke: '#555'
				}
			});
			
			var axes = [];
			var numeric = DHIS.util.chart.def.axis.getNumeric(project);
			axes.push(numeric);
			axes.push(DHIS.util.chart.def.axis.getCategory(project));
			
			DHIS.util.chart.line.series.setTheme(project);
			this.chart = DHIS.util.chart.def.getChart(project, axes, series, this.el.getWidth(), this.el.getHeight());
            
            DHIS.projects[project.state.conf.el] = project;
        },
        pie: function(project) {
            project.chart = Ext.create('Ext.chart.Chart', {
				renderTo: project.state.conf.el,
                width: project.state.conf.width || this.el.getWidth(),
                height: project.state.conf.height || this.el.getHeight(),
                animate: true,
                shadow: true,
                store: project.store,
                insetPadding: 60,
                items: DHIS.util.chart.pie.getTitle(project.state.filter.names[0], project.store.left[0]),
                legend: DHIS.util.chart.def.getLegend(project.state.category.names.length),
                series: [{
                    type: 'pie',
                    field: project.store.left[0],
                    showInLegend: true,
                    label: {
                        field: project.store.bottom[0]
                    },
                    highlight: {
                        segment: {
                            margin: 10
                        }
                    },
                    style: {
                        opacity: 0.9,
						stroke: '#555'
                    },
                    tips: DHIS.util.chart.pie.series.getTips(project)
                }]
            });
            
            DHIS.projects[project.state.conf.el] = project;
        }
    };
    
    DHIS.exe = {
        allow: true,
        queue: [],
        addToQueue: function(conf) {
            DHIS.exe.queue.push(conf);
            if (DHIS.exe.allow) {
                DHIS.exe.allow = false;
                DHIS.exe.execute();
            }
        },
        execute: function() {
            if (this.queue.length) {
                var conf = this.queue.shift();
                this.destroy(conf.el);
                if (conf.uid) {
                    DHIS.state.setState(conf);
                }
                else {
                    DHIS.state.getState(conf);
                }
            }
            else {
				DHIS.exe.allow = true;
			}
		},
		destroy: function(el) {
			if (DHIS.projects[el]) {
				DHIS.projects[el].chart.destroy();
			}
		}
    };
    
    DHIS.initialize();
});
