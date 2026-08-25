package basic.day23;


import java.util.Arrays;


/**
 * <h2>제목: 수열과 구간 쿼리2</h2>
 * <p>정수 배열 arr와 2차원 정수 배열 queries이 주어집니다. queries의 원소는 각각 하나의 query를 나타내며, [s, e, k] 꼴입니다.</p>
 * <p>각 query마다 순서대로 s ≤ i ≤ e인 모든 i에 대해 k보다 크면서 가장 작은 arr[i]를 찾습니다.</p>
 * <p>
 * 각 쿼리의 순서에 맞게 답을 저장한 배열을 반환하는 solution 함수를 완성해 주세요.
 * 단, 특정 쿼리의 답이 존재하지 않으면 -1을 저장합니다.
 * </p>
 * <ul>
 *     <li>1 ≤ arr의 길이 ≤ 1,000</li>
 *     <li>0 ≤ arr의 원소 ≤ 1,000,000</li>
 *     <li>1 ≤ queries의 길이 ≤ 1,000</li>
 *     <li>0 ≤ s ≤ e < arr의 길이</li>
 *     <li>0 ≤ k ≤ 1,000,000</li>
 * </ul>
 */
public class Solution110 {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(solution(new int[]{0, 1, 2, 4, 3}, new int[][]{{0, 4, 2}, {0, 3, 2}, {0, 2, 2}})));
    }

    public static int[] solution(int[] arr, int[][] queries) {
        int[] answer = new int[queries.length];

        for (int q = 0; q < queries.length; q++) {
            int start = queries[q][0];
            int end = queries[q][1];
            int k = queries[q][2];

            int min = -1;
            for (int i = start; i <= end; i++) {
                if (arr[i] > k && (min == -1 || arr[i] < min)) {
                    min = arr[i];
                }
            }
            answer[q] = min;
        }

        return answer;
    }
}
