# Create: MoonScenty Presents

- Create Addon
- 바닐라는 그대로 두고 Create 자체의 진행을 Expert 하게 조이며, 시대마다 그 시대의 재료로 도구와 Curios 악세사리를 직접 만들게 하는 모드입니다.
- Create 말고도 일부 모드에 의존성이 있을 수 있습니다.
- 석기, 브론즈, 스틸, 스테인리스 스틸, 티타늄 시대로 나뉩니다.
- 현재는 석기 컨텐츠 내용만 포함되어 있습니다.


## 석기 시대

### 개념

**석기 시대에는 무인 동력이 없다.** 수차도 풍차도 없고, 사람이 자리를 뜨면 **도는 것**은 아무것도 없다.

시간이 알아서 하는 일은 있다 — 널어놓은 것은 마르고, 뚫어놓은 나무는 수액을 흘린다. 그것은 동력이 아니라 기다림이고, 이 시대에서 자리를 비우는 유일한 이유다.

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
| Applying | `applicator_brush` | `applying` | 레시피가 정한 시간, 바닐라 brushing 모션 | **구현 완료** |

#### Applying만 대상이 블록이다

나머지 셋은 손에 든 아이템을 다른 아이템으로 바꾼다. Applying은 **이미 세워져 있는 블록에 물질을 바른다.** 바른다는 건 무언가 *위에* 하는 일이고, 그 무언가는 블록이다. Create가 케이싱을 조합대가 아니라 놓여 있는 원목을 손봐서 만드는 것과 같은 이유다.

```text
붓 + 반대 손의 물질  →  붓에 물질이 담긴다        (허공 우클릭)
블록에 우클릭 유지   →  processing_time 뒤 블록이 바뀌고 물질 1개 소모
웅크리고 우클릭      →  담긴 물질을 통째로 회수
```

붓은 재사용하는 인프라이고 담은 물질이 소모품이다. 한 종류만 담기고, **용량은 그 아이템 자신의 스택 크기를 따른다** — 수지면 64개, 16개까지만 쌓이는 물질이면 16개.

그래서 `resin`은 도구가 아니라 **평범한 재료**로 남고, 이후 시대에 타르·기름·밀랍이 생겨도 **레시피 한 줄**이면 된다. 붓은 무엇을 담고 있는지 신경 쓰지 않으므로 새 아이템도 새 클래스도 필요 없다.

`applying` 레시피는 어떤 물질을 발랐는지를 함께 지정하므로, **같은 블록이라도 수지를 바르면 A, 타르를 바르면 B**가 된다. 어떤 레시피도 이름을 부르지 않는 아이템은 애초에 붓에 들어가지 않는다.

대상은 클릭한 순간에 기억하지 않고 **매 틱 시선에서 다시 읽는다.** 바닐라 brushing과 같은 방식이며, 고개를 돌리면 작업이 취소된다. 도중에 손을 떼면 아무것도 소모되지 않는다.

블록이 바뀔 때 두 블록이 공유하는 속성(축 방향 등)은 그대로 넘어간다. 동서로 누운 원목은 동서로 누운 케이싱이 된다.

애니메이션은 바닐라 것을 그대로 쓴다. 1인칭 문지르기는 `UseAnim.BRUSH`가, 3인칭 스윙은 `brushing` 모델 오버라이드 4종이 담당한다.

---

### 수액 채취

Resin은 만드는 것이 아니라 **받는 것**이다. 이 시대에서 유일하게 사람이 없어도 진행되는 일이며, 그래서 나머지 전부가 손으로 도는 것과 대비된다.

```text
Hand Drill 로 통나무 우클릭  →  Holed Log        (되돌릴 수 없다)
Holed Log 옆면에 Tapper 설치
     500틱마다  →  Liquid Resin 100mB
     1000mB 차면 1000틱  →  굳어서 Resin 1개
     통 안에 아이템이 있으면 둘 다 멈춘다. 맨손 우클릭으로 회수
```

한 덩이에 **정확히 5분**(500틱 ×10 + 1000틱). 붙어서 지켜볼 시간이 아니라 다른 일을 하고 돌아올 시간이다.

**구멍 뚫린 통나무는 되돌아오지 않는다.** 조합에도 쓸 수 없고 다른 통나무와 섞이지도 않는다. 남은 용도는 톱으로 켜서 판자로 만드는 것뿐이며, 수율은 멀쩡한 통나무와 같다 — 구멍이 앗아간 것은 목재가 아니라 나무다.

