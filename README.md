# Create: MoonScenty Presents

- Create Addon
- Create 여러가지 부분들을 해석하여 MoonScenty가 원하는 컨텐츠로 만들어내는 모드입니다.
- Create 말고도 일부 모드에 의존성이 있을 수 있습니다.
- 석기, 브론즈, 스틸, 스테인리스 스틸, 티타늄 시대로 나뉩니다.
- 현재는 석기 컨텐츠 내용만 포함되어 있습니다.


## 석기 시대

### 시대 개요

석기 시대는 금속 기반 산업에 진입하기 전 단계로, 돌·목재·점토·섬유 등의 원시 재료를 이용하여 기본 도구와 저속 회전 기계, 광물 선광 및 원시 제련 기술을 확보하는 시대이다.

이 시대에서는 별도의 목재 절단대나 광석 분쇄대를 사용하지 않고, **도구와 가공 대상 아이템을 양손에 들고 서로 비비는 방식의 수동 아이템 프로세싱**을 기본 가공 방식으로 사용한다. 이 방식은 Create의 Polishing과 유사한 방식으로 동작하며, 석기 시대의 핵심적인 수동 가공 시스템으로 사용한다.

석기 시대의 주요 목표는 다음과 같다.

- 기본 석기 및 목재 가공 도구 확보
- 식물 섬유 → 끈 → 밧줄 가공
- 점토를 이용한 도가니와 주형 제작
- 숯 생산 기술 확보
- 저속 회전 동력 생산
- 광석의 파쇄, 선광, 분쇄 공정 확보
- Copper와 Tin 제련
- Bronze 생산
- Bronze 회전 부품 제작
- 64 RPM 달성을 통한 브론즈 시대 진입

석기 시대에서 사용하는 회전 부품은 기본적으로 **최대 32 RPM**까지만 견딜 수 있도록 제한한다.

### 석기 시대 핵심 진행 순서

```text
나무 / 돌 / 부싯돌 채집
↓
Flint Knife / Stone Hammer / Stone Chisel / Wooden Saw 제작
↓
Plant Fiber 확보
↓
Twine
↓
Rope
↓
점토 채집
↓
Unfired Crucible / Unfired Ingot Mold 제작
↓
Pit Kiln
↓
Fired Crucible / Ingot Mold 제작
↓
Charcoal 생산
↓
Primitive Hand Crank
↓
Primitive Millstone
↓
Primitive Water Wheel
↓
32 RPM 회전망 구축
↓
Copper / Tin 광석 채굴
↓
Stone Hammer 수동 가공
↓
Create Crushed Ore
↓
Ore Pan / Primitive Sluice
↓
Ore Concentrate
↓
Primitive Millstone
↓
Metal Dust
↓
Fired Crucible + Charcoal + Bellows
↓
Copper / Tin 생산
↓
Copper + Tin
↓
Bronze
↓
Bronze Shaft / Bronze Bearing / Bronze Cogwheel 제작
↓
64 RPM 회전망 구축
↓
브론즈 시대
```

### 수동 아이템 프로세싱

석기 시대에서는 일부 가공을 별도의 블록 없이 플레이어의 양손 아이템 상호작용으로 수행한다.

플레이어가 가공 도구와 가공 대상 아이템을 각각 왼손과 오른손에 들고 서로 비비면 일정 시간 후 아이템 프로세싱이 완료된다.

```text
Main Hand : 가공 대상
Off Hand  : 가공 도구

또는

Main Hand : 가공 도구
Off Hand  : 가공 대상

↓
지속적인 상호작용
↓
가공 진행
↓
입력 아이템 소비
↓
결과 아이템 생성
```

도구는 레시피에 따라 내구도를 소모할 수 있다.

