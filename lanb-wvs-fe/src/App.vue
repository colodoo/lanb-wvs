<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import '@/assets/scss/all.scss'
import '@/assets/scss/list.scss'
// import '@/assets/scss/reset.scss'
import '@/assets/scss/layout.scss'
import '@/assets/scss/modal.scss'
import '@/assets/scss/form.scss'
import '@/assets/scss/button.scss'
import '@/assets/scss/menu.scss'
import '@/assets/scss/tab.scss'
import '@/assets/scss/menu-bar.scss'
import '@/assets/scss/panel.scss'
import '@/assets/scss/table.scss'
import '@/assets/scss/input.scss'
import '@/assets/scss/color.scss'
import '@/assets/scss/dialog.scss'
import '@/assets/scss/tag.scss'
import '@/assets/fonts/iconfont.css'
export default {
  name: 'App',

  watch: {
    // 如果路由有变化，会再次执行该方法
    '$route': 'routerWatch'
  },

  methods: {
    routerWatch () {
      const state = this.$store.state
      const user = state.user
      if (!user.isLogin) {
        this.$router.push({ path: '/login' })
      }
    }
  },

  created () {
    // 在页面加载时读取localStorage里的状态信息
    if (localStorage.getItem('store')) {
      this.$store.replaceState(Object.assign({}, this.$store.state, JSON.parse(localStorage.getItem('store'))))
    }

    // 在页面刷新时将vuex里的信息保存到localStorage里
    window.addEventListener('beforeunload', () => {
      // console.log('beforeunload')
      localStorage.setItem('store', JSON.stringify(this.$store.state))
    })

    const state = this.$store.state
    const user = state.user
    if (!user.isLogin) {
      this.$router.push({ path: '/login' })
    }
  }
}
</script>

<style>
/* @import url(//at.alicdn.com/t/font_727264_9qv3whkrw6.css); */
#app {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>
