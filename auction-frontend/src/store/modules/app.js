const state = {
  activeMenu: '/assets'
}

const mutations = {
  SET_ACTIVE_MENU(state, menu) {
    state.activeMenu = menu
  }
}

const actions = {
  setActiveMenu({ commit }, menu) {
    commit('SET_ACTIVE_MENU', menu)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}