| 도구 | 입력 | 출력 | 용도 |
|---|---|---|---|
| Wooden Saw | Log | Planks | 목재 기본 제재 |
| Wooden Saw | Planks | Wooden Plate | 목재 정밀 가공 |
| Wooden Saw | Wooden Plate | Wooden Gear Blank | 원시 기어용 중간 부품 제작 |
| Stone Hammer | Raw Ore | Crushed Ore | 광석 수동 파쇄 |
| Stone Hammer | Stone | Stone Dust / Gravel | 석재 파쇄 |
| Stone Chisel | Stone / Wooden Part | 정밀 가공 부품 | 형상 가공 |
| Flint Knife | Plant / Leather | Plant Fiber / Leather Strip | 절단 가공 |

### 목재 가공

석기 시대의 목재 가공은 `wooden_saw`를 사용한다.

```text
Wooden Saw + Log
→ Planks

Wooden Saw + Planks
→ Wooden Plate

Wooden Saw + Wooden Plate
→ Wooden Gear Blank
```

초기에는 수작업의 효율을 낮게 설정하고, 브론즈 시대 이후 Mechanical Saw를 사용하면 생산량과 처리 속도가 크게 증가하도록 구성한다.

```text
Stone Age

Log
+ Wooden Saw
→ 4 Planks

Bronze Age

Log
→ Mechanical Saw
→ 6 Planks + Sawdust
```

### 건조 가공

`drying_rack`은 회전 동력을 사용하지 않는 석기 시대의 시간 기반 가공 블록이다.

가죽, 식물 섬유, 젖은 재료 등을 건조하는 데 사용하며, 후속 시대에서는 동일한 공정을 기계화할 수 있다.

```text
Wet Leather / Hide
↓
Drying Rack
↓
Leather
```

### 광석 가공

광석은 바닐라 Furnace에 직접 넣어 Ingot으로 만들 수 없도록 한다.

```text
Raw Ore
↓
Stone Hammer
↓
Create Crushed Ore
↓
Ore Pan / Primitive Sluice
↓
Ore Concentrate
↓
Primitive Millstone
↓
Metal Dust
↓
Fired Crucible
↓
Molten Metal
↓
Ingot Mold
↓
Metal Ingot
```

광석 파쇄 결과물은 별도의 `crushed_*_ore` 아이템을 추가하지 않고 **Create 모드에 이미 존재하는 Crushed Ore 계열 아이템을 그대로 사용한다.**

### 원시 제련

석기 시대의 금속은 일반 Furnace에서 직접 제련하지 않는다.

```text
Metal Dust
+
Fired Crucible
+
Charcoal
+
Bellows
↓
Molten Metal
↓
Ingot Mold
↓
Metal Ingot
```

Bronze는 다음과 같이 생산한다.

```text
Copper ×3
+
Tin ×1
↓
Fired Crucible
↓
Molten Bronze
↓
Ingot Mold
↓
Bronze Ingot
```

Iron은 석기 시대에 광석 가공 및 분말 생산까지는 가능하지만, 현재 시대의 화로 온도로는 제련할 수 없도록 제한한다.

### 회전 동력

석기 시대에는 목재와 석재 기반 회전 부품만 사용할 수 있으며 기본 최대 회전속도는 **32 RPM**이다.

```text
Primitive Hand Crank
→ 초기 수동 동력

Primitive Water Wheel
→ 석기 시대 자동 동력

Wooden Shaft
Stone Cogwheel
Primitive Gearbox
Primitive Vertical Gearbox
→ 최대 32 RPM
```

Bronze Shaft, Bronze Bearing, Bronze Cogwheel을 제작하면 최초로 64 RPM 회전망을 구축할 수 있으며, 이를 브론즈 시대 진입 게이트로 사용한다.

### 아이템

