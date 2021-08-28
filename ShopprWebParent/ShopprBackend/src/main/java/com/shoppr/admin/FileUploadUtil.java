package com.shoppr.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
	
	public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
		
		Path uploadPath = Paths.get(uploadDir);
		
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		
		try (InputStream inputStream = multipartFile.getInputStream()){
			 Path filePath = uploadPath.resolve(fileName);
			 
			 Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		}catch(IOException e) {
			throw new IOException("Could not save file: "+ fileName, e);
		}
	}
	
	public static void cleanDir(String dir) throws IOException {
		Path dirPath = Paths.get(dir);
		
		try {
			Files.list(dirPath).forEach(file -> {
				if(!Files.isDirectory(file)) {
					try {
						Files.delete(file);
					} catch (IOException e) {
						System.out.println("Could not delete the file: " + file);
					}
				}
			});
		}catch(IOException i){
			throw new IOException("Not Found", i);
		}
	}
	
	public static void deleteDirectory(File file) {

	    File[] list = file.listFiles();
	    if (list != null) {
	        for (File temp : list) {
	            //recursive delete
	        	
	        	deleteDirectory(temp);
	            temp.delete();
	            
	        }
	    }
	}
	public static void deleteFolder(String dir, Integer id) throws IOException{
		Path dirPath = Paths.get(dir);

		if(Files.exists(dirPath)) {
			File file = dirPath.toFile();
			
			File[] list = file.listFiles();
		    if (list != null) {
		        for (File temp : list) {
		            //recursive delete

		        	if(temp.getName().equals(id.toString()))
		        	{
			            deleteDirectory(temp);
			            temp.delete();
			            break;
		        	}
		        }
		    }
		}
	}

}
