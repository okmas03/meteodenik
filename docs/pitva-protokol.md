# Pitva – protokol (cv04)

## 1. Tabuľka konštrukcií

| # | Čo som napísal (Kotlin) | Generovaný člen – presný podpis (dekompilát) | Riadkov v Jave ručne |
|---|---|---|---|
| 1 | `data class Zapis(val mesto: String, val teplota: Double, val poznamka: String = "")` | `public final String getMesto()`, `public final double getTeplota()`, `public final String getPoznamka()`, `public final String component1()`, `public final double component2()`, `public final String component3()`, `public final Zapis copy(String mesto, double teplota, String poznamka)`, `public static Zapis copy$default(Zapis, String, double, String, int, Object)`, `public String toString()`, `public int hashCode()`, `public boolean equals(Object other)` + konštruktory `Zapis(String, double, String)` a `Zapis(String, double, String, int, DefaultConstructorMarker)` | cca 50 (3 polia, konštruktor + preťažený konštruktor kvôli predvolenej hodnote, 3 gettery, `equals`, `hashCode`, `toString`, `copy`) |
| 2 | `object Prevody { const val ABSOLUTNI_NULA = -273.15; fun naFahrenheita(celsia: Double) = celsia * 9 / 5 + 32 }` | `public static final Prevody INSTANCE`, `private Prevody()`, `public static final double ABSOLUTNI_NULA = -273.15`, `public final double naFahrenheita(double celsia)` | cca 10 (singleton: statická inštancia, privátny konštruktor, konštanta, metóda) |
| 3 | `fun Double.zaokrouhlenoNaDesetiny(): Double = Math.round(this * 10) / 10.0` (rozširujúca funkcia) | `public static final double zaokrouhlenoNaDesetiny(double $this$zaokrouhlenoNaDesetiny)` v triede `PitvaKt` | cca 5 (pomocná trieda + statická metóda) |
| 4 | `fun prumer(zapisy: List<Zapis>, vyber: (Zapis) -> Double): Double` (funkcia vyššieho rádu, lambda ako parameter) | `public static final double prumer(List zapisy, Function1 vyber)` – vnútri volanie `vyber.invoke(...)` | cca 12 (metóda + cyklus + funkčné rozhranie, napr. `ToDoubleFunction<Zapis>`) |
| 5 | `suspend fun nactiPoZdrzeni(zapisy: List<Zapis>): List<Zapis> { delay(50); return zapisy }` | `public static final Object nactiPoZdrzeni(List zapisy, Continuation $completion)` + anonymná `ContinuationImpl` s poľami `L$0`, `result`, `label` a metódou `invokeSuspend(Object)` | cca 40 (stavový automat so `switch`, ukladanie premenných, obnovenie) – ručne to prakticky nikto nepíše |

## 2. Odpovede na otázky

### 2.1 Koľko metód má `Zapis` a ktoré nemajú v zdrojáku ani náznak?

Dekompilovaná trieda `Zapis` má **11 metód**: `getMesto`, `getTeplota`, `getPoznamka`, `component1`, `component2`, `component3`, `copy`, `copy$default`, `toString`, `hashCode`, `equals` (a k tomu zvlášť **2 konštruktory**). Gettery majú v zdrojáku aspoň náznak – každý zodpovedá jednej vlastnosti `val`. Zvyšných **8 metód** (`component1–3`, `copy`, `copy$default`, `toString`, `hashCode`, `equals`) nemá v kóde žiadnu stopu okrem jediného slova `data` pred `class`.

### 2.2 Prečo má `Zapis` konštruktor navyše s `int` a `DefaultConstructorMarker`?

Súvisí to s predvolenou hodnotou `poznamka: String = ""`. JVM predvolené hodnoty parametrov nepozná, preto kompilátor vygeneruje syntetický konštruktor, ktorému sa navyše odovzdá bitová maska (`int var5`) určujúca, ktoré argumenty volajúci vynechal. Ak je nastavený bit `4` (tretí parameter), konštruktor dosadí `""` a zavolá hlavný konštruktor. `DefaultConstructorMarker` je len prázdna značka (odovzdáva sa `null`), aby sa podpis nemohol zraziť s konštruktorom, ktorý by napísal programátor; po zmazaní predvolenej hodnoty tento konštruktor z dekompilátu zmizne.

### 2.3 Aký je prvý parameter `zaokrouhlenoNaDesetiny` a je funkcia polymorfná?

Prvým parametrom je `double $this$zaokrouhlenoNaDesetiny` – príjemca (`this`) rozširujúcej funkcie sa stal obyčajným parametrom **statickej** metódy v triede `PitvaKt`. Statická metóda sa vyberá už pri kompilácii podľa deklarovaného typu výrazu, nie podľa skutočného typu objektu za behu, takže rozširujúca funkcia **nie je polymorfná** a nedá sa v podtriede prepísať. Na rozdiel od `formatovac.formatuj(...)` (virtuálne volanie cez rozhranie) tu žiadne dynamické rozhodovanie neprebieha – triedu v skutočnosti nijako „nerozširuje“.

### 2.4 Aký typ má druhý parameter `prumer` a čo to hovorí o funkciách ako hodnotách?

Druhý parameter má typ `Function1` – rozhranie zo štandardnej knižnice Kotlinu s jedinou metódou `invoke`, na ktorom dekompilát volá `vyber.invoke(var6)`. JVM nemá funkčné typy, takže lambda je za behu **objekt implementujúci rozhranie** a „zavolať funkciu“ znamená zavolať na ňom `invoke`. Kvôli generikám sa navyše výsledok vracia ako `Object` a musí sa pretypovať (`((Number) …).doubleValue()`), teda dochádza k boxovaniu `Double` – to je jeden z dôvodov, prečo Kotlin ponúka `inline` funkcie.

### 2.5 Čo sa stalo s návratovým typom `nactiPoZdrzeni` a prečo je v nej `switch`?

Návratový typ sa zmenil z `List<Zapis>` na `Object` a pribudol parameter `Continuation $completion`. Funkcia totiž môže vrátiť buď skutočný výsledok, alebo značku `COROUTINE_SUSPENDED`, keď sa uspí (tu na `delay(50)`) a uvoľní vlákno. `switch` podľa poľa `label` z nej robí stavový automat: `case 0` je začiatok – uloží `zapisy` do `L$0`, nastaví `label = 1` a zavolá `delay`; po prebudení sa funkcia zavolá znova cez `invokeSuspend`, skočí na `case 1`, obnoví `zapisy` z `L$0` a pokračuje. Každý bod uspania pridá jeden ďalší `case`.