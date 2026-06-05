import { createRouter, createWebHistory } from 'vue-router'
import SchedulePage from '../pages/SchedulePage.vue'
import GroupsPage from '../pages/GroupsPage.vue'
import AboutPage from '../pages/AboutPage.vue'

const routes = [
  {
    path: '/',
    name: 'Matches',
    component: SchedulePage
  },
  {
    path: '/standings',
    name: 'Standings',
    component: GroupsPage
  },
  {
    path: '/about',
    name: 'About',
    component: AboutPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
