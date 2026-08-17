# Java Stream 문법 정리 (코딩테스트용, 상세판)

일반 배열/컬렉션 문법은 [java-syntax-coding-test.md](java-syntax-coding-test.md) 참고. 이 문서는 Stream만 깊게 판다.

## 0. Stream이 뭔데?

배열이나 컬렉션의 데이터를 **파이프라인**처럼 흘려보내면서 가공하는 도구.

```
[데이터 소스] -> 중간연산 -> 중간연산 -> ... -> 최종연산 -> [결과]
```

- **중간연산(intermediate)**: `filter`, `map`, `sorted`처럼 **다시 Stream을 반환**. 여러 개 이어붙일 수 있음(체이닝). 최종연산이 호출되기 전까진 실제로 아무 연산도 안 일어남(지연 실행/lazy).
- **최종연산(terminal)**: `collect`, `sum`, `forEach`처럼 **Stream이 아닌 실제 결과값**을 반환. 이게 호출되는 순간 파이프라인 전체가 실행됨.
- 스트림은 **한 번 쓰고 버리는 일회용**. 최종연산을 한 번 호출하면 그 스트림 객체는 다시 못 씀 → 재사용하려면 스트림을 새로 만들어야 함.

```java
List<Integer> nums = List.of(5, 3, 8, 1, 9, 2);

int result = nums.stream()             // 1. 스트림 생성
        .filter(x -> x % 2 == 1)       // 2. 중간연산: 홀수만
        .map(x -> x * 10)              // 3. 중간연산: 10배
        .mapToInt(Integer::intValue)   // 4. 중간연산: Stream<Integer> -> IntStream
        .sum();                        // 5. 최종연산: 합계

// nums = [5,3,8,1,9,2] -> 홀수만 [5,3,1,9] -> 10배 [50,30,10,90] -> 합계 180
```

for문으로 쓰면:
```java
int result = 0;
for (int x : nums) {
    if (x % 2 == 1) {
        result += x * 10;
    }
}
```
→ 스트림이 항상 더 짧거나 빠른 건 아니지만, "무엇을 할지"가 선언적으로 읽힌다는 장점이 있음.

---

## 1. 스트림 만들기 (소스별)

### 1-1. 컬렉션에서
```java
List<Integer> list = List.of(1, 2, 3);
Stream<Integer> s = list.stream();
```

### 1-2. 배열에서
```java
int[] arr = {1, 2, 3};
IntStream is = Arrays.stream(arr);          // 원시타입 배열 -> IntStream (박싱 안 됨, 더 빠름)

String[] strArr = {"a", "b"};
Stream<String> ss = Arrays.stream(strArr);  // 객체 배열 -> Stream<T>
```

### 1-3. 값 나열 / 범위로
```java
Stream<Integer> s = Stream.of(1, 2, 3);

IntStream.range(0, 5)          // 0,1,2,3,4       (끝 미포함) - for(i=0; i<5; i++)와 동일한 범위
IntStream.rangeClosed(1, 5)    // 1,2,3,4,5       (끝 포함)   - for(i=1; i<=5; i++)와 동일한 범위
```
> **상황**: "0부터 n-1까지 인덱스로 뭔가 하고 싶다" → `IntStream.range(0, n)`이 for문 대체 가능.
```java
// 예: 배열 인덱스와 값을 같이 쓰고 싶을 때 (스트림은 기본적으로 인덱스를 안 주기 때문)
int[] arr = {10, 20, 30};
IntStream.range(0, arr.length)
        .forEach(i -> System.out.println(i + " : " + arr[i]));
```

### 1-4. 문자열에서
```java
"hello".chars()   // IntStream (문자 하나하나의 유니코드 값)
```

### 1-5. 2차원 배열 평탄화 (`flatMap`)
> **상황**: `int[][] grid`의 모든 원소를 한 줄로 쭉 펴서 다루고 싶을 때.
```java
int[][] grid = {{1, 2}, {3, 4}, {5, 6}};

int total = Arrays.stream(grid)                       // Stream<int[]>  (행 단위)
        .flatMapToInt(row -> Arrays.stream(row))       // 각 행을 IntStream으로 풀어서 하나로 합침
        .sum();
// total = 21
```

---

## 2. 중간연산 상세 (상황별 예시)

