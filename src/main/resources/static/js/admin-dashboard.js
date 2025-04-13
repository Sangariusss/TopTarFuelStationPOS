document.addEventListener('DOMContentLoaded', function () {
  const navLinks = document.querySelectorAll('aside nav a');
  const statsCards = document.querySelectorAll('.stats-card');
  const transactionRows = document.querySelectorAll('.transaction-row');
  const viewAllLink = document.querySelector('.view-all-link');

  // Ефект наведення для пунктів бічної панелі
  navLinks.forEach(link => {
    link.addEventListener('mouseover', function () {
      if (this.style.backgroundColor !== 'rgb(237, 89, 9)') {
        this.style.backgroundColor = '#D04F08';
      }
    });
    link.addEventListener('mouseout', function () {
      if (this.style.backgroundColor !== 'rgb(237, 89, 9)') {
        this.style.backgroundColor = '#262626';
      }
    });
  });

  // Ефект наведення для рядків таблиці
  transactionRows.forEach(row => {
    row.addEventListener('mouseover', function () {
      this.style.backgroundColor = '#D04F08';
    });
    row.addEventListener('mouseout', function () {
      this.style.backgroundColor = '';
    });
  });

  // Ефект наведення для посилання "Переглянути всі транзакції"
  if (viewAllLink) {
    viewAllLink.addEventListener('mouseover', function () {
      this.style.color = '#FFFFFF';
      this.style.textDecoration = 'underline';
    });
    viewAllLink.addEventListener('mouseout', function () {
      this.style.color = '#ED5909';
      this.style.textDecoration = 'none';
    });
  }
});