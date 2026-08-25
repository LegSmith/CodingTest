package basic.day23;

import java.math.BigInteger;

/**
 * <h2>문제: 두 수의 합</h2>
 * <p>
 *     0 이상의 두 정수가 문자열 a, b로 주어질 때, a + b의 값을 문자열로 return 하는 solution 함수를 작성해 주세요.
 * </p>
 * <ul>
 *     <li>1 ≤ a의 길이 ≤ 100,000</li>
 *     <li>1 ≤ b의 길이 ≤ 100,000</li>
 *     <li>a와 b는 숫자로만 이루어져 있습니다.</li>
 *     <li>a와 b는 정수 0이 아니라면 0으로 시작하지 않습니다.</li>
 * </ul>
 */
public class Solution109 {
    public static void main(String[] args) {
        System.out.println(solution("582","734")); // 결과 : "1316"
        System.out.println(solution("18446744073709551615","287346502836570928366")); // 결과 : "305793246910280479981"
        System.out.println(solution("0","0")); // 결과 : "0"
    }

    public static String solution(String a, String b) {
        BigInteger aInt = new BigInteger(a);
        BigInteger bInt = new BigInteger(b);
        return aInt.add(bInt).toString();
    }
}
