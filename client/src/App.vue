<template>
  <div id="app">
    <div class="modal is-active" v-if="loading">
      <div class="modal-background"></div>
      <div class="modal-content has-text-centered">
        <h1 class="title is-1 has-text-primary">Generuji vzorovou semestrálku...</h1>
        <p>Nemělo by to trvat déle než pár minut</p>
      </div>
    </div>
    <div class="container is-flex is-align-items-center">
      <img src="@/assets/wicked.png" alt="Wicked">

      <div class="is-flex is-flex-direction-column py-6 ml-6">
        <h1 class="title is-1">Vzorová semestrálka z 4iz210 za 60 sekund<span class="has-text-grey-light">*</span></h1>
        <h2 class="title is-4 has-text-grey">Tato aplikace složí jenom pro kontrolu výsledků / vzor jak práci vypracovat!</h2>
        <small class="has-text-grey-light">* Well yes, but actually no</small>
      </div>
    </div>

    <hr>

    <div class="container">
      <h2 :class="`title is-3 ${inputs.length >= 3 ? 'has-text-success' : ''}`">Vstupní dokumenty</h2>
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
        <h2 :class="`title is-3 ${query.length >= 5 ? 'has-text-success' : ''}`">Hledaný dotaz</h2>
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
                <button @click="addVariation(i)" :disabled="variation.trim().length === 0"
                        class="button is-small is-fullwidth mt-1">Přidat variantu
                </button>
              </div>
              <div class="card-footer">
                <a href="#" class="card-footer-item" @click.prevent="removeTerm(i)">Smazat</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="inputs.length >= 3 && query.length >= 5">
      <hr>

      <div class="container">
        <h2 :class="`title is-3 ${patterns.length === 5 ? 'has-text-success' : ''}`">Regulární výrazy</h2>
        <p>5 regulárních výrazů, které budou z dokumentů extrahovat informace.</p>

        <button class="button is-info my-5" @click="addPattern()" :disabled="patterns.length === 5">Přidat regulární výraz</button>

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
              <button class="button is-danger mb-2" @click="patterns[pattern].xml.children = []">Reset xml</button>

              <XmlBuilder :root="patterns[pattern].xml" :fixed="true"/>

              <p>Není možné smazat pouze jeden element / komplexní typ, je potřeba resetova celé schéma.</p>
              <small>Pro referenci group ve výrazu použij syntaxi <code>(1)</code> pro první skupinu, <code>(2)</code>
                pro druhou...</small>
            </div>
            <div class="column">
              <div>
                Náhled výstupu dotazu pro
                <div class="select is-small">
                  <select v-model="reference">
                    <option v-for="(_, i) in inputs" :key="i" :value="i">Dokument {{ i + 1 }}</option>
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

    <div v-if="inputs.length >= 3 && query.length >= 5 && patterns.length === 5">
      <hr>

      <div class="hero my-6">
        <div class="hero-body has-text-centered">
          <h2 class="title is-2">
            Tato aplikace mě stála poměrně hodně času a úsilí.<br>
            Je to zcela dobrovolné, ale můžeš mi přispět na pivo 🍺
          </h2>

          <div class="hero buttons">
            <a class="button is-info is-large mt-6" href="https://paypal.me/jirivrba" target="_blank">PayPal</a>
            <a class="button is-link is-large mt-6">1705593015/3030</a>
          </div>
        </div>
        <hr>
        <div class="hero-body has-text-centered">
          <label class="label">xname:</label>
          <input type="text" v-model="xname" class="input is-inline" maxlength="6"><br>
          <small>(slouží pouze pro pojmenování výsledného souboru na serveru)</small>
        </div>
        <hr>
        <div class="hero-buttons">
          <button class="button is-large is-success" :disabled="!(/[a-z0-9]{6}/g.test(xname))" @click="callGenerator()">Vygenerovat vzorovou semestrálku</button>
        </div>
      </div>
    </div>
    <hr>
    <footer class="has-text-centered has-text-grey-light">
      Made with &hearts; and Kotlin
    </footer>
  </div>
</template>

<script>
import XmlBuilder from "@/XmlBuilder";

export default {
  name: 'App',
  components: {XmlBuilder},
  data: () => ({
    input: "",
    inputs: [],
    term: "",
    variation: "",
    query: [],
    pattern: 0,
    reference: 0,
    patterns: [],
    xname: "",
    loading: false,
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
        xml: {
          name: "SHOPITEM",
          type: "complex",
          value: "",
          children: []
        }
      });
      this.pattern = this.patterns.length - 1;
    },
    async callGenerator() {
      this.loading = true;

      const query = Object.fromEntries(this.query.map(q => [q.term, q.variations]));
      const data = {
        xname: this.xname,
        inputs: this.inputs,
        query: query,
        patterns: this.patterns
      };

      await fetch("/generator", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
      })
      .then(response => response.json())
      .then(response => {
        console.log(response);
        window.open(response.link)
      })
      .catch(error => {
        console.error(error);
        window.alert("Při generování semestrálky došlo k chybě, která byla zalogována do konzole prohlížeče.");
      });

      this.loading = false;
    },
    patternReference() {
      const document = this.inputs[this.reference];
      const pattern = this.patterns[this.pattern];

      const resolveTemplate = node => {
        if (node.children.length === 0) {
          return `<${node.name}>${node.value}</${node.name}>`;
        }

        return `<${node.name}>${node.children.map(child => resolveTemplate(child)).join("")}</${node.name}>`;
      }

      const template = resolveTemplate(pattern.xml);
      const matches = Array.from(document.matchAll(pattern.regex));

      if (matches.length === 0) {
        return "Regulární výraz nevrátil žádné výsledky!"
      }

      return matches[0].reduce((text, value, index) => text.replaceAll(`(${index})`, value.toString()), template);
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