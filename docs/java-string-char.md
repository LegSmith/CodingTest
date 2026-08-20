# Java String ↔ char 관계 정리 (코딩테스트용)

일반 String/StringBuilder 기본 문법은 [java-syntax-coding-test.md](java-syntax-coding-test.md) 참고. 이 문서는 **String과 char가 왜 다르고, 왜 자주 헷갈리는지**만 깊게 판다. 계기: [Solution107](../src/basic/day21/Solution107.java) 문자열 구간 뒤집기 풀이 중 `split("")` + `String.replace()`로 삽질하다가 정리.

## 1. char는 문자가 아니라 숫자다

`char`는 **기본형(primitive)**. 내부적으로는 유니코드 코드포인트를 저장하는 16비트 정수다. 그래서 산술 연산이 그대로 된다.

```java
char c = 'a';
int code = c;              // 97 (자동 형변환, char -> int)
char next = (char) (c + 1); // 'b' — int로 계산된 걸 다시 char로 캐스팅해야 함

// 알파벳 순서 판별 (코딩테스트 단골)
'a' + 1        // 98 (int) — char끼리 연산하면 결과는 int로 승격됨
(char)('a' + 1) // 'b' (char로 명시 캐스팅해야 char가 됨)

// 대소문자 변환 (ASCII 규칙 이용)
char upper = (char) ('a' - 32);      // 'A' (대문자는 소문자보다 32 작음)
char lower = (char) ('A' + 32);      // 'a'

// 숫자 문자 -> 실제 숫자
char digit = '7';
int num = digit - '0';               // 7 — '0'~'9'는 연속된 코드값이라 이 트릭이 성립
```

> ⚠️ `char + char`, `char + int`는 결과 타입이 `int`다. `char`에 다시 담으려면 반드시 `(char)` 캐스팅 필요. 캐스팅 깜빡하면 컴파일 에러남 (`incompatible types`).

## 2. String은 char의 배열이 아니라 "불변 객체"다

`String`은 `char[]`를 감싼 **불변(immutable) 클래스**다. 한번 만들어지면 내용이 절대 안 바뀐다. `replace`, `substring`, `toUpperCase` 등은 전부 **새 String을 만들어 반환**하는 거지, 원본을 수정하는 게 아니다.

```java
String s = "hello";
s.replace('h', 'j');   // "jello"를 반환할 뿐, s 자체는 여전히 "hello"
s = s.replace('h', 'j'); // 이렇게 재할당해야 실제로 바뀜
```

이게 왜 문제가 되냐면, 반복문 안에서 String을 계속 이어붙이면 **매 반복마다 새 객체가 생성**돼서 O(n²)이 된다.

```java
// 느림 — 반복마다 새 String 객체 생성 (n번 반복 시 O(n^2))
String result = "";
for (char c : arr) {
    result += c;
}

// 빠름 — 내부 char 배열을 직접 늘려가며 씀 (O(n))
StringBuilder sb = new StringBuilder();
for (char c : arr) {
    sb.append(c);
}
String result = sb.toString();
```

> ⚠️ `String`의 이런 불변성 때문에, **구간을 수정해야 하는 문제(뒤집기, swap 등)는 String을 직접 수정하려 하지 말고 `char[]`로 바꿔서 처리**하는 게 정석이다. [Solution107](../src/basic/day21/Solution107.java)에서 겪은 삽질이 정확히 이 지점 — `String.replace(find, reverseFind)`로 구간을 갈아끼우려다 위치 기반이 아니라 내용 기반 치환이라 버그가 났었다.

## 3. String ↔ char[] ↔ char 변환 총정리

