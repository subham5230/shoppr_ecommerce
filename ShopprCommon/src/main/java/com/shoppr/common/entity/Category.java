package com.shoppr.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=128, nullable=false, unique=true)
	private String name;
	
	@Column(length=64, nullable=false)
	private String alias;
	
	@Column(length=128, nullable=false)
	private String image;
	
	private boolean enabled;
	
	@Column(name="all_parent_ids", length=256, nullable=true)
	private String allParentIDs;
	
	@OneToOne
	@JoinColumn(name="parent_id")
	private Category parent;
	
	@OneToMany(mappedBy="parent")
	@OrderBy("name asc")
	private Set<Category> children = new HashSet<>();
	

	public Category() {
	}

	public Category(Integer id) {
		this.id = id;
	}

	public Category(String string) {
		this.name = string;
		this.alias = string;
		this.image = "default.png";
	}
	
	public Category(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Category(String name, Category parent) {
		this(name);
		this.parent = parent;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}
	

	public String getAllParentIDs() {
		return allParentIDs;
	}

	public void setAllParentIDs(String allParentIDs) {
		this.allParentIDs = allParentIDs;
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	@Transient
	public String getImagePath() {
		
		if(id == null) return "/images/image-thumbnail.png";
		
		return "/category-photos/" + this.id + "/" + this.image;
	}
	
	
	public static Category copyIdAndName(Category category) {
		Category newCategory = new Category();
		newCategory.setId(category.getId());
		newCategory.setName(category.getName());
		
		return newCategory;
	}
	
	public static Category copyIdAndName(Integer id, String name) {
		Category newCategory = new Category();
		newCategory.setId(id);
		newCategory.setName(name);
		
		return newCategory;
	}
	
	
}
