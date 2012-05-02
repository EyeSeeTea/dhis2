
jQuery(document).ready(function() {
	validation2('addInventoryTypeForm', function(form) {
		form.submit();
	}, {
		'beforeValidateHandler' : function() {
            $("#selectedInventoryTypeAttributeList").find("option").attr("selected", "selected");
		},
	});
	
	jQuery("#availableInventoryTypeAttributeList").dhisAjaxSelect({
		source: "inventoryTypeAttributes.action",
		iterator: "inventoryTypeAttributes",
		connectedTo: 'selectedInventoryTypeAttributeList',
		handler: function(item) {
			var option = jQuery("<option />");
			option.text( item.name );
			option.attr( "value", item.id );
			
			return option;
			
		}
	});		
	
	checkValueIsExist("name", "validateInventoryType.action");
});

