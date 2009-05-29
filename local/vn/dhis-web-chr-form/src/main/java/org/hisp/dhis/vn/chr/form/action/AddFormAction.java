package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import com.opensymphony.xwork.Action;

public class AddFormAction implements Action{

	// -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

	private FormService formService;
	
	// -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

	private String name;
	private String label;
	private int noRow;
	private int noColumn;
	private int noColumnLink;
	private String icon;
	private String visible;
	private String attached;
	
	// -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

	public void setFormService(FormService formService) {
		this.formService = formService;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getNoRow() {
		return noRow;
	}

	public void setNoRow(int noRow) {
		this.noRow = noRow;
	}

	public int getNoColumn() {
		return noColumn;
	}

	public void setNoColumn(int noColumn) {
		this.noColumn = noColumn;
	}

	public int getNoColumnLink() {
		return noColumnLink;
	}

	public void setNoColumnLink(int noColumnLink) {
		this.noColumnLink = noColumnLink;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getAttached() {
		return attached;
	}

	public void setAttached(String attached) {
		this.attached = attached;
	}
	
	// -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

	public String execute() throws Exception{
		
		Form form = new Form();
		form.setName(CodecUtils.unescape(name));
		form.setLabel(CodecUtils.unescape(label));
		form.setNoRow(noRow);
		form.setNoColumn(noColumn);
		form.setNoColumnLink(noColumnLink);
		form.setIcon(icon);
		form.setVisible(visible);
		form.setAttached(attached);
		form.setDesc1("");
		
		formService.addForm(form);
		
		return SUCCESS;
	}
}
