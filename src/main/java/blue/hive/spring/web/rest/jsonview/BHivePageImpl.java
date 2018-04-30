package blue.hive.spring.web.rest.jsonview;

import java.util.Iterator;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import blue.hive.spring.web.rest.BHiveView;

/**
 * {@link Page}의 구현체인 {@link PageImpl}이 
 * {@link JsonView} Annotaion을 처리할 수 없어서 옮겨 담기 위한 {@link Page} 구현체
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHivePageImpl<T> implements Page<T>, Slice<T> {

	@JsonIgnore
	private Page<T> page;
	
	public BHivePageImpl(Page<T> page) {
		this.page = page;
	}
	
	public BHivePageImpl(List<T> content, Pageable pageable, long total) {
		this.page = new PageImpl<>(content, pageable, total);
	}

	@Override
	public Iterator<T> iterator() {
		return page.iterator();
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public int getNumber() {
		return page.getNumber();
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public int getSize() {
		return page.getSize();
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public int getNumberOfElements() {
		return page.getNumberOfElements();
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public List<T> getContent() {
		return page.getContent();
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public boolean hasContent() {
		return page.hasContent();
	}

	@JsonView(BHiveView.BaseView.class)
	public Sort getSort() {
		Sort ableSort = BHiveSort.buildFrom(page.getSort());
		return ableSort;
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public boolean isFirst() {
		return page.isFirst();
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public boolean isLast() {
		return page.isLast();
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public boolean hasNext() {
		return page.hasNext();
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public boolean hasPrevious() {
		return page.hasPrevious();
	}

	@Override
	public Pageable nextPageable() {
		return page.nextPageable();
	}

	@Override
	public Pageable previousPageable() {
		return page.previousPageable();
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public int getTotalPages() {
		return page.getTotalPages();
	}

	@Override
	@JsonView(BHiveView.BaseView.class)
	public long getTotalElements() {
		return page.getTotalElements();
	}

	@Override
	public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
		return page.map(converter);
	} 
	
}
