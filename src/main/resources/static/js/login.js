document.addEventListener('DOMContentLoaded', function () {
  const usernameInput = document.getElementById('username');
  const passwordInput = document.getElementById('password');
  const togglePassword = document.getElementById('toggle-password');
  const loginButton = document.getElementById('login-button');

  // Ефект при фокусі для username
  usernameInput.addEventListener('focus', function () {
    this.style.borderColor = '#ED5909';
  });
  usernameInput.addEventListener('blur', function () {
    this.style.borderColor = '#262626';
  });

  // Ефект при фокусі для password
  passwordInput.addEventListener('focus', function () {
    this.style.borderColor = '#ED5909';
  });
  passwordInput.addEventListener('blur', function () {
    this.style.borderColor = '#262626';
  });

  // Перемикання видимості пароля
  togglePassword.addEventListener('click', function () {
    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    passwordInput.setAttribute('type', type);
    this.classList.toggle('fa-eye');
    this.classList.toggle('fa-eye-slash');
  });

  // Ефект наведення для кнопки "Увійти"
  loginButton.addEventListener('mouseover', function () {
    this.style.backgroundColor = '#D04F08';
  });
  loginButton.addEventListener('mouseout', function () {
    this.style.backgroundColor = '#ED5909';
  });
});