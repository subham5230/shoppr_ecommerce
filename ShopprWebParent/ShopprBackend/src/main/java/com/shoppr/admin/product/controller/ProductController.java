package com.shoppr.admin.product.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shoppr.admin.FileUploadUtil;
import com.shoppr.admin.brand.BrandService;
import com.shoppr.admin.category.CategoryService;
import com.shoppr.admin.product.ProductService;
import com.shoppr.admin.security.ShopprUserDetails;
import com.shoppr.common.entity.Brand;
import com.shoppr.common.entity.Category;
import com.shoppr.common.entity.Product;
import com.shoppr.common.entity.ProductImage;
import com.shoppr.common.exception.ProductNotFoundException;

@Controller
public class ProductController {

	@Autowired
	private ProductService service;
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	
	@GetMapping("/products")
	public String getProductsList(Model model) {

		return "redirect:/products/page/1?sortField=&sortDir=&keyword=";
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword,
			@Param("categoryId") Integer categoryId) {
		
		if(sortDir.isEmpty())
			sortField="";
		
		if(pageNum <= 0) {
			
			List<Category> listCategories = categoryService.listCategoriesUsedInForm();
			
			model.addAttribute("keyword", keyword);
			model.addAttribute("totalItems", 0);
			model.addAttribute("listCategories", listCategories);
			return "/products/products";
		}
		
		List<Product> listProducts = null;
		Page<Product> page = service.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		if(pageNum > page.getTotalPages())
			return "redirect:/products/page/" + page.getTotalPages() + "?sortField=" + sortField + "&sortDir=" + sortDir
					+ "&keyword=" + keyword;
		else
			listProducts = page.getContent();
			
		long startCount  = (pageNum - 1) * ProductService.ITEMS_PER_PAGE + 1;
		long endCount = startCount + ProductService.ITEMS_PER_PAGE - 1;
		if(endCount > page.getTotalElements())
			endCount = page.getTotalElements();

		String reverseSortDir = null;

		if(sortDir.equals("asc")) {
			reverseSortDir = "desc";
		}
		if(sortDir.equals("desc")) {
			reverseSortDir = "";
		}
		if(sortDir.isEmpty()) {
			reverseSortDir = "asc";
		}
		
		if(categoryId != null) model.addAttribute("categoryId", categoryId);
	
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("pageTitle", "Manage Products");
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "/products/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		Integer numberOfExistingExtraImages = product.getImages().size();
		
		model.addAttribute("product", product);
		model.addAttribute("pageTitle", "Create Product");
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes,
			@RequestParam(value = "profile-image", required = false) MultipartFile mainImageMultipart,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(name="detailIDs", required = false) String[] detailIDs,
			@RequestParam(name="detailNames", required = false) String[] detailNames,
			@RequestParam(name="detailValues", required = false) String[] detailValues,
			@RequestParam(name="imageIDs", required = false) String[] imageIDs,
			@RequestParam(name="imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopprUserDetails loggeUser) throws IOException {
		
		
		if(loggeUser.hasRole("Salesperson")) {
			service.saveSalespersonUpdate(product);
			redirectAttributes.addFlashAttribute("product_message", "Product updated successfully.");
			
			return "redirect:/products";
		}
		
		setMainImageName(mainImageMultipart, product);
		setExistingExtraImageNames(imageIDs, imageNames, product);
		setNewExtraImageNames(extraImageMultiparts, product);
		setProductDetails(detailIDs, detailNames, detailValues, product);
		
		Integer id = product.getId();
		
		Product savedProduct = service.save(product);
		
		saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
		
		deleteExtraOrphanImages(product);
		
		if(id == null)
			redirectAttributes.addFlashAttribute("product_message", "Product created successfully.");
		
		else
			redirectAttributes.addFlashAttribute("product_message", "Product updated successfully.");
		
		String key = savedProduct.getName();
		
		return "redirect:/products/page/1?sortField=&sortDir=&keyword=" + key ;
	}
	
	private void deleteExtraOrphanImages(Product product) {
		String uploadPath = "../product-images/" + product.getId() + "/extras";
		Path dirPath = Paths.get(uploadPath);
		
		try {
			Files.list(dirPath).forEach(file -> {
				String filename = file.toFile().getName();
				
				if(!product.containsImageName(filename)) {
					try {
						Files.delete(file);
					}
					catch(IOException e) {
						System.out.println("Could not delete extra image: " + filename);
					}
				}
			});
		}
		catch(IOException i) {
			
		}
		
	}

	private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
		
		if(imageIDs == null || imageIDs.length == 0) return;
		
		Set<ProductImage> images = new HashSet<>();
		for(int count=0; count < imageIDs.length; count++) {
			Integer id  = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];
			
			images.add(new ProductImage(id, name, product));
		}
		
