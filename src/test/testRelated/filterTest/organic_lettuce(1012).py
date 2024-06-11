"""
1. 아이디어: BFS(or DFS)
dx = [1, 0, -1, 0]
dy = [0, 1, 0, -1]

def bfs((y, x)):
    q = deque()
    q.append((y, x))
    (y, x) = q.popleft()

    while q:
        for k in range(4): # 상하좌우 탐색
            ey = y - dy[k]
            ex = x - dx[k]
            if 0<= ey <= N-1 and 0 <= ex <= M-1 and chk[ey][ex] == False:
                chk[ey][ex] = True
                q.append((ey, ex))


    if 0 <= y <= N-1 and 0 <= x <= M-1 and chk[y][x] == False:
        chk[y][x] = True
        worms += 1

2. 시간 복잡도: O(E + V) = 50 * 50 + V <= 50 * 50 + 4 * 50 * 50 < 2억


3. 자료구조:
T: 테스트 케이스 개수 int
M: 가로 길이 int
N: 세로 길이 int
lettuce: 배추 개수 int
farm: 농장 배열 int[][]
chk: 비교할 배열 int[][]
worms: 지렁이 마리 수 int


"""

import sys
from collections import deque # deque 자료형은 collections에 있음

input = sys.stdin.readline

T = int(input().strip())
reslt_list = []


dx = [1, 0, -1, 0]
dy = [0, 1, 0, -1]


def bfs(y, x):
    q = deque()
    q.append((y, x))

    while q:
        (y, x) = q.popleft()  # 먼저 pop하고 while q를 돌렸음
        for k in range(4):  # 상하좌우 탐색
            ey = y - dy[k]
            ex = x - dx[k]
            if 0 <= ey < N and 0 <= ex < M:
                if farm[ey][ex] == 1 and chk[ey][ex] == False:
                    # print("ey, ex", ey, ex)  # print로 디버깅 해봐야
                    chk[ey][ex] = True
                    q.append((ey, ex))


for i in range(T):
    (M, N, lettuce) = map(int, input().split())
    farm = [[0 for j in range(M)] for i in range(N)]  # TODO: 2차원 배열 초기화 연습하기
    chk = [[False for j in range(M)] for i in range(N)]
    worms = 0

    for j in range(lettuce):  # 배추 위치 기록
        temp_x, temp_y = map(int, input().split())
        farm[temp_y][temp_x] = 1
    for k in range(N):
        for l in range(M):
            if farm[k][l] == 1 and chk[k][l] == False:
                chk[k][l] = True
                bfs(k, l)
                worms += 1
    reslt_list.append(worms)

for num in reslt_list:
    print(num)
