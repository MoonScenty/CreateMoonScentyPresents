# Create: MoonScenty Presents

- Create Addon
- 바닐라는 그대로 두고 Create 자체의 진행을 Expert 하게 조이며, 시대마다 그 시대의 재료로 도구와 Curios 악세사리를 직접 만들게 하는 모드입니다.
- Create 말고도 일부 모드에 의존성이 있을 수 있습니다.
- 석기, 브론즈, 스틸, 스테인리스 스틸, 티타늄 시대로 나뉩니다.
- 현재는 석기 컨텐츠 내용만 포함되어 있습니다.


## 석기 시대

### 개념

**석기 시대에는 무인 동력이 없다.** 수차도 풍차도 없고, 사람이 자리를 뜨면 도는 것은 아무것도 없다.

가공 수단은 둘뿐이다.

- **손 가공** — 도구를 한 손에, 재료를 다른 손에 들고 지속 우클릭한다. Create의 Sand Paper Polishing이 원형이다.
- **손 크랭크** — 원시 기계를 사람이 직접 돌린다. 손을 떼면 멈춘다.

Create의 안산암 계층은 **네 개의 관문**으로 나뉜다. 관문마다 요구하는 도구가 다르므로, 시대의 길이는 한 동작을 반복하는 데서 나오지 않고 서로 다른 도구를 갖춰 나가는 데서 나온다.

무인 동력을 손에 넣는 것이 이 시대의 졸업 조건이며, 그것이 브론즈 시대의 첫 보상이다.

바닐라는 건드리지 않는다. **Create는 필요한 만큼 건드린다** — 레시피를 바꾸고, 블록에 제약을 걸고, 컨셉에 맞는 기계가 필요하면 Create 풍으로 새로 만든다.

---

### 손 가공

도구를 들고 지속 우클릭하면 일정 시간 뒤 아이템이 바뀐다. 각 방식은 자체 레시피 타입과 전용 도구를 갖는다.

| 방식 | 도구 | 레시피 타입 | 동작 | 상태 |
|---|---|---|---|---|
| Polishing | Sand Paper (Create) | `create:sandpaper_polishing` | — | Create 것을 그대로 둔다 |
| Sawing | `wooden_saw` | `sawing` | 32틱, 앞뒤 왕복 | **구현 완료** |
| Hammering | `stone_hammer` | `hammering` | 40틱, 10틱마다 타격 | **구현 완료** |
| Shaping | `stone_chisel` | `shaping` | 48틱, 짧고 느린 긁기 | **구현 완료** |
| Applying | `resin_dipped_brush` | `applying` | 56틱, 바닐라 brushing 모션 | **구현 완료** |

#### Applying은 브러시가 도구다

물질을 직접 도구로 만들지 않는다. **바닐라 브러시에 물질을 묻혀 쓰고, 한 번 쓰면 물질만 소모되어 일반 브러시로 돌아온다.**

```text
minecraft:brush + resin  →  resin_dipped_brush   (무형 조합)
                                  ↓ Applying 1회
                            minecraft:brush
```

브러시는 재사용하는 인프라이고 담근 물질이 소모품이다. 그래서 `resin`은 도구가 아니라 **평범한 재료**로 남고, 이후 시대에 타르·기름·밀랍이 생겨도 **조합 레시피 한 줄**이면 된다. 새 클래스도 새 레시피 타입도 필요 없다.

`applying` 레시피는 어떤 브러시를 썼는지를 함께 지정하므로, **같은 재료라도 수지를 바르면 A, 타르를 바르면 B**가 나올 수 있다.

가공 도중 손을 떼면 재료는 돌려주고 물질은 남는다. 완료했을 때만 소모된다.

---

### Create 게이팅

#### 네 개의 관문

관문마다 **다른 도구, 다른 방식**을 요구한다.

| # | Create 대상 | 요구하는 원시 부품 | 만드는 방식 | 변경할 파일 |
|---:|---|---|---|---|
| 1 | `create:andesite_alloy` | `andesite_grit` | Hammering | `crafting/materials/andesite_alloy.json` 외 3개 |
| 2 | `create:shaft` | `wooden_stave` | Sawing | `crafting/kinetics/shaft.json` |
| 3 | `create:andesite_casing` | `andesite_cement` | Applying | `item_application/andesite_casing_from_log.json` 외 1개 |
| 4 | `create:mechanical_press` | `stone_die` | Shaping | `crafting/kinetics/mechanical_press.json` |

#### 각 관문의 강제 방식

**1. 합금 — 재료 치환**

