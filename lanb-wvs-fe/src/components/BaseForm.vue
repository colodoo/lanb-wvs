<template>
  <el-card shadow="never" style="margin-bottom: 16px;">
    <div v-if="title !== null" slot="header">
      <span>{{ title }}</span>
    </div>

    <el-form label-width="auto" size="small" label-position="right" :inline="true">
      <el-form-item
        v-for="(field, index) in fields"
        :key="index"
        :label="field.name"
        label-width="80px"
      >
        <el-input v-model="data[field.field]" :placeholder="'请输入' + field.name"></el-input>
      </el-form-item>
      <el-form-item label=" " label-width="80px">
        <el-button type="primary" @click="submit">提交</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
export default {
  name: 'baseForm',

  props: {

    fields: {
      default: function () {
        return []
      },
      type: Array
    },

    data: {
      default: function () {
        return {}
      },
      type: Object
    },

    title: {
      default: null,
      type: String
    },

    submit: {
      default: function () { },
      type: Function
    }
  },

  data () {
    return {
    }
  }
}
</script>

<style scoped>
</style>
