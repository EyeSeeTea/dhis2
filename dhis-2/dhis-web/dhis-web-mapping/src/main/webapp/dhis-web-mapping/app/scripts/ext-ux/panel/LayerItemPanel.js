Ext.define('Ext.ux.panel.LayerItemPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.layeritempanel',
	layout: 'column',
	layer: null,
	checkbox: null,
	numberField: null,
	imageUrl: null,
	text: null,
	width: 184,
	height: 22,
	value: false,
	opacity: 80,
	getValue: function() {
		return this.checkbox.getValue();
	},
	setValue: function(value) {
		this.checkbox.setValue(value);
		this.numberField.setDisabled(!value);
	},
	setNumberFieldValue: function(value) {
		this.numberField.setValue(value);
	},		
	setLayerOpacity: function(value) {
		this.layer.setLayerOpacity(value === 0 ? 0 : value/100);
	},
	setOpacity: function(value) {
		this.setNumberFieldValue(value);
		this.setLayerOpacity(value);
	},
	initComponent: function() {
		var that = this,
			image;
			
		image = Ext.create('Ext.Img', {
			width: 14,
			height: 14,
			src: this.imageUrl
		});
		
		this.checkbox = Ext.create('Ext.form.field.Checkbox', {
			width: 14,
			listeners: {
				change: function(chb, value) {
					that.layer.setVisibility(value);
					that.setValue(value);
				}
			}
		});
		
		this.numberField = Ext.create('Ext.form.field.Number', {
			width: 47,
			height: 18,
			minValue: 0,
			maxValue: 100,
			value: this.layer.layerOpacity * 100,
			allowBlank: false,
			disabled: this.numberFieldDisabled,
			listeners: {
				change: function() {
					that.setLayerOpacity(this.getValue());
				}
			}
		});
		
		this.items = [
			{
				width: this.checkbox.width + 6,
				items: this.checkbox
			},
			{
				width: image.width + 6,
				items: image,
				bodyStyle: 'padding-top: 4px'
			},
			{
				width: 97,
				html: this.text,
				bodyStyle: 'padding-top: 4px'
			},
			{
				width: this.numberField.width,
				items: this.numberField
			}
		];		
		
		this.setValue(this.value);
		this.setOpacity(this.opacity);
		
		this.callParent();
	}
});
