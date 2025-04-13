document.addEventListener('DOMContentLoaded', () => {
  const filterBtn = document.getElementById('filterBtn');
  const prevBtn = document.getElementById('prevBtn');
  const nextBtn = document.getElementById('nextBtn');
  const currentPageInput = document.querySelector('#currentPage');
  const fuelTypeNameInput = document.querySelector('#fuelTypeName');
  const startDateInput = document.querySelector('#startDate');
  const transactionRows = document.querySelectorAll('.transaction-row');

  let currentPage = parseInt(currentPageInput.value, 10);

  // Ефект фокусу для полів вводу та випадаючого списку
  [fuelTypeNameInput, startDateInput].forEach(input => {
    input.addEventListener('focus', function () {
      this.style.borderColor = '#ED5909';
    });
    input.addEventListener('blur', function () {
      this.style.borderColor = '#262626';
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

  // Ефект наведення для кнопки "Фільтрувати"
  filterBtn.addEventListener('mouseover', function () {
    if (!this.disabled) {
      this.style.backgroundColor = '#D04F08';
    }
  });
  filterBtn.addEventListener('mouseout', function () {
    if (!this.disabled) {
      this.style.backgroundColor = '#ED5909';
    }
  });

  // Ефект наведення для кнопок пагінації
  [prevBtn, nextBtn].forEach(button => {
    button.addEventListener('mouseover', function () {
      if (!this.disabled) {
        this.style.backgroundColor = '#D04F08';
      }
    });
    button.addEventListener('mouseout', function () {
      if (!this.disabled) {
        this.style.backgroundColor = '#ED5909';
      }
    });
  });

  function navigateToPage(page) {
    const url = new URL(window.location.href);
    url.searchParams.set('page', page);

    if (fuelTypeNameInput.value) {
      url.searchParams.set('fuelTypeName', fuelTypeNameInput.value);
    } else {
      url.searchParams.delete('fuelTypeName');
    }

    if (startDateInput.value) {
      url.searchParams.set('startDate', startDateInput.value);
    } else {
      url.searchParams.delete('startDate');
    }

    window.location.href = url.toString();
  }

  if (prevBtn) {
    prevBtn.addEventListener('click', () => {
      if (!prevBtn.disabled) {
        const newPage = currentPage - 1;
        navigateToPage(newPage);
      }
    });
  }

  if (nextBtn) {
    nextBtn.addEventListener('click', () => {
      if (!nextBtn.disabled) {
        const newPage = currentPage + 1;
        navigateToPage(newPage);
      }
    });
  }
});
