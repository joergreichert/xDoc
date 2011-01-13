package templates.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

public class CopyUtil {

	private static Logger logger = Logger.getLogger(CopyUtil.class);

	public void copy(String basePath, String source, String targetDirName) {
		int fileStart = source.lastIndexOf(File.separatorChar);
		String dir = source.substring(0, fileStart);
		String fileName = source.substring(fileStart);
		File targetDir = new File(targetDirName + File.separatorChar + dir);
		if(targetDir.exists() || targetDir.mkdirs()){
			File sourceFile = new File(basePath + File.separatorChar + source);
			if(!sourceFile.exists()) {
				logger.error("File " + source + " does not exist.");
				throw new RuntimeException("File " + source + " does not exist.");
			}
			File targetFile = new File(targetDirName + File.separatorChar + dir + File.separatorChar + fileName);
			if(!targetFile.exists() || (targetFile.lastModified() < sourceFile.lastModified())) {
				try {
					targetFile.createNewFile();
				} catch (IOException e1) {
					logger.error("Unable to create file ", e1);
					throw new RuntimeException("Unable to create file " + targetFile.getName(), e1);
				}
				FileChannel sourceFileChannel = null;
				FileChannel targetFileChannel = null;
				try {
					sourceFileChannel = new FileInputStream(sourceFile).getChannel();
					targetFileChannel = new FileOutputStream(targetFile).getChannel();
					targetFileChannel.transferFrom(sourceFileChannel, 0, sourceFileChannel.size());
					logger.info("Copied " + sourceFile + " to " + targetFile);
				} catch (Exception e) {
					logger.error("unable to copy file " + source, e);
				}
				try {
					sourceFileChannel.close();
					targetFileChannel.close();
				} catch (IOException e) {
					logger.error("unable to close channel", e);
				}
			}
		}
	}
}