| 아이템 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 식물 섬유 | `plant_fiber` | 끈과 밧줄 제작에 사용하는 기초 섬유 재료 | 잔디나 식물을 Flint Knife로 가공 |
| 끈 | `twine` | 도구 결속 및 원시 부품 제작용 | Plant Fiber ×3 |
| 밧줄 | `rope` | 수차, 벨트, 기계 구조물 제작용 | Twine ×3 |
| 나무 수지 | `resin` | 목재 기계 부품 접착 및 밀봉용 | 원목에 절삭 도구 사용 또는 수지 생산 수종에서 획득 |
| 가죽 스트립 | `leather_strip` | 풀무, 벨트, 결속 부품 제작용 | Leather + Flint Knife |
| 숯 가루 | `charcoal_dust` | 제련 및 탄소계 가공 재료 | Charcoal → Primitive Millstone |
| 재 | `ash` | 숯 생산 부산물. 후속 화학 및 비료 재료 | Charcoal Pit 부산물 |
| 미가공 도가니 | `unfired_crucible` | Fired Crucible 제작 전 점토 성형물 | Clay Ball ×5 |
| 미가공 주괴 주형 | `unfired_ingot_mold` | 금속 주조용 주형의 굽기 전 상태 | Clay Ball ×4 |
| 주괴 주형 | `ingot_mold` | 용융 금속을 주괴로 성형 | Unfired Ingot Mold → Pit Kiln |
| 미가공 내화 벽돌 | `unfired_fire_brick` | 고온 구조물 제작용 내화 벽돌의 굽기 전 상태 | Clay + Sand |
| 내화 벽돌 | `fire_brick` | 고온 화로 및 이후 고온 설비 제작 재료 | Unfired Fire Brick → Pit Kiln |
| 부싯돌 칼 | `flint_knife` | 식물, 가죽 및 섬유 절단용 수동 가공 도구 | Flint + Stick + Twine |
| 돌 망치 | `stone_hammer` | 광석 및 석재의 수동 파쇄 도구 | Stone Hammer Head + Stick + Twine |
| 돌 망치머리 | `stone_hammer_head` | Stone Hammer 제작용 중간 부품 | Stone 가공 |
| 돌 끌 | `stone_chisel` | 석재 및 목재 부품의 정밀 형상 가공 | Flint 또는 Stone + Stick + Twine |
| 나무 톱 | `wooden_saw` | Log, Plank 및 목재 부품을 양손 가공 방식으로 절단 | Plank + Flint + Twine |
| 광물 팬 | `ore_pan` | Create Crushed Ore를 물에서 수동 선광 | Wooden Bowl + Plank |
| 나무 집게 | `wooden_tongs` | 뜨거운 도가니 및 금속 관련 작업용 | Stick ×2 + Twine |
| 원시 체 | `primitive_sieve` | 광물, 모래, 자갈 등의 수동 분리용 | Stick + Twine |
| 나무 판재 | `wooden_plate` | 원시 기계 및 회전 부품 제작용 가공 목재 | Wooden Saw + Planks |
| 나무 기어 블랭크 | `wooden_gear_blank` | Stone Cogwheel 등 제작용 중간 목재 부품 | Wooden Saw + Wooden Plate |
| 돌 기어 부품 | `stone_cogwheel_item` | Stone Cogwheel 블록 제작용 부품 | Wooden Gear Blank + Stone + Stone Chisel |
| 대형 돌 기어 부품 | `large_stone_cogwheel_item` | Large Stone Cogwheel 제작용 | Stone Cogwheel Item + Plank + Rope |
| 나무 축 부품 | `wooden_shaft_item` | Wooden Shaft 블록 제작용 저속 축 부품 | Wooden Saw + Wooden Plate 또는 Plank |
| 나무 베어링 | `wooden_bearing` | 수차와 회전체 지지용 원시 부품 | Wooden Plate + Resin + Stone |
| 나무 기어박스 부품 | `wooden_gearbox_component` | Primitive Gearbox 제작용 중간 부품 | Wooden Plate + Wooden Shaft Item + Stone Cogwheel Item |
| 구리 정광 | `copper_concentrate` | 선광된 구리 원료 | Crushed Copper Ore → Ore Pan 또는 Primitive Sluice |
| 주석 정광 | `tin_concentrate` | 선광된 주석 원료 | Crushed Tin Ore → Ore Pan 또는 Primitive Sluice |
| 구리 분말 | `copper_dust` | Fired Crucible 제련용 구리 원료 | Copper Concentrate → Primitive Millstone |
| 주석 분말 | `tin_dust` | Fired Crucible 제련용 주석 원료 | Tin Concentrate → Primitive Millstone |
| 철 분말 | `iron_dust` | 후속 시대 제련용 철 재료. 석기 시대에는 제련 불가 | Iron Concentrate → Primitive Millstone |
| 석회석 분말 | `limestone_dust` | 제련용 Flux 및 후속 공정 재료 | Limestone → Primitive Millstone |
| 구리 조각 | `copper_fragment` | 저효율 선광 부산물 | Copper 계열 선광 공정 부산물 |
| 주석 조각 | `tin_fragment` | 저효율 선광 부산물 | Tin 계열 선광 공정 부산물 |
| 청동 주괴 | `bronze_ingot` | 석기 시대 최종 목표 재료 | Copper ×3 + Tin ×1 → Fired Crucible → Ingot Mold |
| 청동 너겟 | `bronze_nugget` | 소형 브론즈 부품 제작용 | Bronze Ingot 분해 |
| 청동 판재 | `bronze_plate` | 브론즈 회전 부품 및 기계 제작용 | Bronze Ingot 수동 가공 또는 주조 |
| 청동 축 | `bronze_shaft` | 64 RPM을 견디는 브론즈 시대 회전축 | Bronze 재료 가공 |
| 청동 베어링 | `bronze_bearing` | 64 RPM 회전체 제작용 핵심 부품 | Bronze + Wooden Bearing |
| 청동 기어 | `bronze_cogwheel` | 64 RPM 기어비 구성용 부품 | Bronze 재료 가공 |
| 청동 기어박스 부품 | `bronze_gearbox_component` | 브론즈 시대 Gearbox 및 Vertical Gearbox 제작에 사용하는 64 RPM용 핵심 중간 부품 | Bronze Plate + Bronze Shaft + Bronze Cogwheel + Bronze Bearing |

