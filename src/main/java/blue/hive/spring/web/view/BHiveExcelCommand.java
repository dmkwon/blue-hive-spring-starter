package blue.hive.spring.web.view;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.supercsv.io.ICsvBeanWriter;

import blue.hive.annotation.ExcelColumn;
import blue.hive.exception.BHiveRuntimeException;
import blue.hive.util.BHiveVOUtil;


/**
 * WideExcelView에 Excel의 출력을 위한 정보 Command DT
 *
 * 뷰명칭: 뷰클래스
 *  - ableExcelView: intelliwframe.spring.web.view.AbleExcelView
 * 	- ableXlsxExcelView: intelliwframe.spring.web.view.AbleXlsxExcelView
 * 	- ableStreamingExcelView: intelliwframe.spring.web.view.AbleStreamingExcelView
 * 	- ableCsvView: intelliwframe.spring.web.view.AbleCsvView
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */

public class BHiveExcelCommand {

	protected final static Logger logger = LoggerFactory.getLogger(BHiveExcelCommand.class);

	/** 모델에 담을때 사용하는 Key */
	public final static String MODEL_KEY = "bHiveExcelCommand";

	/** 확장자 */
	public final static String EXTENSTION = ".xls";
	public final static String EXTENSION_CSV = ".csv";

	/** 자동 Width 적용후 보정값 (헤더컬럼용) */
	private static double AUTO_SIZE_HEADERCOLUMN_MULTIFIER = 1.2;
	/** 자동 Width 적용후 보정값 */
	private static double AUTO_SIZE_COLUMN_MULTIFIER = 1.3;

	/** 메시지 소스를 통한 메시지 변환시 사용하는 Message Prefix */
	protected String messagePrefix = "com.col.";

	////////////////////////////////////////////////////////////////////////////////
	/** 메시지 소스 */
	protected MessageSource messageSource;

	/** 다운로드 파일명 */
	protected String filename = "";
	public void setFilename(String filename) {
		if(StringUtils.isEmpty(filename)) {
			filename = "datalist";
		}
		filename = FilenameUtils.removeExtension(filename);
		this.filename = filename;
	}
	/** 엑셀내 출력 제목(출력용) */
	protected String title = "";
	/** 엑셀 SheetName */
	protected String sheetName = "data sheet";

	/** 출력할 데이터셋 */
	protected List<Object> dataList = new ArrayList<Object>();
	/** 출력한 데이터 아이템의 메타정보 클래스. ExcelColumn Annotation으로 꾸미기 */
	protected Class<?> metadataClass;

	/** 출력데이터의 가로,세로 병합을 처리하기 위한 병합처리모드 */
	protected BHiveExcelMergeMode mergeMode = BHiveExcelMergeMode.NONE;

	/** Excel 출력 ExcelColumn Annotation 맵 */
	protected Map<String, ExcelColumn> anntationMap = new HashMap<String, ExcelColumn>();
	/** Excel 출력 컬럼 순서 Map. 기준 축 */
	protected Map<String, Integer> columnOrderMap = new HashMap<String, Integer>();
	/** 그룹헤더가 있는지 여부 */
	protected boolean hasGroupHeaderMap = false;
	/** Excel 출력 그룹 헤더 맵 (국제화 적용) */
	protected Map<String, String> groupHeaderMap = new HashMap<String, String>();
	/** Excel 출력 헤더 맵 (국제화 적용) */
	protected Map<String, String> headerMap = new HashMap<String, String>();
	/** Excel 출력 컬럼 정렬 Map */
	protected Map<String, Short> columnAlignMap = new HashMap<String, Short>();

	/** Excel 출력 코드표 치환 맵 */
	protected Map<String, Map<String, String>> fieldCodeMapList = new HashMap<String, Map<String, String>>();

	/** 현재시간(출력용) */
	protected DateTime now = new DateTime();

	/** 엑셀 워크북 */
	protected HSSFWorkbook workbook;
	/** CSV Writer */
	protected ICsvBeanWriter csvWriter;

	/** 요청객체 */
	protected HttpServletRequest request;
	/** 응답객체 */
	protected HttpServletResponse response;

	////////////////////////////////////////////////////////////////////////////////
	/**
	 * 생성자 
	 * @param dataList 엑셀에 들어가 데이터
	 * @param metadataClass metadata Class object
	 * @param messageSource message source for i18n
	 */
	public BHiveExcelCommand(List<Object> dataList, Class<?> metadataClass, MessageSource messageSource) {
		super();
		this.dataList = dataList;
		this.metadataClass = metadataClass;
		this.messageSource = messageSource;
	}