**통이 차면 멈춘다.** 통 용량이 정확히 한 덩이분이므로, 굳는 중에 더 받는 일도 넘쳐서 버려지는 일도 없다. 다 굳은 덩이를 꺼내 가기 전까지 그 tapper는 아무것도 하지 않는다. **정확히 이것이 tapper를 여러 개 세우게 만드는 이유다.**

Tapper는 통나무 **옆 칸**에 서서 주둥이를 나무에 박는다. 통나무를 캐면 함께 떨어지고, 안에 굳어 있던 것도 같이 떨어진다.

Liquid Resin은 버킷과 월드 배치가 있는 보통 유체다. 석기 시대에는 파이프가 없으므로 통에서 꺼내는 방법은 굳기를 기다리는 것뿐이지만, 나중 시대에서 쓸 자리는 열어 두었다.

---

### Create 게이팅

#### 네 개의 관문

관문마다 **다른 도구, 다른 방식**을 요구한다.

| # | Create 대상 | 요구하는 원시 부품 | 만드는 방식 | 제거할 Create 레시피 |
|---:|---|---|---|---|
| 1 | `create:andesite_alloy` | `andesite_grit` | Hammering | 조합 2 + 믹싱 2 |
| 2 | `create:shaft` 봉쇄 → `wooden_shaft` | `wooden_stave` | Sawing | `crafting/kinetics/shaft` |
| 3 | `create:andesite_casing` | `andesite_cement` | 조합 (합금 + 수지) | `item_application` 2 |
| 4 | `create:mechanical_press` | `stone_die` | Shaping | `crafting/kinetics/mechanical_press` |

#### 관문을 거는 방법

**Create의 파일을 덮어쓰지 않는다.** 같은 경로에 파일을 두는 방식은 어느 모드의 팩이 위에 오느냐에 달려 있고, 실제로 동작하지 않았다 — 조건부 제거(`neoforge:conditions`)까지 함께 무시됐다. 파일이 아예 읽히지 않은 것이다.

대신 **레시피 ID로 지우고 우리 레시피를 새로 넣는다.**

1. `ModRecipeRemovals`에 넘겨받을 Create 레시피 ID를 적는다.
2. `RecipeManagerMixin`이 `RecipeManager.apply` 진입 시점에 그 항목을 입력 맵에서 걷어낸다. 파싱 자체가 일어나지 않으므로 JEI에도 발전과제에도 죽은 ID가 남지 않는다.
3. 대체 레시피는 `data/createmoonscentypresents/recipe/` 에 우리 것으로 둔다. 결과가 Create 아이템이어도 레시피 ID는 우리 것이어도 된다.

팩 우선순위에 기대지 않으므로 로드 순서와 무관하고, 넘겨받은 목록이 코드 한 곳에 모여 있어 무엇을 건드렸는지 추적된다.

#### 각 관문의 강제 방식

**1. 합금 — 재료 치환**

`andesite_alloy`는 2×2 `"BA"/"AB"`로 네 칸이 다 차 있다. 재료를 더할 자리가 없으므로 **키 `A`의 값만 바꾼다**: `minecraft:andesite` → `andesite_grit`. 패턴도 칸 수도 JEI 표시도 그대로고, 붙는 비용은 앞단의 망치질뿐이다.

아연 우회와 Mixer 우회가 있으므로 Create의 넷을 전부 지운다.

```text
create:crafting/materials/andesite_alloy
create:crafting/materials/andesite_alloy_from_zinc
create:mixing/andesite_alloy
create:mixing/andesite_alloy_from_zinc
```

돌아오는 것은 **아연 쪽 둘뿐이다.** 철 조각은 석기 시대의 재료가 아니므로 그 경로는 열어 두지 않는다. 믹서 레시피도 재료를 작업대와 똑같이 맞춰 두었으므로, 믹서를 얻어도 관문을 건너뛰지 못하고 처리량만 늘어난다.

```text
안산암  →  (망치질)  →  andesite_grit  →  + 아연 조각  →  create:andesite_alloy
```

