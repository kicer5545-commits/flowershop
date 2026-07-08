export function isLoggedIn() {
  return localStorage.getItem('isLoggedIn') === 'true'
}

export function getUser() {
  const userStr = localStorage.getItem('user')
  return userStr ? JSON.parse(userStr) : null
}

export function login(user) {
  localStorage.setItem('isLoggedIn', 'true')
  localStorage.setItem('user', JSON.stringify(user))
}

export function logout() {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('user')
}

export function isAdmin() {
  const user = getUser()
  return user && user.role === 1
}