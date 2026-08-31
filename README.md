# Create: MoonScenty Presents

- Create Addon
- Create 여러가지 부분들을 해석하여 MoonScenty가 원하는 컨텐츠로 만들어내는 모드입니다.
- Create 말고도 일부 모드에 의존성이 있을 수 있습니다.
- 석기, 브론즈, 스틸, 스테인리스 스틸, 티타늄 시대로 나뉩니다.
- 현재는 석기 컨텐츠 내용만 포함되어 있습니다.


## Stone Age Content List

석기시대에서 사용하는 아이템 및 블록 정의 초안입니다.

- Registry ID 기준 네임스페이스는 생략했습니다.
- 예: `plant_fiber` → `modid:plant_fiber`
- 제작 방법은 구현 편의를 위해 재료 중심으로 작성했습니다.
- 수량 및 처리 시간은 밸런싱 단계에서 조정하는 것을 전제로 합니다.
- 석기시대 회전 부품의 기본 최대 속도는 **32 RPM**을 기준으로 합니다.
- 브론즈 부품부터 **64 RPM**을 허용하여 브론즈 시대 진입 게이트로 사용합니다.

### 아이템

| 아이템 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 식물 섬유 | `plant_fiber` | 끈과 밧줄 제작에 사용하는 가장 기초적인 섬유 재료 | 잔디, 키 큰 풀, 일부 작물을 Flint Knife로 절단 시 획득 |
| 끈 | `twine` | 도구 결속, 가죽 가공, 원시 기계 제작용 중간 재료 | Plant Fiber ×3 → Twine ×1 |
| 밧줄 | `rope` | 수차, 벨트, 기계 프레임 등 하중을 받는 원시 부품 제작 | Twine ×3 → Rope ×1 |
| 나무 수지 | `resin` | 목재 기계 부품의 접착 및 밀봉용 재료 | 원목에 Flint Knife 또는 별도 수지 채취 도구 사용 |
| 가죽 스트립 | `leather_strip` | 풀무, 원시 벨트, 기계 결속 부품 제작 | Leather ×1 + Flint Knife → Leather Strip ×3~4 |
| 숯 가루 | `charcoal_dust` | 원시 제련, 탄소 재료, 후속 혼합 공정용 | Charcoal → Primitive Millstone 또는 Mortar로 분쇄 |
| 재 | `ash` | 숯 생산 부산물. 비료 및 후속 화학 재료로 사용 가능 | Charcoal Pit 사용 시 부산물로 획득 |
| 미가공 도가니 | `unfired_crucible` | 굽기 전 상태의 점토 도가니 | Clay Ball ×5 → Unfired Crucible |
| 도가니 | `fired_crucible` | Copper, Tin, Bronze 등을 용융하는 원시 제련 용기 | Unfired Crucible → Pit Kiln |
| 미가공 주괴 주형 | `unfired_ingot_mold` | 금속 주괴 주조용 주형의 굽기 전 상태 | Clay Ball ×4 → Unfired Ingot Mold |
| 주괴 주형 | `ingot_mold` | 용융 금속을 주괴 형태로 굳히는 주조 도구 | Unfired Ingot Mold → Pit Kiln |
| 미가공 내화 벽돌 | `unfired_fire_brick` | 고온 화로 제작에 필요한 벽돌의 굽기 전 상태 | Clay Ball ×2 + Sand ×1 → Unfired Fire Brick ×2 |
| 내화 벽돌 | `fire_brick` | Crucible Furnace 및 고온 구조물 제작 재료 | Unfired Fire Brick → Pit Kiln |
| 돌 기어 | `stone_cogwheel_item` | 석기시대 저속 기어 부품. 최대 32 RPM | Stone/Cobblestone + Wooden Plate/Plank + Stone Chisel |
| 대형 돌 기어 | `large_stone_cogwheel_item` | 기어비 변경용 대형 기어 부품. 최대 32 RPM | Stone Cogwheel ×1 + Plank ×4 + Rope ×2 |
| 나무 축 | `wooden_shaft_item` | 저속 회전력 전달용 축. 최대 32 RPM | Stripped Log/Plank + Wooden Saw + Stone Chisel |
| 나무 베어링 | `wooden_bearing` | 수차와 회전체의 지지 부품 | Plank ×4 + Resin ×1 + Stone Ring/Stone ×1 |
| 원시 벨트 | `primitive_belt_item` | 두 회전축 사이의 저속 동력 전달용 | Rope ×2 + Leather Strip ×2 |
| 나무 기어박스 부품 | `wooden_gearbox_component` | Primitive Gearbox 제작용 핵심 중간 부품 | Wooden Shaft ×1 + Stone Cogwheel ×1 + Plank ×4 |
| 부싯돌 칼 | `flint_knife` | 식물 섬유, 가죽, 밧줄 등의 절단 작업 | Flint ×1 + Stick ×1 + Twine ×1 |
| 돌 망치 | `stone_hammer` | 광석 수동 분쇄 및 일부 조립 레시피용 도구 | Stone Hammer Head ×1 + Stick ×1 + Twine ×1 |
| 돌 망치머리 | `stone_hammer_head` | Stone Hammer 제작용 부품 | Stone ×2 + Stone Chisel |
| 돌 끌 | `stone_chisel` | 석재와 목재의 정밀 가공용 도구 | Flint/Stone ×1 + Stick ×1 + Twine ×1 |
| 나무 톱 | `wooden_saw` | 통나무 및 판재 가공 효율을 높이는 도구 | Plank ×2 + Flint ×2 + Twine ×1 |
| 광물 팬 | `ore_pan` | 분쇄 광석을 물에서 수동 선광 | Bowl/Wooden Bowl ×1 + Plank ×2 |
| 나무 집게 | `wooden_tongs` | 고온 도가니 및 주형을 취급하는 도구 | Stick ×2 + Twine ×1 |
| 원시 체 | `primitive_sieve` | 모래, 자갈, 분쇄 광물을 수동 분리 | Stick ×4 + Twine ×4 |
| 분쇄 구리 광석 | `crushed_copper_ore` | 구리 광석의 1차 가공물 | Raw Copper/Copper Ore + Stone Hammer |
| 분쇄 주석 광석 | `crushed_tin_ore` | 주석 광석의 1차 가공물 | Raw Tin/Tin Ore + Stone Hammer |
| 구리 정광 | `copper_concentrate` | 불순물을 제거한 구리 제련 원료 | Crushed Copper Ore + Ore Pan 또는 Primitive Sluice |
| 주석 정광 | `tin_concentrate` | 불순물을 제거한 주석 제련 원료 | Crushed Tin Ore + Ore Pan 또는 Primitive Sluice |
| 구리 분말 | `copper_dust` | 도가니에서 용융 가능한 구리 원료 | Copper Concentrate → Primitive Millstone |
| 주석 분말 | `tin_dust` | 도가니에서 용융 가능한 주석 원료 | Tin Concentrate → Primitive Millstone |
| 철 분말 | `iron_dust` | 석기시대에는 획득 가능하지만 제련은 불가능하도록 사용하는 후속 시대 재료 | Iron Concentrate → Primitive Millstone |
| 석회석 분말 | `limestone_dust` | 제련 플럭스 및 후속 공정 재료 | Limestone → Primitive Millstone |
| 구리 조각 | `copper_fragment` | 저효율 광석 세척에서 얻는 소량 구리 재료 | Gravel/Sand 또는 Copper 계열 선광 부산물 |
| 주석 조각 | `tin_fragment` | 저효율 광석 세척에서 얻는 소량 주석 재료 | Gravel/Sand 또는 Tin 계열 선광 부산물 |
| 청동 주괴 | `bronze_ingot` | 석기시대 최종 목표 재료. 브론즈 시대 진입 핵심 아이템 | Copper 3 + Tin 1을 Crucible Furnace에서 용융 후 Ingot Mold에 주조 |
| 청동 너겟 | `bronze_nugget` | 작은 브론즈 부품 및 이후 Create 계열 레시피용 | Bronze Ingot ×1 → Bronze Nugget ×9 |
| 청동 판재 | `bronze_plate` | 브론즈 기계 외장과 기계 부품 제작 | 초기에는 Bronze Ingot + Stone Hammer/Primitive Anvil 가공, 이후 브론즈 시대 Press 사용 |
| 청동 축 | `bronze_shaft` | 64 RPM을 견디는 최초의 고급 회전축 | Bronze Ingot/Plate + Casting |
| 청동 베어링 | `bronze_bearing` | 64 RPM 회전체와 브론즈 시대 기계 제작용 핵심 부품 | Bronze Ring/Plate + Wooden Bearing + Resin |
| 청동 기어 | `bronze_cogwheel` | 64 RPM 기어비 구성용 브론즈 부품 | Bronze Plate/Ingot + Wooden Gear Core 또는 Casting |
| 청동 기계 프레임 | `bronze_machine_frame` | 브론즈 시대 첫 기계들의 공통 베이스 부품 | Bronze Plate ×4 + Bronze Bearing ×1 + Wooden Frame/Plank ×4 |

