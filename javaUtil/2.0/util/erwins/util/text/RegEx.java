
package erwins.util.text;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import erwins.util.lib.FileUtil;

/**
 * CharMatcher를 먼저 참조하도록 하자
 * 
 * <p>(?:...) 으로 매칭을 강제로 하지 않게 하면 효율성이 좋아진다. (거의 차이 없음.) </p>
 * <p>match되는 결과가 너무 길면? 안되는듯 하다. </p>
 * <p>^와 $를 사용할려면 멀티라인 모드를 켜자</p>
 * <h1>?의 용도</h1>
 * <p>0개 또는 1개 매칭 / 게으른 수량자 / mode적용</p>
 * <p>전방 / 후방 탐색자 (*등의 무한인자를 쓸 수 없다.)</p>
 * 참고 : qwe.split("\\s*,\\s*") => CSV를 나타낸다. 이유는 .. 모르겠다.ㅠ 
 * 참고 : (.|\s)등의 연산은 내용물이 3~4라인을 넘어갈 경우 정상작동 하지 않는다. 
 */
public enum RegEx {
    
    /** 연속으로 줄바꿈 => 즉 공백문자 검색 : 미검증 */
    BLANK_LINE(Pattern.compile("[/r]?\\n\\s*[/r]?\\n")),
    
    /** 일반 줄바꿈1 : => 원래는 [\\r]?\\n 였지만 수정. 이걸로 둘이 붙은걸 먼저 제거해준다. */
    LINE_ALL(Pattern.compile("\\r\\n")),
    /** 일반 줄바꿈2 : => 윈도우 엔터는 둘다, 리눅스는 n, 웹(파폭)이나 플렉스에서는 r이 들어오는듯 하다 따라서 두반째로 이걸 해준다. */
    LINE_EACH(Pattern.compile("\\r|\\n")),
    
    /** 라인이 끝난 후 나오는 공백.   */
    LINE_END_BLANK(Pattern.compile("\\s*?$(\\s|\\n)",Pattern.MULTILINE)),
    
    /** 메소그 진입시 { 를 줄넘김 해서 쓰는 MS식 코드 컨벤션   */
    MS_CODE_TYPE(Pattern.compile("\\n\\s*(?=\\{)")),
    
            
    /**
     * 주석 날리기 신공!!! 
     * 한줄 또는 여러줄 주석을 나타낸다. (자바독은 포함 안함) 
     * 여러줄 주석의 경우 공백문자부터 시작해야 한다. ex) qwe /*  => 이러면 인식불가. 
     *  ==> 잘 안되는건 여전하다. 시간나면 테스트 해보자.
     **/
    COMMENT(Pattern.compile("//.*|(^[\\s]*/\\*[^\\*](?:.|\\r|\\n)*?\\*/)",Pattern.MULTILINE)),
    
    /** java_doc까지 포함한 버전이다. */
    COMMENT_ALL(Pattern.compile("//.*|(^[\\s]*/\\*(?:.|\\r|\\n)*?\\*/)",Pattern.MULTILINE)),
    
    /** 중간에 .이 1개씩은 들어가도 상관없다. */
    E_MAIL(Pattern.compile("^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$")),
    
    /** 숫자가 아닌것들. */
    NOT_NUMERIC(Pattern.compile("[^\\d]")),
    
    /**  <>로 둘러싸인 것은 전부 추출한다.  */
    TAG(Pattern.compile("<(.*?)>")),
    /** 앞의 태그와 일치하는 태그를 찾기 위해 역참조를 사용한다. 거의 쓸일 없을듯. */
    TAG_FULL(Pattern.compile("<(.*)>(?:.|\\s)*?</\\1>",Pattern.MULTILINE|Pattern.CASE_INSENSITIVE)),
    /** 태그 안의 text만을 매칭시킨다. */
    TAG_TEXT(Pattern.compile("(?<=<(.{1,100})>).*(?=</\\1>)",Pattern.MULTILINE|Pattern.CASE_INSENSITIVE)),
    /** IMG태그 전체를 추출한다. 표준을 안지켜서.. /를 닫지 않은 곳이 많다. 원래는 \\<img .*?(/\\>|\\</img\\>)  */
    TAG_IMG(Pattern.compile("\\<img .*?(\\>|\\<img\\>)",Pattern.CASE_INSENSITIVE)),
    /** 자바스크립트 태그+내용을 추출한다. 버그로 라인세퍼레이트는 놓지 못했다. */
    TAG_SCRIPT(Pattern.compile("<script.*?>(.)*?</script>",Pattern.CASE_INSENSITIVE)),
    
