package blue.hive.fileio.filter;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FilenameUtils;

public class ExcludeFileDirFileFilter implements FileFilter {
	protected final String filename;

	public ExcludeFileDirFileFilter(String filename) {
		this.filename = filename.toLowerCase();
	}

	public boolean accept(File file) {
		if (!file.isDirectory())
			if(FilenameUtils.getName(file.getAbsolutePath()).toLowerCase().equals(filename.toLowerCase()))
				return false;
			else
				return true;
		else
			return true;
	}
}