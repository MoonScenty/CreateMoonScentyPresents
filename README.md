# Create: MoonScenty Presents

- Create Addon
- Create 여러가지 부분들을 해석하여 MoonScenty가 원하는 컨텐츠로 만들어내는 모드입니다.
- Create 말고도 일부 모드에 의존성이 있을 수 있습니다.
- 석기, 브론즈, 스틸, 스테인리스 스틸, 티타늄 시대로 나뉩니다.
- 현재는 석기 컨텐츠 내용만 포함되어 있습니다.


## 석기 시대

### 시대 개요

석기 시대는 금속 기반 산업에 진입하기 전 단계로, 돌·목재·점토·섬유 등의 원시 재료를 이용하여 기본 도구와 저속 회전 기계, 광물 선광 및 원시 제련 기술을 확보하는 시대이다.

이 시대에서는 별도의 `chopping_block`, `crushing_table` 같은 수동 작업 블록을 사용하지 않는다. 대신 **도구와 가공 대상 아이템을 양손에 들고 서로 비비는 방식의 수동 아이템 프로세싱**을 기본 가공 방식으로 사용한다.

이 방식은 Create의 Polishing과 유사한 구조로 동작하며, 석기 시대의 주요 수동 가공 시스템으로 사용한다.

석기 시대의 최종 목표는 다음과 같다.

- 기본 석기 및 목재 가공 도구 확보
- 식물 섬유 → 끈 → 밧줄 가공
- 점토 기반 도가니 및 주형 제작
- 숯 생산
- 32 RPM 회전망 구축
- Copper / Tin 광석 처리
- 원시 제련
- Bronze 생산
- Bronze 회전 부품 제작
- 64 RPM 달성
- 브론즈 시대 진입

석기 시대 회전 부품의 최대 속도는 기본적으로 **32 RPM**으로 제한한다.

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
Create 기존 Crushed Ore
↓
Primitive Sifter
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
Bronze Cogwheel / Bronze Bearing / Bronze Gearbox Component 제작
↓
64 RPM 회전망 구축
↓
브론즈 시대
```

### 수동 아이템 프로세싱

석기 시대의 일부 가공은 별도의 작업 블록 없이 플레이어가 직접 수행한다.

플레이어가 가공 도구와 가공 대상 아이템을 양손에 들고 상호작용하면 일정 시간 동안 아이템 프로세싱이 진행된다.

```text
Main Hand : 가공 대상
Off Hand  : 가공 도구

또는

Main Hand : 가공 도구
Off Hand  : 가공 대상

↓
지속 상호작용
↓
가공 진행
↓
입력 아이템 소비
↓
결과 아이템 생성
```

도구는 레시피에 따라 내구도를 소모할 수 있다.

| 도구 | 입력 | 출력 | 기능 |
|---|---|---|---|
| Wooden Saw | Log | Planks | 목재 기본 제재 |
| Stone Hammer | Raw Ore | Create Crushed Ore | 광석 수동 파쇄 |
| Stone Hammer | Stone | Gravel / Stone Dust | 석재 파쇄 |
| Stone Chisel | Stone / Wooden Part | 가공 부품 | 형상 가공 |
| Flint Knife | Plant / Leather | Plant Fiber / Leather Strip | 절단 가공 |

### 목재 가공

석기 시대의 목재 가공은 `wooden_saw`를 사용한다.

별도의 Chopping Block은 사용하지 않는다.

```text
Wooden Saw + Log
→ Planks
```

필요한 회전 부품은 별도의 `wooden_plate`, `wooden_gear_blank` 같은 중간 아이템을 만들지 않고 기존 재료에서 직접 제작한다.

예시:

```text
Planks
+ Stone
+ Stone Chisel
→ Stone Cogwheel
```

```text
Planks / Stripped Log
+ Wooden Saw
→ Wooden Shaft
```

브론즈 시대 이후에는 Mechanical Saw를 이용해 동일한 목재 가공을 더 빠르고 높은 효율로 자동화할 수 있도록 한다.

### 광석 가공

광석은 바닐라 Furnace에 직접 넣어 Ingot으로 만들 수 없도록 한다.

광석 파쇄 결과물은 별도의 `crushed_*_ore` 아이템을 추가하지 않고 **Create 모드에 이미 존재하는 Crushed Ore 계열 아이템을 그대로 사용한다.**

Bronze 합금에 필요한 Tin은 바닐라와 Create 어느 쪽에도 없으므로 이 모드가 직접 추가하며, **`tin_ore`와 `deepslate_tin_ore`를 월드 생성으로 배치한다.** Copper는 바닐라 광석을 그대로 사용한다.

기본 광석 처리 흐름은 다음과 같다.

```text
Raw Ore
↓
Stone Hammer
↓
Create Crushed Ore
↓
Primitive Sifter
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