### 블록

| 블록 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 원시 작업대 | `primitive_workbench` | 석기 시대 기계 부품 및 복잡한 수동 제작에 사용하는 작업대 | Crafting Table + Stone + Plank |
| 구덩이 가마 | `pit_kiln` | Unfired Crucible, Ingot Mold, Fire Brick 등을 소성 | Clay + Stone 계열 재료 또는 월드 구조 방식 |
| 숯가마 | `charcoal_pit` | Log를 Charcoal과 Ash로 변환 | Log를 쌓고 흙/점토 등으로 밀폐 후 점화하거나 전용 블록 방식 |
| 건조대 | `drying_rack` | 가죽, 식물 섬유, 젖은 재료 등을 시간 기반으로 건조 및 가공 | Stick ×4 + Twine/Rope ×2 |
| 원시 맷돌 | `primitive_millstone` | 8~32 RPM으로 광석 정광, 숯, 곡물 등을 분쇄 | Stone + Wooden Shaft + Wooden Bearing + Wooden Plate |
| 원시 선광기 | `primitive_sluice` | 물을 이용해 Create Crushed Ore를 Concentrate로 정제 | Wooden Plate/Plank + Rope + Primitive Sieve |
| 손 크랭크 | `primitive_hand_crank` | 플레이어가 직접 저속 회전력을 공급 | Wooden Shaft Item + Stick + Stone Cogwheel Item |
| 원시 수차 | `primitive_water_wheel` | 물을 이용해 최대 32 RPM 회전력을 생성 | Wooden Plate/Plank + Wooden Bearing + Rope + Wooden Shaft |
| 원시 기어박스 | `primitive_gearbox` | 회전 방향 변경 및 동력 분배. 최대 32 RPM | Wooden Gearbox Component + Stone Cogwheel Item |
| 수직 기어박스 | `primitive_vertical_gearbox` | 수평 회전축과 수직 회전축 사이에서 회전 방향을 90° 전환. 최대 32 RPM | Wooden Gearbox Component + Stone Cogwheel Item + Wooden Shaft Item |
| 나무 축 | `wooden_shaft` | 석기 시대 저속 회전력 전달. 최대 32 RPM | Wooden Shaft Item |
| 돌 톱니바퀴 | `stone_cogwheel` | 저속 회전 전달 및 기어비 구성. 최대 32 RPM | Stone Cogwheel Item |
| 대형 돌 톱니바퀴 | `large_stone_cogwheel` | 2:1 기어비 구성용 대형 기어. 최대 32 RPM | Large Stone Cogwheel Item |
| 수동 풀무 | `bellows` | 플레이어가 직접 조작하여 Fired Crucible의 가열 효율 또는 온도를 증가 | Wooden Plate/Plank + Leather Strip + Rope |
| 기계식 풀무 | `mechanical_bellows` | 8~32 RPM 회전력을 사용해 지속적인 송풍 제공 | Bellows + Wooden Shaft + Stone Cogwheel + Wooden Bearing |
| 도가니 | `fired_crucible` | Copper, Tin, Bronze 등의 금속을 용융 및 합금하는 설치형 블록 | Unfired Crucible → Pit Kiln |
| 주조대 | `casting_table` | Fired Crucible에서 용융한 금속을 Mold를 사용해 주괴 및 부품으로 성형 | Stone/Stone Slab + Wooden Plate + Ingot Mold |
| 원시 저장통 | `wooden_bin` | 광물, 분말, 숯 등의 고체 아이템 저장 | Wooden Plate/Plank |
| 원시 액체통 | `wooden_tank` | 물과 기타 저온 액체 저장 | Wooden Plate/Plank + Rope + Resin |
| 나무 수로 | `wooden_channel` | Primitive Sluice 등에 물 공급 | Wooden Plate/Plank + Resin |
| 광석 세척통 | `washing_trough` | Ore Pan보다 빠른 반수동 선광 설비 | Wooden Plate/Plank + Primitive Sieve |
| 내화 벽돌 블록 | `fire_bricks` | 고온 설비 및 후속 시대 화로 구조 재료 | Fire Brick ×4 |
| 원시 화로 | `primitive_furnace` | 음식, 유리, 일반 가열용. 금속 광석 직접 제련 불가 | Stone/Cobblestone |
| 원시 모루 | `primitive_anvil` | Copper와 Bronze를 Plate 및 간단한 부품으로 수동 가공 | Stone Block + Smooth Stone |