`andesite_alloy`는 2×2 `"BA"/"AB"`로 네 칸이 다 차 있다. 재료를 더할 자리가 없으므로 **키 `A`의 값만 바꾼다**: `minecraft:andesite` → `andesite_grit`. 패턴도 칸 수도 JEI 표시도 그대로다.

아연 우회와 Mixer 우회가 있으므로 네 파일을 전부 바꾼다.

```text
crafting/materials/andesite_alloy.json
crafting/materials/andesite_alloy_from_zinc.json
mixing/andesite_alloy.json
mixing/andesite_alloy_from_zinc.json
```

**2. 축 — 재료 추가**

`shaft`는 세로 2칸 `"A"/"A"`(합금 ×2 → 축 ×8)이다. 아래 칸을 `wooden_stave`로 바꾼다.

`cogwheel`(축 + 판자)과 `large_cogwheel`(축 + 판자 ×2)은 **둘 다 축을 요구하므로 자동으로 뒤에 선다.** 따로 건드리지 않는다.

**3. 케이싱 — 적용 아이템 치환**

`andesite_casing`은 껍질 벗긴 원목에 합금을 우클릭하는 `item_application`이다. **우클릭 대상 블록은 건드리지 않고 손에 든 아이템만** 바꾼다: `create:andesite_alloy` → `andesite_cement`.

블록 쪽을 모드 블록으로 바꾸면 `ItemApplicationRecipe`가 그것을 어떻게 해석하는지에 의존하게 되고, 실패하면 케이싱이 조용히 제작 불가가 된다. 아이템만 바꾸면 그 위험이 없다.

**4. 프레스 — 재료 추가**

`mechanical_press`는 세로 3칸 `"S"/"C"/"I"`(축·케이싱·철 블록)이다. 5칸 유형으로 넓혀 `stone_die` 2개를 양옆에 넣는다.

```text
 S        →     " S "
 C              "DCD"     D = stone_die
 I              " I "
```

프레스가 케이싱만으로 도달하는 것을 막는다. **이것이 시대의 종결 조건이다.**

#### 무인 동력원 차단

관문 넷을 다 통과해도 무인 동력은 얻지 못한다. 발전기는 따로 막는다.

| 대상 | 막는 방법 |
|---|---|
| `create:water_wheel` | 레시피에 브론즈 재료를 요구시킨다 |
| `create:large_water_wheel` | 동일 |
| `create:windmill_bearing` | 동일 |
| 양털 돛 | 풍차 베어링이 막히면 함께 닫힌다. 태그는 건드리지 않는다 |
| `create:hand_crank` | **막지 않는다.** 우리 크랭크와 같은 층이다 |

양털 8개 + 풍차 베어링이면 512 SU가 나오므로, 이 구멍을 열어 두면 시대 전체가 무의미해진다. 다만 태그에서 양털을 빼는 것은 게이트가 아니라 기능 삭제이므로 하지 않는다. **베어링을 잠그면 돛은 쓸 곳이 없어진다.**

레시피로 닫히지 않는 것이 나오면 mixin을 쓴다. 현재 목록에는 없다.

---

### 진행 순서

다섯 장으로 나뉘고, 각 장은 Create 해금 하나로 끝난다.

```text
[1장] 맨손
덩굴 / 풀 / 묘목 → Plant Fiber → Twine
↓
Wooden Saw / Stone Hammer / Stone Chisel 제작
↓
원목에서 Resin 채취 → brush + resin → Resin Dipped Brush
        (도구 넷이 갖춰진다. 아직 Create는 아무것도 열리지 않았다)

[2장] 첫 관문 — Hammering
Stone Hammer + Andesite → Andesite Grit
↓
create:andesite_alloy   ← 첫 Create 아이템

[3장] 회전 부품 — Sawing
Wooden Saw + 껍질 벗긴 원목 → Wooden Stave
↓
합금 + Stave → create:shaft → cogwheel / large_cogwheel
↓
Primitive Hand Crank + Primitive Millstone 조립
↓
크랭크로 맷돌을 돌려 Grit 생산이 2배가 된다

[4장] 케이싱 — Applying
Resin Dipped Brush + 합금 → Andesite Cement
↓
껍질 벗긴 원목에 Cement 우클릭 → create:andesite_casing

[5장] 졸업 — Shaping
Stone Chisel + 돌 → Stone Die
↓
축 + 케이싱 + 철 블록 + Die ×2 → create:mechanical_press
↓
크랭크로 프레스를 돌려 첫 create:iron_sheet
↓
브론즈 시대 (= 무인 동력 해금)
```