### Primitive Sifter

`primitive_sifter`는 석기 시대의 자동 선광 블록이다.

Create의 Crushed Ore를 입력받아 Ore Concentrate 및 부산물로 분리한다.

`primitive_sieve`는 독립적인 가공 도구가 아니라 **Primitive Sifter 제작에 사용하는 내부 체망 중간 부품**으로 사용한다.

```text
Stick / Plank
+ Twine
→ Primitive Sieve
```

```text
Primitive Sieve
+ Planks
+ Rope
+ Wooden Components
→ Primitive Sifter
```

Primitive Sifter는 최대 32 RPM에서 동작한다.

### 건조 가공

`drying_rack`은 회전 동력을 사용하지 않는 시간 기반 가공 블록이다.

초기 가죽, 섬유, 젖은 재료 등을 자연 건조하는 용도로 사용한다.

예시:

```text
Wet Hide
↓
Drying Rack
↓
Leather
```

```text
Wet Plant Material
↓
Drying Rack
↓
Dried Material
```

후속 시대에서는 동일한 가공을 더 빠른 기계 공정으로 대체할 수 있다.

### 원시 제련

석기 시대의 금속은 일반 Furnace에서 직접 제련하지 않는다.

Copper와 Tin은 광석 처리 후 Dust 상태로 만든 뒤 Fired Crucible에서 용융한다.

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

Bronze는 Copper와 Tin을 도가니 내부에서 합금하여 생산한다.

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

Iron은 석기 시대에 광석 처리 및 Dust 생산까지는 가능하지만, 현재 시대의 열원으로는 제련할 수 없도록 제한한다.

### 회전 동력

석기 시대는 목재와 석재 기반의 저속 회전 부품을 사용한다.

기본 최대 속도는 **32 RPM**이다.

```text
Primitive Hand Crank
→ 초기 수동 동력

Primitive Water Wheel
→ 석기 시대 자동 동력
```

회전 전달에는 다음 블록을 사용한다.

```text
Wooden Shaft
Stone Cogwheel
Large Stone Cogwheel
Primitive Gearbox
Primitive Vertical Gearbox
```

32 RPM을 초과하면 다음 중 하나의 방식으로 제한한다.

- 회전 전달 중단
- Overspeed 상태 발생
- 일정 시간 후 파손
- 즉시 파손

브론즈 시대 부품을 확보하면 64 RPM 회전망을 구축할 수 있다.

### 아이템

