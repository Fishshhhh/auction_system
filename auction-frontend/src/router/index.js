import Vue from 'vue'
import Router from 'vue-router'
import Layout from '@/views/Layout.vue'
import Login from '@/views/Login.vue'
import AssetsView from '../views/AssetsView.vue'
import AuctionsView from '../views/AuctionsView.vue'
import MyBidsView from '../views/MyBidsView.vue'
import OrdersView from '../views/OrdersView.vue'
import ConfigManagement from '../views/ConfigManagement.vue'
import AuctionDetail from '../views/AuctionDetail.vue'
import ScheduledTasksView from '../views/ScheduledTasksView.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/layout',
      component: Layout,
      children: [
        {
          path: '',
          redirect: '/assets'
        },
        {
          path: '/assets',
          name: 'Assets',
          component: AssetsView
        },
        {
          path: '/auctions',
          name: 'Auctions',
          component: AuctionsView
        },
        {
          path: '/auctions/:id',
          name: 'AuctionDetail',
          component: AuctionDetail,
          props: true
        },
        {
          path: '/my-bids',
          name: 'MyBids',
          component: MyBidsView
        },
        {
          path: '/orders',
          name: 'Orders',
          component: OrdersView
        },
        {
          path: '/config',
          name: 'Config',
          component: ConfigManagement
        },
        {
          path: '/scheduled-tasks',
          name: 'ScheduledTasks',
          component: ScheduledTasksView
        }
      ]
    }
  ]
})