### 석기 시대 회전 부품 제한

| 부품 | 최대 RPM | 역할 |
|---|---:|---|
| `wooden_shaft` | 32 RPM | 기본 회전축 |
| `stone_cogwheel` | 32 RPM | 기어비 변경 |
| `large_stone_cogwheel` | 32 RPM | 2:1 증속/감속 |
| `primitive_gearbox` | 32 RPM | 방향 변경 및 동력 분배 |
| `primitive_vertical_gearbox` | 32 RPM | 수평/수직 축 방향 전환 |
| `primitive_millstone` | 32 RPM | 원시 자동 분쇄 |
| `mechanical_bellows` | 32 RPM | 원시 자동 송풍 |

### 브론즈 시대 진입 조건

석기 시대의 최종 목표는 Bronze를 생산하고 64 RPM 회전망을 구축하는 것이다.

필수 진행 아이템은 다음과 같다.

```text
bronze_ingot
bronze_plate
bronze_shaft
bronze_bearing
bronze_cogwheel
bronze_gearbox_component
```

Bronze 부품을 사용하면 기존 32 RPM 제한을 넘어설 수 있다.

```text
Primitive Water Wheel
↓
32 RPM
↓
Bronze Shaft / Bronze Cogwheel
↓
2:1 증속
↓
64 RPM
↓
브론즈 시대 진입
```

석기 시대에서 직접 사용할 수 있는 금속은 Copper, Tin, Bronze 정도로 제한하고, Iron과 그 이상의 금속은 후속 시대의 고온 제련 설비를 필요로 하도록 구성한다.
