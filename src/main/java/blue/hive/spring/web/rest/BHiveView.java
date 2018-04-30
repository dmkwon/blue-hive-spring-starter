package blue.hive.spring.web.rest;



/**
 * 사이트 전체 공용 기본 JsonView
 *
 * 추가적인 Json Serialization View를 만들려면 내려주려는 DT에 직접 View interface를 선언. AbleJsonView.BaseView 상속
 *
 * 추가적인 요청타입. ValidationGroup, Json Deserialization View은 각각의 하위 DT, Entity클래스에 정의
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveView {

	//////////////////////////////////////////////////////////////////////////////
	// Json Serialization View
	// REST API응답시 Json으로 내려줄 속성의 범위를 지정

	/** 최상위 JsonView (REST 공통 전문 필드들에 사용) - 다른 JsonView들을 이 View를 extend하여 구현 필요  */
	public interface BaseView {}

	//각각의 엔티티에 아래의 필요한 View을 정의함. 필요시 추가 View정의해서 사용
	///** 리스트용 JsonView */
	//public interface ListView extends BHiveView.BaseView {}; // Grid List Json View
	///** 기본 JsonView */
	//public interface DefaultView extends BHiveView.BaseView {}; // Default Json View
	///** Select Options 용 JsonView */
	//public interface OptionView extends BHiveView.BaseView  {}; // Option Json View: Select Option 전달 용
	///** 상세용 JsonView */
	//public interface DetailsView extends BHiveView.BaseView {}; // Details Json View

	//////////////////////////////////////////////////////////////////////////////
	//Json Deserialization View
	// 요청 Action에서 받을 특정 속성(컬럼)과 그에 따른 Validation Groups으로 사용.

	/** 최상위 기본 공통 요청 */
	public interface BaseAction {}

	//각각의 엔티티에 아래의 필요한 Action을 정의함. 필요시 추가 Action정의해서 사용
	///** Create 요청 */
	//public interface CreateAction extends BHiveView.BaseAction {};
	///** Update 요청 */
	//public interface UpdateAction extends BHiveView.BaseAction {};
	///** Delete 요청 */
	//public interface DeleteAction extends BHiveView.BaseAction {};

}