**합금은 한 번 쓰고 끝나지 않는다.** 케이싱 하나만 요구하면 시대 전체가 합금 1개로 끝나므로, **나무 베어링이 합금을 하나 먹는다.** 베어링은 기어박스 부품과 원시 맷돌이 둘 다 거치는 자리이므로, 동력을 늘릴수록 망치질로 돌아오게 된다. 축과 톱니바퀴는 금속을 요구하지 않는다 — 많이 놓는 부품에 세금을 매기면 시대가 길어지는 게 아니라 지루해진다.

맷돌 자체도 베어링을 요구하므로 **첫 부스러기는 반드시 손으로 두들겨야 한다.** 그 다음에야 맷돌이 서고, 그때부터 안산암 하나가 부스러기 둘이 된다.

**2. 축 — 대체 없는 봉쇄**

`create:shaft`는 이 시대에 **열지 않는다.** 대체 레시피도 두지 않는다.

`ModKineticLimits`는 블록 id로 상한을 찾으므로 Create의 축은 `UNLIMITED`다. 시대 중간에 이것을 손에 쥐여 주면 `wooden_shaft`를 쓸 이유가 사라지고, 시대 전체가 서 있는 32 RPM 천장이 그 자리에서 무너진다. `cogwheel`(축 + 판자)과 `large_cogwheel`(축 + 판자 ×2)은 축만 열리면 따라오므로 같이 무너진다. Create의 축에 상한을 거는 것은 답이 아니다 — 뒷 시대에는 무제한이어야 하는데 진행 상태를 들고 있지 않아 영구 상한이 된다.

`create:crafting/kinetics/shaft`는 **돌려주지 않는다.** 졸업 시점도 안 된다 — 상한은 시대마다 다시 걸리고(브론즈 64, 그 뒤로도), 상한 없는 축이 손에 들어오는 순간 그 사다리 전체가 무너진다. **시대마다 그 시대의 축으로 돌린다.** Create의 톱니바퀴들도 축을 요구하므로 함께 닫힌 채로 있는다.

톱이 대신 여는 것은 **우리 축**이다. 이쪽은 상한이 걸린다.

```text
판자  →  (톱질)  →  wooden_stave ×2
wooden_stave + twine  →  wooden_shaft
```

살대는 껍질 벗긴 원목이 아니라 **판자에서 켠다.** 나무별 원목 태그가 껍질 벗긴 원목까지 포함하므로, 껍질 벗긴 원목에 살대 레시피를 걸면 같은 입력을 두고 판자 레시피와 경쟁해 둘 중 하나가 무작위로 이긴다.

**3. 케이싱 — 즉발을 시간으로 바꾼다**

`andesite_casing`은 껍질 벗긴 원목에 합금을 **한 번 우클릭하면 즉시** 나오는 `item_application`이다. 이 관문은 그 즉발을 없애고 같은 자리에 Applying을 놓는다.

```text
create:andesite_alloy + resin  →  andesite_cement        (조합)
붓에 andesite_cement 를 담고 껍질 벗긴 원목에 우클릭 유지  →  create:andesite_casing
```

Create의 두 `item_application` 레시피(`from_log` / `from_wood`)는 대체 없이 지우기만 한다. 레시피를 이상한 재료로 바꿔 놓는 것보다 아예 없애는 쪽이 JEI에 헛것이 남지 않는다. 같은 두 태그(`c:stripped_logs` / `c:stripped_woods`)를 Applying이 그대로 받는다.

**Applying은 여기서 아이템을 만들지 않는다.** 대상이 블록이므로 결과도 블록이고, 그래서 `andesite_cement`는 Applying의 산물이 아니라 Applying에 쓰는 재료다. 관문이 요구하는 것은 물건이 아니라 **동작** — 붓을 들고 서서 통나무 하나마다 시간을 들이는 일이며, 이것이 케이싱이 싸지 않다는 감각을 만든다.

**4. 프레스 — 재료 추가**

`mechanical_press`는 세로 3칸 `"S"/"C"/"I"`(축·케이싱·철 블록)이다. 5칸 유형으로 넓혀 `stone_die` 2개를 양옆에 넣는다.

```text
 S        →     " S "
 C              "DCD"     D = stone_die
 I              " I "
```

`S` 자리는 `create:shaft`가 아니라 **`wooden_shaft`**다. 프레스는 석기 시대의 마지막 조립물이므로 석기 시대의 부품으로 짓는다.

