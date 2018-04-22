package blue.hive.spring.web.view;

/**
 * Excel 출력시 Cell의 Merge처리 방식
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public enum BHiveExcelMergeMode {

	/**
	 * 아무처리도 하지 않음
	 */
	NONE,

	/**
	 * 세로 Merge
	 */
	MERGE_VERTICAL,

	/**
	 * 세로 Merge, 앞에서 부터 HIERARCHY 방식 사용 (1열이 7줄 합쳐지면 2열은 그 경계를 초과하여 합치치 않음)
	 */
	MERGE_VERTICAL_HIERARCHY,


}
