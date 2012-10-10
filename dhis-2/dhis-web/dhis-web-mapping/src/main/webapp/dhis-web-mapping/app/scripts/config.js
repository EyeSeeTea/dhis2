Ext.define('Ext.ux.button.ColorButton', {
	extend: 'Ext.button.Button',
	alias: 'widget.colorbutton',
	width: 109,
	height: 22,
	defaultValue: null,
	value: 'f1f1f1',
	getValue: function() {
		return this.value;
	},
	setValue: function(color) {
		this.value = color;
		if (Ext.isDefined(this.getEl())) {
			this.getEl().dom.style.background = '#' + color;
		}
	},
	reset: function() {
		this.setValue(this.defaultValue);
	},
	menu: {},
	menuHandler: function() {},
	listeners: {
		added: function() {
			var that = this;			
			this.defaultValue = this.value;			
			this.menu = Ext.create('Ext.menu.Menu', {
				showSeparator: false,
				items: {
					xtype: 'colorpicker',
					closeAction: 'hide',
					listeners: {
						select: function(cp, color) {
							that.setValue(color);
							that.menu.hide();
							that.menuHandler(cp, color);
						}
					}
				}
			});						
		},
		render: function() {
			this.setValue(this.value);
		}
	}
});