| 아이템 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 식물 섬유 | `plant_fiber` | 끈과 밧줄 제작에 사용하는 기초 섬유 재료 | 잔디 또는 식물을 Flint Knife로 가공 |
| 끈 | `twine` | 도구 결속 및 원시 부품 제작용 | Plant Fiber ×3 |
| 밧줄 | `rope` | 수차, 기계 구조물, 각종 결속 부품 제작용 | Twine ×3 |
| 나무 수지 | `resin` | 목재 부품 접착 및 밀봉용 | 원목 가공 또는 수지 생산 수종에서 획득 |
| 가죽 스트립 | `leather_strip` | 풀무 및 기계 부품 제작용 | Leather + Flint Knife |
| 숯 가루 | `charcoal_dust` | 탄소계 가공 재료 | Charcoal → Primitive Millstone |
| 재 | `ash` | 숯 생산 부산물, 후속 화학/비료 재료 | Charcoal Pit 부산물 |
| 미가공 도가니 | `unfired_crucible` | Fired Crucible 제작 전 점토 성형물 | Clay Ball ×5 |
| 미가공 주괴 주형 | `unfired_ingot_mold` | 주괴 주형의 굽기 전 상태 | Clay Ball ×4 |
| 주괴 주형 | `ingot_mold` | 용융 금속을 주괴 형태로 성형 | Unfired Ingot Mold → Pit Kiln |
| 미가공 내화 벽돌 | `unfired_fire_brick` | 고온 설비 제작용 벽돌의 굽기 전 상태 | Clay + Sand |
| 내화 벽돌 | `fire_brick` | 도가니 및 고온 설비 제작 재료 | Unfired Fire Brick → Pit Kiln |
| 부싯돌 칼 | `flint_knife` | 식물, 가죽, 섬유 절단용 도구 | Flint + Stick + Twine |
| 돌 망치 | `stone_hammer` | 광석 및 석재를 수동 파쇄 | Stone Hammer Head + Stick + Twine |
| 돌 망치머리 | `stone_hammer_head` | Stone Hammer 제작용 중간 부품 | Stone 가공 |
| 돌 끌 | `stone_chisel` | 석재 및 목재 부품 형상 가공 | Flint 또는 Stone + Stick + Twine |
| 나무 톱 | `wooden_saw` | Log 및 목재 재료의 수동 가공 | Plank + Flint + Twine |
| 나무 집게 | `wooden_tongs` | 뜨거운 도가니 및 주조 작업 보조 | Stick ×2 + Twine |
| 원시 체 | `primitive_sieve` | Primitive Sifter 제작용 내부 체망 부품 | Stick/Plank + Twine |
| 나무 베어링 | `wooden_bearing` | 회전체를 지지하는 원시 부품. 수차, 맷돌, 풀무에 사용 | Plank + Resin + Stone |
| 나무 기어박스 부품 | `wooden_gearbox_component` | Primitive Gearbox / Vertical Gearbox 제작용 중간 부품 | Plank + Stone Cogwheel + Wooden Shaft |
| 주석 원석 | `raw_tin` | 주석 광석에서 채굴한 원석 | Tin Ore 채굴 |
| 주석 주괴 | `tin_ingot` | Bronze 합금용 금속. 바닐라와 Create에 없어 이 모드가 추가 | Tin Dust → Fired Crucible → Ingot Mold |
| 구리 정광 | `copper_concentrate` | 선광된 구리 제련 원료 | Create Crushed Copper → Primitive Sifter |
| 주석 정광 | `tin_concentrate` | 선광된 주석 제련 원료 | Create Crushed Tin → Primitive Sifter |
| 구리 분말 | `copper_dust` | Fired Crucible 제련용 구리 원료 | Copper Concentrate → Primitive Millstone |
| 주석 분말 | `tin_dust` | Fired Crucible 제련용 주석 원료 | Tin Concentrate → Primitive Millstone |
| 철 분말 | `iron_dust` | 후속 시대 제련용 철 원료 | Iron Concentrate → Primitive Millstone |
| 석회석 분말 | `limestone_dust` | Flux 및 후속 제련 공정 재료 | Limestone → Primitive Millstone |
| 구리 조각 | `copper_fragment` | 선광 공정 부산물 | Copper 선광 부산물 |
| 주석 조각 | `tin_fragment` | 선광 공정 부산물 | Tin 선광 부산물 |
| 청동 주괴 | `bronze_ingot` | 석기 시대 최종 핵심 재료 | Copper ×3 + Tin ×1 → Fired Crucible → Ingot Mold |
| 청동 너겟 | `bronze_nugget` | 소형 브론즈 부품 제작용 | Bronze Ingot 분해 |
| 청동 판재 | `bronze_plate` | 브론즈 기계 및 회전 부품 제작용 | Bronze Ingot 수동 가공 또는 주조 |
| 청동 베어링 | `bronze_bearing` | 64 RPM 회전체 제작용 핵심 부품 | Bronze + Wooden Bearing |
| 청동 기어박스 부품 | `bronze_gearbox_component` | 브론즈 시대 Gearbox / Vertical Gearbox 제작용 핵심 중간 부품 | Bronze Plate + Bronze Bearing + Bronze Cogwheel 계열 재료 |

### 블록

