<template>
  <div>
    <!-- 编辑表单 -->
    <el-dialog :title="actionTitle" :visible.sync="isModal">
      <el-form :inline="true" :model="editForm">
        <el-form-item
          v-for="(field, index) in fields"
          :key="index"
          :label="field.name"
          :label-width="formLabelWidth"
        >
          <template v-if="field.type === 'select'">
            <el-select clearable v-model="editForm[field.field]">
              <template v-for="(codeType, index) in getCodeTypeMap(field)">
                <el-option :value="codeType.value" :key="index" :label="codeType.name"></el-option>
              </template>
            </el-select>
          </template>
          <template v-else-if="field.type === 'date'">
            <el-date-picker
              v-model="editForm[field.field]"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="选择日期时间"
            ></el-date-picker>
          </template>
          <template v-else>
            <el-input v-model="editForm[field.field]" :placeholder="field.name"></el-input>
          </template>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button :loading="false" type="primary" @click="postForm">确 定</el-button>
        <el-button @click="isModal = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 文字提示框 -->
    <!-- <input
      class="input"
      v-model="input.text"
      v-show="input.show"
      :style="'z-index: 9999; transition: none; position: fixed; ' + 'top: ' + input.offsetTop + '; left: ' + input.offsetLeft"
    />-->

    <!-- 搜索表单 -->
    <el-card shadow="never" v-if="isSearch" style="margin-bottom: 16px;">
      <div slot="header" class="clearfix">
        <span>搜索条件</span>
        <!-- <el-button
          @click="searchShowInner = !searchShowInner"
          icon="el-icon-arrow-up"
          style="float: right; padding: 3px 0"
          type="text"
        ></el-button>-->
      </div>
      <el-form
        v-show="searchShowInner"
        label-width="auto"
        size="small"
        label-position="right"
        :inline="true"
        :model="form"
        class="demo-form-inline"
      >
        <el-form-item
          v-for="(field, index) in fields"
          :key="index"
          :label="field.name"
          :label-width="formLabelWidth"
        >
          <template v-if="field.type === 'select'">
            <el-select clearable v-model="form[field.field]">
              <template v-for="(codeType, index) in getCodeTypeMap(field)">
                <el-option :value="codeType.value" :key="index" :label="codeType.name"></el-option>
              </template>
            </el-select>
          </template>
          <template v-else-if="field.type === 'date'">
            <el-date-picker
              v-model="form[field.field]"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期"
            ></el-date-picker>
          </template>
          <template v-else-if="field.type === 'time'">
            <el-date-picker
              v-model="form[field.field]"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="选择日期时间"
            ></el-date-picker>
          </template>
          <template v-else>
            <el-input v-model="form[field.field]" :placeholder="field.name"></el-input>
          </template>
        </el-form-item>
        <el-form-item label=" " :label-width="formLabelWidth">
          <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
          <el-button @click="formReset" icon="el-icon-refresh">重置</el-button>
        </el-form-item>
        <!-- <el-form-item :label-width="formLabelWidth">
          <el-button @click="formReset" icon="el-icon-refresh">重置</el-button>
        </el-form-item>-->
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never">
      <div>
        <!-- 工具栏 -->
        <div class="tool-bar" v-if="isToolbar">
          <template v-if="isDefaultBtn">
            <el-button size="small" type="primary" icon="el-icon-plus" @click="saveHandle">新增</el-button>
            <el-button size="small" type="primary" icon="el-icon-edit" @click="updateHandle">修改</el-button>
            <el-button size="small" type="primary" icon="el-icon-delete" @click="deleteHandle">删除</el-button>
          </template>

          <template v-for="(btn, index) in btns">
            <el-button size="small" :key="index" type="primary" @click="btnClick(btn)">{{btn.title}}</el-button>
          </template>

          <!-- 工具栏插槽 -->
          <slot name="toolbar"></slot>
        </div>

        <div
          v-show="!show"
          v-loading="!show"
          style="background-color: #f9f9f9; margin: 0px 8px; height: 550px;"
        ></div>
        <transition name="fade">
          <div v-show="show" style="padding: 0px 8px;">
            <slot></slot>
            <table class="table">
              <thead>
                <tr>
                  <th v-if="checkbox.show" style="text-align: center;">
                    <!-- <input
                    v-model="checkbox.checked"
                    @click="selectToggle"
                    class="checkbox"
                    type="checkbox"
                    />-->
                    <el-checkbox v-model="checkbox.checked" @change="selectToggle"></el-checkbox>
                  </th>

                  <template v-for="(field, index) in fields">
                    <th v-show="!field.hidden" :key="index" :width="field.width">{{field.name}}</th>
                  </template>
                </tr>
              </thead>

              <tbody>
                <tr :key="index" v-for="(row, index) in rows">
                  <td v-if="checkbox.show" style="text-align: center;">
                    <el-checkbox v-model="row.checked" :key="index"></el-checkbox>
                  </td>
                  <template v-for="(field, fieldIndex) in fields">
                    <td
                      @dblclick="cellDbclick($event, row)"
                      v-if="!field.hidden"
                      :key="fieldIndex"
                      :width="field.width"
                    >
                      <template v-if="isFieldSolt(field.field)">
                        <slot :name="field.field" :row="row"></slot>
                      </template>
                      <template v-else>
                        <template v-if="field.formatter != null">
                          <span v-html="field.formatter(cellFormatter(field, row), row)"></span>
                        </template>
                        <template v-else>
                          <span
                            @dblclick="cellDbclick($event, row)"
                            :title="cellFormatter(field, row)"
                          >{{cellFormatter(field, row)}}</span>
                        </template>
                      </template>
                    </td>
                  </template>
                  <!-- <slot v-if="$scopedSlots[field] !== null" :name="field.field" :row="row" ></slot> -->
                </tr>
                <tr v-if="rows.length === 0">
                  <td :colspan="noDataColspan" style="text-align: center;">没有数据</td>
                </tr>
              </tbody>
            </table>
          </div>
        </transition>

        <div class="pagination">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="getList"
            :current-page.sync="pageInfo.page"
            :page-sizes="[10, 20, 30, 40]"
            :page-size.sync="pageInfo.rows"
            layout="total, sizes, prev, pager, next, jumper"
            :total="this.total"
          ></el-pagination>
        </div>
      </div>
    </el-card>

    <!-- loading -->
    <!-- <loading
      :active="!show"
      :can-cancel="loading.canCancel"
      :on-cancel="loading.onCancel"
      :color="loading.color"
      :opacity="loading.opacity"
    ></loading>-->
  </div>
