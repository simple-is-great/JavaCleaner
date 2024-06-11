import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DuplicateFinder {
	public class DuplicatesList {
		String hash = null;
		ArrayList<Path> pathList = null;

		public DuplicatesList() {
			hash = null;
			pathList = new ArrayList<>();
		}

		public DuplicatesList(String hash, Path path) {
			if (pathList == null)
				pathList = new ArrayList<>();
			this.hash = hash;
			pathList.add((Path) hashMap.get(hash));
			pathList.add(path);
		}
	}

	private HashMap<String, Path> hashMap = new HashMap<>();
	private ArrayList<DuplicatesList> list = new ArrayList<>();
	private ArrayList<Path> bigSize = new ArrayList<>();
	private static long DUPLICATE_LOWER_BOUND = 500; // 500B
	private static long INITIAL_READ = 1000 * 1000;
	private static final int bufferSize = 10 * 1000 * 1000; // 10MB
	private static final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
	private static final String commonPath = Paths.get("").toAbsolutePath().toString() + "/testRelated";


	public static byte[] randomRead(Path path, long readAmount) {
		if (readAmount <= 0) {
			throw new IllegalArgumentException("read amount must be greater than zero.");
		}

		Random rand;
		try {
			// set readAmount to fileSize if fileSize is smaller than readAmount
			long fileSize = Files.size(path);
			readAmount = fileSize < readAmount ? fileSize : readAmount;
		} catch (IOException e) {
			System.err.println("Error in reading file size " + path);
		}

		byte[] result = new byte[(int) readAmount];
		int chunkSize = (int) (readAmount / 10);
		int remainder = (int) (readAmount % 10);


		try {
			// seed Random instance with file size divided by 1000
			long seed = Files.size(path) / 1000;
			rand = new Random(seed);

			// open the file channel for reading
			try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
				long fileSize = fileChannel.size();

				for (int i = 0; i < 10; i++) {
					// determine the random position to read from
					long randomPosition = Math.abs(rand.nextLong()) % Math.max(fileSize - chunkSize, 1);
					fileChannel.position(randomPosition);

					// read from the random position into the buffer
					buffer.clear();  // Clear buffer before each read
					int bytesRead = fileChannel.read(buffer);
					// Check if bytesRead is valid

					if (bytesRead > 0) {
						buffer.flip();  // Prepare buffer for reading
						// Ensure we do not go out of bounds in the result array
						buffer.get(result, i * chunkSize, Math.min(bytesRead, chunkSize));
					} else if (bytesRead == -1) {
						System.err.println("Reached end of file unexpectedly.");
						break;
					}

					// Handle the remainder if there is any
					if (remainder > 0) {
						randomPosition = Math.abs(rand.nextLong()) % Math.max(fileSize - remainder, 1);
						fileChannel.position(randomPosition);
						bytesRead = fileChannel.read(buffer);
						if (bytesRead > 0) {
							buffer.flip();
							buffer.get(result, 10 * chunkSize, Math.min(bytesRead, remainder));
						}
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error in randomRead, error reading file: " + path);
			e.printStackTrace();
		}

		return result;
	}

	public static List<Path> getPathList(Path path) {
		// 1: 파일 경로를 재귀적으로 탐색하며 Path 저장
		List<Path> pathList = new ArrayList<>();
		Path dsStore = Paths.get(commonPath + "/listPath/.DS_Store");

		try {
			Files.walk(path).forEach(source -> {
				if (!Files.isDirectory(source)) { // do not add directory to path list
					if (!source.equals(dsStore)) {
						pathList.add(source);
					}

				}
			});
		} catch (IOException e) {
			System.err.println("Error in getting file list: " + path);
		}

		return pathList;
	}


	public static Map<Long, List<Path>> collectSameSizeFiles(Path path) {
		// 2단계: Paths.size() 활용, size 같은 것끼리 모으기
		List<Path> pathList = getPathList(path);
		Map<Long, List<Path>> sizePathMap = new HashMap<>();

		try {
			for (Path p : pathList) {
				Long size = Files.size(p);
				List<Path> innerList;
				if (size > 0) {
					innerList = sizePathMap.get(size);
					if (innerList == null) {
						innerList = new ArrayList<>();
					}

					innerList.add(p);
					Collections.sort(innerList);
					sizePathMap.put(size, innerList);
				}
			}
			for (Long key : sizePathMap.keySet()) {
				if (sizePathMap.get(key).size() == 1) {
					// remove innerList with size 1
					sizePathMap.remove(key);
				}
			}
		} catch (IOException e) {
			System.err.println("Error in getting file size: ");
		}

		return sizePathMap;
	}

	public static Map<String, List<Path>> findDuplicates(String sourceDirectory) {
//		3단계: size 같은 것만 파일 읽어서 해시값 생성
//				- 파일 전체 범위를 1MB 정도 랜덤하게 샘플링
//		- if 파일 크기 1MB 미만 -> 전체 읽어서 해시값(sha256)으로 치환
//		- 1MB씩 증가시키면서 5MB일때까지 탐색
//		- 동일하다면 -> 맵(HashMap)에 "파일 경로": [파일 리스트] 담아서 리턴
		Path sourceDir = Paths.get(sourceDirectory);
		Map<String, List<Path>> duplicatesMap = new HashMap<>();

		Map<Long, List<Path>> sameSizePathMap = collectSameSizeFiles(sourceDir);
		Set<Long> sameSizeMapKeys = sameSizePathMap.keySet();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
			StringBuilder hexHash = new StringBuilder();
			String hashStr = "";
			for (Long key : sameSizeMapKeys) {
				for (Path p : sameSizePathMap.get(key)) {
					byte[] hashBytes = digest.digest(randomRead(p, INITIAL_READ));
					for (byte b : hashBytes) {
						hexHash.append(String.format("%02x", b));
					}

					hashStr = hexHash.toString();
					List<Path> innerList = duplicatesMap.get(hashStr);
					if (innerList == null) {
						innerList = new ArrayList<>();
					}
					innerList.add(p);
					duplicatesMap.put(hashStr, innerList);
					hexHash.delete(0, hexHash.length());
				}

				if (duplicatesMap.get(hashStr).size() == 1) {
					// delete key-val pairs with single Path
					duplicatesMap.remove(hashStr);
				}
			}
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Error in getting hashing instance");
		}

		return duplicatesMap;
	}
}