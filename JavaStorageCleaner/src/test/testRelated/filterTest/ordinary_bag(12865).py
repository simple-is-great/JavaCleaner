"""
1. 아이디어: knapsack -> DP!

2. 시간 복잡도: N <= 100, K <= 100,000, item[0] <= 100,000 item[1] <= 1,000

3. 변수:
N, K
weights: 무게 저장할 리스트
values: 가치 저장할 리스트
items: 물품 & 무게 저장하는 리스트
memo: 최대 가치 저장하는 리스트

"""

import sys

input = sys.stdin.readline

N, K = map(int, input().split())
weights = [0] * (N + 1)
values = [0] * (N + 1)
for i in range(1, N + 1):
    weights[i], values[i] = map(int, input().split())

memo = [[0, ""] for _ in range(K + 1)]  # 최대 가치, 아이템 사용여부 리스트

for i in range(1, K + 1):
    for j in range(1, N + 1):  # 물건은 중복해 담을 수 없음
        if (
            weights[j] <= i  # weights 비교
            and memo[i][0] < memo[i - weights[j]][0] + values[j]  # values 비교
            and str(j) not in memo[i - weights[j]][1]  # 사용 여부 확인
        ):
            memo[i][0] = memo[i - weights[j]][0] + values[j]
            memo[i][1] = memo[i - weights[j]][1] + str(j)  # 기존 데이터에 j append

answer = max(memo)[0]
if answer > 0:
    print(answer)
else:
    print(0)
