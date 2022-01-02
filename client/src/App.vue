<template>
  <div id="app">
    <div class="container is-flex is-align-items-center">
      <img src="@/assets/wicked.png" alt="Wicked">

      <div class="is-flex is-flex-direction-column py-6 ml-6">
        <h1 class="title is-1">Semestrálka z 4iz210 za 60 sekund<span class="has-text-grey-light">*</span></h1>
        <small class="has-text-grey-light">* Well yes, but actually no</small>
      </div>
    </div>

    <hr>

    <div class="container">
      <h2 class="title is-3">Vstupní dokumenty</h2>
      <p>3 - 5 vstupních dokumentů, ze kterých se bude celá semestrálka generovat.</p>

      <div v-if="inputs.length < 5">
        <textarea v-model="input" class="textarea"></textarea>
        <button @click="addInput()" :disabled="input.trim().length === 0" class="button is-info mt-2">Přidat dokument
        </button>
      </div>
    </div>

    <div class="my-6 columns is-centered">
      <div class="column is-2" v-for="(input, i) in inputs" :key="i">
        <div class="card">
          <div class="card-header">
            <div class="card-header-title">Dokument {{ i + 1 }}</div>
          </div>
          <div class="card-content" v-html="highlight(input)"></div>
          <div class="card-footer">
            <a href="#" class="card-footer-item" @click="removeInput(i)">Smazat</a>
          </div>
        </div>
      </div>
    </div>

    <div v-if="inputs.length >= 3">
      <hr>

      <div class="container">
        <h2 class="title is-3">Hledaný dotaz</h2>
        <p>Minimálně 5 slov, které se budou hledat v dokumentech.</p>
        <p>Ke každému ze slov je potřeba doplnit varianty (např. "baterií" je varianta slova "baterie") a zkontrolovat,
          že všechny výskyty jsou správně podbarveny.</p>

        <div v-if="query.length < 8" class="mt-5">
          <input v-model="term" class="input is-inline">
          <button @click="addTerm()" :disabled="term.trim().length === 0" class="button is-info ml-2 is-inline">Přidat
            slovo
          </button>
        </div>

        <div class="mt-3 mb-6 columns is-centered">
          <div class="column is-2 " v-for="(term, i) in query" :key="i">
            <div class="card">
              <div class="card-header">
                <div class="card-header-title">{{ term.term }}</div>
              </div>
              <div class="card-content">
                <div v-if="term.variations.length === 0" class="">Žádné varianty slova</div>
                <div v-else>
                  <div v-for="(variation, j) in term.variations" :key="j">
                    <b class="is-clickable has-text-danger" @click="removeVariation(i, j)">&times;</b>
                    {{ variation }}
                  </div>
                </div>

                <input v-model="variation" class="input is-small mt-2">
                <button @click="addVariation(i)" :disabled="variation.trim().length === 0" class="button is-small is-fullwidth mt-1">Přidat variantu</button>
              </div>
              <div class="card-footer">
                <a href="#" class="card-footer-item" @click.prevent="removeTerm(i)">Smazat</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="query.length >= 5">
      <hr>

      <div class="container">
        <h2 class="title is-3">Regulární výrazy</h2>
        <p>5 regulárních výrazů, které budou z dokumentů extrahovat informace.</p>

        <button class="button is-info my-5" @click="addPattern()" :disabled="patterns.length >= 5">Přidat regulární výraz</button>

        <p v-if="patterns.length === 0" class="has-text-grey-light">Zatím nejsou přidané žádné regulární výrazy</p>
        <div v-else class="mb-5">
          <div class="tabs is-centered">
            <ul>
              <li v-for="(current, i) in patterns" :key="i" :class="pattern === i ? 'is-active' : ''">
                <a href="#" @click.prevent="pattern = i">
                  Výraz {{ i + 1 }}
                  <code v-if="current.regex.trim().length !== 0" class="ml-2">{{ current.regex }}</code>
                </a>
              </li>
            </ul>
          </div>

          <div class="columns">
            <div class="column">
              <label class="label">Výraz:</label>
              <input v-model="patterns[pattern].regex" class="input">

              <label class="label mt-2">Popis výrazu:</label>
              <input v-model="patterns[pattern].description" class="input">

              <label class="label mt-2">XML šablona:</label>
              <textarea v-model="patterns[pattern].xml" class="textarea is-family-monospace"
                        placeholder="<RESOLUTION><WIDTH>(1)</WIDTH><HEIGHT>(2)</HEIGHT></RESOLUTION>"
              ></textarea>
              <small>Pro referenci group ve výrazu použij syntaxi <code>(1)</code> pro první skupinu, <code>(2)</code> pro druhou...</small>
            </div>
            <div class="column">
              <div>
                Náhled výstupu dotazu pro
                <div class="select is-small">
                <select v-model="reference">
                  <option v-for="(_, i) in inputs" :key="i" :value="i">Dokument {{ i + 1}}</option>
                </select>
                </div>
              </div>

              <div class="mt-3">
                <code>
                  {{ patternReference() }}
                </code>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: 'App',
  data: () => ({
    input: "",
    inputs: [
      "Notebook - Intel Pentium Quad Core N3510 Bay Trail, multidotykový 10.1\" 1366x768, RAM 4GB, Intel HD Graphics, HDD 500GB 5400 otáček, WiFi, Bluetooth 4.0, Webkamera 1 MPx, 3článková baterie, HDMI, Windows 8 32bit",
      "Notebook - Intel Pentium 2129Y Ivy Bridge, multidotykový 11.6\" LED 1366x768, RAM 6GB DDR3, Intel HD Graphics, HDD 500GB, WiFi, Bluetooth, webkamera, 3 článková baterie, Windows 8 64-bit (V5-132P-21296G50nbb)",
      "Ultrabook - Intel Core i3 4010U Haswell, multidotykový 13.3\" LED 1920x1080, RAM 4GB, Intel HD Graphics 4400, SSHD 500GB + 8GB cache pro zrychlení běhu OS, WiFi, Bluetooth 4.0, Webkamera, HDMI, USB 3.0, podsvícená klávesnice, Windows 8 64-bit"
    ],
    term: "",
    variation: "",
    query: [
      {
        term: "baterie",
        variations: []
      },
      {
        term: "bluetooth",
        variations: []
      },
      {
        term: "intel",
        variations: []
      },
      {
        term: "core",
        variations: []
      },
      {
        term: "pentium",
        variations: []
      }
    ],
    pattern: 0,
    reference: 0,
    patterns: [
      {
        regex: "(\\d+)x(\\d+)",
        description: "Rozlišení monitoru",
        xml: "<RESOLUTION><WIDTH>(1)</WIDTH><HEIGHT>(2)</HEIGHT></RESOLUTION>"
      }
    ]
  }),
  methods: {
    addInput() {
      this.inputs.push(this.input);
      this.input = "";
    },
    removeInput(index) {
      this.inputs.splice(index, 1);
    },
    addTerm() {
      this.query.push({term: this.term.toLowerCase(), variations: []});
      this.term = "";
    },
    removeTerm(index) {
      this.query.splice(index, 1);
    },
    addVariation(index) {
      this.query[index].variations.push(this.variation.toLowerCase());
      this.variation = "";
    },
    removeVariation(term, variation) {
      this.query[term].variations.splice(variation, 1);
    },
    addPattern() {
      this.patterns.push({
        regex: "",
        description: "",
        xml: ""
      });
    },
    patternReference() {
      const document = this.inputs[this.reference];
      const pattern = this.patterns[this.pattern];

      const matches = Array.from(document.matchAll(pattern.regex))[0];

      return matches.reduce((text, value, index) => text.replaceAll(`(${index})`, value.toString()), pattern.xml);
    },
    highlight(input) {
      return input.split(/(\s+|[,.])/)
          .filter(word => word.trim().length !== 0)
          .map(word => {
            const lowercase = word.toLowerCase();
            const match = this.query.find(query => query.term === lowercase || query.variations.includes(lowercase));

            if (match === null) {
              return word;
            }

            const colors = ["#ffff00", "#00ff00", "#00ffff", "#ff00ff", "#ff9900", "#9900ff", "#0099ff", "#99ff00"];
            const color = colors[this.query.indexOf(match)];

            return `<span style="background: ${color}">${word}</span>`
          })
          .join(" ");
    }
  }
}
</script>