| 방향 | 코드 | 비고 |
|---|---|---|
| String → char[] | `s.toCharArray()` | 가장 표준. 코딩테스트에서 구간 조작할 땐 이걸로 시작 |
| String → char (1글자) | `s.charAt(i)` | 인덱스 범위 벗어나면 `StringIndexOutOfBoundsException` |
| String → char[] (스트림) | `s.chars().mapToObj(c -> (char) c)` | `IntStream`으로 나오는 게 함정 — 아래 4번 참고 |
| char[] → String | `new String(charArray)` | 배열 전체를 문자열로 |
| char[] → String (구간) | `new String(charArray, offset, count)` | 배열 일부만 |
| char → String (1글자) | `String.valueOf(c)` 또는 `Character.toString(c)` | `"" + c`도 되지만 관용적이지 않음 |
| char[] → String (join) | `new String(arr)` 또는 `String.valueOf(arr)` | 둘 다 동일 |

```java
char[] arr = "hello".toCharArray();
arr[0] = 'j';
String result = new String(arr);   // "jello"
```

> ⚠️ **`s.split("")`은 쓰지 마라.** 각 글자를 `String[]`으로 쪼개는데, 정규식 split이라 느리고, 빈 문자열이 앞에 낄 수도 있는 등 함정이 많다. 한 글자씩 다루고 싶으면 `toCharArray()`가 정답.

## 4. `chars()`는 `IntStream`을 반환한다 (Stream 쓸 때 함정)

```java
"abc".chars()                       // IntStream — Character 아니고 int 스트림!
    .forEach(c -> System.out.println(c));   // 97 98 99 (숫자로 출력됨)

"abc".chars()
    .mapToObj(c -> (char) c)        // int -> char로 캐스팅해서 Stream<Character>로
    .forEach(System.out::println);  // a b c (문자로 출력됨)
```

`String`에 문자 단위 스트림 메서드가 없어서 `chars()`(int 스트림)를 거쳐야 하고, 캐스팅을 빼먹으면 아스키 코드 숫자가 그대로 나온다. List/Stream으로 문자 다루는 문제([Solution107 List 버전](../src/basic/day21) 참고)에서 자주 걸리는 실수.

## 5. char 비교 & Character 유틸리티

`char`는 기본형이라 `==`로 비교해도 안전하다 (String과 달리 참조 비교 문제가 없음).

```java
char c = 'a';
if (c == 'a') { ... }          // OK, 기본형끼리 값 비교

Character.isDigit(c);          // 숫자인가
Character.isAlphabetic(c);     // 알파벳인가
Character.isUpperCase(c);      // 대문자인가
Character.isLowerCase(c);      // 소문자인가
Character.isWhitespace(c);     // 공백인가
Character.toUpperCase(c);      // 대문자로 변환 (ASCII 뺄셈 트릭 대신 이거 써도 됨)
Character.toLowerCase(c);
Character.getNumericValue(c);  // '7' -> 7 (digit - '0' 트릭의 안전한 대안)
```

> ⚠️ `String`끼리는 `==`가 아니라 `.equals()`로 비교해야 한다 (String Pool 때문에 리터럴끼리는 우연히 `==`가 통과하는 경우가 있어서 더 헷갈림). **`char`는 기본형이라 이 문제 자체가 없다** — `char` 비교는 항상 `==`가 맞다.

## 6. 실전 패턴: 구간 뒤집기는 char[] + 투 포인터

String/char 관계를 정리하면 왜 이 패턴이 정석인지 설명이 된다.

```java
char[] arr = my_string.toCharArray();   // 1. 불변 String을 가변 char[]로

int left = start, right = end;
while (left < right) {                   // 2. 기본형 char라서 임시변수 스왑이 싸고 빠름
    char tmp = arr[left];
    arr[left] = arr[right];
    arr[right] = tmp;
    left++;
    right--;
}

String answer = new String(arr);         // 3. 다 끝난 뒤 한 번만 String으로 변환
```

- `String`은 불변이라 **중간 과정에서 절대 직접 수정 못 함** → 매번 새 객체를 만들면 느림
- `char`는 기본형이라 **스왑이 박싱/언박싱 없이 매우 저렴함** → `char[]`로 바꿔서 다루는 게 성능상 최선
- 다 바꾸고 나서 **딱 한 번** `new String(arr)`로 합치는 게 핵심 — 중간에 String으로 왔다갔다 하지 않는다