</template>

<script>
import Loading from 'vue-loading-overlay'
import 'vue-loading-overlay/dist/vue-loading.css'
export default {

  name: 'baseTable',

  props: {

    // 是否默认双击事件
    isDefaultDbclick: {
      type: Boolean,
      default: false
    },

    // 条件
    query: {
      type: Object,
      default: null
    },

    // 是否显示搜索条件
    isSearch: {
      type: Boolean,
      default: true
    },

    // 是否显示工具栏
    isToolbar: {
      type: Boolean,
      default: true
    },

    // 字段配置
    fields: {
      type: Array,
      default: () => []
    },

    // 模块基础地址
    baseURL: {
      type: String,
      default: ''
    },

    api: {
      type: Object,
      default: () => {
        return {
          'save': '/save',
          'delete': '/delete',
          'update': '/update',
          'query': '/queryPage'
        }
      }
    },

    // 按钮配置
    btns: {
      type: Array,
      default: () => {
        return []
      }
    },

    isDefaultBtn: {
      type: Boolean,
      default: () => {
        return true
      }
    }
  },

  data () {
    return {

      searchShowInner: true,
      rows: [],
      pageInfo: {
        page: 1,
        rows: 10
      },
      total: 0,
      isModal: false,
      checkbox: {
        checked: false,
        show: true
      },
      action: 'save',
      editForm: null,
      form: null,
      show: false,
      day: '',
      loading: {
        canCancel: true,
        onCancel: () => {
          this.show = true
        },
        opacity: 0,
        color: 'rgb(0, 123, 255)'
      },

      // 删除操作确认框
      deleteAction: {
        isDialog: false,
        dialogMsg: '确认删除该行?',
        ok: () => {

        }
      },

      transform: '',
      input: {
        offsetTop: '0px',
        offsetLeft: '0px',
        text: '',
        show: false
      },
      formLabelWidth: '100px'
    }
  },

  computed: {
    maxPage () {
      if (this.total % this.pageInfo.rows === 0) {
        return parseInt(this.total / this.pageInfo.rows)
      }
      return parseInt(this.total / this.pageInfo.rows) + 1
    },

    actionTitle () {
      if (this.action === 'save') {
        return '新增表单'
      } else if (this.action === 'update') {
        return '编辑表单'
      }
      return '表单'
    },

    noDataColspan () {
      let colspan = 0
      for (let index = 0; index < this.fields.length; index++) {
        const field = this.fields[index]
        if (!field.hidden) {
          colspan++
        }
      }
      if (this.checkbox.show) {
        colspan++
      }
      return colspan
    }
  },

  methods: {
    isFieldSolt (field) {
      return (typeof this.$scopedSlots[field]) !== 'undefined'
    },

    handleSizeChange (val) {
      this.pageInfo.rows = val
      this.getList()
    },

    getElementLeft (element) {
      var actualLeft = element.offsetLeft
      var current = element.offsetParent
      while (current !== null) {
        actualLeft += (current.offsetLeft + current.clientLeft)
        current = current.offsetParent
      }
      return actualLeft
    },

    getElementTop (element) {
      var actualTop = element.offsetTop
      var current = element.offsetParent
      while (current !== null) {
        actualTop += (current.offsetTop + current.clientTop)
        current = current.offsetParent
      }
      return actualTop
    },

    // 单元格双击事件
    cellDbclick (event, row) {
      // this.input.show = true
      // let offsetTop = this.getElementTop(event.srcElement) + 'px'
      // let offsetLeft = this.getElementLeft(event.srcElement) + 'px'
      // let text = event.target.innerText

      // this.input.offsetTop = offsetTop
      // this.input.offsetLeft = offsetLeft
      // this.input.text = text

      if (!this.isDefaultDbclick) {
        return
      }

      this.initCheckbox(this.rows)
      row.checked = true
      this.updateHandle()
    },

    // 取得代码类型列表
    getCodeTypeMap (field) {
      return this.$store.state.cache.codeInfo[field.codeType]
    },

    // 单元格格式化
    cellFormatter (field, row) {
      // 处理字符串
      let val = String(row[field.field])
      // 处理为空情况
      val = val === null || val === 'null' ? '' : val
      // 处理需要格式化内容
      if (typeof field.codeType !== 'undefined') {
        let codeTypeName = field.codeType
        let codeInfo = this.$store.state.cache.codeInfo[codeTypeName]
        // 处理为空情况
        if (typeof codeInfo === 'undefined') {
          return
        }
        for (let index = 0; index < codeInfo.length; index++) {
          const element = codeInfo[index]
          if (element.value === val) {
            return element.name
          }
        }
        return val
      } else {
        return val
      }
    },

    // 提交表单
    postForm () {
      const tmpParam = this.editForm
      delete tmpParam.checked
      if (this.action === 'save') {
        this
          .$post(this.baseURL + this.api.save, tmpParam)
          .then(data => {
            if (data.success) {
              this.$message({
                showClose: true,
                message: '保存成功',
                type: 'success'
              })
              this.isModal = false
              this.getList()
            } else {
              this.$message({
                showClose: true,
                message: data.msg,
                type: 'error'
              })
            }
          })
      } else if (this.action === 'update') {
        this
          .$post(this.baseURL + this.api.update, tmpParam)
          .then(data => {
            if (data.success) {
              this.isModal = false
              this.$message({
                showClose: true,
                message: '更新成功',
                type: 'success'
              })
              this.getList()
            } else {
              this.$message({
                showClose: true,
                message: data.msg,
                type: 'error'
              })
            }
          })
      }
    },

    // 表单重置
    formReset () {
      this.initSearchForm()
      this.search()
    },

    selectToggle () {
      for (let i = 0; i < this.rows.length; i++) {
        const row = this.rows[i]
        row.checked = this.checkbox.checked
      }
    },

    // 取被选中的行数据
    getSelectedRows () {
      const tmpList = []
      for (let i = 0; i < this.rows.length; i++) {
        const row = this.rows[i]
        if (row.checked) {
          tmpList.push(row)
        }
      }
      return tmpList
    },

    saveHandle () {
      this.initEditForm()
      this.isModal = true
      this.action = 'save'
    },

    deleteHandle () {
      const rows = this.getSelectedRows()
      if (rows.length !== 1) {
        this.$message({
          message: '请选中一行数据',
          showClose: true,
          type: 'warning'
        })
        return
      }

      this.$confirm('此操作将永久删除该数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.show = false
        const row = rows[0]
        delete row.checked
        this
          .$post(this.baseURL + this.api.delete, row)
          .then(data => {
            if (data.success) {
              this.$message({
                type: 'success',
                showClose: true,
                message: '删除成功!'
              })
              this.deleteAction.isDialog = false
              this.checkbox.checked = false
              this.show = true
              this.getList()
            } else {
              this.$message({
                type: 'info',
                message: '已取消删除'
              })
              this.getList()
            }
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },

    updateHandle () {
      const rows = this.getSelectedRows()
      if (rows.length !== 1) {
        this.$message({
          message: '请选中一行数据',
          showClose: true,
          type: 'warning'
        })
        return
      }
      /**
       * 对象拷贝,防止改动到表格内容
       */
      const tmpRow = JSON.parse(JSON.stringify(rows[0]))

      // 遍历对象转字符串
      for (let key in tmpRow) {
        tmpRow[key] = tmpRow[key] + ''
      }

      this.editForm = tmpRow
      this.isModal = true
      this.action = 'update'
    },

    // 初始化checkbox
    initCheckbox (rows) {
      for (let i = 0; i < rows.length; i++) {
        const row = rows[i]
        row.checked = false
      }
      return rows
    },

    // 查询
    search () {
      this.pageInfo.page = 1
      this.getList()
    },

    getList () {
      this.show = false
      const tmpParam = this.form
      tmpParam.page = this.pageInfo

      this
        .$post(this.baseURL + this.api.query, tmpParam)
        .then(data => {
          this.rows = this.initCheckbox(data.rows)
          this.total = data.total

          // 强制刷新dom,防止checkbox出现问题
          this.$nextTick(() => {
            this.show = true
          })
        })
    },

    // 改变页码
    pageChange (val) {
      const num = val - this.pageInfo.page
      if (typeof (this.pageInfo.page) === 'string') {
        this.pageInfo.page = parseInt(this.pageInfo.page)
      }
      if (this.pageInfo.page + num > this.maxPage) {
        return
      }
      if (this.pageInfo.page + num <= 0) {
        return
      }
      this.pageInfo.page += num
      this.checkbox.checked = false
      this.getList()
    },

    // 初始化编辑表单
    initEditForm () {
      const tmpForm = {}
      for (let index = 0; index < this.fields.length; index++) {
        const field = this.fields[index]
        tmpForm[field.field] = ''
      }
      this.editForm = tmpForm
    },

    // 初始化查询表单
    initSearchForm () {
      const tmpForm = {}
      for (let index = 0; index < this.fields.length; index++) {
        const field = this.fields[index]
        tmpForm[field.field] = ''
      }
      this.form = tmpForm
    },

    // 按钮点击事件
    btnClick (btn) {
      if (typeof btn.click !== 'undefined') {
        btn.click(this.getSelectedRows(), this)
      }
    },

    keydownHandle (event) {
      var e = event || window.e
      var keyCode = e.keyCode || e.which
      switch (keyCode) {
        case 13:
          this.getList()
          break
      }
    },

    afterEnter () {
      const modal = this.$refs.modal
      const width = modal.offsetWidth
      const height = modal.offsetHeight
      // 如果不是是偶数,则加0.5px
      if (width % 2 !== 0) {
        this.transformX = 'calc(-50% + 0.5px)'
      } else {
        this.transformX = '-50%'
      }

      if (height % 2 !== 0) {
        this.transformY = 'calc(-50% + 0.5px)'
      } else {
        this.transformY = '-50%'
      }

      this.transform = 'translate(' + this.transformX + ',' + this.transformY + ')'
    },

    // 初始化query条件存在情况
    initQueryForm () {
      if (this.query != null) {
        for (const queryKey in this.query) {
          if (this.query.hasOwnProperty(queryKey)) {
            const queryItem = this.query[queryKey]
            // 遍历条件,满足则填充
            for (const formKey in this.form) {
              if (this.form.hasOwnProperty(formKey)) {
                if (queryKey === formKey) {
                  this.form[formKey] = queryItem
                }
              }
            }
          }
        }
      }
    }
  },

  created () {
    this.initEditForm()
    this.initSearchForm()
    this.getList()
    document.addEventListener('keydown', this.keydownHandle)
    // query条件初始化存在情况
    this.initQueryForm()
  },

  beforeDestroy () {
    document.removeEventListener('keydown', this.keydownHandle)
  },

  watch: {
    'pageInfo.rows': function (newVal, oldVal) {
      if (newVal <= 0) {
        this.pageInfo.rows = oldVal
      }
    },

    'pageInfo.page': function (newVal, oldVal) {
      if (newVal <= 0 && newVal > this.maxPage) {
        this.pageInfo.page = oldVal
      }
    }
  },

  components: {
    Loading
  }

}

</script>
<style lang="scss" scoped>
.form .input {
  // margin-top: 8px;
  margin-right: 8px;
}

.form .button {
  margin-top: 8px;
  margin-right: 8px;
}

.pagination .rows {
  float: right;
}

.tool-bar {
  .button {
    margin-right: 5px;
  }
}

.demo-form-inline {
  width: auto;
}

.el-date-editor.el-input,
.el-date-editor.el-input__inner {
  width: 190px;
}

.el-select {
  width: 190px;
}

.el-form-item--mini.el-form-item,
.el-form-item--small.el-form-item {
  margin-bottom: 16px;
}

.el-form-item__label {
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
}
</style>