### 블록

| 블록 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 건조대 | `drying_rack` | 가죽, 식물, 젖은 재료를 시간 기반으로 건조 | Stick ×4 + Rope/Twine ×2 |
| 구덩이 가마 | `pit_kiln` | Unfired Crucible, Mold, Fire Brick 등을 저온 소성 | Clay ×4 + Stone/Cobblestone ×4 또는 월드 구조형으로 제작 |
| 숯가마 | `charcoal_pit` | 목재를 밀폐 상태에서 Charcoal과 Ash로 변환 | 별도 아이템 제작 없이 Log 더미 + 흙/점토 밀폐 구조 또는 전용 블록 제작 |
| 원시 맷돌 | `primitive_millstone` | 8~32 RPM에서 광물, 숯, 곡물 등을 분쇄 | Stone ×5 + Wooden Shaft ×1 + Wooden Bearing ×1 + Plank ×2 |
| 원시 선광기 | `primitive_sluice` | 물과 중력을 이용해 Crushed Ore를 Concentrate로 정제 | Plank ×6 + Rope ×2 + Primitive Sieve ×1 |
| 손 크랭크 | `primitive_hand_crank` | 플레이어가 직접 회전력을 공급. 약 16 RPM의 낮은 Stress Capacity 제공 | Stick ×2 + Wooden Shaft ×1 + Stone Cogwheel ×1 |
| 원시 수차 | `primitive_water_wheel` | 흐르는 물을 이용해 최대 32 RPM의 자동 회전력을 생성 | Plank ×8 + Wooden Bearing ×1 + Rope ×4 + Wooden Shaft ×1 |
| 원시 기어박스 | `primitive_gearbox` | 회전축의 방향을 변경하고 여러 축으로 동력을 분배. 최대 32 RPM | Wooden Gearbox Component ×1 + Plank ×4 + Stone Cogwheel ×2 |
| 나무 축 블록 | `wooden_shaft` | 석기시대 회전 동력 전달. 32 RPM 초과 시 파손 또는 동작 정지 | Wooden Shaft Item ×1 |
| 돌 톱니바퀴 | `stone_cogwheel` | 저속 회전 전달 및 기어비 구성. 최대 32 RPM | Stone Cogwheel Item ×1 |
| 대형 돌 톱니바퀴 | `large_stone_cogwheel` | 2:1 기어비 구성용 대형 기어. 최대 32 RPM | Large Stone Cogwheel Item ×1 |
| 원시 벨트 | `primitive_belt` | 서로 떨어진 두 축 사이에서 회전력을 전달. 최대 32 RPM | Primitive Belt Item을 두 회전축 사이에 사용 |
| 풀무 | `bellows` | 플레이어가 수동으로 조작하여 Crucible Furnace의 온도를 높임 | Plank ×4 + Leather Strip ×4 + Rope ×2 |
| 기계식 풀무 | `mechanical_bellows` | 8~32 RPM 회전 동력을 받아 Crucible Furnace에 지속적으로 공기를 공급 | Bellows ×1 + Wooden Shaft ×1 + Stone Cogwheel ×1 + Wooden Bearing ×1 |
| 도가니 화로 | `crucible_furnace` | Charcoal과 Bellows를 사용해 Copper, Tin, Bronze 등을 용융 | Fire Brick ×6 + Stone ×2 + Fired Crucible ×1 |
| 도가니 블록 | `crucible` | 용융 금속을 저장하고 합금하는 화로 부속 블록 | Fired Crucible ×1 |
| 주조대 | `casting_table` | Crucible의 Molten Metal을 Mold에 부어 주괴/부품으로 성형 | Stone Slab ×3 + Plank ×2 + Ingot Mold ×1 |
| 원시 저장통 | `wooden_bin` | 광석, 분말, 숯 등 고체 아이템을 소량 저장 | Plank ×6 + Slab ×2 |
| 원시 액체통 | `wooden_tank` | 물 등 비가열 액체를 소량 저장 | Plank ×6 + Rope ×2 + Resin ×1 |
| 나무 수로 | `wooden_channel` | Primitive Sluice와 기타 장치에 물을 전달 | Plank ×3 + Resin ×1 |
| 광석 세척통 | `washing_trough` | Ore Pan보다 빠르게 광석을 수동 세척하는 중간 단계 설비 | Plank ×5 + Wooden Channel ×1 + Primitive Sieve ×1 |
| 내화 벽돌 블록 | `fire_bricks` | Crucible Furnace와 이후 고온 멀티블록의 구조 재료 | Fire Brick ×4 → Fire Bricks Block ×1 |
| 원시 화로 | `primitive_furnace` | 음식, 벽돌, 유리, 일반 가열 전용. 금속 광석 직접 제련은 불가 | Cobblestone/Stone ×8 |
| 원시 모루 | `primitive_anvil` | Copper/Bronze Ingot을 수동으로 Plate 및 간단한 부품으로 가공 | Stone Block ×3 + Smooth Stone ×3 |
| 브론즈 기어박스 | `bronze_gearbox` | 64 RPM 회전망의 시작점. 브론즈 시대 진입용 동력 전달 블록 | Bronze Plate ×4 + Bronze Cogwheel ×2 + Bronze Shaft ×1 + Bronze Bearing ×1 |