    /** 멀티 라인에서 맨 처음의 공백 검색. */
    FIRST_BLANK(Pattern.compile("^\\s*",Pattern.MULTILINE)),
    /** 전화번호 매칭..  그룹 매칭으로 뭐 할라 그랬더니  아무짝에도 쓸모 없잔아.. ㅠㅠ  */
    TEL(Pattern.compile("\\(?(0\\d{2,3})\\)?-?(\\d{3,4})-?(\\d{4})")),
    
    //======================   위에껀 참고용. 풀매치 체크 위해서[]* 했다. CharMatcher에 없는거 여기 추가하자. =============================
    
    /** 키보드에서 입력(?) 가능한, 출력 가능한 특수문자. 비번에도 사용되는  %^&*()<> 같은것들  */
    PUNCT(Pattern.compile("[\\p{Punct}]*")),
    /** 대소문자 알파벳  */
    ALPAH(Pattern.compile("[a-zA-Z]*")),
    /** 대소문자 알파벳 + 숫자 (소수점. 제외)  */
    ALPAH_NUMERIC(Pattern.compile("[a-zA-Z\\d]*")),
    /** 숫자 (소수점. 제외)  CharMatcher.DIGIT 으로 사용해도 됨  */
    NUMERIC(Pattern.compile("[\\d]*")),
    /** 한글 완성문자 */
    HAN(Pattern.compile("[가-힣]*")),
    /** 한글 전체 뷁ㄱㅇㅣㅏ  자모음 전체 포함     ㄱ-힝 으로 하면 뭐 안되는거 있나?? 일단 이렇게 함 */
    HAN_ALL(Pattern.compile("[가-힣ㄱ-ㅎㅏ-ㅣ]*")),
    ;
    
    private Pattern pattern;
    
    private RegEx(Pattern pattern){
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }
    
    // ===========================================================================================
    //                                    method
    // ===========================================================================================
    
    /** 주어진 text와 정확히 매칭되는지 검사한다. */
    public boolean isFullMatch(CharSequence text) {
        return isFullMatch(this.pattern,text);
    }
    
    /** 주어진 text를 정규식으로 replace한다. replaced에 $로 매칭자를 사용할 수 있다. */
    public String replace(CharSequence text,String replaced) {
        return replace(this.pattern,text,replaced);
    }
    
    /**CharMatcher와 동일하다. */
    public String retainFrom(CharSequence text) {
        return retainFrom(this.pattern,text);
    }
    
    /**CharMatcher와 동일하다. */
    public String removeFrom(CharSequence text) {
        return replace(this.pattern,text,"");
    }

    // ===========================================================================================
    //                                    static method
    // ===========================================================================================    
    
    public static String replace(Pattern pattern,CharSequence text,String replaced) {
        Matcher m = pattern.matcher(text);
        return m.replaceAll(replaced);
    }
    
    public static String find(Pattern pattern,CharSequence  text) {
        Matcher m = pattern.matcher(text);
        if(m.find()) return m.group(); 
        return null;
    }
    
    /**CharMatcher와 동일하다. */
    public static String retainFrom(Pattern pattern,CharSequence text) {
    	Matcher m = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        while(m.find()) {
        	String result = m.group();
        	if(Strings.isNullOrEmpty(result)) continue; //?? 사실 잘 모르겠다. 
        	sb.append(result);
        }
        return sb.toString();
    }
    
    /** \d*, as23dasd003 ==> [23,003]
     *  MatchResult는 매칭결과, 시작점, 종료점 등을 가진다. */
    public static List<MatchResult> findMatch(Pattern pattern,CharSequence  text) {
        Matcher m = pattern.matcher(text);
        List<MatchResult> matchResult = Lists.newArrayList();
        while(m.find()) {
        	String result = m.group();
        	if(Strings.isNullOrEmpty(result)) continue;
        	matchResult.add(m.toMatchResult());
        }
        return matchResult;
    }
    