### `filter` — 조건에 맞는 것만 남기기
> **상황**: 양수만 뽑고 싶다, 특정 길이 이상 문자열만 남기고 싶다.
```java
List<Integer> positives = nums.stream()
        .filter(x -> x > 0)
        .collect(Collectors.toList());

List<String> longWords = words.stream()
        .filter(w -> w.length() >= 5)
        .toList();
```

### `map` — 각 원소를 변환하기
> **상황**: 문자열 리스트를 길이 리스트로, 정수를 제곱값으로 바꾸고 싶다.
```java
List<Integer> lengths = words.stream()
        .map(String::length)             // w -> w.length() 와 동일
        .toList();

List<Integer> squared = nums.stream()
        .map(x -> x * x)
        .toList();
```

### `mapToInt` / `mapToObj` / `boxed` — 박싱·언박싱 변환
> **상황**: `Stream<Integer>`로 합계를 구하려면 `IntStream`으로 바꿔야 `sum()`을 쓸 수 있음. 반대로 `IntStream`에서 객체 메서드(예: `Collectors.toList()`)를 쓰려면 다시 박싱해야 함.
```java
// Stream<Integer> -> IntStream (sum, average, max 같은 숫자 전용 메서드 쓰려고)
int sum = list.stream().mapToInt(Integer::intValue).sum();

// IntStream -> Stream<Integer> (collect, distinct 같은 객체 메서드 쓰려고)
List<Integer> boxedList = IntStream.range(0, 5).boxed().toList();

// IntStream -> Stream<String> 같은 객체 변환
List<String> strs = IntStream.range(0, 3)
        .mapToObj(i -> "item" + i)
        .toList();
// ["item0", "item1", "item2"]
```

### `sorted` — 정렬
> **상황**: 오름차순/내림차순 정렬, 커스텀 기준 정렬.
```java
list.stream().sorted().toList();                                  // 오름차순
list.stream().sorted(Comparator.reverseOrder()).toList();         // 내림차순

// 문자열을 길이 기준으로 정렬, 길이 같으면 사전순
words.stream()
        .sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()))
        .toList();
```
> ⚠️ `IntStream.sorted()`는 **오름차순만** 지원(커스텀 Comparator 불가). 내림차순 필요하면 `.boxed()` 후 `Comparator.reverseOrder()` 사용.

### `distinct` — 중복 제거
> **상황**: 배열에서 중복 없는 값들만 뽑고 싶다.
```java
int[] arr = {1, 2, 2, 3, 3, 3};
int[] unique = Arrays.stream(arr).distinct().toArray();
// [1, 2, 3]
```

### `limit` / `skip` — 개수 자르기
> **상황**: 정렬 후 상위 3개만, 혹은 앞 N개를 건너뛰고 싶다.
```java
// 상위 3개 (내림차순 정렬 후 앞 3개)
List<Integer> top3 = nums.stream()
        .sorted(Comparator.reverseOrder())
        .limit(3)
        .toList();

// 앞 2개 건너뛰고 나머지
List<Integer> rest = nums.stream().skip(2).toList();
```

### `peek` — 중간 값 확인 (디버깅용)
```java
list.stream()
        .peek(x -> System.out.println("필터 전: " + x))
        .filter(x -> x > 0)
        .peek(x -> System.out.println("필터 후: " + x))
        .toList();
```
> 실제 로직에 넣지 말고 디버깅할 때만 임시로 쓰는 용도.

---

## 3. 최종연산 상세 (상황별 예시)

### `collect(Collectors.toList())` / `.toList()`
> **상황**: 결과를 리스트로 받고 싶다.
```java
List<Integer> result = nums.stream().filter(x -> x > 0).collect(Collectors.toList());
List<Integer> result2 = nums.stream().filter(x -> x > 0).toList(); // Java 16+, 더 간결 (단, 불변 리스트라 이후 add 불가)
```

### `collect(Collectors.joining())` — 문자열 이어붙이기
> **상황**: 문자열 리스트를 구분자로 합쳐서 하나의 문자열로 만들고 싶다. (`Stream<String>`에만 사용 가능)
```java
List<String> words = List.of("a", "b", "c");
String joined = words.stream().collect(Collectors.joining());        // "abc"
String joined2 = words.stream().collect(Collectors.joining(", "));   // "a, b, c"
String joined3 = words.stream().collect(Collectors.joining(", ", "[", "]")); // "[a, b, c]" (접두/접미 붙이기)
```

