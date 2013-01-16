PT.app = {};
PT.app.init = {};

Ext.onReady( function() {
	Ext.Ajax.method = 'GET';
	Ext.QuickTips.init();

    Ext.override(Ext.LoadMask, {
		onHide: function() {
			this.callParent();
		}
	});

	// Init

	var pt = PT.core.getInstance();

	PT.app.init.onInitialize = function(r) {
		var createViewport;

		createViewport = function() {
			var westRegion,
				centerRegion,

				indicatorAvailable,
				indicatorSelected,
				indicator,
				dataElementAvailable,
				dataElementSelected,
				dataElement,
				dataSetAvailable,
				dataSetSelected,
				dataSet,
				rewind,
				relativePeriod,
				fixedPeriodAvailable,
				fixedPeriodSelected,
				fixedPeriod,
				organisationUnit

				getOrganisationUnitGroupSetItems,
				setListeners;

			indicatorAvailable = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-left',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				store: pt.store.indicatorAvailable,
				tbar: [
					{
						xtype: 'label',
						text: 'Available', //i18n pt.i18n.available
						cls: 'pt-toolbar-multiselect-left-label'
					},
					'->',
					{
						xtype: 'button',
						icon: 'images/arrowright.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.select(indicatorAvailable, indicatorSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowrightdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.selectAll(indicatorAvailable, indicatorSelected);
						}
					},
					' '
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.select(this, indicatorSelected);
						}, this);
					}
				}
			});

			indicatorSelected = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-right',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				ddReorder: true,
				store: pt.store.indicatorSelected,
				tbar: [
					' ',
					{
						xtype: 'button',
						icon: 'images/arrowleftdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselectAll(indicatorAvailable, indicatorSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowleft.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselect(indicatorAvailable, indicatorSelected);
						}
					},
					'->',
					{
						xtype: 'label',
						text: 'Selected', //i18n DV.i18n.selected,
						cls: 'pt-toolbar-multiselect-right-label'
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.unselect(indicatorAvailable, this);
						}, this);
					}
				}
			};

			indicator = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-data">Indicators</div>', //i18n
				hideCollapseTool: true,
				items: [
					{
						xtype: 'combobox',
						cls: 'pt-combo',
						style: 'margin-bottom:8px',
						width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding,
						valueField: 'id',
						displayField: 'name',
						fieldLabel: 'Select group', //i18n pt.i18n.select_group
						fieldCls: 'pt-combo-label-default',
						editable: false,
						store: {
							xtype: 'store',
							fields: ['id', 'name', 'index'],
							proxy: {
								type: 'ajax',
								url: pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.indicatorgroup_get,
								reader: {
									type: 'json',
									root: 'indicatorGroups'
								}
							},
							listeners: {
								load: function(s) {
									s.add({
										id: 0,
										name: 'All indicator groups', //i18n pt.i18n.all_indicator_groups
										index: -1
									});
									s.sort([
										{
											property: 'index',
											direction: 'ASC'
										},
										{
											property: 'name',
											direction: 'ASC'
										}
									]);
								}
							}
						},
						listeners: {
							select: function(cb) {
								var store = pt.store.indicator.available;
								store.parent = cb.getValue();

								if (pt.util.store.containsParent(store)) {
									pt.util.store.loadFromStorage(store);
									pt.util.multiselect.filterAvailable(indicatorAvailable, indicatorSelected);
								}
								else {
									if (cb.getValue() === 0) {
										store.proxy.url = pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.indicator_getall;
										store.load();
									}
									else {
										store.proxy.url = pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.indicator_get + cb.getValue() + '.json';
										store.load();
									}
								}
							}
						}
					},
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							indicatorAvailable,
							indicatorSelected
						]
					}
				],
				listeners: {
					expand: function() {
						pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_indicator);
						pt.util.multiselect.setHeight(
							[indicatorAvailable, indicatorSelected],
							this,
							pt.conf.layout.west_fill_accordion_indicator
						);
					}
				}
			});

			dataElementAvailable = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-left',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				store: pt.store.dataElementAvailable,
				tbar: [
					{
						xtype: 'label',
						text: 'Available', //i18n pt.i18n.available
						cls: 'pt-toolbar-multiselect-left-label'
					},
					'->',
					{
						xtype: 'button',
						icon: 'images/arrowright.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.select(dataElementAvailable, dataElementSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowrightdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.selectAll(dataElementAvailable, dataElementSelected);
						}
					},
					' '
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.select(this, dataElementSelected);
						}, this);
					}
				}
			});

			dataElementSelected = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-right',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				ddReorder: true,
				store: pt.store.dataElementSelected,
				tbar: [
					' ',
					{
						xtype: 'button',
						icon: 'images/arrowleftdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselectAll(dataElementAvailable, dataElementSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowleft.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselect(dataElementAvailable, dataElementSelected);
						}
					},
					'->',
					{
						xtype: 'label',
						text: 'Selected', //i18n DV.i18n.selected,
						cls: 'pt-toolbar-multiselect-right-label'
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.unselect(dataElementAvailable, this);
						}, this);
					}
				}
			};

			dataElement = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-data">Data elements</div>', //i18n
				hideCollapseTool: true,
				items: [
					{
						xtype: 'combobox',
						cls: 'pt-combo',
						style: 'margin-bottom:8px',
						width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding,
						valueField: 'id',
						displayField: 'name',
						fieldLabel: 'Select group', //i18n pt.i18n.select_group
						fieldCls: 'pt-combo-label-default',
						editable: false,
						store: {
							xtype: 'store',
							fields: ['id', 'name', 'index'],
							proxy: {
								type: 'ajax',
								url: pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.dataelementgroup_get,
								reader: {
									type: 'json',
									root: 'dataElementGroups'
								}
							},
							listeners: {
								load: function(s) {
									s.add({
										id: 0,
										name: 'All data element groups', //i18n pt.i18n.all_indicator_groups
										index: -1
									});
									s.sort([
										{
											property: 'index',
											direction: 'ASC'
										},
										{
											property: 'name',
											direction: 'ASC'
										}
									]);
								}
							}
						},
						listeners: {
							select: function(cb) {
								var store = pt.store.dataElementAvailable;
								store.parent = cb.getValue();

								if (pt.util.store.containsParent(store)) {
									pt.util.store.loadFromStorage(store);
									pt.util.multiselect.filterAvailable(dataElementAvailable, dataElementSelected);
								}
								else {
									if (cb.getValue() === 0) {
										store.proxy.url = pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.dataelement_getall;
										store.load();
									}
									else {
										store.proxy.url = pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.dataelement_get + cb.getValue() + '.json';
										store.load();
									}
								}
							}
						}
					},
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							dataElementAvailable,
							dataElementSelected
						]
					}
				],
				listeners: {
					expand: function() {
						pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_indicator);
						pt.util.multiselect.setHeight(
							[dataElementAvailable, dataElementSelected],
							this,
							pt.conf.layout.west_fill_accordion_indicator
						);
					}
				}
			});

			dataSetAvailable = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-left',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				store: pt.store.dataSetAvailable,
				tbar: [
					{
						xtype: 'label',
						text: 'Available', //i18n pt.i18n.available
						cls: 'pt-toolbar-multiselect-left-label'
					},
					'->',
					{
						xtype: 'button',
						icon: 'images/arrowright.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.select(dataSetAvailable, dataSetSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowrightdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.selectAll(dataSetAvailable, dataSetSelected);
						}
					},
					' '
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.select(this, dataSetSelected);
						}, this);
					}
				}
			});

			dataSetSelected = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-right',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				ddReorder: true,
				store: pt.store.dataSetSelected,
				tbar: [
					' ',
					{
						xtype: 'button',
						icon: 'images/arrowleftdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselectAll(dataSetAvailable, dataSetSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowleft.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselect(dataSetAvailable, dataSetSelected);
						}
					},
					'->',
					{
						xtype: 'label',
						text: 'Selected', //i18n DV.i18n.selected,
						cls: 'pt-toolbar-multiselect-right-label'
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.unselect(dataSetAvailable, this);
						}, this);
					}
				}
			};

			dataSet = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-data">Data sets</div>', //i18n
				hideCollapseTool: true,
				items: [
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							dataSetAvailable,
							dataSetSelected
						]
					}
				],
				listeners: {
					expand: function() {
						pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_indicator);
						pt.util.multiselect.setHeight(
							[dataElementAvailable, dataElementSelected],
							this,
							pt.conf.layout.west_fill_accordion_indicator
						);
					}
				}
			});

			rewind = Ext.create('Ext.form.field.Checkbox', {
				paramName: 'rewind',
				boxLabel: 'Rewind one period',
				xable: function() {
					this.setDisabled(pt.util.checkbox.isAllFalse());
				}
			};

			relativePeriod = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-period">Relative periods</div>', //i18n DV.i18n.relative_periods
				hideCollapseTool: true,
				autoScroll: true,
				items: [
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							{
								xtype: 'panel',
								layout: 'anchor',
								bodyStyle: 'border-style:none; padding:0 0 0 10px',
								defaults: {
									labelSeparator: '',
									listeners: {
										added: function(chb) {
											if (chb.xtype === 'checkbox') {
												pt.cmp.dimension.relativeperiod.checkbox.push(chb);
											}
										},
										change: function() {
											rewind.xable();
										}
									}
								},
								items: [
									{
										xtype: 'label',
										text: 'Months', //i18n DV.i18n.months,
										cls: 'pt-label-period-heading'
									},
									{
										xtype: 'checkbox',
										paramName: 'reportingMonth',
										boxLabel: 'Last month', //i18n DV.i18n.last_month
									},
									{
										xtype: 'checkbox',
										paramName: 'last3Months',
										boxLabel: 'Last 3 months', //i18n DV.i18n.last_3_months
									},
									{
										xtype: 'checkbox',
										paramName: 'last12Months',
										boxLabel: 'Last 12 months', //i18n DV.i18n.last_12_months,
										checked: true
									}
								]
							},
							{
								xtype: 'panel',
								layout: 'anchor',
								bodyStyle: 'border-style:none; padding:0 0 0 32px',
								defaults: {
									labelSeparator: '',
									listeners: {
										added: function(chb) {
											if (chb.xtype === 'checkbox') {
												pt.cmp.dimension.relativeperiod.checkbox.push(chb);
											}
										},
										change: function() {
											rewind.xable();
										}
									}
								},
								items: [
									{
										xtype: 'label',
										text: 'Quarters', //i18n DV.i18n.quarters,
										cls: 'dv-label-period-heading'
									},
									{
										xtype: 'checkbox',
										paramName: 'reportingQuarter',
										boxLabel: 'Last quarter', //i18n DV.i18n.last_quarter
									},
									{
										xtype: 'checkbox',
										paramName: 'last4Quarters',
										boxLabel: 'Last 4 quarters', //i18n DV.i18n.last_4_quarters
									}
								]
							},
							{
								xtype: 'panel',
								layout: 'anchor',
								bodyStyle: 'border-style:none; padding:0 0 0 32px',
								defaults: {
									labelSeparator: '',
									listeners: {
										added: function(chb) {
											if (chb.xtype === 'checkbox') {
												pt.cmp.dimension.relativeperiod.checkbox.push(chb);
											}
										},
										change: function() {
											rewind.xable();
										}
									}
								},
								items: [
									{
										xtype: 'label',
										text: 'Six-months', //i18n DV.i18n.six_months,
										cls: 'dv-label-period-heading'
									},
									{
										xtype: 'checkbox',
										paramName: 'lastSixMonth',
										boxLabel: 'Last six-month', //i18n DV.i18n.last_six_month
									},
									{
										xtype: 'checkbox',
										paramName: 'last2SixMonths',
										boxLabel: 'Last two six-months', //i18n DV.i18n.last_two_six_month
									}
								]
							}
						]
					},
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							{
								xtype: 'panel',
								layout: 'anchor',
								bodyStyle: 'border-style:none; padding:5px 0 0 10px',
								defaults: {
									labelSeparator: '',
									listeners: {
										added: function(chb) {
											if (chb.xtype === 'checkbox') {
												pt.cmp.dimension.relativeperiod.checkbox.push(chb);
											}
										},
										change: function() {
											rewind.xable();
										}
									}
								},
								items: [
									{
										xtype: 'label',
										text: 'Years', //i18n DV.i18n.years,
										cls: 'dv-label-period-heading'
									},
									{
										xtype: 'checkbox',
										paramName: 'thisYear',
										boxLabel: 'This year', //i18n DV.i18n.this_year
									},
									{
										xtype: 'checkbox',
										paramName: 'lastYear',
										boxLabel: 'Last year', //i18n DV.i18n.last_year
									},
									{
										xtype: 'checkbox',
										paramName: 'last5Years',
										boxLabel: 'Last 5 years', //i18n DV.i18n.last_5_years
									}
								]
							},
							{
								xtype: 'panel',
								layout: 'anchor',
								bodyStyle: 'border-style:none; padding:5px 0 0 46px',
								defaults: {
									labelSeparator: ''
								},
								items: [
									{
										xtype: 'label',
										text: 'Options',
										cls: 'pt-label-period-heading-options'
									},
									rewind
								]
							}
						]
					}
				],
				listeners: {
					added: function() {
						pt.cmp.dimension.panels.push(this);
					},
					expand: function() {
						pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_relativeperiod);
					}
				}
			});

			fixedPeriodAvailable = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-left',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				store: DV.store.fixedPeriodAvailable,
				tbar: [
					{
						xtype: 'label',
						text: 'Available', //i18n DV.i18n.available,
						cls: 'pt-toolbar-multiselect-left-label'
					},
					'->',
					{
						xtype: 'button',
						icon: 'images/arrowright.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.select(fixedPeriodAvailable, fixedPeriodSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowrightdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.selectAll(fixedPeriodAvailable, fixedPeriodSelected);
						}
					},
					' '
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.select(fixedPeriodAvailable, fixedPeriodSelected);
						}, this);
					}
				}
			});

			fixedPeriodSelected = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'dv-toolbar-multiselect-right',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				ddReorder: false,
				store: DV.store.fixedPeriodSelected,
				tbar: [
					' ',
					{
						xtype: 'button',
						icon: 'images/arrowleftdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselectAll(fixedPeriodAvailable, fixedPeriodSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowleft.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselect(fixedPeriodAvailable, fixedPeriodSelected);
						}
					},
					'->',
					{
						xtype: 'label',
						text: 'Selected', //i18n DV.i18n.selected,
						cls: 'dv-toolbar-multiselect-right-label'
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.unselect(fixedPeriodAvailable, fixedPeriodSelected);
						}, this);
					}
				}
			});

			fixedPeriods = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-period">Fixed periods</div>', //i18n DV.i18n.fixed_periods
				hideCollapseTool: true,
				items: [
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							{
								xtype: 'combobox',
								cls: 'pt-combo',
								style: 'margin-bottom:8px',
								width: 261,
								valueField: 'id',
								displayField: 'name',
								fieldLabel: 'Select type', //i18n DV.i18n.select_type,
								fieldCls: 'pt-combo-label-default',
								editable: false,
								queryMode: 'remote',
								store: pt.store.periodType,
								periodOffset: 0,
								listeners: {
									select: function() {
										var pt = new PeriodType(),
											periodType = this.getValue();

										var periods = pt.get(periodType).generatePeriods({
											offset: this.periodOffset,
											filterFuturePeriods: true,
											reversePeriods: true
										});

										pt.store.fixedPeriodAvailable.setIndex(periods);
										pt.store.fixedPeriodAvailable.loadData(periods);
										pt.util.multiselect.filterAvailable(fixedPeriodAvailable, fixedPeriodSelected);
									}
								}
							},
							{
								xtype: 'button',
								text: 'Prev year', //i18n
								style: 'margin-left:4px',
								height: 24,
								handler: function() {
									var cb = this.up('panel').down('combobox');
									if (cb.getValue()) {
										cb.periodOffset--;
										cb.fireEvent('select');
									}
								}
							},
							{
								xtype: 'button',
								text: 'Next year', //i18n
								style: 'margin-left:3px',
								height: 24,
								handler: function() {
									var cb = this.up('panel').down('combobox');
									if (cb.getValue() && cb.periodOffset < 0) {
										cb.periodOffset++;
										cb.fireEvent('select');
									}
								}
							}
						]
					},
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							fixedPeriodAvailable,
							fixedPeriodSelected
						]
					}
				],
				listeners: {
					added: function() {
						pt.cmp.dimension.panels.push(this);
					},
					expand: function() {
						pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_fixedperiod);
						pt.util.multiselect.setHeight(
							[fixedPeriodAvailable, fixedPeriodSelected],
							this,
							pt.conf.layout.west_fill_accordion_fixedperiod
						);
					}
				}
			});

			organisationUnit = Ext.create('Ext.panel.Panel', {
				hideCollapseTool: true,
				collapsed: false,
				items: [
					{
						layout: 'column',
						bodyStyle: 'border:0 none; padding-bottom:4px',
						items: [
							{
								xtype: 'checkbox',
								columnWidth: 0.5,
								boxLabel: 'User organisation unit', //i18n DV.i18n.user_orgunit,
								labelWidth: pt.conf.layout.form_label_width,
								handler: function(chb, checked) {
									pt.cmp.dimension.organisationunit.toolbar.xable(checked, pt.cmp.favorite.userOrganisationUnitChildren.getValue());
									pt.cmp.dimension.organisationunit.treepanel.xable(checked, pt.cmp.favorite.userOrganisationUnitChildren.getValue());
								},
								listeners: {
									added: function() {
										pt.cmp.favorite.userOrganisationUnit = this;
									}
								}
							},
							{
								xtype: 'checkbox',
								columnWidth: 0.5,
								boxLabel: 'User organisation unit children', //i18n DV.i18n.user_orgunit_children,
								labelWidth: pt.conf.layout.form_label_width,
								handler: function(chb, checked) {
									pt.cmp.dimension.organisationunit.toolbar.xable(checked, DV.cmp.favorite.userOrganisationUnit.getValue());
									pt.cmp.dimension.organisationunit.treepanel.xable(checked, DV.cmp.favorite.userOrganisationUnit.getValue());
								},
								listeners: {
									added: function() {
										pt.cmp.favorite.userOrganisationUnitChildren = this;
									}
								}
							}
						]
					},










			westRegion = Ext.create('Ext.panel.Panel', {
				region: 'west',
				preventHeader: true,
				collapsible: true,
				collapseMode: 'mini',
				items: [





			setListeners = function() {
				pt.store.indicatorAvailable.on('load', function() {
					pt.util.multiselect.filterAvailable(indicatorAvailable, indicatorSelected);
				});

				pt.store.dataElementAvailable.on('load', function() {
					pt.util.multiselect.filterAvailable(dataElementAvailable, dataElementSelected);
				});

				pt.store.dataSetAvailable.on('load', function() {
					pt.util.multiselect.filterAvailable(dataSetAvailable, dataSetSelected);
				});
			}();