		product.setImages(images);
	}

	private void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
		if(detailNames == null || detailNames.length == 0) return;
		
		for(int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			
			if(id != 0) {
				product.addDetail(id, name, value);
			}
			
			else if(!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
		}
		
	}

	private void saveUploadedImages(MultipartFile mainImageMultipart, 
			MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
		
		if(!mainImageMultipart.isEmpty()) {
			
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			String uploadDir = "../product-images/" + savedProduct.getId();
			try {
				FileUploadUtil.cleanDir(uploadDir);
			} catch (IOException e) {
				
			}
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}
		
		if(extraImageMultiparts.length > 0) {
			
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
			
			for(MultipartFile multipartFile : extraImageMultiparts) {
				if(!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
				}
			}
		}
	}
	
	private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
		
		if(!mainImageMultipart.isEmpty()) {
			
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
	
	private void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
		
		if(extraImageMultiparts.length > 0) {
			for(MultipartFile multipartFile : extraImageMultiparts) {
				if(!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					if(!product.containsImageName(fileName))
						product.addExtraImage(fileName);
				}
			}
		}
	}
	
	@GetMapping("/products/{id}/enabled/{enabled}")
	public String updateUserEnabledStatus(@PathVariable(name="id") Integer id,
											@PathVariable(name="enabled") boolean enabled
											,RedirectAttributes redirectAttributes,
											@Param("currentPage") String currentPage,
											@Param("sortField") String sortField, 
											@Param("sortDir") String sortDir,
											@Param("keyword") String keyword,
											@Param("categoryId") Integer categoryId) {
		
		service.updateProductEnabledStatus(id, enabled);
		String status = enabled? "enabled" : "disabled";
		String message = "Product ID: "+ id + " has been " + status;
		
		redirectAttributes.addFlashAttribute("product_message", message);
		
		String categoryUrl = categoryId != null ?  "&categoryId=" + categoryId : "";
		
		return "redirect:/products/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir
				+ "&keyword=" + keyword + categoryUrl;
	}
	
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes,
			Model model,
			@Param("currentPage") String currentPage,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword,
			@Param("categoryId") Integer categoryId) {
		

		try {
			
			String dir = "../product-images";
			try {
				FileUploadUtil.deleteFolder(dir, id);
			} catch (IOException e) {
				
			}
			
			service.delete(id);
			redirectAttributes.addFlashAttribute("product_message", "Product: " + id + " has been removed.");
			
		}catch(ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("product_message", e.getMessage());
			
		}
		
		String categoryUrl = categoryId != null ?  "&categoryId=" + categoryId : "";
		
		return "redirect:/products/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir
				+ "&keyword=" + keyword + categoryUrl;
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Product product = service.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();
			
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", "Edit Product");
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
		
			return "products/product_form";
		}
		catch(ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("product_message", e.getMessage());
			
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetail(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Product product = service.get(id);
			
			model.addAttribute("product", product);
			
			return "products/product_detail_modal";
		}
		catch(ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("product_message", e.getMessage());
			
			return "redirect:/products";
		}
	}
	
}
