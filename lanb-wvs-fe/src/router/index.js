import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Index',
      component: require('@/page/IndexZui').default,
      children: [
        {
          path: 'dashboard',
          components: {
            content: require('@/page/Dashboard').default
          }
        },
        {
          path: 'menu-table',
          components: {
            content: require('@/page/MenuTable').default
          }
        },
        {
          path: 'code-type-table',
          components: {
            content: require('@/page/CodeTypeTable').default
          }
        },
        {
          path: 'bug-table',
          components: {
            content: require('@/page/BugTable').default
          }
        },
        {
          path: 'bug-level-table',
          components: {
            content: require('@/page/BugLevelTable').default
          }
        },
        {
          path: 'asset-table',
          components: {
            content: require('@/page/AssetTable').default
          }
        },
        {
          path: 'task-table',
          components: {
            content: require('@/page/TaskTable').default
          }
        },
        {
          path: 'task-create',
          components: {
            content: require('@/page/TaskCreate').default
          }
        }
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: require('@/page/Login').default
    },
    {
      path: '/index-ice',
      name: 'IndexIce',
      component: require('@/page/IndexIce.vue').default
    },
    {
      path: '/index-zui',
      name: 'IndexZui',
      component: require('@/page/IndexZui.vue').default
    },
    {
      path: '/404',
      name: 'BlankPage',
      component: require('@/page/BlankPage.vue').default
    }
  ]
})
