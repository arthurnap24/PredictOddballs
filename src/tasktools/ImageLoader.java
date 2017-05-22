package tasktools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ImageLoader {
	
	public static void main(String[] args) {
		File file = new File("res/sounds");
		try {
		  //creates a file if you don't have one, overwrites existing
		  
			PrintWriter imageList = new PrintWriter("res/sounds/soundList.txt");
			String[] imageNames = file.list();
			for(String string : imageNames) {
				if(string.contains(".wav")) {
					imageList.println(string);
				}
			}
			imageList.close();
		} catch(FileNotFoundException e) {
			System.out.println("not found");
		}
		
		
	}
}