### `Collectors.groupingBy` — 그룹으로 묶기
> **상황**: 문자열들을 길이별로 묶고 싶다, 숫자들을 짝/홀수 그룹으로 세고 싶다.
```java
List<String> words = List.of("a", "bb", "cc", "ddd");

// 길이별로 묶기 -> Map<Integer, List<String>>
Map<Integer, List<String>> byLength = words.stream()
        .collect(Collectors.groupingBy(String::length));
// {1=[a], 2=[bb, cc], 3=[ddd]}

// 그룹별 "개수"만 필요하면 다운스트림 collector 추가
Map<Integer, Long> countByLength = words.stream()
        .collect(Collectors.groupingBy(String::length, Collectors.counting()));
// {1=1, 2=2, 3=1}
```

> **문자 빈도수 세기 (코딩테스트 단골 패턴)**
```java
String s = "hello";
Map<Character, Long> freq = s.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
// {e=1, h=1, l=2, o=1}

// 가장 많이 나온 문자 찾기
char mostFrequent = freq.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElseThrow();
// 'l'
```

### `Collectors.partitioningBy` — 조건 하나로 딱 둘로 나누기
> **상황**: 짝수/홀수, 합격/불합격처럼 true/false 두 그룹으로만 나누고 싶다. (`groupingBy`보다 이 경우엔 이게 더 명확)
```java
Map<Boolean, List<Integer>> parts = nums.stream()
        .collect(Collectors.partitioningBy(x -> x % 2 == 0));
List<Integer> evens = parts.get(true);
List<Integer> odds  = parts.get(false);
```

### `Collectors.toMap` — Map으로 수집
> **상황**: 리스트를 "key -> value" 형태의 Map으로 바로 만들고 싶다.
```java
List<String> words = List.of("apple", "kiwi", "banana");

Map<String, Integer> wordToLength = words.stream()
        .collect(Collectors.toMap(w -> w, String::length));
// {apple=5, kiwi=4, banana=6}
```

### `sum` / `average` / `max` / `min` / `count` — 숫자 집계
> **상황**: 배열의 합, 평균, 최댓값을 구하고 싶다. (`IntStream`/`LongStream`/`DoubleStream` 전용, `Stream<Integer>`엔 없음!)
```java
int[] arr = {3, 1, 4, 1, 5};

int total = Arrays.stream(arr).sum();                     // 14
OptionalDouble avg = Arrays.stream(arr).average();         // 값이 없을 수도 있어서 Optional
double avgValue = avg.orElse(0);                            // 2.8

OptionalInt max = Arrays.stream(arr).max();
int maxValue = max.orElse(Integer.MIN_VALUE);               // 5

long cnt = Arrays.stream(arr).filter(x -> x > 2).count();  // 3
```

> `Stream<Integer>`(객체 스트림)로 최댓값을 구하려면 Comparator를 넘겨야 함:
```java
Optional<Integer> max = list.stream().max(Comparator.naturalOrder());
```

### `anyMatch` / `allMatch` / `noneMatch` — 조건 검사
> **상황**: "배열에 음수가 하나라도 있나?", "전부 양수인가?"
```java
boolean hasNegative = nums.stream().anyMatch(x -> x < 0);   // 하나라도 만족하면 true
boolean allPositive = nums.stream().allMatch(x -> x > 0);   // 전부 만족해야 true
boolean noneNegative = nums.stream().noneMatch(x -> x < 0); // 아무도 만족 안 해야 true
```

### `findFirst` / `findAny` — 조건에 맞는 첫 원소 찾기
> **상황**: 조건을 만족하는 첫 번째 원소를 찾고 싶다 (for문 + break 대체).
```java
Optional<Integer> firstEven = nums.stream()
        .filter(x -> x % 2 == 0)
        .findFirst();

int value = firstEven.orElse(-1);       // 없으면 -1
// 또는
if (firstEven.isPresent()) {
    System.out.println(firstEven.get());
}
```

### `reduce` — 누적 계산
> **상황**: 합계/곱셈처럼 앞에서부터 값을 하나로 누적하고 싶다. (`sum()`이 없는 `Stream<Integer>`에서 직접 누적 로직을 쓸 때 유용)
```java
int sum = list.stream().reduce(0, Integer::sum);              // 초기값 0, 누적은 덧셈
int product = list.stream().reduce(1, (a, b) -> a * b);       // 초기값 1, 누적은 곱셈

// 초기값 없이 - 결과가 없을 수도 있어서 Optional
Optional<Integer> maxManual = list.stream().reduce((a, b) -> a > b ? a : b);
```

