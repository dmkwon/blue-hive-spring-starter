package blue.hive.fileio.filter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class FailFastFileFilter implements FileFilter {
	protected final List<FileFilter> children = new ArrayList<FileFilter>();

	public FailFastFileFilter(FileFilter... filters) {
		for (FileFilter filter: filters) {
			if (filter != null)
				this.children.add(filter);
		}       
	}

	public boolean accept(File pathname) {
		for (FileFilter filter: this.children) {
			if (!filter.accept(pathname)) {
				return false; // fail on the first reject
			}
		}

		return true;
	}
}