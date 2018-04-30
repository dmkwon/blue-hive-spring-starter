package blue.hive.validation.constraints.impl;

import java.util.ArrayList;

import javax.validation.ConstraintValidatorContext;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import blue.hive.validation.constraints.BHiveAllowExtensions;

/**
 * BHiveAllowExtensions Validator
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveAllowExtensionsForMultipartFile extends BHiveConstraintValidator<BHiveAllowExtensions, MultipartFile> {

	protected String allowExtensionsString = "";
	protected ArrayList<String> allowExtensions = new ArrayList<String>();
	
	@Override
	public void initialize(BHiveAllowExtensions constraintAnnotation) {
		this.allowExtensionsString = constraintAnnotation.value();
		String[] extentions = StringUtils.tokenizeToStringArray(this.allowExtensionsString, ",");
		for (String extention : extentions) {
			allowExtensions.add(extention.toLowerCase());
		}
	}

	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		if(value == null || value.isEmpty()) {
			return true;
		}
		String filename = value.getOriginalFilename();
		String extension = FilenameUtils.getExtension(filename).toLowerCase();
		boolean allowed = false;
		for (String allowExtension : this.allowExtensions) {
			if(allowExtension.equalsIgnoreCase(extension)) {
				allowed = true;
				break;
			}
		}
		if(logger.isTraceEnabled()) {
			logger.trace("isValid({}) allowextension:{} => allowed: {}", filename, allowExtensionsString, allowed);
		}
		return allowed;
	}

}