    /** 하나라도 매칭되는지 */
    public static boolean isMatch(String pattern,CharSequence text) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        return m.find();
    }
    
    /**  []* 시리즈가 전체 다 매칭되는지  */
    public static boolean isFullMatch(Pattern pattern,CharSequence text) {
        Matcher m = pattern.matcher(text);
        return m.matches();
    }
    
    // ===========================================================================================
    //                                    기타.
    // ===========================================================================================       
    
    /** ANT식의 aaa*bbb등 *이 1개만 들어간거에 적용된다. from spring */
    public static boolean simpleMatch(String pattern, String str) {
		if (pattern == null || str == null) {
			return false;
		}
		int firstIndex = pattern.indexOf('*');
		if (firstIndex == -1) {
			return pattern.equals(str);
		}
		if (firstIndex == 0) {
			if (pattern.length() == 1) {
				return true;
			}
			int nextIndex = pattern.indexOf('*', firstIndex + 1);
			if (nextIndex == -1) {
				return str.endsWith(pattern.substring(1));
			}
			String part = pattern.substring(1, nextIndex);
			int partIndex = str.indexOf(part);
			while (partIndex != -1) {
				if (simpleMatch(pattern.substring(nextIndex), str.substring(partIndex + part.length()))) {
					return true;
				}
				partIndex = str.indexOf(part, partIndex + 1);
			}
			return false;
		}
		return (str.length() >= firstIndex &&
				pattern.substring(0, firstIndex).equals(str.substring(0, firstIndex)) &&
				simpleMatch(pattern.substring(firstIndex), str.substring(firstIndex)));
	}    

    /**
     * 문자열을 치환하여 HTML링크를 만들어 준다. 게시판 댓글 등에 사용 https?
     * 공백 문자는 자동으로 링크가 안되니 주의! 완전한게 아니다.
     */
    public static String linkedText(CharSequence sText) {
        Pattern p = Pattern.compile("(http|https|ftp)://[^\\s^\\.]+(\\.[^\\s^\\.]+)*");
        Matcher m = p.matcher(sText);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "<a href='" + m.group() + "'>" + m.group() + "</a>");
        }
        m.appendTail(sb);

        return sb.toString();
    }

    /**
     * 특정 단어 필터링. 단어는 특수문자 사용 금지.
     */
    public static String filterText(CharSequence sText,String ... filters) {
        String filter = StringUtil.join(filters, "|");
        Pattern p = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sText);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, maskWord(m.group()));
        }
        m.appendTail(sb);

        return sb.toString();
    }

    /**
     * 단어의 앞글자만 빼고 나머지를 *표시 해주자.
     */
    public static String maskWord(String word) {
        StringBuffer buff = new StringBuffer();
        char[] ch = word.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (i < 1) {
                buff.append(ch[i]);
            } else {
                buff.append("*");
            }
        }
        return buff.toString();
    }
    
    /** reg에 해당하는 문자열을 전부 추출한다. 멀티라인은 안됨! 
     *  -> 컨트롤러의@RequestMapping(.*) 을 체크할때 사용함 */
    public List<String> findMatchString(Iterator<File> i,String reg) {
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile(reg);
        while(i.hasNext()){
            File each = i.next();
            if(!StringUtil.isMatch(each.getName(),"Controller")) continue;
            List<String> lines = FileUtil.readLines(each);
            for(String line : lines){
                Matcher m = pattern.matcher(line);
                while (m.find()) result.add(m.group());    
            }
        }
        return result;
    }
    
    /** 패턴과 매칭되는 결과를 리턴한다. */
    public static List<String> findAll(Pattern pattern,CharSequence  text) {
        Matcher m = pattern.matcher(text);
        List<String> list = new ArrayList<String>();
        while (m.find()){
            String result = m.group();
            if(!StringUtil.isEmpty(result)) list.add(result);
        }
        return list;
    }
    
    private static Pattern NUMBER = Pattern.compile("\\d*");
    
    /** 마지막 숫자 부분만 추출해서 숫자를 더한다.
     * ex) plusIgnoreNumeric(123_3399_E21,100) --> 123_3399_E121  */
    public static String plusIgnoreNumeric(String body,String add) {
        List<String> list =  findAll(NUMBER, body);
        if(list.size()==0) return null;
        
        String lastNum = list.get(list.size()-1);
        String added = StringUtil.plus(lastNum, add);
        int index = body.lastIndexOf(lastNum);
        String result = body.substring(0, index);
        return result + added;
    }
    
	/**
	 * 내부 문구를 찾을때 사용된다. 
	 * 비탐욕적 서치 추가
	 * ex) extractBetween("<span>(",")</span>");
	 *  */ 
	public static Pattern extractBetween(String pref,String suff){
		return Pattern.compile("(?<="+StringEscapeUtil.escapeRegEx(pref)+").*?(?="+StringEscapeUtil.escapeRegEx(suff)+")"); 
	}


}