### `forEach` — 그냥 순회하며 실행 (반환값 없음)
```java
list.stream().forEach(System.out::println);
```
> 단순 순회라면 굳이 스트림 쓸 필요 없이 `for (int x : list)`가 더 직관적인 경우가 많음. `forEach`는 스트림 파이프라인 끝에 필터/맵을 이미 적용한 상태에서 쓸 때 의미 있음.

### `toArray` — 배열로 변환
```java
int[] arr = Arrays.stream(list.toArray(new Integer[0]))... // 비효율적인 예시 (아래처럼 쓸 것)

int[] arr = list.stream().mapToInt(Integer::intValue).toArray();  // List<Integer> -> int[]
Integer[] objArr = list.stream().toArray(Integer[]::new);          // List<Integer> -> Integer[]
```

---

## 4. 실전 레시피 모음 (코딩테스트에 바로 붙여쓰기)

```java
// 1) int[] -> List<Integer>
List<Integer> list = Arrays.stream(arr).boxed().toList();

// 2) List<Integer> -> int[]
int[] arr = list.stream().mapToInt(Integer::intValue).toArray();

// 3) 배열 오름차순 정렬된 새 배열 (원본 유지하고 싶을 때)
int[] sorted = Arrays.stream(arr).sorted().toArray();

// 4) 문자열 하나하나 정렬해서 다시 합치기 (아나그램 비교 등에 자주 씀)
String sortedStr = Arrays.stream(s.split(""))
        .sorted()
        .collect(Collectors.joining());

// 5) 두 배열의 원소를 순서대로 묶어서 문자열로 (좌표 출력 등)
int[] xs = {1, 2, 3};
int[] ys = {4, 5, 6};
// 스트림만으로는 두 배열 동시 순회가 번거로움 -> 이런 경우 IntStream.range(0, n)으로 인덱스를 돌리는 게 낫다
List<String> points = IntStream.range(0, xs.length)
        .mapToObj(i -> "(" + xs[i] + "," + ys[i] + ")")
        .toList();
// ["(1,4)", "(2,5)", "(3,6)"]

// 6) 조건 만족하는 개수 세기
long count = Arrays.stream(arr).filter(x -> x > 0).count();

// 7) 최댓값의 인덱스 찾기 (스트림만으로는 까다로움 - 이럴 땐 그냥 for문이 더 낫다)
int maxIdx = 0;
for (int i = 1; i < arr.length; i++) {
    if (arr[i] > arr[maxIdx]) maxIdx = i;
}
```

---

## 5. 주의할 점 / 흔한 실수

- **스트림은 1회용**. 아래처럼 재사용하면 `IllegalStateException` 발생:
  ```java
  Stream<Integer> s = list.stream();
  s.count();
  s.forEach(System.out::println); // 예외! 이미 소비된 스트림
  ```
  → 다시 쓰려면 `list.stream()`을 또 호출해서 새 스트림을 만들어야 함.

- **`IntStream`/`Stream<Integer>` 헷갈리지 않기**: `sum()`, `average()`는 `IntStream` 전용. `Stream<Integer>`에는 없음 → `mapToInt`로 변환 필요.

- **`Optional` 값 꺼낼 때 `.get()` 바로 쓰지 않기**: 값이 없을 수 있으므로 `.orElse(기본값)` 또는 `.isPresent()` 체크 후 사용. 값이 확실히 있다고 로직상 보장될 때만 `.orElseThrow()` 사용.

- **스트림이 항상 빠른 건 아님**: 오토박싱/언박싱, 람다 오버헤드 때문에 단순 for문보다 느릴 때가 많음. 코딩테스트에서 시간복잡도가 빠듯하면 for문으로 바꿔볼 것.

- **디버깅이 for문보다 어려움**: 중간 값을 보려면 `peek()`을 끼워 넣거나, 파이프라인을 잘게 쪼개서 변수에 담아 확인.

- **람다 안에서 바깥 변수 재할당 불가**: 람다는 "effectively final" 변수만 캡처 가능.
  ```java
  int total = 0;
  list.forEach(x -> total += x); // 컴파일 에러! 람다 안에서 바깥 지역변수 수정 불가
  ```
  → 누적이 필요하면 `reduce`나 `sum()` 같은 스트림 자체 기능을 쓰거나, `int[] box = {0}` 같은 우회 대신 애초에 for문을 쓰는 게 낫다.