프레스가 케이싱만으로 도달하는 것을 막는다. **이것이 시대의 종결 조건이다.**

#### 무인 동력원 차단

관문 넷을 다 통과해도 무인 동력은 얻지 못한다. **따로 막을 것은 없다** — 관문 2가 `create:shaft`를 닫은 것으로 셋이 다 닫힌다.

| 대상 | Create 원본 레시피 | 왜 닫혀 있나 |
|---|---|---|
| `create:water_wheel` | 판자 ×8 + `create:shaft` | 축이 없다 |
| `create:large_water_wheel` | 판자 ×8 + `create:water_wheel` | 작은 수차가 없다 |
| `create:windmill_bearing` | 나무 반블록 + 돌 + `create:shaft` | 축이 없다 |
| 양털 돛 | — | 풍차 베어링이 막히면 함께 닫힌다. 태그는 건드리지 않는다 |
| `create:hand_crank` | — | **막지 않는다.** 우리 크랭크와 같은 층이다 |

양털 8개 + 풍차 베어링이면 512 SU가 나오므로, 이 구멍을 열어 두면 시대 전체가 무의미해진다. 다만 태그에서 양털을 빼는 것은 게이트가 아니라 기능 삭제이므로 하지 않는다. **베어링을 잠그면 돛은 쓸 곳이 없어진다.**

브론즈 시대에 무인 동력을 열 때는 이 셋을 브론즈 축을 쓰는 레시피로 넘겨받는다. 원본을 되살리는 것이 아니라, 다른 Create 부품들과 같은 방식으로 그 시대의 축 위에 다시 얹는다.

레시피로 닫히지 않는 것이 나오면 mixin을 쓴다. 현재 목록에는 없다.

---

### 진행 순서

다섯 장으로 나뉘고, 각 장은 Create 해금 하나로 끝난다.

