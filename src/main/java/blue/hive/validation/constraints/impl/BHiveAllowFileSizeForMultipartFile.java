package blue.hive.validation.constraints.impl;

import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import blue.hive.validation.constraints.BHiveAllowFileSize;

/**
 * BHiveAllowFileSize Validator
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveAllowFileSizeForMultipartFile extends BHiveConstraintValidator<BHiveAllowFileSize, MultipartFile> {

	protected long allowSize = 0;
	//protected String fancyAllowSize = "";
	
	@Override
	public void initialize(BHiveAllowFileSize constraintAnnotation) {
		this.allowSize = constraintAnnotation.value();
		//this.fancyAllowSize = AbleStringUtil.readableFileSize(this.allowSize);
	}

	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		if(value == null || value.isEmpty()) {
			return true;
		}
		long filesize = value.getSize();
		boolean allowed = (filesize <= allowSize);
		if(logger.isTraceEnabled()) {
			logger.trace("isValid({}) allowSize:{} => allowed: {}", filesize, allowSize, allowed);
		}
		return allowed;
	}
}
