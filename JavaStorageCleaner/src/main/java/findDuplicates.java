package main.java;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

public class findDuplicates {
	public class DuplicatesList{
		String hash = null;
		ArrayList<Path> pathList = null;
		
		public DuplicatesList() {
			hash = null;
			pathList = new ArrayList<>();
		}
		
		public DuplicatesList(String hash, Path path) {
			if(pathList == null)
				pathList = new ArrayList<>();
			this.hash = hash;
			pathList.add((Path)hashMap.get(hash));
			pathList.add(path);
		}
	}
	private HashMap<String, Path> hashMap = new HashMap<>();
	private ArrayList<DuplicatesList> list = new ArrayList<>();
	private ArrayList<Path> bigSize = new ArrayList<>();
	
	public static void main(String[] args) throws IOException {
		String pathStr = "/Users/duck/Desktop/Code/java/hw1";
		Path path = Paths.get(pathStr);
		findDuplicates demo = new findDuplicates();
		demo.hashing(path);
		
		Collection<Path> values = demo.hashMap.values();
		for(Path p : values)
			System.out.println(p.getFileName());
		System.out.println();
		if(demo.list.isEmpty()) System.out.println("No Duplicates");
		else {
			System.out.println("Duplicates Files");
			for(DuplicatesList l : demo.list) {
				System.out.print("[ ");
				for(Path p : l.pathList)
					System.out.print(p.getFileName() + " ");
				System.out.println("]");
			}
		}
		
		if(demo.bigSize.isEmpty()) System.out.println("No Big Size");
		else {
			System.out.println("\n----- more than 700 bytes -----");
			for(Path p : demo.bigSize) {
				System.out.println(p.getFileName() + ": " + Files.size(p) + " bytes");
			}
			System.out.println("---------------------------------\n");
		}
	}
	
	private void hashing(Path path) throws IOException {
		Files.walkFileTree(path, new FileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
				try {
					if(Files.size(file) >= 700) bigSize.add(file);
					
					MessageDigest digest = MessageDigest.getInstance("SHA-256");
					byte[] hashBytes = digest.digest(Files.readAllBytes(file));
					StringBuilder hexhash = new StringBuilder();
					for(byte b : hashBytes)
						hexhash.append(String.format("%02x", b));
					
					String hashStr = hexhash.toString();
					if(hashMap.get(hashStr) == null)
						hashMap.put(hashStr, file);
					else {
						boolean nohash = true;
						for(DuplicatesList l : list) {
							if(l.hash.equals(hashStr)) {
								l.pathList.add(file);
								nohash = false;
							}
						}
						if(nohash) list.add(new DuplicatesList(hashStr, file));
					}
				} catch (NoSuchAlgorithmException e) {
					System.err.println("Hashing Error");
					e.printStackTrace();
				}
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException{
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) throws IOException {
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});
		
	}
}