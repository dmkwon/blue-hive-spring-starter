package blue.hive.type;

/**
 * 내부값을 가지는 Labeled Enum이 지원할 기본 인터페이스
 *  
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 * @param <T> Labeled Enum의 값 타입
 */
public interface BHiveValueEnum<T> {
	T getValue();
};