	/** 
	 * 출력용 필드 코드표를 추가
	 * @param field 코드표를 적용할 필드
	 * @param codeDTList 코드표를 만들어 낼 코드DT 목록
	 * @param valueProp 코드DT의 값 속성명
	 * @param nameProp 코드DT의 이름 속성명
	 * @throws IllegalAccessException throw illegal access exception
	 * @throws InvocationTargetException throw invocation target exception
	 * @throws NoSuchMethodException throw no such method exception
	 */
	public void addCodeMap(String field, List<Object> codeDTList, String valueProp, String nameProp) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, String> fieldCodeMap = new HashMap<String, String>();
		for (Object codeDT : codeDTList) {
			String value = BeanUtils.getProperty(codeDT, valueProp);
			String name = BeanUtils.getProperty(codeDT, nameProp);
			fieldCodeMap.put(value, name);
		}
		fieldCodeMapList.put(field, fieldCodeMap);
	}

	/** 
	 * 출력용 필드 코드표를 추가
	 * @param field 코드표를 적용할 필드
	 * @param fieldCodeMap 코드표, value-name 쌍
	 */
	public void addCodeMap(String field, Map<String, String> fieldCodeMap) {
		fieldCodeMapList.put(field, fieldCodeMap);
	}

	////////////////////////////////////////////////////////////////////////////////
	/** 
	 * 출력용 CellStyle 생성 (폰트 적용)
	 * @param bgColor background color to use
	 * @param align   align to use
	 * @param valign  vertical align to use
	 * @param font    font to use
	 * @return HSSFCellStyle hssf cell style to use
	 */
	protected HSSFCellStyle createCustomCellStyle(short bgColor, HorizontalAlignment align, VerticalAlignment valign, HSSFFont font) {
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setFillForegroundColor(bgColor);
		cellStyle.setAlignment(align);
		cellStyle.setVerticalAlignment(valign);
		if(font != null) {
			cellStyle.setFont(font);
		}
		return cellStyle;
	}

	/** 
	 * 출력용 CellStyle 생성 (폰트 속성 적용) 
	 * @param bgColor background color to use
	 * @param align   align to use
	 * @param valign  vertical align to use
	 * @param fontHeightInPoints  font height in points to use
	 * @param fontColor   font color to use
	 * @param boldWeight  boldweight to use
	 * @param underline   under line to use
	 * @return HSSFCellStyle hssf cell style to use
	 */
	protected HSSFCellStyle createCustomCellStyle(short bgColor, HorizontalAlignment align, VerticalAlignment valign, short fontHeightInPoints, short fontColor, boolean boldWeight, byte underline) {
		HSSFFont font = createFont(fontHeightInPoints, fontColor, boldWeight, null, underline);
		return createCustomCellStyle(bgColor, align, valign, font);
	}

	/** 
	 * 출력용 CellStyle 생성 
	 * @param bgColor background color to use
	 * @param align   align to use
	 * @param valign  vertical align to use
	 * @return HSSFCellStyle hssf cell style to use
	 */
	protected HSSFCellStyle createCustomCellStyle(short bgColor, HorizontalAlignment align, VerticalAlignment valign) {
		return createCustomCellStyle(bgColor, align, valign, null);
	}

	/** 
	 * 출력용 Font 생성
	 * @param fontHeightInPoints  font height in points to use
	 * @param fontColor   font color to use
	 * @param boldWeight  bold is or not?
	 * @param fontName    font name to use
	 * @param underline   under line to use
	 * @return HSSFCellStyle hssf cell style to use
	 */
	private HSSFFont createFont(short fontHeightInPoints, short fontColor, boolean boldWeight, String fontName, byte underline/*HSSFFont.U_NONE*/) {
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints(fontHeightInPoints);
		font.setColor(fontColor);
		font.setBold(boldWeight);
		if(!StringUtils.isEmpty(fontName)) {
			font.setFontName(fontName);
		} else {
			font.setFontName("맑은 고딕");
		}
		font.setUnderline(underline);
		return font;
	}

	/** 
	 * 출력용 CellStyle에 Border style적용 
	 * @param cellStyle  cell Style to use
	 * @param borderColor   border Color to use
	 * @param borderStyle  border Style to use
	 */
	private void setBorderStyleToCellStyle(HSSFCellStyle cellStyle, short borderColor, BorderStyle borderStyle) {
		cellStyle.setLeftBorderColor(borderColor);
		cellStyle.setRightBorderColor(borderColor);
		cellStyle.setTopBorderColor(borderColor);
		cellStyle.setBottomBorderColor(borderColor);
		cellStyle.setBorderLeft(borderStyle);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setBorderBottom(borderStyle);
	}

	/** 
	 * 제목 출력용 Style 
	 * @return HSSFCellStyle hssf cell style to use
	 */
	protected HSSFCellStyle createTitleCellStyle() {
		return createTitleCellStyle((short) 12, false);
	}
	protected HSSFCellStyle createTitleCellStyle(short fontHeightInPoints, short fontColor, byte underline, boolean setBorder) {
		HSSFFont font = createFont(fontHeightInPoints, fontColor, true, null, underline);
		HSSFCellStyle cellStyle = createCustomCellStyle(HSSFColor.WHITE.index, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, font);
		if(setBorder == true) {
			setBorderStyleToCellStyle(cellStyle, HSSFColor.GREY_50_PERCENT.index, BorderStyle.THIN);
			cellStyle.setWrapText(true);
		}
		return cellStyle;
	}
	protected HSSFCellStyle createTitleCellStyle(short fontHeightInPoints, boolean setBorder) {
		return createTitleCellStyle(fontHeightInPoints, HSSFColor.BLACK.index, HSSFFont.U_NONE, setBorder);
	}

	/** 
	 * 부제목 출력용 Style 
	 * @return HSSFCellStyle hssf cell style to use
	 */
	protected HSSFCellStyle createSubTitleCellStyle() {
		return createCustomCellStyle(HSSFColor.WHITE.index, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, (short) 10, HSSFColor.GREY_50_PERCENT.index, false, HSSFFont.U_NONE);
	}

	/** 
	 * 헤더 출력용 Style 
	 * @return HSSFCellStyle hssf cell style to use
	 */
	protected HSSFCellStyle createHeaderCellStyle() {
		HSSFCellStyle headerStyle = createCustomCellStyle(
				HSSFColor.GREY_25_PERCENT.index,
				HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER,
				(short) 10, HSSFColor.BLACK.index, true,
				HSSFFont.U_NONE);
		setBorderStyleToCellStyle(headerStyle, HSSFColor.GREY_50_PERCENT.index, BorderStyle.THIN);
		headerStyle.setWrapText(true);
		return headerStyle;
	}

	/** 
	 * Data 출력용 Style 
	 * @param align align to use
	 * @return HSSFCellStyle hssf cell style to use
	 */
	protected HSSFCellStyle createDataCellStyle(HorizontalAlignment align) {
		HSSFCellStyle dataCellStyle = createCustomCellStyle(HSSFColor.WHITE.index, align, VerticalAlignment.TOP);
		setBorderStyleToCellStyle(dataCellStyle, HSSFColor.GREY_50_PERCENT.index, BorderStyle.THIN);
		dataCellStyle.setWrapText(true);
		return dataCellStyle;
	}

	////////////////////////////////////////////////////////////////////////////////
	//Helper

	/** 
	 * Excel Row의 특정 Cell에 값을 렌더링 (스타일 적용가능) 
	 * @param excelRow HSSFRow excelRow
	 * @param cellnum int cell number
	 * @param cellValue String cell value
	 * @param cellStyle HSSFCellStyle object
	 */
	protected static void renderCustomCellValue(HSSFRow excelRow, int cellnum, String cellValue, HSSFCellStyle cellStyle) {
		renderCustomCellValue(excelRow, cellnum, cellValue, cellStyle, 1, 1);
	}

	/** 
	 * Excel Row의 특정 Cell에 값을 렌더링 (스타일 적용가능) 
	 * @param excelRow HSSFRow excelRow
	 * @param cellnum int cell number
	 * @param cellValue String cell value
	 * @param cellStyle HSSFCellStyle object
	 * @param colSpan col span value
	 * @param rowSpan row span value
	 */
	protected static void renderCustomCellValue(HSSFRow excelRow, int cellnum, String cellValue, HSSFCellStyle cellStyle, int colSpan, int rowSpan) {
		HSSFCell excelCell;
		excelCell = excelRow.createCell(cellnum);
		excelCell.setCellValue(cellValue);
		if(cellStyle != null) {
			excelCell.setCellStyle(cellStyle);
			for(int r = 0; r < rowSpan; r++) {
				boolean isRowCreated = false;
				HSSFRow rowTemp = excelRow.getSheet().getRow(excelRow.getRowNum()+r);
				if(rowTemp == null) {
					isRowCreated = true;
					rowTemp = excelRow.getSheet().createRow(excelRow.getRowNum()+r);
				}
				for(int c = 0; c < colSpan; c++) {
					HSSFCell cellTemp = null;
					if(isRowCreated) {
						cellTemp = rowTemp.createCell(cellnum+c);
					} else {
						cellTemp = rowTemp.getCell(cellnum+c);
						if(cellTemp == null) {
							cellTemp = rowTemp.createCell(cellnum+c);
						}
					}
					cellTemp.setCellStyle(cellStyle);
				}
			}
		}
		if (rowSpan > 1 && colSpan > 1) {
			mergeRange(excelRow.getSheet(), excelRow.getRowNum(), cellnum, rowSpan, colSpan);
		} else if (colSpan > 1) {
			mergeHorizontal(excelRow.getSheet(), excelRow.getRowNum(), cellnum, colSpan);
		} else if (rowSpan > 1) {
			mergeVertical(excelRow.getSheet(), excelRow.getRowNum(), cellnum, rowSpan);
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/** 
	 * 엑셀 생성 (MAIN Start Method)
	 * @param workbook HSSFRow excelRow
	 * @param request HttpServletRequest request object
	 * @param response HttpServletResponse response object
	 */
	public void buildExcelDocument(HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) {
		this.workbook = workbook;
		this.request = request;
		this.response = response;

		try {
			//http://hammer.tistory.com/26

			//출력을 위한 설정정보 구성
			prepareMetadata();

			HSSFSheet excelSheet = workbook.createSheet(this.sheetName);

			int rownum = 0;
			rownum = renderTitle(excelSheet, rownum);
			//rownum = renderSubtitle(excelSheet, rownum);
			rownum = renderExcelHeader(excelSheet, rownum);
			int dataRownumStarted = rownum;
			rownum = renderExcelRows(excelSheet, rownum);
			postRenderSheet(excelSheet, dataRownumStarted, rownum);
		} catch (Exception e) {
			checkDataItemTypeWithMetadataClass();
			throw new BHiveRuntimeException("Failed to create excel.", e);
		}
	}
	/** 
	 * 오류발생시 Metadata의 클래스 호환성 확인 
	 */
	protected void checkDataItemTypeWithMetadataClass() {
		if(this.dataList.size() > 0) {
			Class<?> dataItemType = this.dataList.get(0).getClass();
			if(!this.metadataClass.isAssignableFrom(dataItemType)) {
				logger.warn("[CAUTION] Given metadataClass is not assignable from item's class of given list.");
				logger.warn(" - metadataClass: {}", metadataClass);
				logger.warn(" - dataItemType: {}", dataItemType);
			}
		}
	}

	/**
	 * Metadata 클래스에서 출력을 위한 설정정보맵 구성 
	 * @throws IntrospectionException throws Intro spection Exception
	 */
	protected void prepareMetadata() throws IntrospectionException {
		if(columnOrderMap.size() == 0) {
			//Order 맵
			Map<String, Integer> orderMap = BHiveVOUtil.getExcelColumnOrderMap(this.metadataClass);
			this.columnOrderMap = orderMap;
		}
		if(groupHeaderMap.size() == 0) {
			//그룹 헤더 맵
			Map<String, String> groupHeaderMap = BHiveVOUtil.getExcelColumnGroupNameMap(this.metadataClass, messageSource, this.messagePrefix, LocaleContextHolder.getLocale());
			this.groupHeaderMap = groupHeaderMap;
			for (Map.Entry<String, String> key : this.groupHeaderMap.entrySet()) {
				String value = key.getValue();
				if(!StringUtils.isEmpty(value)) {
					hasGroupHeaderMap = true;
					break;
				}
			}
		}
		if(headerMap.size() == 0) {
			//헤더 맵
			Map<String, String> headerMap = BHiveVOUtil.getExcelColumnNameMap(this.metadataClass, messageSource, this.messagePrefix, LocaleContextHolder.getLocale());
			this.headerMap = headerMap;
		}
		if(anntationMap.size() == 0) {
			//ExcelColumn Annotation 맵
			Map<String, ExcelColumn> anntationMap = BHiveVOUtil.getExcelColumnAnnotationMap(this.metadataClass);
			this.anntationMap = anntationMap;
		}
	}

	/** 
	 * 타이틀 출력
	 * @param excelSheet HSSFSheet object
	 * @param rownum int rownum
	 * @return next title row index
	 */
	protected int renderTitle(HSSFSheet excelSheet, int rownum) {
		if(StringUtils.isEmpty(this.title)) {
			return rownum;
		}

		HSSFRow excelRow = excelSheet.createRow(rownum);
		HSSFCell excelCell = excelRow.createCell(0);
		excelCell.setCellValue(this.title);

		HSSFCellStyle titleStyle = createTitleCellStyle();
		excelCell.setCellStyle(titleStyle);

		//컬럼수만큼 셀 가로 병합
		mergeHorizontalByColumnCount(excelSheet, rownum);
		rownum++;

		rownum = renderCustomTitle(excelSheet, rownum);

		return rownum;
	}

	/** 
	 * 타이틀 직후 커스텀 출력  
	 * @param excelSheet HSSFSheet object
	 * @param rownum int rownum
	 * @return next custom title row index
	 */
	protected int renderCustomTitle(HSSFSheet excelSheet, int rownum) { return rownum; }

	/** 
	 * 서브 타이틀 출력 (누가, 언제 출력) 
	 * @param excelSheet HSSFSheet object
	 * @param rownum int rownum
	 * @return next sub title row index
	 */
	protected int renderSubtitle(HSSFSheet excelSheet, int rownum) {
		User me = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if(principal instanceof User) {
			me = (User)principal;
		}
		String subtitle = "";
		if(me != null) {
			subtitle = String.format("엑셀생성자: %s, 생성일시: %s", me.getUsername(), now.toString("yyyy-MM-dd HH:mm:ss"));
		} else {
			subtitle = String.format("생성일시: %s", now.toString("yyyy-MM-dd HH:mm:ss"));
		}

		HSSFRow excelRow = excelSheet.createRow(rownum);
		HSSFCell excelCell = excelRow.createCell(0);
		excelCell.setCellValue(subtitle);

		HSSFCellStyle subTitleStyle = createSubTitleCellStyle();
		excelCell.setCellStyle(subTitleStyle);

		//컬럼수만큼 셀 가로 병합
		mergeHorizontalByColumnCount(excelSheet, rownum);
		rownum++;

		rownum = renderCustomSubTitle(excelSheet, rownum);

		return rownum;
	}

	/** 
	 * 서브타이틀 직후 커스텀 출력 
	 * @param excelSheet HSSFSheet object
	 * @param rownum int rownum
     * @return next custom sub title row index
	 */
	protected int renderCustomSubTitle(HSSFSheet excelSheet, int rownum) { return rownum; }

	/** 
	 * 헤더 렌더링 
	 * @param excelSheet HSSFSheet object
	 * @param rownum int rownum
	 * @return next header row index
	 */
	protected int renderExcelHeader(HSSFSheet excelSheet, int rownum) {
		HSSFCellStyle headerStyle = createHeaderCellStyle();

		HSSFRow excelHeaderRow1 = excelSheet.createRow(rownum++);
		HSSFRow excelHeaderRow2 = (hasGroupHeaderMap) ? excelSheet.createRow(rownum++) : excelHeaderRow1;

		//자동 높이 적용을 위한 headerMaxLine 계산
		double headerMaxLine1 = 1; //1줄 개별 높이
		double headerMaxLine2 = 1; //2줄 개별 높이
		double headerMaxLine12 = 1; //1,2줄이 Merge된 경우

		int cellnum = 0;
		for(String field : columnOrderMap.keySet()) {
			HSSFCell excelHeaderCell1 = excelHeaderRow1.createCell(cellnum);
			HSSFCell excelHeaderCell2 = (hasGroupHeaderMap) ? excelHeaderRow2.createCell(cellnum) : excelHeaderCell1;
			String headerValue1 = this.groupHeaderMap.get(field);
			String headerValue2 = this.headerMap.get(field);
			if (hasGroupHeaderMap == false) {
				//일반 헤더 모드
				excelHeaderCell2.setCellValue(headerValue2);
				excelHeaderCell2.setCellStyle(headerStyle);
			} else {
				//그룹 헤더 모드
				boolean hasGroupHeaderCellValue = !StringUtils.isEmpty(headerValue1);
				if (hasGroupHeaderCellValue == false) {
					//그룹헤더값 없음
					excelHeaderCell1.setCellValue(headerValue2.replace("(", "\r\n("));
					excelHeaderCell1.setCellStyle(headerStyle);
					excelHeaderCell2.setCellStyle(headerStyle);
					//최대 라인수
					headerMaxLine12 = Math.max(headerValue2.split("\n").length, headerMaxLine12);
				} else {
					//그룹헤더값 있음
					excelHeaderCell1.setCellValue(headerValue1);
					excelHeaderCell1.setCellStyle(headerStyle);
					excelHeaderCell2.setCellValue(headerValue2);
					excelHeaderCell2.setCellStyle(headerStyle);
					//최대 라인수
					headerMaxLine1 = Math.max(headerValue1.split("\n").length, headerMaxLine1);
					headerMaxLine2 = Math.max(headerValue2.split("\n").length, headerMaxLine2);
				}

				//autoSize
				excelSheet.autoSizeColumn(cellnum);
				excelSheet.setColumnWidth(cellnum, (int)(excelSheet.getColumnWidth(cellnum)*AUTO_SIZE_HEADERCOLUMN_MULTIFIER));

				if (hasGroupHeaderCellValue == false) {
					//그룹헤더 없는 경우 세로 병합
					excelSheet.addMergedRegion(new CellRangeAddress(excelHeaderRow1.getRowNum(), excelHeaderRow2.getRowNum(), cellnum, cellnum));
				}
			}
			cellnum++;
		}

		if (hasGroupHeaderMap) {
			//그룹헤더 가로 병합
			mergeHorizontalCellHasEqualValue(excelSheet, excelHeaderRow1.getRowNum(), 0, columnOrderMap.size());
			//병합후 AutoSize 재적용
			for(int cellIdx=0; cellIdx <columnOrderMap.keySet().size(); cellIdx++) {
				excelSheet.autoSizeColumn(cellIdx);
				excelSheet.setColumnWidth(cellIdx, (int)(excelSheet.getColumnWidth(cellIdx)*AUTO_SIZE_HEADERCOLUMN_MULTIFIER));
			}
		}

		//최대 라인에 따른 높이 적용
		if (headerMaxLine12 > (headerMaxLine1 + headerMaxLine2)) {
			double headerMaxLineAdd = (headerMaxLine12 - (headerMaxLine1 + headerMaxLine2)) / 2;
			headerMaxLine1 += headerMaxLineAdd;
			headerMaxLine2 += headerMaxLineAdd;
		}
		excelHeaderRow1.setHeightInPoints((float)(excelSheet.getDefaultRowHeightInPoints() * headerMaxLine1));
		excelHeaderRow2.setHeightInPoints((float)(excelSheet.getDefaultRowHeightInPoints() * headerMaxLine2));

		return rownum;
	}

	/** 
	 * 데이터 렌더링 
	 * @param excelSheet HSSFSheet object
	 * @param rownum int rownum
	 * @throws IllegalAccessException throws IllegalAccess Exception 
	 * @throws InvocationTargetException throws Invocation Target Exception
	 * @throws NoSuchMethodException throws No Such Method Exception
	 * @return int rendered Excel rows
	 */
	protected int renderExcelRows(HSSFSheet excelSheet, int rownum) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<ExcelColumn.ExcelAlign, HSSFCellStyle> cellStyleMap = new HashMap<ExcelColumn.ExcelAlign, HSSFCellStyle>();
		HSSFCellStyle cellStyleLeft =createDataCellStyle(HorizontalAlignment.LEFT);
		HSSFCellStyle cellStyleCenter =createDataCellStyle(HorizontalAlignment.CENTER);
		HSSFCellStyle cellStyleRight =createDataCellStyle(HorizontalAlignment.RIGHT);
		HSSFCellStyle cellStyleDefaultFallback = cellStyleLeft;
		cellStyleMap.put(ExcelColumn.ExcelAlign.Left, cellStyleLeft);
		cellStyleMap.put(ExcelColumn.ExcelAlign.Center, cellStyleCenter);
		cellStyleMap.put(ExcelColumn.ExcelAlign.Right, cellStyleRight);
		cellStyleMap.put(ExcelColumn.ExcelAlign.DefaultByType, null);

		int cellnum = 0;
		for (Object dt : dataList) {
			HSSFRow excelRow = excelSheet.createRow(rownum++);
			cellnum = 0;
			for(String columnName : columnOrderMap.keySet()) {
				ExcelColumn annotation = this.anntationMap.get(columnName);
				String fieldValue = BeanUtils.getProperty(dt, columnName);
				PropertyDescriptor propDesc = org.springframework.beans.BeanUtils.getPropertyDescriptor(dt.getClass(), columnName);
				if(propDesc != null) {
					Class<?> fieldType = propDesc.getPropertyType();

					//필드 타입에 따른 값 변환 및 포매팅 처리
					if(fieldType.isAssignableFrom(DateTime.class)) {
						//DateTime 형이면 날짜 포매팅
						if (fieldValue != null) {
							DateTime date = new DateTime(fieldValue);
							fieldValue = date.toString(annotation.dateTimeFormat());
						}
					} else if(fieldCodeMapList.containsKey(columnName)) {
						//codeMap에 해당 필드용 코드변환 맵이 있으면 변환
						Map<String, String> codeMap = fieldCodeMapList.get(columnName);
						logger.trace("####[EXCEL] codeMap[" + columnName + " = " + fieldValue + "] => " + codeMap.toString());
						if(codeMap.containsKey(fieldValue)) {
							fieldValue = codeMap.get(fieldValue); //코드변환
						}
					}

					//셀값 적용
					HSSFCell cell = excelRow.createCell(cellnum++);
					cell.setCellValue(fieldValue);

					//Style적용
					HSSFCellStyle dataCellStyle = null;
					//Style의 Align 선택처리
					dataCellStyle = cellStyleMap.get(annotation.align());
					if(dataCellStyle == null) {
						if( fieldType.isAssignableFrom(Long.class) ||
								fieldType.isAssignableFrom(Integer.class) ||
								fieldType.isAssignableFrom(Short.class) ||
								fieldType.isAssignableFrom(Double.class) ||
								fieldType.isAssignableFrom(Float.class) ) {
							dataCellStyle = cellStyleRight;
						} else {
							dataCellStyle = cellStyleDefaultFallback;
						}
					}
					cell.setCellStyle(dataCellStyle);
				}

			}
		}
		return rownum;
	}

	/** 
	 * 렌더링 후 처리
	 * @param excelSheet HSSFSheet object
	 * @param dataRownumStarted 데이터 출력 시작 위치
	 * @param rownum 최종 출력 위치
	 * @throws UnsupportedEncodingException Unsupported Encoding Exception
	 */
	protected void postRenderSheet(HSSFSheet excelSheet, int dataRownumStarted, int rownum) throws UnsupportedEncodingException {
		//Column의 Width 설정 (커스텀/AutoSize)
		applyColumnWidth(excelSheet);

		//Merge모드에 따른 Data Cell 병합
		applyDataCellMergeMode(excelSheet, dataRownumStarted, rownum);

		//컬럼 숨김적용
		applyColumnHidden(excelSheet);

		//응답헤더 설정
		setResponseHeader();
	}

	/** 
	 * 컬럼폭 적용 (기본: 자동폭 계산) 
	 * @param excelSheet HSSFSheet object
	 */
	protected void applyColumnWidth(HSSFSheet excelSheet) {
		int cellnum = 0;
		for(String field : columnOrderMap.keySet()) {
			ExcelColumn annotation = anntationMap.get(field);
			//width 처리
			logger.trace("####[EXCEL] postRenderSheet => applyColumnWidth {} width: {}", field, annotation.width());
			if(annotation.width() <= 0) {
				int oldWidthByHeader = excelSheet.getColumnWidth(cellnum); //헤더, 그룹헤더처리시 지정된 AutoWidth
				excelSheet.autoSizeColumn(cellnum);
				excelSheet.setColumnWidth(cellnum, (int)(excelSheet.getColumnWidth(cellnum)*AUTO_SIZE_COLUMN_MULTIFIER));
				if(oldWidthByHeader > excelSheet.getColumnWidth(cellnum)) {
					excelSheet.setColumnWidth(cellnum, oldWidthByHeader);
				}
				logger.trace("####[EXCEL] postRenderSheet => applyColumnWidth {} autoSizeColumn({})", field, cellnum);
			} else {
				excelSheet.setColumnWidth(cellnum, (int)(annotation.width()*256));
			}
			cellnum++;
		}
	}

	/** 
	 * Merge Mode에 따른 Data Cell 병합 
	 * @param excelSheet HSSFSheet object
	 * @param dataRownumStarted 처리 시작 row index
	 * @param rownum int 처리할 row 갯수
	 */
	protected void applyDataCellMergeMode(HSSFSheet excelSheet, int dataRownumStarted, int rownum) {
		switch (this.mergeMode) {
		case MERGE_VERTICAL:
			mergeVerticalAtAllColumn(excelSheet, dataRownumStarted, rownum);
			break;
		case MERGE_VERTICAL_HIERARCHY:
			mergeVerticalHierarchyAtAllColumn(excelSheet, dataRownumStarted, rownum);
			break;
		default:
			break;
		}
	}

	/** 
	 * 컬럼 숨김적용
	 * @param excelSheet HSSFSheet object
	 */
	protected void applyColumnHidden(HSSFSheet excelSheet) {
		int cellnum = 0;
		for(String field : columnOrderMap.keySet()) {
			ExcelColumn annotation = anntationMap.get(field);
			//width 처리
			logger.trace("####[EXCEL] postRenderSheet => applyColumnHidden {} hidden: {}", field, annotation.hidden());
			if(annotation.hidden()) {
				excelSheet.setColumnHidden(cellnum, true);
			}
			cellnum++;
		}
	}

	/** 
	 * 응답헤더 설정 (다운로드 파일명등...)
 	 * @throws UnsupportedEncodingException throws Un supported Encoding Exception
	 */
	protected void setResponseHeader() throws UnsupportedEncodingException {
		//파일명
		String attachmentFilename = FilenameUtils.removeExtension(filename) + "_" + now.toString("yyyyMMdd_HHmmss") + EXTENSTION;
		setAttachementFilenameHeader(attachmentFilename);
		response.setHeader("Content-Transfer-Encoding", "binary");
		//jQuery FileDownload Cookie
		response.setHeader("Set-Cookie", "fileDownload=true; path=/");
	}

	/** 
	 * 브라우저 종류에 따른 파일명 설정 
	 * @param attachmentFilename 첨부 파일명
	 * @throws UnsupportedEncodingException throws Un supported Encoding Exception
	 */
	private void setAttachementFilenameHeader(String attachmentFilename) throws UnsupportedEncodingException {
		String user_agent = request.getHeader("user-agent");
		boolean isInternetExplorer = (user_agent.indexOf("MSIE") > -1) || (user_agent.indexOf("Trident") > -1);
		logger.trace("####[EXCEL/CSV] user_agent:"+user_agent);
		logger.trace("####[EXCEL/CSV] isInternetExplorer:"+isInternetExplorer);
		if (isInternetExplorer) {
			response.setHeader("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(attachmentFilename, "utf-8") + "\";");
		} else {
			response.setHeader("Content-disposition", "attachment; filename=\"" + MimeUtility.encodeWord(attachmentFilename, "utf-8", "Q") + "\";");
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/**
	 * 엑셀 셀을 가로 Merge
	 * @param excelSheet 엑셀 시트
	 * @param rownum 대상셀 RowIndex
	 * @param cellnum 대상셀 ColumnIndex
	 * @param colSpan 가로 셀 폭
	 */
	protected static void mergeHorizontal(HSSFSheet excelSheet, int rownum, int cellnum, int colSpan) {
		if(colSpan > 0) {
			excelSheet.addMergedRegion(new CellRangeAddress(rownum, rownum, cellnum, cellnum + colSpan-1));
		}
	}

	/**
	 * 엑셀 셀을 세로 Merge
	 * @param excelSheet 엑셀 시트
	 * @param rownum 대상셀 RowIndex
	 * @param cellnum 대상셀 ColumnIndex
	 * @param rowSpan 세로 셀 폭
	 */
	protected static void mergeVertical(HSSFSheet excelSheet, int rownum, int cellnum, int rowSpan) {
		if(rowSpan > 0) {
			excelSheet.addMergedRegion(new CellRangeAddress(rownum, rownum + rowSpan - 1, cellnum, cellnum));
		}
	}

	/**
	 * 엑셀 셀을 Range Merge
	 * @param excelSheet 엑셀 시트
	 * @param rownum 대상셀 RowIndex
	 * @param cellnum 대상셀 ColumnIndex
	 * @param rowSpan 세로 셀 폭
	 * @param colSpan 가로 셀 폭
	 */
	protected static void mergeRange(HSSFSheet excelSheet, int rownum, int cellnum, int rowSpan, int colSpan) {
		if(rowSpan > 0 && colSpan > 0) {
			excelSheet.addMergedRegion(new CellRangeAddress(rownum, rownum + rowSpan - 1, cellnum, cellnum + colSpan - 1));
		}
	}

	/** 
	 * 주어진 줄을 컬럼수만큼 가로병합(주로 제목부분 줄에 적용) 
	 * @param excelSheet 가로 병합할 sheet
	 * @param atRownum 병합할 row index
	 */
	protected void mergeHorizontalByColumnCount(HSSFSheet excelSheet, int atRownum) {
		int columnCount = this.columnOrderMap.size();
		mergeHorizontal(excelSheet, atRownum, 0, columnCount);
	}

	/** 
	 * 컬럼데이터를 세로 Merge
	 * @param excelSheet 가로 병합할 sheet
	 * @param dataRownumStarted 병합시작할 row index
	 * @param dataRownumMax 병합할 최대 row 갯수
	 */
	protected void mergeVerticalAtAllColumn(HSSFSheet excelSheet, int dataRownumStarted, int dataRownumMax) {
		int columnCount = this.columnOrderMap.size();
		for(int cellnum=0; cellnum<columnOrderMap.size(); cellnum++) {
			mergeVerticalCellHasEqualValue(excelSheet, cellnum, dataRownumStarted, dataRownumMax, false, columnCount-1);
		}
	}

	/** 
	 * 컬럼데이터를 세로 Merge, 앞에서 부터 HIERARCHY 방식 사용 (1열이 7줄 합쳐지면 2열은 그 경계를 초과하여 합치치 않음) 
	 * @param excelSheet 가로 병합할 sheet
	 * @param dataRownumStarted 병합시작할 row index
	 * @param dataRownumMax 병합할 최대 row 갯수
	 */
	protected void mergeVerticalHierarchyAtAllColumn(HSSFSheet excelSheet, int dataRownumStarted, int dataRownumMax) {
		int columnCount = this.columnOrderMap.size();
		if(columnCount > 0) {
			mergeVerticalCellHasEqualValue(excelSheet, 0, dataRownumStarted, dataRownumMax, true, columnCount-1);
		}
	}

	/** 
	 * 특정 Column의 세로방향으로 값이 같으면 Merge
	 * @param excelSheet 시트
	 * @param cellnum 처리할 Column의 CellNum
	 * @param dataRownumStarted 시작 Rownum
	 * @param dataRownumMax 종료 Rownum (초과경계값)
	 * @param hierarchy 트리형으로 적용
	 * @param cellnumMax 최대 Cellnum (초과경계값)
	 */
	protected static void mergeVerticalCellHasEqualValue(HSSFSheet excelSheet, int cellnum, int dataRownumStarted, int dataRownumMax, boolean hierarchy, int cellnumMax) {
		logger.trace("mergeVerticalCell - cellnum: {}, dataRownumStarted: {}, dataRownumMax: {}, hierarchy: {}, cellnumMax: {}", cellnum, dataRownumStarted, dataRownumMax, hierarchy, cellnumMax);
		if(dataRownumStarted >= dataRownumMax) {
			return;
		}
		if(hierarchy && cellnum >= cellnumMax) {
			return;
		}
		HSSFRow beginRow = null;
		HSSFCell beginCell = null;
		HSSFRow currentRow = null;
		HSSFCell currentCell = null;
		int rowSpan = 1;
		for(int rownum = dataRownumStarted; rownum < dataRownumMax; rownum++) {
			if(beginRow == null) {
				beginRow = excelSheet.getRow(rownum);
				beginCell = beginRow.getCell(cellnum);
				continue;
			}

			currentRow = excelSheet.getRow(rownum);
			currentCell = currentRow.getCell(cellnum);

			boolean isEqual = beginCell.toString().equals(currentCell.toString());
			boolean isRemainRow = (rownum < dataRownumMax-1);
			if(isEqual) {
				//equal
				rowSpan++;
			}

			if((!isEqual || !isRemainRow) && rowSpan > 1) {
				//Merge Vertial
				excelSheet.addMergedRegion(new CellRangeAddress(beginRow.getRowNum(), beginRow.getRowNum()+rowSpan-1, cellnum, cellnum));
				if(hierarchy && cellnum < cellnumMax-1) {
					mergeVerticalCellHasEqualValue(excelSheet, cellnum+1, beginRow.getRowNum(), beginRow.getRowNum()+rowSpan, hierarchy, cellnumMax);
				}
			}

			if(!isEqual)
			{
				//non-equal
				rowSpan = 1;
				beginRow = currentRow;
				beginCell = currentCell;
			}
		}
	}

	/** 
	 * 특정 Row의 가로방향으로 값이 같으면 Merge
	 * @param excelSheet 시트
	 * @param rownum 줄번호
	 * @param cellnumStart 컬럼시작
	 * @param cellnumEnd 컬럼끝 (초과경계값)
	 */
	protected static void mergeHorizontalCellHasEqualValue(HSSFSheet excelSheet, int rownum, int cellnumStart, int cellnumEnd) {
		logger.trace("mergeHorizontalCell - rownum: {}, cellnumStart: {}, cellnumEnd: {}", rownum, cellnumStart, cellnumEnd);
		if (cellnumStart >= cellnumEnd) {
			return;
		}
		HSSFRow row = excelSheet.getRow(rownum);
		HSSFCell beginCell = null;
		HSSFCell currentCell = null;
		int cellSpan = 1;
		for(int cellnum = cellnumStart; cellnum < cellnumEnd; cellnum++) {
			logger.trace(" cellnum:{}", cellnum);
			if(beginCell == null) {
				beginCell = row.getCell(cellnum);
				continue;
			}
			currentCell = row.getCell(cellnum);

			boolean isEqual = beginCell.toString().equals(currentCell.toString());
			boolean isRemainCell = (cellnum < cellnumEnd-1);
			logger.trace(" -> cellnum:{}, isEqual: {}, isRemainCell: {}, cellnumEnd: {}", cellnum, isEqual, isRemainCell);
			if(isEqual) {
				//equal
				cellSpan++;
			}

			if((!isEqual || !isRemainCell) && cellSpan > 1) {
				//Merge Horizontal
				excelSheet.addMergedRegion(new CellRangeAddress(rownum, rownum, beginCell.getColumnIndex(), beginCell.getColumnIndex()+cellSpan-1));
				logger.trace(" ==> addMergedRegion => firstRow: {}, lastRow: {}, firstCol: {}, lastCol: {}", rownum, rownum, cellnum, cellnum+cellSpan-1);
			}

			if(!isEqual)
			{
				//non-equal
				cellSpan = 1;
				beginCell = currentCell;
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/** 
	 * CSV 생성. Super Fast 
	 * @param model CSV 로 생성할 데이터
	 * @param csvWriter ICsvBeanWriter object
	 * @param request HttpServletRequest object
	 * @param response HttpServletResponse object
	 */
	public void buildCsvDocument(Map<String, Object> model, ICsvBeanWriter csvWriter, HttpServletRequest request, HttpServletResponse response) {
		this.csvWriter = csvWriter;
		this.request = request;
		this.response = response;
		try {
			//출력을 위한 설정정보 구성
			prepareMetadata();
			setResponseCSVHeader();
			renderCsvHeader(model);
			renderCsvRows(model);
		} catch (Exception e) {
			checkDataItemTypeWithMetadataClass();
			throw new BHiveRuntimeException("Failed to create excel.", e);
		}
	}

	/** 
	 * CSV 헤더 생성 
	 * @param model CSV 로 생성할 데이터
	 * @throws IOException throws IO Exception
	 */
	private void renderCsvHeader(Map<String, Object> model) throws IOException {
		if(!StringUtils.isEmpty(this.title)) {
			logger.trace("####[CSV] writeComment");
			csvWriter.writeComment(this.title);
			logger.trace("####[CSV] writeComment ok");
		}
		List<String> headers = new ArrayList<String>();
		for(String field : columnOrderMap.keySet()) {
			String headerValue1 = this.groupHeaderMap.get(field);
			String headerValue2 = this.headerMap.get(field);
			if(!StringUtils.isEmpty(headerValue1)) {
				headerValue2 = headerValue1 + " " + headerValue2;
			}
			headers.add(headerValue2);
		}
		csvWriter.writeHeader(headers.toArray(new String[0]));
		logger.trace("####[CSV] writeHeader ok");
	}

	/** 
	 * CSV 데이터 생성
	 * @param model CSV 로 생성할 데이터
	 * @throws IOException throws IO Exception
	 */
	private void renderCsvRows(Map<String, Object> model) throws IOException {
		String[] columns = columnOrderMap.keySet().toArray(new String[0]);
		for (Object dt : dataList) {
			csvWriter.write(dt, columns);
		}
		logger.trace("####[CSV] writeRows ok");
	}

	/** 
	 * CSV 응답헤더 설정 (다운로드 파일명등...)
	 * @throws IOException throws IO Exception
	 */
	protected void setResponseCSVHeader() throws IOException {
		//파일명
		String attachmentFilename = FilenameUtils.removeExtension(filename) + "_" + now.toString("yyyyMMdd_HHmmss") + EXTENSION_CSV;
		setAttachementFilenameHeader(attachmentFilename);
		response.setHeader("Content-Transfer-Encoding", "binary");
		//jQuery FileDownload Cookie
		response.setHeader("Set-Cookie", "fileDownload=true; path=/");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public BHiveExcelMergeMode getMergeMode() {
		return mergeMode;
	}

	public void setMergeMode(BHiveExcelMergeMode mergeMode) {
		this.mergeMode = mergeMode;
	}

}