| 블록 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 원시 작업대 | `primitive_workbench` | 석기 시대 기계 부품 및 복잡한 수동 제작용 작업대 | Crafting Table + Stone + Plank |
| 건조대 | `drying_rack` | 가죽, 섬유, 젖은 재료 등을 시간 기반으로 건조 | Stick + Twine/Rope |
| 구덩이 가마 | `pit_kiln` | Unfired Crucible, Ingot Mold, Fire Brick 등을 소성 | Clay + Stone 계열 재료 또는 월드 구조 방식 |
| 숯가마 | `charcoal_pit` | Log를 Charcoal과 Ash로 변환 | Log 밀폐 구조 또는 전용 블록 방식 |
| 원시 맷돌 | `primitive_millstone` | 8~32 RPM에서 광물 정광, 숯, 곡물 등을 분쇄 | Stone + Wooden Shaft + Wooden Bearing + Plank |
| 원시 선광기 | `primitive_sifter` | Create Crushed Ore를 Ore Concentrate 및 부산물로 분리 | Primitive Sieve + Plank + Rope + Wooden Components |
| 손 크랭크 | `primitive_hand_crank` | 플레이어가 직접 저속 회전력을 공급 | Stick + Wooden Shaft + Stone Cogwheel |
| 원시 수차 | `primitive_water_wheel` | 물을 이용해 최대 32 RPM 회전력을 생성 | Plank + Wooden Bearing + Rope + Wooden Shaft |
| 원시 기어박스 | `primitive_gearbox` | 수평 회전 방향 변경 및 동력 분배, 최대 32 RPM | Plank + Stone Cogwheel + Wooden Shaft |
| 원시 수직 기어박스 | `primitive_vertical_gearbox` | 수평 회전축과 수직 회전축 사이에서 회전 방향을 90° 전환, 최대 32 RPM | Plank + Stone Cogwheel + Wooden Shaft |
| 나무 축 | `wooden_shaft` | 석기 시대 저속 회전력 전달, 최대 32 RPM | Plank / Stripped Log + Wooden Saw |
| 돌 톱니바퀴 | `stone_cogwheel` | 저속 회전 전달 및 기어비 구성, 최대 32 RPM | Plank + Stone + Stone Chisel |
| 대형 돌 톱니바퀴 | `large_stone_cogwheel` | 2:1 기어비 구성용 대형 기어, 최대 32 RPM | Stone Cogwheel + Plank + Rope |
| 수동 풀무 | `bellows` | 플레이어가 직접 조작하여 Fired Crucible의 온도 상승 | Plank + Leather Strip + Rope |
| 기계식 풀무 | `mechanical_bellows` | 8~32 RPM에서 자동 송풍 | Bellows + Wooden Shaft + Stone Cogwheel + Wooden Bearing |
| 도가니 | `fired_crucible` | Copper, Tin, Bronze 등의 금속 용융 및 합금 | Unfired Crucible → Pit Kiln |
| 주조대 | `casting_table` | 용융 금속을 Ingot Mold 또는 주형에 부어 성형 | Stone/Slab + Plank + Ingot Mold |
| 원시 저장통 | `wooden_bin` | 광물, 분말, 숯 등의 고체 아이템 저장 | Plank |
| 원시 액체통 | `wooden_tank` | 물과 기타 저온 액체 저장 | Plank + Rope + Resin |
| 나무 수로 | `wooden_channel` | Primitive Sifter 등에 물 공급 | Plank + Resin |
| 광석 세척통 | `washing_trough` | Ore Pan보다 빠른 반수동 광석 세척 | Plank + 물 공급 구조 |
| 내화 벽돌 블록 | `fire_bricks` | 고온 설비 및 후속 시대 화로 구조 재료 | Fire Brick ×4 |
| 원시 화로 | `primitive_furnace` | 음식, 유리, 일반 가열용. 금속 광석 직접 제련 불가 | Stone / Cobblestone |
| 원시 모루 | `primitive_anvil` | Copper / Bronze를 Plate 및 간단한 부품으로 가공 | Stone Block + Smooth Stone |
| 주석 광석 | `tin_ore` | 주석 원석을 채굴할 수 있는 광석 | 월드 생성 |
| 심층암 주석 광석 | `deepslate_tin_ore` | 심층암 지대의 주석 광석 | 월드 생성 |
| 주석 원석 블록 | `raw_tin_block` | 주석 원석 보관용 압축 블록 | Raw Tin ×9 |
| 주석 블록 | `tin_block` | 주석 주괴 보관용 압축 블록 | Tin Ingot ×9 |
| 청동 축 | `bronze_shaft` | 64 RPM 회전망의 회전력 전달용 브론즈 회전축 | Bronze 계열 재료 가공 |
| 청동 톱니바퀴 | `bronze_cogwheel` | 64 RPM 회전망의 기어비 구성용 브론즈 회전 블록 | Bronze 계열 재료 + Stone Cogwheel 또는 직접 주조 |

### 석기 시대 회전 부품 제한

| 블록 | 최대 RPM | 역할 |
|---|---:|---|
| `wooden_shaft` | 32 RPM | 기본 회전축 |
| `stone_cogwheel` | 32 RPM | 기어비 변경 |
| `large_stone_cogwheel` | 32 RPM | 2:1 증속 / 감속 |
| `primitive_gearbox` | 32 RPM | 수평 방향 변경 및 동력 분배 |
| `primitive_vertical_gearbox` | 32 RPM | 수평 / 수직 축 방향 전환 |
| `primitive_millstone` | 32 RPM | 원시 자동 분쇄 |
| `primitive_sifter` | 32 RPM | 원시 자동 선광 |
| `mechanical_bellows` | 32 RPM | 자동 송풍 |

### 브론즈 시대 진입 조건

석기 시대의 최종 목표는 Bronze를 생산하고 **64 RPM 회전망을 구축하는 것**이다.

핵심 진행 요소는 다음과 같다.

```text
bronze_ingot
bronze_plate
bronze_shaft
bronze_bearing
bronze_cogwheel
bronze_gearbox_component
```

Bronze 계열 회전 부품을 사용하면 석기 시대의 32 RPM 한계를 넘어설 수 있다.

```text
Primitive Water Wheel
↓
32 RPM
↓
Bronze Cogwheel / Bronze Gearbox 계열
↓
2:1 증속
↓
64 RPM
↓
브론즈 시대 진입
```

석기 시대에서 직접 제련 가능한 금속은 Copper, Tin, Bronze 정도로 제한한다.

Iron 및 그 이상의 금속은 브론즈 시대 이후의 고온 제련 설비를 요구하도록 구성한다.
