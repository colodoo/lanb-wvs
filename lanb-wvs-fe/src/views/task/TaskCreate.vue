<template>
  <div>
    <div class="row">
      <div class="col-6">
        <el-card shadow="never" style="min-height: 600px;">
          <div slot="header" class="clearfix">
            <span>新建扫描任务</span>
          </div>
          <el-form :model="form" label-width="80px" size="small">
            <el-form-item label="任务名">
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-input v-model="form.taskName" placeholder="请输入任务名"></el-input>
                </el-col>
                <el-col :span="4">
                  <el-link type="primary" @click="generateTaskName">生成</el-link>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="任务周期">
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-select v-model="form.cycle" clearable>
                    <el-option value="0" label="仅一次"></el-option>
                    <el-option value="1" label="每日扫描任务"></el-option>
                    <el-option value="2" label="每周扫描任务"></el-option>
                    <el-option value="3" label="每月扫描任务"></el-option>
                    <el-option value="4" label="自定义周期"></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="资产列表">
              <el-row :gutter="12">
                <el-col :span="12">
                  <input hidden class="input" v-model="form.assets" placeholder="请选择资产列表" />
                  <el-tag
                    v-for="asset in form.assetArr"
                    :key="asset.assetId"
                    type="info"
                    effect="plain"
                  >{{asset.assetName}}</el-tag>
                  <el-button size="small" @click="isModal = true">+ 选择</el-button>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="漏洞列表">
              <el-row :gutter="12">
                <el-col :span="12">
                  <input hidden class="input" v-model="form.bugs" placeholder="请选择漏洞列表" />
                  <el-tag
                    v-for="bug in form.bugArr"
                    :key="bug.bugId"
                    type="info"
                    effect="plain"
                  >{{bug.bugName}}</el-tag>
                  <el-button size="small" @click="isModalBug = true">+ 选择</el-button>
                </el-col>
              </el-row>
            </el-form-item>

            <el-form-item label="扫描方式">
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-select v-model="form.sacnType" clearable>
                    <el-option value="1" label="基础扫描"></el-option>
                    <el-option value="2" label="组合扫描"></el-option>
                    <el-option value="3" label="自定义扫描"></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </el-form-item>

            <el-form-item label="报表模板">
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-select v-model="form.reportTemplate" clearable>
                    <el-option value="0" label="基础模板"></el-option>
                    <el-option value="1" label="自定义扫描1"></el-option>
                    <el-option value="2" label="自定义扫描2"></el-option>
                    <el-option value="3" label="自定义扫描3"></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </el-form-item>

            <el-form-item label="任务描述">
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-input type="textarea" :rows="1" v-model="form.desc" placeholder="请输入任务描述"></el-input>
                </el-col>
              </el-row>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" icon="el-icon-plus" @click="createTask">创建</el-button>
              <el-button icon="el-icon-refresh" @click="form = defaultForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <div class="col-6">
        <el-card shadow="never" style="height: 600px;">
          <div slot="header" class="clearfix">
            <span>扫描历史</span>
          </div>
          <ul class="list">
            <template v-for="(hisTask, index) in hisTaskList">
              <li :key="index">{{ hisTask.createTime }}&nbsp;创建任务&nbsp;{{ hisTask.taskName }}</li>
            </template>
          </ul>
        </el-card>
      </div>
    </div>

    <el-dialog title="资产列表"  width="70%" :visible.sync="isModal">
      <baseTable :isToolbar="false" ref="assetModal" :fields="fields" baseURL="/api/asset"></baseTable>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="assetModalClick">确定</el-button>
        <el-button type="primary" @click="isModal = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="漏洞列表" width="70%" :visible.sync="isModalBug">
      <baseTable :isToolbar="false" ref="bugModal" :fields="bugFileds" baseURL="/api/bug"></baseTable>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="bugModalClick">确定</el-button>
        <el-button type="primary" @click="isModalBug = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
