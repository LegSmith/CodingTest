# Java 문법 치트시트 (코딩테스트용)

바이브코딩만 하다 보니 까먹은 기본 문법들 모음. 필요할 때 Ctrl+F로 찾아보는 용도.

> Stream 관련 문법은 [java-stream.md](java-stream.md), String ↔ char 관계는 [java-string-char.md](java-string-char.md) 참고.

## 1. 배열 (Array)

배열은 **크기 고정**. 선언 시점에 크기가 정해지고 이후 늘리거나 줄일 수 없음.

```java
int[] arr = new int[5];              // 크기 5, 기본값 0으로 채워짐
int[] arr2 = {1, 2, 3};              // 선언과 동시에 초기화
int[] arr3 = new int[]{1, 2, 3};     // 위와 동일

arr.length                            // 배열 길이 (메서드 아님, 필드)

// 복사
int[] copy = Arrays.copyOf(arr, arr.length);
int[] sub = Arrays.copyOfRange(arr, 1, 3); // [1, 3) 구간

// 정렬
Arrays.sort(arr);                     // 오름차순, 제자리 정렬
Arrays.sort(arr, Collections.reverseOrder()); // Integer[] 등 객체 배열만 가능 (int[]는 불가)

// 채우기 / 출력 / 비교
Arrays.fill(arr, 0);
System.out.println(Arrays.toString(arr));   // [1, 2, 3]
Arrays.equals(arr, arr2);

// 2차원 배열
int[][] grid = new int[3][4];
System.out.println(Arrays.deepToString(grid));

// 배열 <-> List
List<Integer> list = Arrays.stream(arr).boxed().collect(Collectors.toList());
Integer[] boxedArr = list.toArray(new Integer[0]);
```

**"동적으로 추가/제거"가 필요하면 배열이 아니라 `ArrayList`를 써야 함.**

---

## 2. ArrayList (동적 배열)

```java
List<Integer> list = new ArrayList<>();

list.add(1);                 // 맨 뒤에 추가
list.add(0, 99);             // 인덱스 0에 삽입 (뒤 원소들 밀림)
list.get(0);                 // 조회
list.set(0, 100);            // 값 교체
list.remove(list.size() - 1);         // 인덱스로 제거 (마지막 원소 제거)
list.remove(Integer.valueOf(100));    // 값으로 제거 (주의: remove(int)는 인덱스로 해석됨!)
list.contains(3);
list.indexOf(3);
list.size();
list.isEmpty();
list.clear();

// Java 21+ : List가 SequencedCollection을 구현하면서 첫/끝 원소 접근이 쉬워짐
list.getFirst();
list.getLast();
list.addFirst(0);
list.addLast(99);
list.removeFirst();
list.removeLast();

// 정렬
Collections.sort(list);                          // 오름차순
list.sort(Comparator.reverseOrder());             // 내림차순
list.sort((a, b) -> a - b);                       // 커스텀 비교

// 순회
for (int x : list) { ... }
list.forEach(x -> System.out.println(x));
```

> ⚠️ `remove(3)` vs `remove(Integer.valueOf(3))`
> - `remove(int index)` : 인덱스 3번 원소 제거
> - `remove(Object o)` : 값이 3인 원소 제거
> `List<Integer>`는 오토박싱 때문에 헷갈리기 쉬움. 값 기준 제거는 반드시 `Integer.valueOf()`로 감싸기.

> ⚠️ `getFirst()` / `getLast()` / `removeFirst()` / `removeLast()`는 Java 21부터 `List` 인터페이스에 추가됨. 그 이전 버전에서는 `list.get(0)`, `list.get(list.size()-1)` 등으로 대체.

---

## 3. Stack / Queue / Deque

