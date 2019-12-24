package sep.project.DTOs;

import sep.project.enums.FieldType;

public class FieldDTO {
		
	private String name;
	
	private String label;
	
	private FieldType type;
	
	private Boolean required;
	
	public FieldDTO(){
		
	}
	
	public FieldDTO(String name, String label, FieldType type, Boolean required){
		this.name = name;
		this.label = label;
		this.type = type;
		this.required = required;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
