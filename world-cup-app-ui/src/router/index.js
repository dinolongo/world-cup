import { createRouter, createWebHistory } from 'vue-router'
import SchedulePage from '../pages/SchedulePage.vue'
import GroupsPage from '../pages/GroupsPage.vue'
import PredictionPage from '../pages/PredictionPage.vue'
import KnockoutsPage from '../pages/KnockoutsPage.vue'
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
    path: '/prediction',
    name: 'Prediction',
    component: PredictionPage
  },
  {
    path: '/knockouts',
    name: 'Knockouts',
    component: KnockoutsPage
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