### 석기시대 기본 가공 흐름

```text
Raw Ore
↓
Stone Hammer
↓
Crushed Ore
↓
Ore Pan / Primitive Sluice
↓
Ore Concentrate
↓
Primitive Millstone
↓
Metal Dust
↓
Crucible Furnace + Charcoal + Bellows
↓
Molten Metal
↓
Casting Table + Ingot Mold
↓
Metal Ingot
```

#### 브론즈 제작

```text
Copper Dust / Copper
×3

+

Tin Dust / Tin
×1

↓
Crucible Furnace
↓
Molten Bronze
↓
Casting Table
↓
Bronze Ingot
```

## 시대 진입 게이트

### 석기시대

- Wooden Shaft 최대 속도: **32 RPM**
- Stone Cogwheel 최대 속도: **32 RPM**
- Primitive Belt 최대 속도: **32 RPM**
- Primitive Gearbox 최대 속도: **32 RPM**
- 기본 자동 동력원: Primitive Water Wheel
- 금속 가공 한계: Copper / Tin / Bronze
- Iron은 분말 단계까지 허용하되 제련 불가

### 브론즈 시대 진입

다음 부품을 제작한 후 64 RPM 회전망을 구축하면 브론즈 시대에 진입합니다.

- `bronze_ingot`
- `bronze_shaft`
- `bronze_bearing`
- `bronze_cogwheel`
- `bronze_gearbox`

```text
Primitive Water Wheel
        ↓
32 RPM
        ↓
Bronze Gear / Gearbox
        ↓
2:1 증속
        ↓
64 RPM
        ↓
BRONZE AGE
```
