package com.shoppr.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shoppr.common.entity.Brand;
import com.shoppr.common.entity.Category;
import com.shoppr.common.entity.Product;

@DataJpaTest(showSql=false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repo;
	
	@Autowired 
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct1() {
		Brand brand = entityManager.find(Brand.class, 10);
		Category category = entityManager.find(Category.class, 15);
		
		Product product = new Product();
		product.setName("Galaxy S20+");
		product.setAlias("galaxy_s20_plus");
		product.setShortDescription("Latest Samsung Smartphone.");
		product.setFullDescription("Latest Samsung Smartphone, launched in 2020!");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(899);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateProduct2() {
		Brand brand = entityManager.find(Brand.class, 38);
		Category category = entityManager.find(Category.class, 6);
		
		Product product = new Product();
		product.setName("Dell Inspiron 4577");
		product.setAlias("inspiron_4577");
		product.setShortDescription("Latest Dell laptop.");
		product.setFullDescription("Latest dell laptop, launched in 2020!");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(1899);
		product.setCost(1700);
		product.setEnabled(true);
		product.setInStock(true);
		
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllProducts() {
		Iterable<Product> products  =repo.findAll();
		
		products.forEach(System.out::println);
	}
	
	@Test
	public void testUpdateProduct() {
		Integer id = 1;
		
		Product product = repo.findById(id).get();
		product.setPrice(799);
		
		repo.save(product);
		
		Product updatedProduct = entityManager.find(Product.class, id);
		
		System.out.println(updatedProduct);
		assertThat(updatedProduct.getPrice()).isEqualTo(799);
	}
	
	@Test
	public void testDeleteProduct() {
		Integer id = 2;
		
		repo.deleteById(id);
		
		Optional<Product> result = repo.findById(id);
		
		assertThat(!result.isPresent());
	}
	
	@Test
	public void saveProductWithImages() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.setMainImage("main-image.png");
		product.addExtraImage("extra-image-1.png");
		product.addExtraImage("extra-image-2.png");
		product.addExtraImage("extra-image-3.png");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}
	
	@Test
	public void testSaveProductWithDetails() {
		Integer id = 1;
		Product product = repo.findById(id).get();
		
		product.addDetail("Device Memory", "128GB");
		product.addDetail("Processor", "Mediatek");
		product.addDetail("RAM", "8GB");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getDetails()).isNotEmpty();
	}
}
