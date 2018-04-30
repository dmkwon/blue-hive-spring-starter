package blue.hive.spring.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

/**
 * 이전 ExcelView - 필요시 최신 Spring 소스를 참고하여 새로 구현필요
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
@SuppressWarnings("deprecation")
public class BHiveLegacyExcelView extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

		BHiveExcelCommand command = (BHiveExcelCommand)model.get(BHiveExcelCommand.MODEL_KEY);
		command.buildExcelDocument(workbook, request, response);

	}

}

