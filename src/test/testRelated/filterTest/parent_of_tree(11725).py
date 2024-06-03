"""
1. 아이디어: BFS


2. 시간 복잡도: E = 100,000 , O(E+V) <= O(E + E) -> 가능


3. 변수:
N: 노드의 개수
edges: 간선 저장
parent: 0 ~ N까지 값을 담음, BFS 하면서 parent 노드를 바로 기록
visited: 노드 방문 여부 체크
"""

import sys
from collections import deque

input = sys.stdin.readline

N = int(input().strip())

edges = [sorted(map(int, input().split())) for _ in range(N - 1)]
parent = [0] * (N + 1)
visited = [False] * (N + 1)
edges.sort()

q = deque()  # 1부터 시작
q.append(1)
visited[1] = True

while q:
    vertice = q.popleft()
    visitedE = []
    for idx, edge in enumerate(edges):
        # print("edge:", edge)
        if edge[0] > vertice:
            break
        if edge[0] == vertice and visited[edge[1]] is False:
            parent[edge[1]] = str(edge[0])
            q.append(edge[1])
            visited[edge[1]] = True
            visitedE.append(edge)

        elif edge[1] == vertice and visited[edge[0]] is False:
            parent[edge[0]] = str(edge[1])
            q.append(edge[0])
            visited[edge[0]] = True
            visitedE.append(edge)

    for E in visitedE:
        edges.remove(E)
# print(parent[2:])
print("\n".join(parent[2:]))
