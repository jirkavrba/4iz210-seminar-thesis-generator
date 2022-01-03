<template>
  <div class="notification" style="border: 1px solid black">
    <code v-if="fixed">&lt;{{ root.name }}&gt;</code>
    <div v-else>
      <code>&lt;</code><input class="input is-inline is-small is-family-monospace" v-model="root.name"><code>&gt;</code>
    </div>
    <div class="tag">{{ root.type }}</div>
    <div v-if="root.type !== 'complex'">
      <input v-model="root.value" class="input">
    </div>
    <XmlBuilder v-else v-for="(child, i) in root.children" :key="i" :root="child" :fixed="false"/>
    <div v-if="root.type === 'complex'" class="buttons">
      <button class="button is-info is-small" @click="root.children.push({type: 'complex', name: '', value: '', children: []})">complex</button>
      <button class="button is-link is-small" @click="root.children.push({type: 'string', name: '', value: '', children: []})">string</button>
      <button class="button is-link is-small" @click="root.children.push({type: 'integer', name: '', value: '', children: []})">integer</button>
    </div>
    <code>&lt;/{{ root.name }}&gt;</code>
  </div>
</template>
<script>
export default {
  name: "XmlBuilder",
  props: ["root", "fixed"],
  mounted() {
    console.log(this.root)
  }
}
</script>