크랭크로 프레스를 돌리는 것은 **일부러 불편하게 둔다.** 한 장 찍을 때마다 사람이 돌려야 하므로, 수차가 왜 보상인지가 그 자리에서 설명된다.

---

### 손 크랭크와 스테이션

크랭크는 **정확히 1대**의 기계만 돌린다. 2대를 돌리려면 크랭크를 2대 놓고 번갈아 돌려야 하며, 그 불편함이 이 시대가 임시라는 것을 알려 준다.

| 손 가공 | 크랭크 스테이션 | 이득 |
|---|---|---|
| Hammering | `primitive_millstone` | Andesite → Grit ×2 (손은 ×1) |
| Sawing | — | 이 시대에는 없다 |
| Shaping | — | 이 시대에는 없다 |
| Applying | — | 이 시대에는 없다 |

**손 경로는 어떤 관문에서도 막히지 않는다.** 스테이션은 처리량으로만 이기므로 아무도 진행이 멈추지 않고, 모두가 자발적으로 옮겨 간다.

나머지 셋에 스테이션을 주지 않는 것은 의도다. 시대가 길어져야 하면 여기가 첫 번째 확장 지점이고, 그때까지는 **"기계는 하나뿐"**이 이 시대의 인상을 만든다.

#### 32 RPM 제한

석기 시대 부품은 32 RPM을 넘기면 과속이 처음 닿은 부품 하나가 부서지고 회전망이 끊긴다. 부서진 부품은 드롭되므로 손실은 없다. (`ModKineticLimits`, 구현 완료)

| 블록 | 최대 RPM |
|---|---:|
| `wooden_shaft` / `wooden_powered_shaft` | 32 |
| `stone_cogwheel` / `large_stone_cogwheel` | 32 |
| `primitive_gearbox` / `primitive_vertical_gearbox` | 32 |
| `primitive_millstone` | 32 |

제한은 이 모드의 부품에만 걸린다. Create 축으로 갈아타면 해소되지만, 그 시점에는 이미 3장을 통과한 뒤다.

---

### 아이템

| 아이템 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 식물 섬유 | `plant_fiber` | 끈의 재료 | 덩굴·풀·묘목 (무형, 도구 불필요) |
| 끈 | `twine` | 네 도구의 공통 결속재 | Plant Fiber ×3 |
| 나무 수지 | `resin` | 브러시에 묻히는 물질 | 원목에서 채취 |
| 수지 브러시 | `resin_dipped_brush` | **Applying 도구.** 1회용, 쓰면 일반 브러시로 돌아온다 | `minecraft:brush` + Resin |
| 나무 톱 | `wooden_saw` | **Sawing 도구** | Planks ×2 + Flint ×1 + Twine ×1 |
| 돌 망치 | `stone_hammer` | **Hammering 도구.** 3×3 범위 채굴도 한다 | 안산암 ×2 + Stick ×2 + Twine ×1 |
| 돌 끌 | `stone_chisel` | **Shaping 도구** | Flint ×1 + Stick ×1 + Twine ×1 |
| 안산암 분말 | `andesite_grit` | 관문 1. 합금의 재료 | Hammering: 안산암 |
| 나무 살대 | `wooden_stave` | 관문 2. Create 축의 재료 | Sawing: 껍질 벗긴 원목 |
| 안산암 시멘트 | `andesite_cement` | 관문 3. 케이싱을 만드는 도포재 | Applying: 합금 |
| 돌 거푸집 | `stone_die` | 관문 4. 프레스의 성형 부품 | Shaping: 돌 |
| 나무 베어링 | `wooden_bearing` | 회전체 지지 부품 | Planks ×2 + Twine ×2 |

### 블록

| 블록 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 나무 축 | `wooden_shaft` | 회전 전달, 최대 32 RPM | Wooden Stave + Twine |
| 돌 톱니바퀴 | `stone_cogwheel` | 기어비 구성, 최대 32 RPM | Wooden Shaft + 돌 |
| 대형 돌 톱니바퀴 | `large_stone_cogwheel` | 2:1 증감속, 최대 32 RPM | Wooden Shaft + 돌 ×2 |
| 원시 기어박스 | `primitive_gearbox` | 방향 전환, 최대 32 RPM | Stone Cogwheel ×4 + Wooden Bearing |
| 원시 수직 기어박스 | `primitive_vertical_gearbox` | 수평·수직 축 전환 | Primitive Gearbox 상호 변환 |
| 손 크랭크 | `primitive_hand_crank` | 사람이 돌리는 유일한 동력원. 1대만 구동 | Planks ×3 + Wooden Shaft |
| 원시 맷돌 | `primitive_millstone` | Andesite → Grit ×2 | Stone Cogwheel + Wooden Bearing + 돌 |

