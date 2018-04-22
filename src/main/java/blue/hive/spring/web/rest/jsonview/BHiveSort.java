package blue.hive.spring.web.rest.jsonview;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonView;

import blue.hive.spring.web.rest.BHiveView;

/**
 * {@link Sort}와 내부의 {@link Order} 구현체가  
 * {@link JsonView} Annotaion을 처리할 수 없어서 옮겨 담기 위한 {@link Sort} 구현체
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveSort extends Sort {

	private static final long serialVersionUID = -4227395649734560065L;

	public BHiveSort(List<Order> orders) {
		super(orders);
	}
	
	public static BHiveSort buildFrom(Sort sort) {
		if(sort == null) {
			return null;
		}
		List<Order> orders = new ArrayList<Order>();
		for(Order order : sort) {
			orders.add(new AbleOrder(order.getDirection(), order.getProperty(), order.getNullHandling()));
		}
		return new BHiveSort(orders);
	}
	
	public static class AbleOrder extends Order {
		private static final long serialVersionUID = -4235892487869747963L;

		public AbleOrder(String property) {
			super(property);
		}

		public AbleOrder(Direction direction, String property) {
			super(direction, property);
		}

		public AbleOrder(Direction direction, String property, NullHandling nullHandlingHint) {
			super(direction, property, nullHandlingHint);
		}

		@Override
		@JsonView(BHiveView.BaseView.class)
		public Direction getDirection() {
			return super.getDirection();
		}

		@Override
		@JsonView(BHiveView.BaseView.class)
		public String getProperty() {
			return super.getProperty();
		}

		@Override
		@JsonView(BHiveView.BaseView.class)
		public boolean isAscending() {
			return super.isAscending();
		}

		@Override
		@JsonView(BHiveView.BaseView.class)
		public boolean isIgnoreCase() {
			return super.isIgnoreCase();
		}

		@Override
		@JsonView(BHiveView.BaseView.class)
		public NullHandling getNullHandling() {
			return super.getNullHandling();
		}
		
	}
	
}