```java
// 스택처럼 쓰기 (Stack 클래스보다 ArrayDeque 권장 - 더 빠르고 범용적)
Deque<Integer> stack = new ArrayDeque<>();
stack.push(1);      // 맨 위에 추가
stack.pop();        // 맨 위 제거 후 반환
stack.peek();        // 맨 위 확인 (제거 안 함)
stack.isEmpty();

// 큐처럼 쓰기
Deque<Integer> queue = new ArrayDeque<>();
queue.offer(1);      // 뒤에 추가 (= addLast)
queue.poll();        // 앞에서 제거 후 반환 (= removeFirst, 비어있으면 null)
queue.peek();         // 앞 확인

// 양쪽 다 쓰고 싶으면 (덱)
deque.offerFirst(1); deque.offerLast(2);
deque.pollFirst();   deque.pollLast();
```

`Queue<Integer> q = new LinkedList<>();` 도 흔히 쓰지만 알고리즘 문제에선 `ArrayDeque`가 성능상 더 낫다.

---

## 4. HashMap / HashSet

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.get("a");                          // 없으면 null
map.getOrDefault("b", 0);              // 없으면 기본값
map.containsKey("a");
map.containsValue(1);
map.remove("a");

// 값 갱신 (카운팅할 때 자주 씀)
map.put("a", map.getOrDefault("a", 0) + 1);
map.merge("a", 1, Integer::sum);       // 위와 동일, 더 간결

// 순회
for (Map.Entry<String, Integer> e : map.entrySet()) {
    e.getKey(); e.getValue();
}
map.forEach((k, v) -> System.out.println(k + "=" + v));

// HashSet - 중복 제거 / 존재 여부 확인
Set<Integer> set = new HashSet<>();
set.add(1);
set.contains(1);
set.remove(1);

// 순서가 필요하면
LinkedHashMap / LinkedHashSet   // 삽입 순서 유지
TreeMap / TreeSet                // 정렬된 순서 유지 (내부적으로 이진트리)
```

---

## 5. String / StringBuilder

`String`은 **불변(immutable)**. `+=` 로 반복해서 이어붙이면 매번 새 객체 생성됨 → 반복문에서는 `StringBuilder` 사용.

```java
StringBuilder sb = new StringBuilder();
sb.append("a").append(1);
sb.insert(0, "x");
sb.deleteCharAt(sb.length() - 1);      // 마지막 글자 제거
sb.reverse();
sb.toString();

// 문자열 자주 쓰는 것들
String s = "hello,world";
s.split(",");                 // ["hello", "world"]
s.charAt(0);
s.substring(1, 3);            // [1,3)
s.indexOf("l");
s.contains("lo");
s.replace("l", "L");
s.trim();
s.toUpperCase() / toLowerCase();
String.join(",", list);       // List -> String
s.toCharArray();              // String -> char[]
new String(charArray);        // char[] -> String

// String <-> 숫자
Integer.parseInt("123");
String.valueOf(123);
```

---

## 6. 정렬 & Comparator

```java
// 객체 배열/리스트 정렬
Arrays.sort(arr, (a, b) -> a - b);              // 오름차순
Arrays.sort(arr, Comparator.reverseOrder());     // 내림차순

// 2차원 배열 정렬 (예: [[점수, 이름], ...] 를 점수 기준 정렬)
Arrays.sort(arr2d, (a, b) -> a[0] - b[0]);

// 커스텀 객체 정렬
list.sort(Comparator.comparing(Person::getAge));
list.sort(Comparator.comparing(Person::getAge).reversed());
list.sort(Comparator.comparing(Person::getAge).thenComparing(Person::getName));
```

> ⚠️ `int[]` 는 `Arrays.sort(arr, comparator)` 못 씀 (원시타입 배열은 커스텀 비교자 불가). 커스텀 정렬이 필요하면 `Integer[]`로 박싱하거나 2차원 배열/리스트 사용.

---

## 7. 기타 자주 쓰는 것

```java
Math.max(a, b) / Math.min(a, b)
Math.abs(x)
Math.pow(2, 3)          // double 반환 주의
(int) Math.sqrt(x)

// 최댓값/최솟값 초기값
int max = Integer.MIN_VALUE;
int min = Integer.MAX_VALUE;

// 2차원 배열 순회 (그래프/BFS·DFS에서 자주 씀)
int[] dx = {-1, 1, 0, 0};
int[] dy = {0, 0, -1, 1};
```