---

### 시대 보상

보상은 주지 않는다. **이 시대에서 모은 재료로 직접 만든다.**

| 아이템 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 돌 망치 | `stone_hammer` | 곡괭이 대상 3×3 범위 채굴. 채굴 속도는 바닐라보다 느리고 등급도 올려주지 않는다 — **면적으로만 이긴다** | 안산암 ×2 + Stick ×2 + Twine ×1 |
| 견습생 고글 | `apprentice_goggles` | Curios `head`. 원시 부품의 현재 RPM과 32 한계를 표시한다 | Glass ×2 + Andesite Grit ×2 + Twine ×2 |
| 채집 가방 | `gatherers_satchel` | Curios `back`. 반경 4블록 드롭 아이템 자동 회수 | Twine ×4 + Leather ×2 + Andesite Grit ×1 |

돌 망치는 **1장에서** 만들 수 있어야 하므로 분말을 요구하지 않는다. 분말을 요구하면 "분말을 갈려면 망치가 필요한데 망치를 만들려면 분말이 필요한" 잠금이 생긴다.

고글은 **첫 증속 이전에** 만들 수 있다. 회전 부품을 하나도 요구하지 않으므로, 과속 파손이 일어나기 전에 경고 도구가 손에 들어온다.

---

### 브론즈 시대 진입 조건

> **손 크랭크로 `create:mechanical_press`를 돌려 첫 `create:iron_sheet`를 만든다.**

`create:iron_sheet`는 Create 레시피 1,884개 중 `pressing/iron_ingot.json` 하나만 생산한다(확인함). 이것이 나오면 Mixer·Encased Fan·Saw·Blaze Burner가 한꺼번에 열리므로 브론즈 시대의 출발선으로 그대로 쓴다.

철은 바닐라 화로에서 그대로 제련한다. **바닐라 제련을 금지하지 않는다.**

브론즈 시대의 첫 보상은 **수차**다. 즉 무인 동력이다.

---

### 구현 현황

**동작까지 완료** — 손 가공 4종(`sawing` / `hammering` / `shaping` / `applying`)의 레시피 타입·도구·데이터 컴포넌트, `resin_dipped_brush`와 그 조합 레시피, `ModKineticLimits`(32 RPM), `primitive_hand_crank`, `primitive_millstone`, `wooden_shaft`, `stone_cogwheel`, `large_stone_cogwheel`, `primitive_gearbox`.

**아직 없다** — 관문 아이템 4종(`andesite_grit`, `wooden_stave`, `andesite_cement`, `stone_die`), 손 가공 레시피 전부, Create 레시피 덮어쓰기 8개 파일, 발전기 차단 3개, 보상 2종과 Curios 의존성, `stone_hammer`의 3×3 범위 채굴, Shaping·Applying의 JEI 카테고리.

**이 시대에 쓰지 않는다** — `drying_rack`, `pit_kiln`, `charcoal_pit`, `fired_crucible`, `primitive_sifter`, 점토 가공 라인, 광석 정광·분말 라인, 주석 라인. 전부 브론즈 이후로 미룬다.

#### 알려진 결함

`ModHandCrankBlockEntity`가 `getStressConfigKey()`를 재정의하지 않아, Create의 구현이 `AllBlocks.HAND_CRANK.has(state)` 검사에서 탈락해 **`copper_valve_handle`의 용량을 읽는다.** 등록한 값이 죽어 있고 크랭크 소리도 나지 않는다. 두 줄로 고친다.

#### 확인이 필요한 것

- **Create 레시피를 모드 리소스로 덮어쓰는 것이 런타임에 실제로 우선하는지.** 이 설계의 관문 전부가 여기 걸려 있다. 안 되면 내장 데이터팩 → `neoforge:conditions` 순으로 시도한다.
- 크랭크가 프레스를 실제로 돌릴 수 있는지. 크랭크는 256 SU를 내고 프레스는 8 RPM에서 64 SU를 쓰므로 계산상 가능하지만, 조작감은 재봐야 한다.
- **시대 분량.** 관문 4개 + 도구 4개 + 스테이션 1개가 몇 시간인지는 재보기 전에는 모른다. 짧으면 스테이션을 늘리는 쪽으로 확장하고, **요구 수량이나 타이머를 늘리는 쪽으로는 확장하지 않는다.**
