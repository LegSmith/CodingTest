package basic.day21;

/**
 * <h2>문제: 문자열 여러 번 뒤집기</h2>
 * <p>
 *     문자열 my_string과 이차원 정수 배열 queries가 매개변수로 주어집니다. queries의 원소는 [s, e] 형태로, my_string의
 *     인덱스 s부터 인덱스 e까지를 뒤집으라는 의미입니다. my_string에 queries의 명령을 순서대로 처리한 후의 문자열을
 *     return 하는 solution 함수를 작성해 주세요.
 * </p>
 * <ul>
 *     <li>my_string은 영소문자로만 이루어져 있습니다.</li>
 *     <li>1 ≤ my_string의 길이 ≤ 1,000</li>
 *     <li>queries의 원소는 [s, e]의 형태로 0 ≤ s ≤ e < my_string의 길이를 만족합니다.</li>
 *     <li>1 ≤ queries의 길이 ≤ 1,000</li>
 * </ul>
 */

public class Solution107 {
    public static void main(String[] args) {
        System.out.println(solution("rermgorpsam", new int[][]{{2,3},{0,7},{5,9},{6,10}}));
    }

    public static String solution(String my_string, int[][] queries){
        char[] arr = my_string.toCharArray();

        for (int[] query : queries) {
            int start = query[0];
            int end = query[query.length-1];
            while (start<end) {
                char tmp = arr[start];
                arr[start] = arr[end];
                arr[end] = tmp;
                start++;
                end--;
            }
        }

        return new String(arr);
    }
}
