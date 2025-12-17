const state = {
  isLoggedIn: false,
  currentUser: {
    id: 0,
    username: '',
    fullName: '',
    userType: 4
  }
}

const mutations = {
  SET_LOGIN_STATUS(state, status) {
    state.isLoggedIn = status
  },
  SET_USER_INFO(state, user) {
    state.currentUser = user
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
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}