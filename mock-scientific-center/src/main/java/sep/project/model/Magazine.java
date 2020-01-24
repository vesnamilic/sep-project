package sep.project.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Magazine {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private String name;

	@Column
	private String ISBN;
	
	@Column
	private String whoPays;
	
	@Column
	private float price;
	
	@ManyToMany
	Set<ScientificArea> scientificArea;
	
	public Magazine() {
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public String getWhoPays() {
		return whoPays;
	}

	public void setWhoPays(String whoPays) {
		this.whoPays = whoPays;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Set<ScientificArea> getScientificArea() {
		return scientificArea;
	}

	public void setScientificArea(Set<ScientificArea> scientificArea) {
		this.scientificArea = scientificArea;
	}
	
}