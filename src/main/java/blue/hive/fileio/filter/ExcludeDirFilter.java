package blue.hive.fileio.filter;

import java.io.File;
import java.io.FileFilter;

public class ExcludeDirFilter implements FileFilter {

	public ExcludeDirFilter() {}

	public boolean accept(File file) {
		if (file.isDirectory())
			return false;
		else
			return true;
	}
}