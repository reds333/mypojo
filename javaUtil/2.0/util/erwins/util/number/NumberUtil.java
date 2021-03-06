package erwins.util.number;

import java.util.Collection;

import org.apache.commons.lang.math.NumberUtils;


/**
 * 간이 숫자 유틸
 * 빅데시말은 사용하지 말것
 * @author sin
 */
public abstract class NumberUtil extends NumberUtils {
    
	/** 0이면 비어있다고 판단 */
    public static boolean isEmpty(Number num) {
        if(num==null) return true;
        if(num.doubleValue() == 0) return true;
        return false;
    }
    
    public static long nullSafeDiv(Long small,Long large) {
        if(small==null) return 0;
        if(isEmpty(large)) return 0;
        return small / large;
    }
    
    /**
     * sql의 between과 공일하다. 같은 값이라면 true를 리턴
     */
    public static boolean isBetween(Number start,Number end,Number value) {
        if(value.doubleValue() < start.doubleValue()) return false;
        if(value.doubleValue() > end.doubleValue()) return false;
        return true;
    }
    
    /** 
     * 간단 AVG 계산기
     * max는 Collections.max 참고.
     *  */
    public static <T extends Number,C extends Collection<T>> long avg(C values) {
    	long sum = 0;
    	long count = 0;
    	for(Number each : values){
    		if(each==null) continue;
    		sum += each.longValue();
    		count++;
    	}
    	return sum / count;
    }
    


}
