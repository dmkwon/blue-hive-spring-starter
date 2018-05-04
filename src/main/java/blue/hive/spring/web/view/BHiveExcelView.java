package blue.hive.spring.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import blue.hive.exception.BHiveRuntimeException;


/**
 * support excel view
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
@SuppressWarnings("deprecation")
public class BHiveExcelView extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

		BHiveExcelCommand command = (BHiveExcelCommand)model.get(BHiveExcelCommand.MODEL_KEY);
		if(command == null) {
			logger.error("BHiveExcelCommand cannot found at Model");
			throw new BHiveRuntimeException("엑셀파일 생성에 실패하였습니다.");
		}

		command.buildExcelDocument(workbook, request, response);
	}
}