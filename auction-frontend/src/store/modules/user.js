const state = {
  isLoggedIn: localStorage.getItem('isLoggedIn') === 'true' || false,
  currentUser: JSON.parse(localStorage.getItem('currentUser')) || {
    id: 0,
    username: '',
    fullName: '',
    userType: 4
  }
}

const mutations = {
  SET_LOGIN_STATUS(state, status) {
    state.isLoggedIn = status
    localStorage.setItem('isLoggedIn', status)
  },
  SET_USER_INFO(state, user) {
    state.currentUser = user
    localStorage.setItem('currentUser', JSON.stringify(user))
  }
}

const actions = {
  login({ commit }, user) {
    commit('SET_LOGIN_STATUS', true)
    commit('SET_USER_INFO', user)
  },
  logout({ commit }) {
    commit('SET_LOGIN_STATUS', false)
    commit('SET_USER_INFO', {
      id: 0,
      username: '',
      fullName: '',
      userType: 4
    })
    // 清除localStorage中的用户信息
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('currentUser')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}