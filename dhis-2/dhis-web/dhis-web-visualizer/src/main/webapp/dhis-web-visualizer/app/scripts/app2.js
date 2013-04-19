DV.app = {};
DV.app.init = {};

Ext.onReady( function() {
	Ext.Ajax.method = 'GET';
	Ext.QuickTips.init();

	document.body.oncontextmenu = function() {
		return false;
	};

    Ext.override(Ext.LoadMask, {
		onHide: function() {
			this.callParent();
		}
	});

	// Init

	var dv = DV.core.getInstance();

	DV.app.getInit = function(r) {
		var init = Ext.decode(r.responseText);

		for (var i = 0; i < init.rootNodes.length; i++) {
			init.rootNodes[i].path = '/' + dv.conf.finals.root.id + '/' + init.rootNodes[i].id;
		}

		// Ougs
		for (var i = 0, dim = dv.conf.finals.dimension, oug; i < init.ougs.length; i++) {
			oug = init.ougs[i];
			oug.dimensionName = oug.id;
			oug.objectName = dv.conf.finals.dimension.organisationUnitGroupSet.objectName;
			dim.objectNameMap[oug.id] = oug;
		}

		// Degs
		for (var i = 0, dim = dv.conf.finals.dimension, deg; i < init.degs.length; i++) {
			deg = init.degs[i];
			deg.dimensionName = deg.id;
			deg.objectName = dv.conf.finals.dimension.dataElementGroupSet.objectName;
			dim.objectNameMap[deg.id] = deg;
		}

		init.afterRender = function() {

			// Resize event handler
			dv.viewport.westRegion.on('resize', function() {
				var panel = dv.util.dimension.panel.getExpanded();

				if (panel) {
					panel.onExpand();
				}
			});

			// Left gui
			var viewportHeight = dv.viewport.westRegion.getHeight(),
				numberOfTabs = dv.init.ougs.length + dv.init.degs.length + 5,
				tabHeight = 28,
				minPeriodHeight = 380,
				settingsHeight = 92;

			if (viewportHeight > numberOfTabs * tabHeight + minPeriodHeight + settingsHeight) {
				if (!Ext.isIE) {
					dv.viewport.accordion.setAutoScroll(false);
					dv.viewport.westRegion.setWidth(pt.conf.layout.west_width);
					dv.viewport.accordion.doLayout();
				}
			}
			else {
				dv.viewport.westRegion.hasScrollbar = true;
			}

			dv.cmp.dimension.panels[0].expand();

			// Load favorite from url
			var id = dv.util.url.getUrlParam('id');

			if (id) {
				dv.util.pivot.loadTable(id);
			}

			// Fade in
			Ext.defer( function() {
				Ext.getBody().fadeIn({
					duration: 400
				});
			}, 500 );
		};

		return init;
	};