var df = require('date-formatter')
export default {
  name: 'taskCreate',
  data () {
    return {

      defaultForm: {
        taskName: '',
        bugs: '',
        bugArr: [],
        assets: '',
        assetArr: [],
        cycle: '0',
        sacnType: '1',
        reportTemplate: '0',
        desc: ''
      },

      form: {
        taskName: '',
        bugs: '',
        bugArr: [],
        assets: '',
        assetArr: [],
        cycle: '0',
        sacnType: '1',
        reportTemplate: '0',
        desc: ''
      },

      isModal: false,

      isModalBug: false,

      fields: [
        {
          field: 'assetId',
          name: '资产ID',
          hidden: true
        },
        {
          field: 'assetName',
          name: '资产名称'
        },
        {
          field: 'updateTime',
          name: '创建时间',
          type: 'date'
        },
        {
          field: 'createUserId',
          name: '创建人'
        },
        {
          field: 'createTime',
          name: '更新时间',
          type: 'date'
        },
        {
          field: 'updateUserId',
          name: '更新人'
        },
        {
          field: 'remark',
          name: '备注'
        }
      ],

      bugFileds: [
        {
          field: 'bugId',
          name: '漏洞ID',
          hidden: true
        },
        {
          field: 'bugName',
          name: '漏洞名'
        },
        {
          field: 'desc',
          name: '漏洞描述',
          type: 'textarea'
        },
        {
          field: 'createTime',
          name: '创建时间',
          type: 'date'
        },
        {
          field: 'createUserName',
          name: '创建人'
        },
        {
          field: 'updateTime',
          name: '更新时间',
          type: 'date'
        },
        {
          field: 'updateUserName',
          name: '更新人'
        },
        {
          field: 'remark',
          name: '备注',
          type: 'textarea'
        }
      ],

      hisTaskList: []

    }
  },

  methods: {
    createTask () {
      this
        .$post('/api/task/execCreateTask', this.form)
        .then(data => {
          if (data.success) {
            this.$confirm('是否跳转到列表页面?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'success'
            }).then(() => {
              this.$router.push({
                path: '/task/task-table'
              })
            }).catch(() => {
              this.$message({
                type: 'info',
                message: '已取消跳转'
              })
            })

            this.getHisTaskList()
          } else {
            this.$message({
              type: 'error',
              message: data.msg
            })
          }
        })
        .catch(() => {
        })
    },

    getIds (array, field) {
      const resultArr = []
      for (let index = 0; index < array.length; index++) {
        const element = array[index]
        resultArr.push(element[field])
      }
      return resultArr.join(',')
    },

    getArr (array, field) {
      const resultArr = []
      for (let index = 0; index < array.length; index++) {
        const element = array[index]
        resultArr.push(element[field])
      }
      return resultArr
    },

    unique (oldArr, newArr, key) {
      if (oldArr.length === 0) {
        return newArr
      }
      const tmpOldArr = oldArr
      for (let i = 0; i < newArr.length; i++) {
        const newElment = newArr[i]
        for (let j = 0; j < tmpOldArr.length; j++) {
          const oldElement = tmpOldArr[j]
          if (newElment[key] === oldElement[key]) {
            break
          }
          if (j === tmpOldArr.length - 1) {
            oldArr.push(newElment)
          }
        }
      }
      console.log(oldArr)
      return oldArr
    },

    assetModalClick () {
      const assetModal = this.$refs.assetModal
      const selectedRowArr = assetModal.getSelectedRows()
      this.form.assetArr = this.unique(this.form.assetArr, selectedRowArr, 'assetId')
      this.form.assets = this.getIds(this.form.assetArr, 'assetId')
      this.isModal = false
    },

    bugModalClick () {
      const bugModal = this.$refs.bugModal
      const selectedRowArr = bugModal.getSelectedRows()
      this.form.bugArr = this.unique(this.form.bugArr, selectedRowArr, 'bugId')
      this.form.bugs = this.getIds(this.form.bugArr, 'bugId')
      this.isModalBug = false
    },

    generateTaskName () {
      const date = new Date()
      const dateStr = df(date, 'YYYY-MM-DD hh:mm:ss')
      const taskName = '[' + dateStr + ']日常任务'
      this.form.taskName = taskName
      this.form.desc = taskName
    },

    getHisTaskList () {
      this.$post('/api/task/queryPage', { page: { rows: -1, page: 1 } }).then(data => {
        if (data.rows != null) {
          this.hisTaskList = data.rows
        }
      }).catch(e => {
        console.log(e)
      })
    }
  },

  created () {
    this.getHisTaskList()
  }
}
</script>

<style lang="scss" scoped>
.edit-btn {
  display: inline-block;
  margin-left: 8px;
  color: #3080fe;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }

  i {
    font-size: 9pt;
  }
}

.el-tag + .el-tag {
  margin-left: 8px;
  margin-top: 8px;
}
</style>
