"""
1. 아이디어:
- 육각형의 점 구하기 -> Min, Max 찾기 -> Missing pt 찾기 -> 넓이 구하기

2. 시간 복잡도:


3. 자료 구조:
K: m^2당 참외 수
points: int[]
cur_x, cur_y: 현재 x, y 위치,
x_max, x_min: points 중 X max, min int, int
y_max, y_min: points 중 Y max, min int, int
missing_pt: x_max, x_min, y_max, y_min 조합 중 points에 없는 것 int[]
x_middle, y_middle: points 중 X, Y min, max 하나도 포함 안 하는 점 int, int
square_temp: 뺄 면적 int
area: 최종 면적 int
answer = area * K
"""

import sys

input = sys.stdin.readline
K = int(input().strip())
points = []
cur_x, cur_y = 0, 0
x_max, x_min = 0, 0
y_max, y_min = 0, 0
missing_pt = []
middle = []
x_middle, y_middle = 0, 0
area = 0
answer = 0

for _ in range(6):
    # 1. 점 기록
    dir, dist = map(int, input().strip().split())
    if dir == 1:
        cur_x += dist
    elif dir == 2:
        cur_x -= dist
    elif dir == 3:
        cur_y += dist
    else:
        cur_y -= dist
    points.append((cur_x, cur_y))
# print(points)

# 2. Min, Max 찾기
x_max, x_min = max(points, key=lambda x: x[0])[0], min(points, key=lambda x: x[0])[0]
y_max, y_min = max(points, key=lambda x: x[1])[1], min(points, key=lambda x: x[1])[1]

edge_points = [(x_max, y_max), (x_max, y_min), (x_min, y_max), (x_min, y_min)]
# print("edge", edge_points)
for pts in edge_points:
    if pts not in points:
        missing_pt = pts

# middle: save values with higher abs
for pt in points:
    if ((pt[0] != x_max) and (pt[0] != x_min)) and (
        (pt[1] != y_max) and (pt[1] != y_min)
    ):
        middle.append((pt[0], pt[1]))
x_middle, y_middle = (
    max(middle, key=lambda x: abs(x[0]))[0],
    max(middle, key=lambda x: abs(x[1]))[1],
)

# 3. 넓이 구하기
square_temp = abs(missing_pt[0] - x_middle) * abs(missing_pt[1] - y_middle)
area = abs(x_max - x_min) * abs(y_max - y_min) - square_temp
answer = area * K
print(answer)