```text
[1장] 맨손
덩굴 / 풀 / 묘목 → Plant Fiber → Twine
↓
Wooden Saw / Stone Hammer / Stone Chisel / Applicator Brush 제작
↓
Hand Drill로 통나무에 구멍 → Tapper 설치 → 고인 수액이 굳어 Resin
        (도구 넷이 갖춰진다. 아직 Create는 아무것도 열리지 않았다)

[2장] 첫 관문 — Hammering
Stone Hammer + Andesite → Andesite Grit
↓
create:andesite_alloy   ← 첫 Create 아이템

[3장] 회전 부품 — Sawing
Wooden Saw + 판자 → Wooden Stave
↓
Stave + Twine → wooden_shaft → stone_cogwheel / large_stone_cogwheel
        (create:shaft 는 열리지 않는다. 32 RPM 천장을 지키기 위해서다)
↓
Primitive Hand Crank + Primitive Millstone 조립
↓
크랭크로 맷돌을 돌려 Grit 생산이 2배가 된다

[4장] 케이싱 — Applying
합금 + Resin → Andesite Cement
↓
붓에 Cement를 담고 껍질 벗긴 원목에 우클릭 유지 → create:andesite_casing

[5장] 졸업 — Shaping
Stone Chisel + 돌 → Stone Die
↓
wooden_shaft + 케이싱 + 철 블록 + Die ×2 → create:mechanical_press
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
| 식물 섬유 | `plant_fiber` | 끈의 재료 | 덩굴 / 풀·키큰풀·고사리 / 묘목 (무형, 도구 불필요) |
| 끈 | `twine` | 네 도구의 공통 결속재 | Plant Fiber ×3 |
| 나무 수지 | `resin` | 붓에 담는 물질 | Tapper에서 액체 수지 1000mB가 굳어서 |
| 액체 수지 | `liquid_resin` | 유체. Tapper에 고인다. 버킷과 월드 배치 있음 | Tapper가 구멍 난 통나무에서 받는다 |
| 손 드릴 | `hand_drill` | 통나무를 우클릭해 구멍을 낸다. 내구도 128 | 철괴 ×2 + Stick ×1 |
| 도포용 붓 | `applicator_brush` | **Applying 도구.** 물질 한 종류를 그 아이템의 스택 크기만큼 담고, 1회에 1개 소모한다. 닳지 않는다 | Plant Fiber ×1 + Twine ×1 + Stick ×1 |
| 나무 톱 | `wooden_saw` | **Sawing 도구** | Planks ×2 + Flint ×1 + Twine ×1 |
| 돌 망치 | `stone_hammer` | **Hammering 도구** | 안산암 ×2 + Stick ×2 + Twine ×1 |
| 돌 끌 | `stone_chisel` | **Shaping 도구** | Flint ×1 + Stick ×1 + Twine ×1 |
| 안산암 분말 | `andesite_grit` | 관문 1. 합금의 재료 | Hammering: 안산암 |
| 나무 살대 | `wooden_stave` | 관문 2. Create 축의 재료 | Sawing: 판자 ×1 → ×2 |
| 안산암 시멘트 | `andesite_cement` | 관문 3. 붓에 담아 원목에 바르는 도포재 | 합금 ×1 + Resin ×1 |
| 돌 거푸집 | `stone_die` | 관문 4. 프레스의 성형 부품 | Shaping: 돌 |
| 나무 베어링 | `wooden_bearing` | 회전체 지지 부품. **이 시대에서 금속이 들어가는 유일한 부품** | Planks ×2 + Twine ×2 + Andesite Alloy ×1 |
| 나무 기어박스 부품 | `wooden_gearbox_component` | 기어박스의 속심. 나무로 감싼 베어링 | Wooden Bearing ×1 + 돌 ×1 |

돌 망치는 **1장에서** 만들 수 있어야 하므로 분말을 요구하지 않는다. 분말을 요구하면 "분말을 갈려면 망치가 필요한데 망치를 만들려면 분말이 필요한" 잠금이 생긴다.

### 블록

| 블록 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 나무 축 | `wooden_shaft` | 회전 전달, 최대 32 RPM | Wooden Stave + Twine |
| 돌 톱니바퀴 | `stone_cogwheel` | 기어비 구성, 최대 32 RPM | Wooden Shaft + 돌 |
| 대형 돌 톱니바퀴 | `large_stone_cogwheel` | 2:1 증감속, 최대 32 RPM | Wooden Shaft + 돌 ×2 |
| 원시 기어박스 | `primitive_gearbox` | 방향 전환, 최대 32 RPM | Stone Cogwheel ×4 + Wooden Gearbox Component |
| 원시 수직 기어박스 | `primitive_vertical_gearbox` | 수평·수직 축 전환 | Primitive Gearbox 상호 변환 |
| 손 크랭크 | `primitive_hand_crank` | 사람이 돌리는 유일한 동력원. 1대만 구동 | Planks ×3 + Wooden Shaft |
| 원시 맷돌 | `primitive_millstone` | Andesite → Grit ×2 | Stone Cogwheel + Wooden Bearing + 돌 |
| 채취통 | `tapper` | 구멍 난 통나무 옆에 붙어 수액을 받고 굳힌다. 통 1000mB | 판자 ×7 + 철 조각 ×1 |
| 구멍 난 통나무 | `holed_<나무>_log` | 8종. 조합에 못 쓰고 톱으로 판자 ×4만 나온다 | Hand Drill로 통나무에 구멍 |

---

### 시대 보상

보상은 주지 않는다. **이 시대에서 모은 재료로 직접 만든다.**

| 아이템 이름 | registry_id | 기능 내용 | 제작 방법 |
|---|---|---|---|
| 견습생 고글 | `apprentice_goggles` | Curios `head`. 원시 부품의 현재 RPM과 32 한계를 표시한다 | Glass ×2 + Andesite Grit ×2 + Twine ×2 |
| 채집 가방 | `gatherers_satchel` | Curios `back`. 반경 4블록 드롭 아이템 자동 회수 | Twine ×4 + Leather ×2 + Andesite Grit ×1 |

고글은 **첫 증속 이전에** 만들 수 있다. 회전 부품을 하나도 요구하지 않으므로, 과속 파손이 일어나기 전에 경고 도구가 손에 들어온다.

---

### 브론즈 시대 진입 조건

> **손 크랭크로 `create:mechanical_press`를 돌려 첫 `create:iron_sheet`를 만든다.**

`create:iron_sheet`는 Create 레시피 1,884개 중 `pressing/iron_ingot.json` 하나만 생산한다(확인함). 이것이 나오면 Mixer·Encased Fan·Saw·Blaze Burner가 한꺼번에 열리므로 브론즈 시대의 출발선으로 그대로 쓴다.

철은 바닐라 화로에서 그대로 제련한다. **바닐라 제련을 금지하지 않는다.**

브론즈 시대의 첫 보상은 **수차**다. 즉 무인 동력이다.

---

### 구현 현황

**동작까지 완료** — 손 가공 4종(`sawing` / `hammering` / `shaping` / `applying`)의 레시피 타입·도구·데이터 컴포넌트, `applicator_brush`(적재·회수·블록 적용·4종 브러시 모델), 수액 채취 한 줄 전부(`hand_drill`, 구멍 난 통나무 8종, `tapper`, `liquid_resin` 유체, `tapping`·`coagulating` 레시피 타입과 그 레시피들), `ModKineticLimits`(32 RPM), `primitive_hand_crank`, `primitive_millstone`, `wooden_shaft`, `stone_cogwheel`, `large_stone_cogwheel`, `primitive_gearbox`.

**석기 시대 전 구간이 조합으로 이어진다** — 1장 도구 넷, 3장 회전 부품 전부(`wooden_bearing`, `stone_cogwheel`, `large_stone_cogwheel`, `primitive_gearbox`와 수직 변환, `primitive_hand_crank`, `primitive_millstone`), 5장 프레스까지 크리에이티브 없이 도달한다. JEI 카테고리는 일곱 개(Sawing / Hammering / Shaping / Applying / Tapping / Coagulating / Drying).

**관문 넷 완료** — 레시피를 ID로 지우는 방식(`ModRecipeRemovals` + `RecipeManagerMixin`)과 그 위에 올린 관문 넷 전부. 넘겨받은 Create 레시피는 여덟 개(합금 4, 축 1, 케이싱 2, 프레스 1)이고, `andesite_grit`·`wooden_stave`·`andesite_cement`·`stone_die` 네 아이템과 그 가공 레시피, 대체 레시피 전부가 들어가 있다. 1장부터 5장까지 조합으로 이어진다.

`create:shaft`는 **아무 레시피도 없고, 돌려줄 계획도 없다.** 상한이 걸리지 않는 부품이기 때문이다. 따라서 Create의 톱니바퀴·기어박스 등 축을 요구하는 것 전부가 조합으로 닿지 않는다. 뒷 시대는 그 시대의 축과 그 시대의 부품으로 짓는다 — Create의 회전 부품 레시피를 시대별로 넘겨받는 일이 브론즈 이후의 과제로 남는다.

**아직 없다** — 시대 보상 2종(`apprentice_goggles`, `gatherers_satchel`). Curios 의존성만 있고 쓰는 코드가 없다.

**쓰이지 않는 아이템** — `bronze_gearbox_component`. 나무 쪽은 원시 기어박스의 속심으로 자리를 잡았으니, 브론즈 기어박스도 같은 자리에 놓으면 된다.

`applying`에는 시험용 레시피가 하나 있다 — 수지를 금 간 석재 벽돌에 발라 메운다. 건조대의 젖은 스펀지와 같은 역할로, 관문 재료가 생기기 전에 붓을 실제로 굴려 볼 수 있게 하는 것이 목적이다.

Tapper는 유체 캐퍼빌리티를 내놓지 않는다. 석기 시대에는 통에서 유체를 빼낼 수단이 없으므로 굳기를 기다리는 것 외에 길이 없고, 이 제약이 통이 차면 멈추는 규칙을 의미 있게 만든다. 파이프가 생기는 시대에 열면 된다.

**이 시대에 쓰지 않는다** — `drying_rack`, `pit_kiln`, `charcoal_pit`, `primitive_sifter`, 점토 가공 라인, 광석 정광·분말 라인, 주석 라인. 전부 브론즈 이후로 미룬다.

#### 확인이 필요한 것

- 크랭크가 프레스를 실제로 돌릴 수 있는지. 크랭크는 256 SU를 내고 프레스는 8 RPM에서 64 SU를 쓰므로 계산상 가능하지만, 조작감은 재봐야 한다.
- **시대 분량.** 관문 4개 + 도구 4개 + 스테이션 1개가 몇 시간인지는 재보기 전에는 모른다. 짧으면 스테이션을 늘리는 쪽으로 확장하고, **요구 수량이나 타이머를 늘리는 쪽으로는 확장하지 않는다.**
