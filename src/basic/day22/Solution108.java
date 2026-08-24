package basic.day22;

/**
 * <h2>문제: 조건 문자열</h2>
 * <p>
 *     문자열에 따라 다음과 같이 두 수의 크기를 비교하려고 합니다.
 * </p>
 * <p>두 수가 n과 m이라면</p>
 * <ul>
 *     <li>">", "=" : n >= m</li>
 *     <li>"<", "=" : n <= m</li>
 *     <li>">", "!" : n > m</li>
 *     <li>"<", "!" : n < m</li>
 * </ul>
 * <p>두 문자열 ineq와 eq가 주어집니다. ineq는 "<"와 ">"중 하나고, eq는 "="와 "!"중 하나입니다. 그리고 두 정수 n과 m이
 * 주어질 때, n과 m이 ineq와 eq의 조건에 맞으면 1을 아니면 0을 return하도록 solution 함수를 완성해주세요.</p>
 */
public class Solution108 {
    public static void main(String[] args) {
        System.out.println(String.valueOf(solution("<","=",20,50))); // 결과: 1
        System.out.println(String.valueOf(solution(">","!",41,78))); // 결과: 0
    }

    public static int solution(String ineq, String eq, int n, int m) {
        boolean result = switch (ineq + eq) {
            case ">=" -> n >= m;
            case "<=" -> n <= m;
            case ">!" -> n > m;
            case "<!" -> n < m;
            default -> false;
        };
        return result ? 1 : 0;
    }

}
